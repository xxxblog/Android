package com.edu.xidian.hellowordld.helloworld.bluethread;

/**
 * Created by HH on 2018/6/24.
 */

public class Constant {
    //必须使用这个UUID
    public static final String CONNECTTION_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    //开始监听
    public static final int MSG_START_LISTENING=1;
    //结束监听
    public static final int MSG_FINISH_LISTENING=2;
    //有客户连接
    public static final int MSG_GOT_A_CLIENT=3;
    //连接到服务器
    public static final int MSG_CONNECTED_TO_SERVER=4;
    //获取到数据
    public static final int MSG_GOT_DATA=5;
    //连接到服务器

    //发送数据
    public static final int MSG_SEND_DATA=6;

    //出错
    public static final int MSG_ERROR = 10;
}
