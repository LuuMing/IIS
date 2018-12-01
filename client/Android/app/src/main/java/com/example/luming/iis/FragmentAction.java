package com.example.luming.iis;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
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


public class FragmentAction extends Fragment {
private ListView listView;
private List<String> list = new ArrayList<>();
private ArrayAdapter<String> adapter;
private JSONObject config;
private Button button;
private EditText editText;
private Handler handler;
@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_action,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                try {
                    Toast.makeText(getActivity(), ((JSONObject)msg.obj).getString("content") , Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        listView = getActivity().findViewById(R.id.action_list);
        try {
            config = new JSONObject((String)getActivity().getIntent().getExtras().get("json"));
            Iterator<?> it = config.keys();
            String key = "";
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                if(null!=key && !"".equals(key)){
                     JSONObject setting = new JSONObject(config.getString(key));
                    String type = setting.getString("type");
                     if(type.equals("action")||type.equals("setter"))
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
                button = getActivity().findViewById(R.id.action_button);
                editText = getActivity().findViewById(R.id.action_text);
                final String action = (String) adapterView.getItemAtPosition(i);
                try {
                   JSONObject setting = new JSONObject(config.getString(action));
                   switch (setting.getString("type"))
                   {
                       case "action":
                           Toast.makeText(getActivity(),"ACTION", Toast.LENGTH_SHORT).show();
                           editText.setFocusableInTouchMode(false);
                           editText.setFocusable(false);
                           editText.setText(action+"：执行");
                           button.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   String str = new String("{\"name\":\""+action+"\"}");
                                   Toast.makeText(getActivity(), str , Toast.LENGTH_SHORT).show();
                                    send(str);
                                    receive();
                               }
                           });
                           break;
                       case "setter":
                           Toast.makeText(getContext(),"SETTER", Toast.LENGTH_SHORT).show();
                           editText.setFocusableInTouchMode(true);
                           editText.setFocusable(true);
                           editText.setText("");
                           editText.setHint("请输入控制量");
                           button.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   String value = editText.getText().toString().trim();
                                   String str = ( "{\"name\":\""+action+"\",\"value\":\""+value+"\"}");
                                   Toast.makeText(getActivity(), str , Toast.LENGTH_SHORT).show();
                                   send(str);
                                   receive();
                               }
                           });
                           break;
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
