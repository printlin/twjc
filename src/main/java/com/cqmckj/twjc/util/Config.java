package com.cqmckj.twjc.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yl
 * 2020/2/8.
 */
public class Config {
    public static float twLimit=37.4f;
    public static int rcxMax=50;
    public static int rcxMin=0;
    public static int rcxRange=10;
    public static int markRadius=10;
    public static int markThickness=2;
    public static int markColorR=255;
    public static int markColorG=0;
    public static int markColorB=0;
    public static int cameraRgb=0;
    public static int cameraRcx=1;
    public static String detectPath="D:\\opencv\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml";
    public static int detectFaceMax=400;
    public static int detectFaceMin=50;
    public static int detectSleep=50;
    static {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("src/main/resources/app.properties"));
            Properties prop = new Properties();
            prop.load(in);
            twLimit=Float.valueOf(prop.getProperty("tw.limit"));
            rcxMax=Integer.valueOf(prop.getProperty("rcx.max"));
            rcxMin=Integer.valueOf(prop.getProperty("rcx.min"));
            rcxRange=Integer.valueOf(prop.getProperty("rcx.range"));
            markRadius=Integer.valueOf(prop.getProperty("mark.radius"));
            markThickness=Integer.valueOf(prop.getProperty("mark.thickness"));
            markColorR=Integer.valueOf(prop.getProperty("mark.color.r"));
            markColorG=Integer.valueOf(prop.getProperty("mark.color.g"));
            markColorB=Integer.valueOf(prop.getProperty("mark.color.b"));
            cameraRgb=Integer.valueOf(prop.getProperty("camera.rgb"));
            cameraRcx=Integer.valueOf(prop.getProperty("camera.rcx"));
            detectPath=prop.getProperty("detect.path");
            detectFaceMax=Integer.valueOf(prop.getProperty("detect.face.max"));
            detectFaceMin=Integer.valueOf(prop.getProperty("detect.face.min"));
            detectSleep=Integer.valueOf(prop.getProperty("detect.sleep"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

}
