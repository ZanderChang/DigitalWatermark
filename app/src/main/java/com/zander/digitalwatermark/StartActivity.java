package com.zander.digitalwatermark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

/**
 * Created by Zander on 7/10/2017.
 */

public class StartActivity extends AppCompatActivity {
    private Bitmap oriBitmap = null;
    private Button btn_add_pic_watermark;
    private Button btn_add_qrcode_watermark;
    private ImageView iv_show_photo;
    private Boolean SIMPLE_MODE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 获取原始bitmap路径
        Intent intent = getIntent();
        final String oriPath = intent.getStringExtra("oriPic");
        oriBitmap = BitmapFactory.decodeFile(oriPath);

        // 传递是否采用简易模式
        SIMPLE_MODE = intent.getBooleanExtra("SIMPLE_MODE", false);

        iv_show_photo = (ImageView) findViewById(R.id.iv_show_photo);
        iv_show_photo.setImageBitmap(oriBitmap);

        btn_add_pic_watermark = (Button) findViewById(R.id.btn_add_pic_watermark);
        btn_add_pic_watermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start abstract watermark activity
                Intent newIntent = new Intent(StartActivity.this, MainActivity.class);
                newIntent.putExtra("oriPic", oriPath);
                newIntent.putExtra("from", "taking_photo");
                newIntent.putExtra("type", "pic");
                newIntent.putExtra("SIMPLE_MODE", SIMPLE_MODE);
                startActivity(newIntent);
            }
        });

        btn_add_qrcode_watermark = (Button) findViewById(R.id.btn_add_qrcode_watermark);
        btn_add_qrcode_watermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(StartActivity.this, MainActivity.class);
                newIntent.putExtra("oriPic", oriPath);
                newIntent.putExtra("from", "taking_photo");
                newIntent.putExtra("type", "qrcode");
                newIntent.putExtra("SIMPLE_MODE", SIMPLE_MODE);
                startActivity(newIntent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(StartActivity.this, FirstActivity.class);
        startActivity(intent);
    }
}
