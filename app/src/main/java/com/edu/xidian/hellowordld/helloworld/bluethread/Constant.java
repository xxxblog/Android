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

    //连接出错
    public static final int MSG_CONNECT_ERROR=6;
    //关闭套接字出错
    public static final int MSG_CLOSESOCKET_ERROR=7;
    //断开连接出错
    public static final int MSG_CLIENT_CLOS_ERROR=7;
    //发送数据
    public static final int MSG_SEND_DATA=8;

    //出错
    public static final int MSG_ERROR = 10;

    //拔刀音效1
    public static final String bajian1="bajian1.mp3";
    //拔刀音效2
    public static final String bajian2="bajian2.mp3";
    //拔刀音效3
    public static final String bajian3="bajian_3m.mp3";

    //紧张气氛音效1  12秒
    public static final String jinzhang1_12="jinzhang_12m.mp3";
    //紧张气氛音效2  15秒
    public static final String jinzhang2_15="jinzhang_15.mp3";

    public static final String[] jinZhangArray={"jinzhang_12m.mp3","jinzhang_15.mp3"};

    public static final int[] timeOfJinZhang={12,15};
}
