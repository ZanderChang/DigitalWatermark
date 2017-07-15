package com.zander.digitalwatermark;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.github.yoojia.qrcode.qrcode.QRCodeDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.zander.digitalwatermark.Watermark.generateSimpleWatermarkPlain;
import static com.zander.digitalwatermark.Watermark.generateSimpleWatermarkQRCode;
import static com.zander.digitalwatermark.Watermark.generateWatermarkPlain;
import static com.zander.digitalwatermark.Watermark.generateWatermarkQRCode;

public class MainActivity extends AppCompatActivity {
    private int WATERMARK_SIZE = 70;
    private HideW hideW;
    private ImageView iv_adding_watermark;
    public LocationClient mLocationClient = null;
    public static final int ABSTRACT_WATERMARK_QRCODE = 1;
    public static final int ADD_WATERMARK = 2;
    public static final int ABSTRACT_WATERMARK_PLAIN = 3;
    public static final int ADD_WATERMARK_TEST = 4;
    private String oriPath;
    private Bitmap dubiousBitmap;
    private Boolean fromAlbum = true;
    private Boolean addQRCode = true; // 嵌入水印类型
    private ProgressDialog AddPd;
    private ProgressDialog AbstractPd;
    private Boolean SIMPLE_MODE;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ABSTRACT_WATERMARK_QRCODE:
                    String res = (String) msg.obj;
                    AbstractPd.dismiss();
                    // 展示水印信息
                    Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                            setTitle("水印信息").
                            setMessage(res).
                            create();
                    alertDialog.show();
                    break;
                case ADD_WATERMARK:
                    Bitmap finalBitmap = (Bitmap) msg.obj;
                    // 将添加水印的图片保存到系统相册
                    SavePicIntoSystemAlbum(finalBitmap);
                    // 显示添加水印的图片
                    iv_adding_watermark.setImageBitmap(finalBitmap);
                    // 将嵌入水印后的图片设为待检测的可疑图片
                    dubiousBitmap = finalBitmap;
                    AddPd.dismiss();
                    break;
                case ADD_WATERMARK_TEST:
                    Bitmap testBitmap = (Bitmap) msg.obj;
                    iv_adding_watermark.setImageBitmap(testBitmap);
                    break;
                case ABSTRACT_WATERMARK_PLAIN:
                    iv_adding_watermark.setImageBitmap((Bitmap) msg.obj);
                    AbstractPd.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_adding_watermark = (ImageView) findViewById(R.id.iv_adding_watermark);
        String tmp = Environment.getExternalStorageDirectory().getPath() + "/myImage";

        // 获取原始bitmap路径
        Intent intent = getIntent();
        oriPath = intent.getStringExtra("oriPic");
        String from = intent.getStringExtra("from"); // 启动该activity的来源

        // 是否使用简易模式
        SIMPLE_MODE = intent.getBooleanExtra("SIMPLE_MODE", false);

        // 初始化百度定位sdk
        if (!SIMPLE_MODE) {
            if (mLocationClient == null) {
                mLocationClient = new LocationClient(getApplicationContext());
                mLocationClient.registerLocationListener(new BaiduLocationListener());
                initLocation();
            } else {
                mLocationClient.start();
            }
        }


        // 若启动源来自手动拍摄则嵌入水印
        if ("taking_photo".equals(from)) {
            fromAlbum = false;
            String type = intent.getStringExtra("type");
            if ("pic".equals(type)) { // 嵌入图片水印
                addQRCode = false;
                if (!SIMPLE_MODE) {
                    WATERMARK_SIZE = 120;
                } else {
                    WATERMARK_SIZE = 32;
                }
            } else {
                addQRCode = true;
                if (!SIMPLE_MODE) {
                    WATERMARK_SIZE = 70;
                } else {
                    WATERMARK_SIZE = 21; // 仅包含imei
                }
            }
            hideW = new HideW(0, WATERMARK_SIZE);
            if (!SIMPLE_MODE) {
                mLocationClient.requestLocation();
            } else {
                Bitmap simpleWatermarkBitmap;
                if (addQRCode) {
                    simpleWatermarkBitmap = generateSimpleWatermarkQRCode(MainActivity.this);
                } else {
                    simpleWatermarkBitmap = generateSimpleWatermarkPlain(MainActivity.this);
                }
//                iv_adding_watermark.setImageBitmap(simpleWatermarkBitmap);
                // 原始图片
                Bitmap oriBitmap = BitmapFactory.decodeFile(oriPath);
                // 添加水印!
                AddPd = ProgressDialog.show(MainActivity.this, "提示", "嵌入水印中，请稍后……");
                AddWatermarkThread addWatermarkThread = new AddWatermarkThread(oriBitmap, simpleWatermarkBitmap);
                addWatermarkThread.start();
            }
        } else { // 否则直接显示该图片
            fromAlbum = true;
            dubiousBitmap = BitmapFactory.decodeFile(oriPath);
            iv_adding_watermark.setImageBitmap(dubiousBitmap);
        }

