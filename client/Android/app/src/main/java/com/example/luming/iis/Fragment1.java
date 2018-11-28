package com.example.luming.iis;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Fragment1 extends Fragment {
private ListView listView;
private List<String> list = new ArrayList<>();
private ArrayAdapter<String> adapter;
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

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject((String)getActivity().getIntent().getExtras().get("json"));
            Iterator<?> it = jsonObject.keys();
            String key = "";
            while(it.hasNext()){//遍历JSONObject
                key = (String) it.next().toString();
                if(null!=key && !"".equals(key)){
                    JSONObject setting = new JSONObject(jsonObject.getString(key));
                    if(setting.getString("type").equals("action"))
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
    }
}
