package com.edu.xidian.hellowordld.helloworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.xidian.hellowordld.helloworld.bluethread.AcceptThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.ClientConnectThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.Constant;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HH on 2018/6/23.
 */

public class GameActivity extends Activity {
    ShakeDetector myshakeDetect;
    Context context;
    TextView gameview;
    ShakeDetector.OnShakeListener listener;
    AcceptThread serverAcceptThread;
    ProgressBar starGameBar;
    ClientConnectThread gClientThread,textClient;
    public Handler mUIHandler;
    long startTime,endTime,ontherTime,myTime,flagTime;
    ImageView gameImageview;
    Button gameButton;
    int flag,iIsOrNotPlay,oIsOrNotPlay;
    String hello = new String("hello.");
    //播放音频
    private MediaPlayer mp = new MediaPlayer();
    public BluetoothAdapter mBluetoothAdapter;
    public  List<BluetoothDevice> myBoubdDeviceList;
    private static ByteBuffer longtoByteBuffer = ByteBuffer.allocate(8);
    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //保存上下文
        context = getApplicationContext();
        gameview = findViewById(R.id.game_textview);
        gameButton = (Button)findViewById(R.id.game_button);
        starGameBar = findViewById(R.id.start_game);
        gameImageview = findViewById(R.id.game_image);
        myshakeDetect=new ShakeDetector(context);
        Intent intent=getIntent();
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        flag=intent.getIntExtra("flag",1);
        flagTime=123;
        //己方是否开始玩，0：没开始，1：开始了
        iIsOrNotPlay=0;
        oIsOrNotPlay=0;
        myTime=flagTime;
        ontherTime=flagTime;
        starGameBar.setVisibility(View.INVISIBLE);
        //----------handle传不过来，所以需要在这个界面连接和创建
        //应该使用套接字线程的Hadnler
        mUIHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String info = (String) msg.obj;
                switch (msg.what) {
                    case Constant.MSG_CONNECTED_TO_SERVER:
                        gameview.setText("连接成功");
                        break;
                    case Constant.MSG_GOT_DATA:
                        //byte[] testmy=((String) msg.obj).getBytes();
                        String testString=msg.obj.toString();
                        String[] he = testString.split("\\.");
                        //如果收到hello代表对方准备开始游戏,如果我们也准备开始游戏里则开始游戏
                        if (he[0].equals("hello")){
                            //我们已经准备开始了
                            if (iIsOrNotPlay==1){
                                gameview.setText("开始游戏");
                                starGameBar.setVisibility(View.INVISIBLE);
                                gameImageview.setImageResource(R.drawable.badao);
                                myshakeDetect.start();
                                startTime = System.currentTimeMillis();
                            }else {
                                oIsOrNotPlay=1;
                            }
                        }
                        //否则对方发送的为时间，进行结果判断
                        else {
                            byte[] testmy = testString.getBytes();
                            ontherTime = BytesToLong(testmy);
                            String buer=String.valueOf(hello.equals(testString));
                            //gameview.setText("收到消息时间" + String.valueOf(ontherTime));
                            showResult(ontherTime, myTime);
                        }
                        break;
                    case Constant.MSG_SEND_DATA:
                        //gameview.setText("检测到正在发送消息");
                        break;
                    case Constant.MSG_ERROR:
                        Toast.makeText(GameActivity.this,"连接错误",Toast.LENGTH_SHORT).show();
                }
            }
        };
        //flag=1为服务器,分别获取自己的套接字线程
        if (flag!=1){
            gameview.setText("我是客户端");
            //
            //textClient = bluteToothActivity.clientConnectThread;
            int i = intent.getIntExtra("i",1);
            connectServer(i);
            /*myBoubdDeviceList=bluteToothActivity.mBoubdDeviceList;
            gameview.setText("开始连接服务端");
            gClientThread = new ClientConnectThread(myBoubdDeviceList.get(i),mBluetoothAdapter,mUIHandler);
            gClientThread.start();
            connectServer(intent.getIntExtra("i",1));*/
        }else {
            gameview.setText("我是服务端");
            createServer();
            //serverAcceptThread = bluteToothActivity.serverAcceptThread;
        }



        //准备游戏
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //************************播放音乐显示，图片，音乐停止时开启移动检测
                byte[] hi=hello.getBytes();
                starGameBar.setVisibility(View.VISIBLE);
                if (flag!=1){
                    gClientThread.sendData(hi);
                    iIsOrNotPlay=1;
                }else {
                    serverAcceptThread.sendData(hi);
                    iIsOrNotPlay=1;
                }
                //对方已发送
                if(oIsOrNotPlay!=0){
                    starGameBar.setVisibility(View.INVISIBLE);
                    gameImageview.setImageResource(R.drawable.badao);
                    myshakeDetect.start();
                    startTime = System.currentTimeMillis();
                }

                /*Intent intent=getIntent();
                int i = intent.getIntExtra("i",1);
                myBoubdDeviceList=bluteToothActivity.mBoubdDeviceList;
                BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
                gameview.setText("开始连接服务端");
                gClientThread = new ClientConnectThread(myBoubdDeviceList.get(i),BTAdapter,mUIHandler);
                gClientThread.start();*/
            }
        });

        //监听到摇动时，注册listener，完成接口内容
        myshakeDetect.registerOnShakeListener(new ShakeDetector.OnShakeListener(){
            @Override
            public void onShake(){
                gameview.setText("");
                endTime =  System.currentTimeMillis();
                myshakeDetect.stop();
                myTime=endTime-startTime;

                byte[] myByte;

                myByte=long2byte(endTime-startTime);
                if (flag!=1){
                    gameview.setText("发送time:"+String.valueOf(endTime-startTime));
                    gClientThread.sendData(myByte);
                }else {
                    gameview.setText("发送time:"+String.valueOf(endTime-startTime));
                    serverAcceptThread.sendData(myByte);
                }
                showResult(ontherTime,myTime);
            }
        });

        //**********游戏部分
        //2.播放音乐
        //音乐停止时开始监听
        //启动监听者,开始计时
        /*myshakeDetect.start();
        startTime = System.currentTimeMillis();*/

    }

    private void connectServer(int i) {
        myBoubdDeviceList=bluteToothActivity.mBoubdDeviceList;
        gameview.setText("开始连接服务端");
        if (gClientThread !=null){
            gClientThread.cancel();
        }
        gClientThread = new ClientConnectThread(myBoubdDeviceList.get(i),mBluetoothAdapter,mUIHandler);
        gClientThread.start();
    }
    public void showResult(Long oTime,Long mTime){
        //先将时间和flag时间相比
        if(!oTime.equals(flagTime) && !mTime.equals(flagTime)){
            //发来的时间比自己小，本机输
            if (oTime<mTime){
                gameImageview.setImageResource(R.drawable.loos);
                gameview.setText("很遗憾，你的速度没有对方快。");
            }else {
                gameImageview.setImageResource(R.drawable.win);
                gameview.setText("你的手速快过了对方！");
            }
            ontherTime=flagTime;
            myTime=flagTime;
            iIsOrNotPlay=0;
            oIsOrNotPlay=0;
        }
        else {
            if (oTime.equals(flagTime)){
                gameview.setText("对方未发送时间");
            }
            if (mTime.equals(flagTime)){
                gameview.setText("你还没开始游戏");
            }
        }
    }

    private void createServer() {
        gameview.setText("创建服务器");
        if (serverAcceptThread !=null){
            serverAcceptThread.cancel();
        }
        serverAcceptThread = new AcceptThread(mBluetoothAdapter,mUIHandler);
        serverAcceptThread.start();
    }
    public static byte[] long2byte(long res) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((res >> offset) & 0xff);
        }
        return buffer;
    }
    public static long BytesToLong(byte[] buffer) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (buffer[i] & 0xff);
        }
        return values;
    }
    /*private void initMP() {
        try {
            AssetFileDescriptor fd = getAssets().openFd("samsara.mp3");
        }catch (IOException ioe){

        }
            try {
            mp.setDataSource(file.getPath());//设置播放音频文件的路径
            mp.prepare();//mp就绪
        } catch(Exceprion e) {
            e.printStackTrace();
        }
    }*/


}