        Button btn_abstract = (Button) findViewById(R.id.btn_abstract);
        btn_abstract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractPd = ProgressDialog.show(MainActivity.this, "提示", "提取水印中，请稍后……");
                // 提取图片中的水印信息
                AbstractWatermarkThread abstractWatermarkThread = new AbstractWatermarkThread();
                abstractWatermarkThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();// Important!
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        // option.setScanSpan(1000);
        option.setIsNeedAddress(true); // acquire more information
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private class BaiduLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (!fromAlbum)
            {
                Bitmap watermarkBitmap;
                if (addQRCode) {
                    watermarkBitmap = generateWatermarkQRCode(MainActivity.this, bdLocation);
                } else {
                    watermarkBitmap = generateWatermarkPlain(MainActivity.this, bdLocation);
                }
                // 原始图片
                Bitmap oriBitmap = BitmapFactory.decodeFile(oriPath);
                // 添加水印!
                AddPd = ProgressDialog.show(MainActivity.this, "提示", "嵌入水印中，请稍后……");
                AddWatermarkThread addWatermarkThread = new AddWatermarkThread(oriBitmap, watermarkBitmap);
                addWatermarkThread.start();
//                Bitmap qrcode = generateWatermarkPlain(MainActivity.this, bdLocation);
//                Message msg = new Message();
//                msg.what = ADD_WATERMARK_TEST;
//                msg.obj = qrcode;
//                handler.sendMessage(msg);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    class AbstractWatermarkThread extends Thread {

        public AbstractWatermarkThread() {}

        public void run() {
            Message msg = new Message();
            if (!fromAlbum) {
                if (addQRCode) { // 提取二维码并扫描
                    String res = AbstractWatermark(dubiousBitmap);
                    msg.what = ABSTRACT_WATERMARK_QRCODE;
                    msg.obj = res;
                    handler.sendMessage(msg);
                } else {
                    Bitmap qrcode = AbstractWatermarkBitmap(dubiousBitmap);
                    msg.what = ABSTRACT_WATERMARK_PLAIN;
                    msg.obj = qrcode;
                    handler.sendMessage(msg);
                }
            } else { // 图片来自系统图库，依次使用提取二维码和提取水印图片的方法
                String res = AbstractWatermark(dubiousBitmap);
                if (res != null) {
                    msg.what = ABSTRACT_WATERMARK_QRCODE;
                    msg.obj = res;
                    handler.sendMessage(msg);
                } else { // 如果解析二维码失败则直接返回解析水印的图片
                    Bitmap qrcode = AbstractWatermarkBitmap(dubiousBitmap);
                    msg.what = ABSTRACT_WATERMARK_PLAIN;
                    msg.obj = qrcode;
                    handler.sendMessage(msg);
                }
            }

        }
    }

    class AddWatermarkThread extends Thread {
        private Bitmap oriBitmap;
        private Bitmap qrcode;

        public AddWatermarkThread(Bitmap oriBitmap, Bitmap qrcode) {
            this.oriBitmap = oriBitmap;
            this.qrcode = qrcode;
        }

        public void run() {
            Bitmap finalBitmap = hideW.hideinfo(oriBitmap, qrcode);
//            // 保存剪切攻击后的图片
//            SavePicIntoSystemAlbum(cutBitmap(finalBitmap));
//            // 保存缩放攻击后的图片
//            SavePicIntoSystemAlbum(scaleBitmap(finalBitmap, true));
//            // 保存添加椒盐噪声后的图片
//            SavePicIntoSystemAlbum(addSaltAndPepperNoise(finalBitmap, 0.95f));
            Message msg = new Message();
            msg.what = ADD_WATERMARK;
            msg.obj = finalBitmap;
            handler.sendMessage(msg);// 执行耗时的方法之后发送消给handler
        }
    }


    private void SavePicIntoSystemAlbum(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/myImage");
        if (!dir.exists()) {
            dir.mkdir();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA);
        String picName = format.format(new Date()) + ".jpg";
        String path = dir.toString() + "/" + picName;

        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(path);
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fs);// 把bitmap写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fs != null;
                fs.flush();
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), path, picName, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // 发送广播通知更新数据库
        Uri uri = Uri.fromFile(new File(path));
        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    private String AbstractWatermark(Bitmap bitmap) {
        String res = "未检测到水印信息";
        Bitmap qrcode = null;

        Take take = new Take(WATERMARK_SIZE, hideW.dct);
        //Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        qrcode = take.take_wm(bitmap);
        // 从二维码读取信息
        final QRCodeDecoder mDecoder = new QRCodeDecoder.Builder().build();
        if (qrcode != null) {
            String tmp = mDecoder.decode(qrcode);
            if (tmp != null) {
                res = tmp;
            }
        }
        return res;
    }

    private Bitmap AbstractWatermarkBitmap(Bitmap bitmap) {
        Bitmap qrcode = null;

        Take take = new Take(WATERMARK_SIZE, hideW.dct);
        //Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        qrcode = take.take_wm(bitmap);

        return qrcode;
    }
}
