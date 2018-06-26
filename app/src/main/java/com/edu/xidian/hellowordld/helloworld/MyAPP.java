package com.edu.xidian.hellowordld.helloworld;

import android.app.Application;
import android.os.Handler;

import com.edu.xidian.hellowordld.helloworld.bluethread.AcceptThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.ClientConnectThread;

/**
 * Created by HH on 2018/6/25.
 */

public class MyAPP extends Application {
    // 共享变量
    private Handler handler = null;
    private ClientConnectThread clientConnectThread =null;
    private AcceptThread acceptThread = null;
    // set方法
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    public void setClientConnectThread(ClientConnectThread clientConnectThread){
        this.clientConnectThread=clientConnectThread;
    }
    public void setAcceptThread(AcceptThread acceptThread){
        this.acceptThread=acceptThread;
    }

    // get方法
    public Handler getHandler() {
        return handler;
    }
    public ClientConnectThread getClientConnectThread() {
        return clientConnectThread;
    }
    public AcceptThread getAcceptThread() {
        return acceptThread;
    }
}
