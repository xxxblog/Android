package com.edu.xidian.hellowordld.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button mBtutton;
    private Button hBtutton;
    private Button bluteBtutton;
    @Override
    //onCreate声明创建周期的方法
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //声明布局文件
        setContentView(R.layout.activity_main);

        //找到并且类型转换
        hBtutton = (Button) findViewById(R.id.bt_2);
        mBtutton = (Button) findViewById(R.id.bt_3);
        bluteBtutton = (Button) findViewById(R.id.bt_1);

        //监听
        bluteBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到双人模式
                Intent intentBlute = new Intent(MainActivity.this,bluteToothActivity.class);
                startActivity(intentBlute);
            }
        });
        mBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到帮助界面
                Intent intentHelp = new Intent(MainActivity.this,helpActivity.class);
                startActivity(intentHelp);
                //startActivity(helpActivity);
            }
        });

        hBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到设置界面
                Intent intentH = new Intent(MainActivity.this,settingActivity.class);
                startActivity(intentH);
                //startActivity(helpActivity);
            }
        });
    }
}
