package com.edu.xidian.hellowordld.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.edu.xidian.hellowordld.helloworld.bluethread.AcceptThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.ClientConnectThread;

/**
 * Created by HH on 2018/6/23.
 */

public class GameActivity extends Activity {
    ShakeDetector myshakeDetect;
    Context context;
    TextView gameview;
    ShakeDetector.OnShakeListener listener;
    AcceptThread gServerThread;
    ClientConnectThread gClientThread;
    public Handler mUIHandler;
    long startTime,endTime;
    private static int BLUTOOTH_STATUS = 0;
    public BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保存上下文
        context = getApplicationContext();
        myshakeDetect=new ShakeDetector(context);
        setContentView(R.layout.activity_game);
        //注册listener，完成接口内容
        myshakeDetect.registerOnShakeListener(new ShakeDetector.OnShakeListener(){
            @Override
            public void onShake(){
                endTime =  System.currentTimeMillis();
                gameview.setText("time:"+(endTime-startTime));
                myshakeDetect.stop();
                //

                //Intent intentMain = new Intent(GameActivity.this,MainActivity.class);
                //startActivity(intentMain);
            }
        });


        /*//蓝牙部分：1.打开蓝牙，2.搜索3.创建socket4.传输数据5.关闭蓝牙
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        //检查蓝牙设备是否可用
        if (mBluetoothAdapter !=null){
            //检查蓝牙状态是否可用
            if (!mBluetoothAdapter.isEnabled()) {
                //请求开启蓝牙
                Intent enableIentene  =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //请求结果
                startActivityForResult(enableIentene,BLUTOOTH_STATUS);
            }
        }
        else {
            new AlertDialog.Builder(this).setTitle("标题").setMessage("蓝牙打开失败").setPositiveButton("确定",null).show();
            //tvBluetoothState.setText("蓝牙设备不可用！");
        }*/
        //**********游戏部分
        //2.播放音乐
        //音乐停止时开始监听
        //启动监听者,开始计时
        myshakeDetect.start();
        startTime = System.currentTimeMillis();
        gameview = findViewById(R.id.game_textview);
    }


}
