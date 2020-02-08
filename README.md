# 简介
本项目通过人脸识别技术与热成像技术相结合，实现了人体体温自动化检测。使用OpenCV的级联分类器实现人脸位置检测，再结合热成像数据计算人脸位置的最高温度值作为人体体温值。
# 使用
1. 下载并安装OpenCV
2. 克隆本仓库`git clone https://github.com/printlin/twjc.git`
3. 使用IDEA打开工程
4. 配置参数
5. 运行
# 环境配置
### 配置OpenCV Jar包
依次打开`File > Project Structure > Modules > Dependencies`，点击加号，添加OpenCV安装目录中的opencv-xxx.jar。
### 配置OpenCV DLL 
依次打开`Run/Debug Configurations > Application > Configuration > VM options`，填入`-Djava.library.path=D:\opencv\opencv\build\java\x64`，等号后面填写您本地的OpenCV目录。
### 参考
[java 调用opencv IDEA环境配置](https://blog.csdn.net/zwl18210851801/article/details/81075781)
# 参数配置
在项目resource目录下有一个app.properties配置文件，可进行自定义配置。
- 报警温度`tw.limit=37.4`
- 热成像最大温度`rcx.max=40`
- 热成像最小温度`rcx.min=0`
- 热成像检测范围`rcx.range=10`
- 最高温度点的标记圆圈半径`mark.radius=10`
- 标记的线条大小`mark.thickness=2`
- 标记颜色R通道`mark.color.r=0`
- 标记颜色G通道`mark.color.g=255`
- 标记颜色B通道`mark.color.b=0`
- 彩色相机索引`camera.rgb=0`
- 热成像相机索引`camera.rcx=1`
- 识别模型`detect.path=D:\\opencv\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml`
- 人脸最大像素值`detect.face.max=400`
- 人脸最小像素值`detect.face.min=50`
- 识别间隔毫秒`detect.sleep=50`
# 运行效果
![效果图1](https://github.com/printlin/images/blob/master/twjc/run1.png)
![效果图2](https://github.com/printlin/images/blob/master/twjc/run2.png)
