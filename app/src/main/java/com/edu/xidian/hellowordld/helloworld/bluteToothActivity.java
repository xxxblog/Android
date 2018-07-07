package com.edu.xidian.hellowordld.helloworld;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.xidian.hellowordld.helloworld.bluethread.AcceptThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.ClientConnectThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.ConnectedThread;
import com.edu.xidian.hellowordld.helloworld.bluethread.Constant;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class bluteToothActivity extends Activity {
    private static int BLUTOOTH_STATUS = 0;
    private Button serchButton,creteServerButton,startGame;
    public List<BluetoothDevice> mDeviceList=new ArrayList<>();
    public static List<BluetoothDevice> mBoubdDeviceList=new ArrayList<>();
    public   BluetoothAdapter BTAdapter;
    public DeviceAdapter  mAdapter,bindAapter;
    public ListView  mListView,bindListView;
    public static Handler mUIHandler;
    public ConnectedThread mConnectThread;
    public static ClientConnectThread clientConnectThread;
    public static AcceptThread serverAcceptThread;
    public BluetoothSocket serverSocket;
    public ProgressBar creteServerBar,serchBar;
    public TextView textView;
    public Bundle mBundle;
    ArrayAdapter dArrayAdapter;
    ArrayList bandDeviceList,findDeviceList ;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blute_tooth);
        //**************测试用
        textView = findViewById(R.id.newdecie);
        serchButton = (Button) findViewById(R.id.serch_button);
        creteServerButton = (Button) findViewById(R.id.crete_server);
        mAdapter = new DeviceAdapter(mDeviceList,getApplicationContext());
        bindAapter = new DeviceAdapter(mBoubdDeviceList,getApplicationContext());
        creteServerBar = (ProgressBar) findViewById(R.id.progressBar1);
        creteServerBar.setVisibility(View.INVISIBLE);
        serchBar = (ProgressBar) findViewById(R.id.progressBar2);
        serchBar.setVisibility(View.INVISIBLE);

        //获取蓝牙的适配器
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        mListView = findViewById(R.id.devie_List);
        mListView.setAdapter(mAdapter);

        bindListView = findViewById(R.id.bindDevie_List);
        bindListView.setAdapter(bindAapter);

        //搜索button
        serchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert (BTAdapter != null);
                //刷新数据
                //serchBar.setVisibility(View.VISIBLE);
                mAdapter.refresh(mDeviceList);
                bindAapter.refresh(mBoubdDeviceList);
                //开启扫描
                BTAdapter.startDiscovery();
                //监听搜索到的
                mListView.setOnItemClickListener(bindDeviceClick);
                //绑定设备跟新
                bindAapter.notifyDataSetChanged();
                getBindDevice(BTAdapter);
            }
        });
        //创建服务器按钮
        creteServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(bluteToothActivity.this, GameActivity.class);
                intent.putExtra("flag", 1);
                startActivity(intent);
            }
        });

        //********************蓝牙控制
        if(BTAdapter==null){
            Toast.makeText(this,"当前设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
        }
        //开启蓝牙
        turnOnBlueTooth(BTAdapter);
        //开启蓝牙可见
        turnOnCanSee(BTAdapter);
        //获取绑定设备
        getBindDevice(BTAdapter);

        IntentFilter filter = new IntentFilter();
        //*************监听广播
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //接收
        registerReceiver(mReceiver,filter);
    }


    //开启蓝牙
    private void turnOnBlueTooth(BluetoothAdapter BtAdapter){
        if(!BtAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,0);
            //直接开启蓝牙
            //bTAdatper.enable();
        }
    }
    //开启蓝牙可见
    private void turnOnCanSee(BluetoothAdapter BtAdapter){
        if (BtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置为一直开启
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(i);
        }
    }
    //获取已经绑定设备
    private void getBindDevice(BluetoothAdapter BtAdapter){
        if(BTAdapter.isEnabled()){
            mBoubdDeviceList= new ArrayList<>(BtAdapter.getBondedDevices());
            bindAapter.refresh(mBoubdDeviceList);
            bindListView.setOnItemClickListener(connectClick);
        }
    }

    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
            //点击发现的设备后绑定，安卓版本>4.4
            BluetoothDevice device = mDeviceList.get(i);
            device.createBond();
            //点击后加入已绑定设备
            mBoubdDeviceList.add(device);
        }
    };

    //连接已绑定设备,本机作为客户端
    //问题-----点击两次客户端和服务端都崩了,device写错了
    private AdapterView.OnItemClickListener connectClick = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
            //点击发现的设备后绑定
            BluetoothDevice device = mBoubdDeviceList.get(i);
            Toast.makeText(bluteToothActivity.this,"开始连接设备",Toast.LENGTH_SHORT).show();
            if (clientConnectThread !=null){
                clientConnectThread.cancel();
            }

            //*******************在下一个界面连接服务器
            Intent intent=new Intent();
            intent.setClass(bluteToothActivity.this, GameActivity.class);
            intent.putExtra("flag", 2);
            intent.putExtra("i", i);
            startActivity(intent);

        }
    };

    //*****************广播
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //开始查找
                setProgressBarIndeterminateVisibility(true);
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(bluteToothActivity.this,"开始搜索",Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //查找结束
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(bluteToothActivity.this,"搜索完毕",Toast.LENGTH_SHORT).show();
            }

            //找到了蓝牙设备
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                serchBar.setVisibility(View.INVISIBLE);
                Toast.makeText(bluteToothActivity.this,"找到设备",Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //发现设备为绑定状态
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //此处的adapter是列表的adapter，不是BluetoothAdapter
                    mDeviceList.add(device);
                    //通知Adapter更新数据
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

}


