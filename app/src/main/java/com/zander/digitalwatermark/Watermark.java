package com.zander.digitalwatermark;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.github.yoojia.qrcode.qrcode.QRCodeDecoder;
import com.github.yoojia.qrcode.qrcode.QRCodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Zander on 7/10/2017.
 */

class Watermark {
    private static Boolean TEST = false;

    static Bitmap generateSimpleWatermarkPlain(Context context) {
        String imei = getIMEI(context);
        //1、创建一个bitmap，并放入画布
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //2、设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);//设置画笔颜色
        paint.setStrokeWidth(4.0f);// 设置画笔粗细
        paint.setTextSize(10f);//设置文字大小

        //3.在画布上绘制内容
        canvas.drawColor(Color.WHITE);//默认背景是黑色的
        canvas.drawText(imei.substring(0, 4), 2, 8, paint);
        canvas.drawText(imei.substring(4, 8), 2, 16, paint);
        canvas.drawText(imei.substring(8, 12), 2, 24, paint);
        canvas.drawText(imei.substring(12, 15), 2, 32, paint);

        return bitmap;
    }

    static Bitmap generateWatermarkPlain(Context context, BDLocation bdLocation) {
        String[] location = getLocation(bdLocation);
        String date = getDate();
        String imei = getIMEI(context);
        String androidID = getAndroidID(context);

        //1、创建一个bitmap，并放入画布
        Bitmap bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //2、设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);//设置画笔颜色
        paint.setStrokeWidth(4.0f);// 设置画笔粗细
        paint.setTextSize(10f);//设置文字大小

        //3.在画布上绘制内容
        canvas.drawColor(Color.WHITE);//默认背景是黑色的
        int i = 0;
        for (; i < location.length; ++i) {
            if (location[i] == null) { break; }
            canvas.drawText(location[i], 2, 10 + i * 10, paint);
        }
        canvas.drawText("时间：", 2, 10 + i * 10, paint);
        canvas.drawText(date, 2, 20 + i * 10, paint);
        canvas.drawText("IMEI: ", 2, 30 + i * 10, paint);
        canvas.drawText(imei, 2, 40 + i * 10, paint);
        canvas.drawText("Android ID：", 2, 50 + i * 10, paint);
        canvas.drawText(androidID, 2, 60 + i * 10, paint);

        return bitmap;
//        return RotateBitmap(bitmap, -45);
    }

    static Bitmap generateSimpleWatermarkQRCode(Context context) {
        String imei = getIMEI(context);
        int size = 1;
        return new QRCodeEncoder.Builder()
                .width(size) // 二维码图案的宽度
                .height(size)
                .paddingPx(0) // 二维码的内边距
                .marginPt(0) // 二维码的外边距
                .build()
                .encode(imei);
    }
    static Bitmap generateWatermarkQRCode(Context context, BDLocation bdLocation) {
        String[] location = getLocation(bdLocation);
        String date = getDate();
        String imei = getIMEI(context);
        String androidID = getAndroidID(context);
        String content = "";
        for (String i : location) {
            if (i == null) { break; }
            content += (i + "\n");
        }
        content +=  ("时间：" + date + "\nIMEI：" + imei + "\nAndroid ID： " + androidID);
        int size = 70;
        Bitmap qrCodeImage = new QRCodeEncoder.Builder()
                .width(size) // 二维码图案的宽度
                .height(size)
                .paddingPx(0) // 二维码的内边距
                .marginPt(0) // 二维码的外边距
                .build()
                .encode(content);
        if (TEST)
        {
            final QRCodeDecoder mDecoder = new QRCodeDecoder.Builder().build();
            String decode = mDecoder.decode(qrCodeImage);
            Toast.makeText(context, decode, Toast.LENGTH_LONG).show();
        }
        return qrCodeImage;
    }

    private static String[] getLocation(BDLocation bdLocation) {
        String[] res = new String[5];
        if (bdLocation != null) {
            res[0] = "位置：";
            res[1] = "  经度：" + String.valueOf(bdLocation.getLongitude());
            res[2] = "  纬度：" + String.valueOf(bdLocation.getLatitude());
            res[3] = bdLocation.getCountry() + bdLocation.getCity() + bdLocation.getDistrict();
            res[4] = bdLocation.getStreet();
        }
        return res;
    }

    private static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }

    private static String getIMEI(Context context) {
        String res = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED ) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (tm.getDeviceId() != null)
            {
                res += ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            }
        }
        return res;
    }

    private static String getAndroidID(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private static Bitmap RotateBitmap(Bitmap bitmap, int degree) {
//        Canvas canvas = new Canvas();
//        canvas.drawColor(Color.BLACK);
//        canvas.drawBitmap(bitmap, 0, 0, null);
        Matrix matrix = new Matrix();
//        // matrix.postScale(1f, 1f);
        matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
//        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
