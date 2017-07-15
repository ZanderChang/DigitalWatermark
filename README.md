# DigitalWatermark

一款基于DCT算法的水印嵌入和提取的移动智能终端数字图像证据系统APP，水印载体形式包括二维码和图片两种.在简易模式下，水印信息仅包括IMEI；在完整模式下，水印信息包括地点（经纬度以及在联网情况下获取的具体地理位置描述）、时间、IMEI、Android ID。

## 注意事项

* App中实时定位使用[百度定位SDK](http://lbsyun.baidu.com/index.php?title=android-locsdk)完成，重新调试编译时请根据[这里](http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/key)申请AK，然后修改```AndroidManifest.xml```中```com.baidu.lbsapi.API_KEY```的值。
* 在Android 6.0系统中```MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), path, picName, null)```会导致程序异常奔溃，且无法捕获异常，在5.0系统中无此现象。

## Built With

* [NextQRCode](https://github.com/yoojia/NextQRCode) - 基于ZXing Android实现的QRCode扫描支持库
* [Android Studio](https://developer.android.com/studio/index.html) - The Official IDE for Android