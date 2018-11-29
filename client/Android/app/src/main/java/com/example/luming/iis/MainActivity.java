package com.example.luming.iis;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private DatabaseOperator dbOperator;
    private List<Device> DeviceList = new ArrayList<Device>();
    private Button btnAdd;
    private deviceAdapter adapter;
    private Socket socket = null;
    private BufferedReader in;
    private OutputStream out;
     private static  Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 1:
                        Toast.makeText(MainActivity.this, (String)msg.obj , Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                        intent.putExtra("json",(String) msg.obj);
                        startActivity(intent);
                        break;
                    case 0:
                        Toast.makeText(MainActivity.this, "连接失败" , Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        this.setContentView(R.layout.content_main);
        dbOperator = new DatabaseOperator(this);
        adapter = new deviceAdapter( MainActivity.this,R.layout.list_item, DeviceList );
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Device device = (Device) adapterView.getItemAtPosition(i);
                //Toast.makeText(MainActivity.this, device.getName()+device.getIp()+device.getPort() , Toast.LENGTH_SHORT).show();
                connection(device.getIp(),device.getPort());
                Toast.makeText(MainActivity.this,"正在连接，请稍后", Toast.LENGTH_SHORT).show();
            }

        });


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
                        Integer device_port = Integer.parseInt(port.getText().toString().trim());
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

    private void connection(final String HOST, final Integer PORT)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                Message message = new Message();
                try{
                    mSocket socket = mSocket.getInstance();
                    SocketAddress socketAddress = new InetSocketAddress(HOST,PORT);
                    socket.connect(socketAddress,300);
                    InputStream in = mSocket.getIn();
                    byte [] buffer = new byte[1024];
                    in.read(buffer,0,1024);
                    message.what = 1;
                    message.obj = new String(buffer);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    message.what = 0;
                    message.obj = "error";
                }
                mHandler.sendMessage(message);
            }
        }.start();
    }


}