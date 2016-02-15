## FreeOCR
基于微软牛津计划做的简易OCR封装,主要面向纸制图书的OCR识别.



## 特点

精简,无任何广告!

易用:目前仅有读取图片以及从相机读取图片2个模式


## 开发日程:

任务          | 进度          |  完成时间
------------- | ------------- | -------------
右下角悬浮按钮|√|2016-2-12|
首屏布局设计||
拍照识别|||
取图识别|||
|||
|||
|||
|||
|||
|||
|||
|||
|||
|||
|||


## Build the sample

You will
need a [Microsoft Azure Account](<http://www.azure.com>) if you don't have one already.

1. You must obtain a subscription key for Vision API by following instructions in [Subscription
key management](<http://www.projectoxford.ai/doc/general/subscription-key-mgmt>).

2.  Start Android Studio and open project from Vision \> Android \> Sample folder.

3.  In Android Studio -\> "Project" panel -\> "Android" view, open file
    "app/res/values/strings.xml", and find the line
    "Please\_add\_the\_subscription\_key\_here;". Replace the
    "Please\_add\_the\_subscription\_key\_here" value with your subscription key
    string from the first step. If you cannot find the file "strings.xml", it is
    in folder "Sample\app\src\main\res\values\string.xml".

4.  In Android Studio, select menu "Build \> Make Project" to build the sample,
    and "Run" to launch this sample app.

## todo

添加相机图片/拍摄图片,(截取),然后识别,同时保存到数据库,待识别结果出来后,追加文字到结果.

列表显示文档

文档详情

添加一个应用缩略图


https://github.com/Microsoft/ProjectOxford-ClientSDK/tree/master/Vision/Android

