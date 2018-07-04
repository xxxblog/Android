package com.edu.xidian.hellowordld.helloworld.bluethread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.os.Handler;

/**
 * Created by HH on 2018/6/24.
 */

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmsocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;
    private static ByteBuffer buff = ByteBuffer.allocate(8);
    public ConnectedThread(BluetoothSocket socket,Handler handler){
        mmsocket = socket;
        InputStream tmpIN = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        try {
            tmpIN = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }catch (IOException e){}

        mmInStream = tmpIN;
        mmOutStream = tmpOut;
    }

    //将接收到的数据
    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;

        while (true){
            try{
                bytes = mmInStream.read(buffer);
                if (bytes > 0) {
                    //测试
                    //将数据转换为long类型
                    //buff.put(buffer, 0, buffer.length);
                    Message msg = new Message();
                    //msg.obj = buff.getLong();
                    //msg.obj=buffer;
                    msg.obj = new String(buffer);
                    //msg.obj = new String(buffer,0,bytes,"utf-8");
                    msg.what = Constant.MSG_GOT_DATA;
                    mHandler.sendMessage(msg);
                    //将收到的数据转换为
                    //Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA,new String(buffer,0,bytes,"utf-8"));
                }
                Log.d("GOTMSG", "massage size" + bytes);
            }catch (IOException e){
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR,e));
                break;
            }
        }
    }

    public void write(byte[] bytes){
        try {
            mmOutStream.write(bytes);
            mHandler.sendEmptyMessage(Constant.MSG_SEND_DATA);
        }catch (IOException e){}
    }

    public void cancel(){
        try {
            mmsocket.close();
        }catch (IOException e){}
    }
}
