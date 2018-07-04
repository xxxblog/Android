package com.edu.xidian.hellowordld.helloworld.bluethread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by HH on 2018/6/24.
 */

public class ClientConnectThread extends Thread{
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private ConnectedThread mConnectedThread;

    //用于产生连接的套接字，并且将该套接字传给connectThread作为通信套接字，但也提供了传输数据的方法
    public ClientConnectThread(BluetoothDevice device, BluetoothAdapter btAdapter, Handler mUIHandler) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        mBluetoothAdapter = btAdapter;
        mHandler = mUIHandler;
        //连接的socket由设备产生
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){}
        mmSocket = tmp;
    }
    public void run(){
        //关闭发现设备
        mBluetoothAdapter.cancelDiscovery();
        //连接服务器
        try{
            mmSocket.connect();
            mHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        }catch (Exception connectException){
            mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR,connectException));
            try {
                mmSocket.close();
            }catch (IOException closeException){}
            return;
        }
        //做数据处理
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        mHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        //因为发送数据都是一样的

        mConnectedThread = new ConnectedThread(mmSocket,mHandler);
        mConnectedThread.start();
    }
    public void cancel(){
        try {
            mmSocket.close();
        }catch (IOException e){}
    }

    public void sendData(byte[] bytes){
        if( mConnectedThread != null){
            mConnectedThread.write(bytes);
        }
    }


}

