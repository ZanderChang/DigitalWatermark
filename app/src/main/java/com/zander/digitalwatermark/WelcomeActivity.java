package com.zander.digitalwatermark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zander on 7/13/2017.
 */

public class WelcomeActivity extends AppCompatActivity{

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //欢迎界面完毕后启动主界面
            Intent it = new Intent(WelcomeActivity.this, FirstActivity.class);
            startActivity(it);
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            WelcomeActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

//        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_welcome);
//
//        Glide.with(this).load(R.drawable.back).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                layout.setBackground(resource);
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
}
