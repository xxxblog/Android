package com.edu.xidian.hellowordld.helloworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private MediaPlayer  mMediaPlayer ;
    private MediaPlayer  badaoMediaPlayer = new MediaPlayer();
    private int jinZhangID;
    private int timeSize;
    //管理音频
    private AudioManager mAm;
    //设置数据
    SharedPreferences mSharedPreferences;

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
        //获取用户身份
        flag=intent.getIntExtra("flag",1);
        flagTime=123;
        //获取选择的音乐和时间上限
        mSharedPreferences = this.getSharedPreferences("data",MODE_PRIVATE);

        initMP(badaoMediaPlayer,Constant.bajian2);
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
                        String testString=msg.obj.toString();
                        gameview.setText(testString);
                        String[] he = testString.split("\\.");
                        //如果收到hello代表对方准备开始游戏,如果我们也准备开始游戏里则开始游戏
                        if (he[0].equals("hello")){
                            //我们已经准备开始了
                            if (flag!=1) {
                                timeSize=Integer.valueOf(he[2]);
                                jinZhangID=Integer.valueOf(he[1]);
                            }
                            if (iIsOrNotPlay==1){
                                if (mMediaPlayer==null){
                                    mMediaPlayer = new MediaPlayer();
                                }
                                initJinZhang(mMediaPlayer, Constant.jinZhangArray[jinZhangID],timeSize);
                                starGameBar.setVisibility(View.INVISIBLE);
                                gameview.setText("请在音乐结束后晃动手机");
                                mMediaPlayer.start();
                                //紧张音乐播放完毕
                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        myshakeDetect.start();
                                        startTime = System.currentTimeMillis();
                                        gameview.setText("紧张完毕");
                                        relaseMP(mMediaPlayer);
                                        mMediaPlayer=null;
                                    }
                                });
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
                        flagTime=123;
                        iIsOrNotPlay=0;
                        oIsOrNotPlay=0;
                        myTime=flagTime;
                        ontherTime=flagTime;
                        Toast.makeText(GameActivity.this,"连接断开",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GameActivity.this,bluteToothActivity.class);
                        startActivity(intent);
                    case Constant.MSG_CONNECT_ERROR:
                        flagTime=123;
                        iIsOrNotPlay=0;
                        oIsOrNotPlay=0;
                        myTime=flagTime;
                        ontherTime=flagTime;
                        Toast.makeText(GameActivity.this,"请重新进入房间",Toast.LENGTH_SHORT).show();
                        Intent intentHelp = new Intent(GameActivity.this,bluteToothActivity.class);
                        startActivity(intentHelp);
                }
            }
        };
        //flag=1为服务器,分别获取自己的套接字线程
        if (flag!=1){
            gameview.setText("我是客户端");
            //获取要连接的设备编号
            int i = intent.getIntExtra("i",1);
            connectServer(i);
        }else {
            //gameview.setText("我是服务端");
            createServer();
            jinZhangID = mSharedPreferences.getInt("jinzhang",0);
        }

        //准备游戏Button
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //************************播放音乐显示，图片，音乐停止时开启移动检测
                byte[] hi=hello.getBytes();
                if (mMediaPlayer==null){
                mMediaPlayer = new MediaPlayer();
                }
                starGameBar.setVisibility(View.VISIBLE);
                if (flag!=1){
                    gClientThread.sendData(hi);
                    iIsOrNotPlay=1;
                }else {
                    int temp = mSharedPreferences.getInt("time",6);
                    timeSize=getRandomeTime(4,temp);
                    String h ="hello."+jinZhangID+"."+timeSize+".";
                    byte[] hello=h.getBytes();
                    serverAcceptThread.sendData(hello);
                    iIsOrNotPlay=1;
                }
                //对方已发送
                if(oIsOrNotPlay!=0){
                    initJinZhang(mMediaPlayer,Constant.jinZhangArray[jinZhangID],timeSize);
                    starGameBar.setVisibility(View.INVISIBLE);
                    gameview.setText("请在音乐结束后晃动手机");
                    //播放紧张的音乐
                    mMediaPlayer.start();
                    //紧张音乐结束后检测晃动
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            myshakeDetect.start();
                            startTime = System.currentTimeMillis();
                            gameview.setText("请晃动手机");
                            relaseMP(mMediaPlayer);
                            mMediaPlayer=null;
                        }
                    });
                }
            }
        });

        //监听到摇动时，注册listener，完成接口内容
        myshakeDetect.registerOnShakeListener(new ShakeDetector.OnShakeListener(){
            @Override
            public void onShake(){
                badaoMediaPlayer.start();
                gameImageview.setImageResource(R.drawable.badao);
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

    }

    private int getRandomeTime(int min,int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
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
            gameButton.setText("再次游戏");
        }
        else {
            if (oTime.equals(flagTime)){
                gameImageview.setImageResource(R.drawable.win);
                gameview.setText("对方甚至没来的及发送他的时间");
            }
            if (mTime.equals(flagTime)){
                //gameImageview.setImageResource(R.drawable.loos);
                gameview.setText("请拔出您的宝剑！");
            }
        }

    }

    private void createServer() {
        //gameview.setText("创建服务器");
        if (serverAcceptThread !=null){
            serverAcceptThread.cancel();
        }
        serverAcceptThread = new AcceptThread(mBluetoothAdapter,mUIHandler);
        serverAcceptThread.start();
    }
    public static byte[] long2byte(long res) {
        byte[] buffer=new byte[8];
        for (int i=0;i<8;i++) {
            int offset=64-(i+1)*8;
            buffer[i]=(byte)((res>>offset)&0xff);
        }
        return buffer;
    }
    public static long BytesToLong(byte[] buffer) {
        long  values=0;
        for (int i=0; i<8;i++) {
            values<<=8;values|=(buffer[i]&0xff);
        }
        return values;
    }
    private void initJinZhang(MediaPlayer mp,String musicName,int lenth) {
        try {
            AssetFileDescriptor fd = getAssets().openFd(musicName);
            mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),lenth*20000);
            mp.prepare();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void initMP(MediaPlayer mp,String musicName) {
        try {
            AssetFileDescriptor fd = getAssets().openFd(musicName);
            mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),fd.getLength() );
            mp.prepare();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void relaseMP(MediaPlayer mp){
        mp.stop();
        mp.release();
    }


}
