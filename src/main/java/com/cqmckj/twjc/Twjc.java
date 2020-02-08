package com.cqmckj.twjc;

import com.cqmckj.twjc.util.Config;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Created by yl
 * 2020/2/7.
 */
public class Twjc{
    private boolean isLoop = true;
    private JFrame frame;
    private JLabel img,gImg;
    public Twjc(){
        frame = new JFrame("体温检测");
        frame.setLayout(null);
        frame.setSize(1280,530);
        Font font = new Font("宋体",0,14);
        JLabel labelRgb = new JLabel("彩色相机");
        labelRgb.setFont(font);
        labelRgb.setBounds(10, 10, 200, 20);
        JLabel labelRcx = new JLabel("热成像相机");
        labelRcx.setFont(font);
        labelRcx.setBounds(650, 10, 200, 20);
        img = new JLabel();
        img.setBounds(0, 30, 640, 480);
        gImg = new JLabel();
        gImg.setBounds(640, 30, 640, 480);
        frame.add(labelRgb);
        frame.add(labelRcx);
        frame.add(img);
        frame.add(gImg);
        frame.setVisible(true);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((screen_width - frame.getWidth()) / 2,
                (screen_height - frame.getHeight()) / 2);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                frame.setVisible(false);
                isLoop = false;
                System.exit(0);
            }
        });
        frame.setVisible(true);

        init();
    }

    static {
        //加载opencv
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private void init(){
        Scalar markColor = null;
        Scalar markColorDef = new Scalar(Config.markColorB,Config.markColorG,Config.markColorR);
        Scalar markColorErr = new Scalar(0,0,255);
        org.opencv.core.Point circlePoint = new org.opencv.core.Point();
        org.opencv.core.Point putTextPoint = new org.opencv.core.Point();
        org.opencv.core.Point rectanglePoint1 = new org.opencv.core.Point();
        org.opencv.core.Point rectanglePoint2 = new org.opencv.core.Point();

        VideoCapture videoCaptureRgb = new VideoCapture();
        VideoCapture videoCaptureRcx = new VideoCapture();
        CascadeClassifier faceDetector = new CascadeClassifier(Config.detectPath);
        if (!videoCaptureRgb.open(Config.cameraRgb)) {
            System.out.println("彩色相机打开失败");
            System.exit(0);
            return;
        }
        if (!videoCaptureRcx.open(Config.cameraRcx)) {
            System.out.println("热成像相机打开失败");
            videoCaptureRgb.release();
            System.exit(0);
            return;
        }
        Mat rgbImage = new Mat();
        Mat rcxImage = new Mat();
        MatOfRect faceDetections = new MatOfRect();
        while (isLoop) {
            //限制检测频率
            try {Thread.sleep(Config.detectSleep);} catch (InterruptedException e) {}
            //获得彩色图像
            if (!videoCaptureRgb.read(rgbImage)) {
                continue;
            }
            //获得热成像图像
            if (!videoCaptureRcx.read(rcxImage)) {
                continue;
            }
            //基于彩色图像进行人脸识别
            faceDetector.detectMultiScale(rgbImage, faceDetections);
            //遍历识别结果
            for(Rect rect:faceDetections.toArray()){
                //人脸大小过滤
                if(rect.width>Config.detectFaceMax || rect.width<Config.detectFaceMin){
                    continue;
                }
                //截取人脸进行温度提取
                Mat faceImg = new Mat(rcxImage, rect);
                BufferedImage buffImg = conver2Image(faceImg);
                int[] nubRe = findMaxTemp(buffImg);
                //获得温度
                float temp = formatTemp(nubRe[0]);
                if(temp>Config.twLimit){
                    markColor=markColorErr;
                    tempErr(faceImg,temp);
                }else{
                    markColor=markColorDef;
                }
                //绘图
                circlePoint.x=rect.x+nubRe[1];
                circlePoint.y=rect.y+nubRe[2];
                Imgproc.circle(rgbImage,circlePoint,Config.markRadius,markColor,Config.markThickness);
                putTextPoint.x=rect.x+2;
                putTextPoint.y=rect.y+20;
                Imgproc.putText(rgbImage,temp+"",putTextPoint,0, .7, markColor,Config.markThickness,Imgproc.LINE_AA,false);
                rectanglePoint1.x=rect.x;
                rectanglePoint1.y=rect.y;
                rectanglePoint2.x=rect.x + rect.width;
                rectanglePoint2.y=rect.y + rect.height;
                Imgproc.rectangle(rgbImage, rectanglePoint1, rectanglePoint2,markColor,Config.markThickness);
            }
            //UI显示图像
            img.setIcon(new ImageIcon(conver2Image(rgbImage)));
            gImg.setIcon(new ImageIcon(conver2Image(rcxImage)));
        }
        //释放资源
        videoCaptureRgb.release();
        videoCaptureRcx.release();
        faceDetections.release();
        rgbImage.release();
        rcxImage.release();
    }

    /**
     * 温度异常回调函数
     * @param faceImg 人脸图像
     * @param temp 温度
     */
    private static void tempErr(Mat faceImg,float temp){
        //TODO 自行实现
    }

    /**
     * 热成像转换为温度
     * @param temp 色彩平均值
     * @return 热成像上下限中的比值，一位小数
     */
    private static float formatTemp(int temp){
        return ((int)((temp/255f)*(Config.rcxMax-Config.rcxMin)*10))/10f;
    }

    /**
     * 从图像中查找最高的温度范围
     * @param image 图像
     * @return 0：温度最大值  1：x位置  2：y位置
     */
    private static int[] findMaxTemp(BufferedImage image){
        int[] re = {0,0,0};
        int width = image.getWidth();
        int height = image.getHeight();
        int avg,max=-1,maxX=-1,maxY=-1;
        for(int i = 0;i<width-Config.rcxRange;i++){
            for(int j = 0;j<height-Config.rcxRange;j++){
                avg = avgPix(image,i,j,Config.rcxRange);
                if(avg>max){
                    maxX = i;
                    maxY = j;
                    max = avg;
                }
            }
        }
        re[0] = max;
        re[1] = maxX;
        re[2] = maxY;
        return re;
    }

    /**
     * 统计一定范围像素点的GRB平均值
     * @param image 图像
     * @param x 像素点x位置
     * @param y 像素点y位置
     * @param len 范围，以xy为起点的正方形边长
     * @return 平均值
     */
    private static int avgPix(BufferedImage image,int x,int y,int len){
        int sum = 0;
        for(int i=0;i<len;i++){
            for(int j=0;j<len;j++){
                sum += grayPix(image.getRGB(x+i,y+j));
            }
        }
        return sum/(len*len);
    }

    /**
     * rgb三通道的平均值
     * @param rgb 值
     * @return 平均值
     */
    private static int grayPix(int rgb){
        return ( ((rgb>>16)&0xff) + ((rgb>>8)&0xff) +(rgb&0xff) ) / 3;
    }

    /**
     * opencv图像对象转BufferedImage
     * @param mat opencv图像对象
     * @return BufferedImage
     */
    private static BufferedImage conver2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int dims = mat.channels();
        int[] pixels = new int[width*height];
        byte[] rgbdata = new byte[width*height*dims];
        mat.get(0, 0, rgbdata);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int index = 0;
        int r=0, g=0, b=0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                if(dims == 3) {
                    index = row*width*dims + col*dims;
                    b = rgbdata[index]&0xff;
                    g = rgbdata[index+1]&0xff;
                    r = rgbdata[index+2]&0xff;
                    pixels[row*width+col] = ((255&0xff)<<24) | ((r&0xff)<<16) | ((g&0xff)<<8) | b&0xff;
                }
                if(dims == 1) {
                    index = row*width + col;
                    b = rgbdata[index]&0xff;
                    pixels[row*width+col] = ((255&0xff)<<24) | ((b&0xff)<<16) | ((b&0xff)<<8) | b&0xff;
                }
            }
        }
        setRGB( image, 0, 0, width, height, pixels);
        return image;
    }

    /**
     * conver2Image工具函数
     */
    private static void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
            image.getRaster().setDataElements(x, y, width, height, pixels);
        else
            image.setRGB(x, y, width, height, pixels, 0, width);
    }

    /**
     * 主函数
     */
    public static void main(String[] a){
        new Twjc();
    }
}
