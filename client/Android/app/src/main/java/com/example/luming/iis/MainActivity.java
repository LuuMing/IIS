package com.example.luming.iis;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private DatabaseOperator dbOperator;
    private List<Device> DeviceList = new ArrayList<Device>();
    private Button btnAdd;
    private Button btnLog;
    private Button btnReg;
    private deviceAdapter adapter;
    private static  Handler mHandler;
    private EditText mUserNameText;
    private EditText mUserPassWordText;
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // @mHandler 0: 连接失败 1: 连接成功 2:登陆成功 3:注册成功
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 0:
                        Toast.makeText(MainActivity.this, msg.obj.toString() , Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, (String)msg.obj , Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                        intent.putExtra("json",(String) msg.obj);
                        startActivity(intent);
                        break;
                    case 2:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //把数据进行保存
                        editor.putString("user_id",msg.obj.toString());
                        editor.commit();
                        btnLog.setText("用户 ID: "+msg.obj);
                        btnLog.setActivated(false);
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, (String)msg.obj , Toast.LENGTH_SHORT).show();
                }
            }
        };
        this.setContentView(R.layout.content_main);
        sharedPreferences = getSharedPreferences("config",0);

        dbOperator = DatabaseOperator.getInstance(this);
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


        btnAdd = this.findViewById(R.id.btnAdd);
        btnLog = this.findViewById(R.id.btnLog);
        btnReg = this.findViewById(R.id.btnReg);
        btnLog.setText("用户 ID: "+ sharedPreferences.getString("user_id","点击登陆"));
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
                        dbOperator.addDevice(new Device(device_name,device_ip,device_port));
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
        btnLog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("登陆我的数据中心");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_login_register, null);
                builder.setView(view);
                mUserNameText = view.findViewById(R.id.username);
                mUserPassWordText  = view.findViewById(R.id.password);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                attemptLogin();
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
        btnReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("注册账户");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_login_register, null);
                builder.setView(view);
                mUserNameText = view.findViewById(R.id.username);
                mUserPassWordText  = view.findViewById(R.id.password);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        attemptRegister();
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
            DeviceList.addAll(dbOperator.queryAllDevice());
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
                    message.obj = "连接失败";
                }
                mHandler.sendMessage(message);
            }
        }.start();
    }
    private void attemptLogin() {
        // Reset errors.
        mUserNameText.setError(null);
        mUserPassWordText.setError(null);

        new Thread(new MyLoginThread()).start();
    }
    private void attemptRegister() {
        // Reset errors.
        mUserNameText.setError(null);
        mUserPassWordText.setError(null);
        new Thread(new MyRegisterThread()).start();
    }

    public class MyLoginThread implements Runnable {
        @Override
        public void run() {
            Message message = new Message();
            String info = WebService.executeHttpGet("LogLet",mUserNameText.getText().toString(), mUserPassWordText.getText().toString());
            if(info.equals("NULL")) {
                message.what = 0;
                message.obj = "登陆失败";
            }
            else {
                message.what = 2;
                message.obj = info;
            }
            mHandler.sendMessage(message);
        }
    }

    public class MyRegisterThread implements Runnable {
        @Override
        public void run() {
            Message message = new Message();
            String info = WebService.executeHttpGet("RegLet",mUserNameText.getText().toString(), mUserPassWordText.getText().toString());
            if(info.equals("NULL")) {
                message.what = 0;
                message.obj = "注册失败";
            }
            else {
                message.what = 3;
                message.obj = "注册成功请登录";
            }
            mHandler.sendMessage(message);
        }
    }

}