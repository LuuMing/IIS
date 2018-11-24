package com.example.luming.iis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by luming on 2018/11/23.
 */

public class deviceAdapter extends ArrayAdapter {
    private final int resourceId;

    public deviceAdapter(Context context, int textViewResourceId, List<Device> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Device device = (Device) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
      //  ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);//获取该布局内的图片视图
        TextView deviceName = (TextView) view.findViewById(R.id.device_name);//获取该布局内的文本视图
        TextView deviceIp = (TextView) view.findViewById(R.id.device_ip);
        TextView devicePort = (TextView) view.findViewById(R.id.device_port);
        //fruitImage.setImageResource(fruit.getImageId());//为图片视图设置图片资源
        deviceName.setText(device.getName());//为文本视图设置文本内容
        deviceIp.setText(device.getIp());//为文本视图设置文本内容
        devicePort.setText(device.getPort().toString());//为文本视图设置文本内容
        return view;
    }
}
