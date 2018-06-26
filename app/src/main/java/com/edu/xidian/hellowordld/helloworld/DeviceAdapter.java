package com.edu.xidian.hellowordld.helloworld;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HH on 2018/6/24.
 */

public class DeviceAdapter extends BaseAdapter{
    private List<BluetoothDevice> mData;
    private Context mContext;

    public DeviceAdapter( List<BluetoothDevice> Data,Context context){
        mData=Data;
        mContext=context.getApplicationContext();
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        if(itemView == null){
            itemView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2,viewGroup,false);
        }

        TextView line1 = (TextView) itemView.findViewById(android.R.id.text1);
        TextView line2 = (TextView) itemView.findViewById(android.R.id.text2);

        //获取对应蓝牙设备
        BluetoothDevice device = (BluetoothDevice) getItem(i);

        //显示名称
        line1.setText(device.getName());
        //显示地址
        line2.setText(device.getAddress());
        return itemView;
    }
    public void refresh(List<BluetoothDevice> data){
        mData=data;
        //通知Adapter数据改变了
        notifyDataSetChanged();
    }

}
