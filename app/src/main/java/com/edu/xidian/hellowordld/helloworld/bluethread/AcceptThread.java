package com.edu.xidian.hellowordld.helloworld.bluethread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;
import android.os.Handler;

/**
 * Created by HH on 2018/6/24.
 */

public class AcceptThread extends Thread{
    private static final String NAME = "BlueToothClass";
    private static final UUID MY_UUID=UUID.fromString(Constant.CONNECTTION_UUID);

    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBlueToothAdapter;
    private final Handler mHandler;
    //新起的线程去处理数据
    private ConnectedThread mConnectedThread;

    //handler用于和UI通信
    public AcceptThread(BluetoothAdapter adapter,Handler handler){
        mBlueToothAdapter=adapter;
        mHandler = handler;
        BluetoothServerSocket tmp = null;

        //创建服务器端SOCKET
        try {
            tmp = mBlueToothAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
        }catch (IOException e){}
        mmServerSocket = tmp;
    }

    //服务器端死循环
    public void run(){
        BluetoothSocket socket = null;
        while (true){
            try{
                mHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                //获取用于通信的soc
                socket = mmServerSocket.accept();
            }catch (IOException e){
                 mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR,e));
                 break;
            }
            if (socket != null){
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        //值处理一个客户
        if(mConnectedThread != null){
            mConnectedThread.start();
        }
        mHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLIENT);
        //开启数据处理线程
        mConnectedThread = new ConnectedThread(socket,mHandler);
        mConnectedThread.start();
    }

    public void cancel(){
        try {
            mmServerSocket.close();
            mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        }catch (IOException e){}
    }

    public void sendData(byte[] data){
        if (mConnectedThread != null){
            mConnectedThread.write(data);
        }
    }

}
