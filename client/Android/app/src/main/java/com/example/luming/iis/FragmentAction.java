package com.example.luming.iis;
import android.os.Bundle;
import android.os.Handler;
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
private Handler mhandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_action,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                           editText.setClickable(false);
                           editText.setText(action+"：执行");
                           button.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   JSONObject jsonObject;
                                   String str ="{\"name\":\""+action+"\"}";
                                           Toast.makeText(getActivity(), str , Toast.LENGTH_SHORT).show();
                                   try {
                                       jsonObject = new JSONObject(str);
                                       send(jsonObject);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                               }
                           });
                           break;
                       case "setter":
                           Toast.makeText(getContext(),"SETTER", Toast.LENGTH_SHORT).show();
                           editText.setText("请输入控制量");
                           editText.setClickable(true);
                           break;
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void send( final JSONObject json)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try{
                   OutputStream out = mSocket.getOut();
                   out.write(json.toString().getBytes());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
