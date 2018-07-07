package com.edu.xidian.hellowordld.helloworld;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.edu.xidian.hellowordld.helloworld.bluethread.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class settingActivity extends Activity {
    public static SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;
    public AudioManager am;
    private MediaPlayer mMediaPlayer;
    int first=0;
    List<String> timeChoice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSharedPreferences=settingActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        Spinner spinner = (Spinner) findViewById(R.id.jinzhang_spinner);
        Spinner time_spinner = (Spinner) findViewById(R.id.tine_spinner);
        final List<String> jinzhang = new ArrayList<>();
        jinzhang.add("音效1");
        jinzhang.add("音效2");
        SpinnerAdapter yinxiaoAdapter = new SpinnerAdapter(this);
        spinner.setAdapter(yinxiaoAdapter);
        yinxiaoAdapter.setDatas(jinzhang);

        timeChoice = new ArrayList<>();
        for (int j=4;j<=Constant.timeOfJinZhang[0];j++){
            timeChoice.add(j+"秒");
        }
        SpinnerAdapter  timeAdapter = new SpinnerAdapter(this);
        time_spinner.setAdapter(timeAdapter);
        timeAdapter.setDatas(timeChoice);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mMediaPlayer!=null){
                    if (mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer=null;
                    }
                }
                if(first!=0){
                    timeChoice.clear();
                    for (int j=4;j<=Constant.timeOfJinZhang[position];j++){
                        timeChoice.add(j+"秒");
                    }
                    mMediaPlayer = new MediaPlayer();
                    initJinZhang(mMediaPlayer,position);
                    mMediaPlayer.start();
                    editor.putInt("jinzhang", position);
                    editor.commit();
                }
                first=1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putInt("jinzhang", 1);
                editor.putInt("time",6);
                editor.commit();
            }
        });

        time_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("time",position+4);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editor.putInt("jinzhang", 1);
                editor.commit();
            }
        });
    }

    public static String getJinZhang(){
        String jinZhang=mSharedPreferences.getString("jinzhang","");
        if (jinZhang.length()>0){
            return jinZhang;
        }
        return Constant.jinzhang1_12;
    }
    public static int getTime(){
        int musicTime=mSharedPreferences.getInt("time",0);
        if (musicTime>0){
            return musicTime;
        }
        return 6;
    }

    private void initJinZhang(MediaPlayer mp,int i) {
        try {
            AssetFileDescriptor fd = getAssets().openFd(Constant.jinZhangArray[i]);
            mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),fd.getLength());
            mp.prepare();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void onBackPressed() {
        if(mMediaPlayer!=null){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer=null;
            }
        }
        Intent intentH = new Intent(settingActivity.this,MainActivity.class);
        startActivity(intentH);
    }
}
