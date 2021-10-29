package com.example.edu.hhu.wangzb;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.os.Bundle;


/**
 * @className: LogoActivity
 * @description: 过场动画
 * @author: ZiboWang
 * @date: 2021/10/25
 * @version:
 **/



public class LogoActivity extends Activity{

    private Handler handler = new Handler(Looper.getMainLooper()){

        public void handleMessage(android.os.Message msg) {
           if(msg.what==1){
               Intent intent = new Intent(LogoActivity.this,LoginActivity.class);
               startActivity(intent);
               finish();
           }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        handler.sendEmptyMessageDelayed(1,3000);
    }
}