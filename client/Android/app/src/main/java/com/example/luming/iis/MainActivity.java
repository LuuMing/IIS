package com.example.luming.iis;
import android.app.*;
import android.content.DialogInterface;
import android.os.*;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private DatabaseOperator dbOperator;
    private List<Device> DeviceList = new ArrayList<Device>();
    private Button btnAdd;
    private deviceAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.content_main);
        dbOperator = new DatabaseOperator(this);
        adapter = new deviceAdapter( MainActivity.this,R.layout.list_item, DeviceList );
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               // builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("配置设备信息");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText name = (EditText)view.findViewById(R.id.name);
                final EditText ip = (EditText)view.findViewById(R.id.ip);
                final EditText port = (EditText)view.findViewById(R.id.port);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 获取
                        String device_name = name.getText().toString().trim();
                        String device_ip = ip.getText().toString().trim();
                        String device_port = port.getText().toString().trim();
                        // 打印出来
                       Toast.makeText(MainActivity.this, "名称: " + device_name + ", IP地址: " + device_ip + "端口"+device_port, Toast.LENGTH_SHORT).show();
                        dbOperator.add(new Device(device_name,device_ip,device_port));
                        onResume();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });

    }
    protected void onResume()
    {
        super.onResume();
        if(adapter != null){
            DeviceList.clear();
            DeviceList.addAll(dbOperator.queryAll());
            adapter.notifyDataSetChanged();
        }

    }


}