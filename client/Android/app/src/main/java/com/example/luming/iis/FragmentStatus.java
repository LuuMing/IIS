package com.example.luming.iis;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FragmentStatus extends Fragment {
    private ListView listView;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private JSONObject config;
    private Handler handler;
    private Boolean isDestroy;
    private String module_name;
    private String send_cmd;
    private String rec_value;
    private DatabaseOperator databaseOperator;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isDestroy  = new Boolean(false);
        databaseOperator = DatabaseOperator.getInstance(getContext());

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0 :
                    removeMessages(0);
                    if (module_name != null) {
                        send("{\"name\":\"" + module_name + "\"}");
                        receive();
                    }
                    break;
                    case 1 :
                        try {
                            rec_value = ((JSONObject)msg.obj).getString("content");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(), rec_value , Toast.LENGTH_SHORT).show();
                        databaseOperator.addLog(module_name,send_cmd,rec_value);
                        removeMessages(1);
                        if(!isDestroy)
                            sendEmptyMessageDelayed(0, 1000);
                        break;
                }

            }
        };
        listView = getActivity().findViewById(R.id.status_list);
        try {
            config = new JSONObject((String)getActivity().getIntent().getExtras().get("json"));
            Iterator<?> it = config.keys();
            String key = "";
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                if(null!=key && !"".equals(key)){
                    JSONObject setting = new JSONObject(config.getString(key));
                    String type = setting.getString("type");
                    if(type.equals("status"))
                    {
                        list.add(key);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                module_name = (String) adapterView.getItemAtPosition(i);
                send("{\"name\":\""+module_name+"\"}");
                Toast.makeText(getActivity(), module_name , Toast.LENGTH_SHORT).show();
                receive();
            }
        });
    }

    private void send( final String json)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try{
                    JSONObject j = new JSONObject(json);
                    OutputStream out = mSocket.getOut();
                    out.write(j.toString().getBytes());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void receive()
    {
        new Thread()
        {
            Message message = new Message();
            @Override
            public void run()
            {
                try {
                    byte[] buffer = new byte[1024];
                    InputStream inputStream = mSocket.getIn();
                    inputStream.read(buffer);
                    JSONObject jsonObject = new JSONObject(new String(buffer));
                    message.what = 1;
                    message.obj = jsonObject;
                    handler.sendMessage(message);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
            isDestroy = false;
        } else {
            isDestroy = true;
        }
    }
}


