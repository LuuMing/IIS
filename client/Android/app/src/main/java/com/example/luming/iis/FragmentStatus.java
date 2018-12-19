package com.example.luming.iis;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
    private String send_cmd = "NULL";      //暂时没用
    private String rec_value;
    private DatabaseOperator databaseOperator;
    private LineChart lineChart;
    private String numData = "50";
    private EditText editTextDataNum;
    private EditText editTextDelayTime;
    private Integer delayTime = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lineChart = (LineChart) getActivity().findViewById(R.id.lineChart);
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
                        String tmpDealyTime = editTextDelayTime.getText().toString();
                        if(tmpDealyTime.equals(""))
                            delayTime = 1;
                        else
                            delayTime = Integer.parseInt(tmpDealyTime);
                        databaseOperator.addLog(module_name,send_cmd,rec_value);
                        drawlineChart();
                        removeMessages(1);
                        if(!isDestroy)
                            sendEmptyMessageDelayed(0, delayTime * 1000);
                        break;
                }

            }
        };

        editTextDataNum = getActivity().findViewById(R.id.numData);
        editTextDelayTime = getActivity().findViewById(R.id.delayTime);

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
                isDestroy = false;
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
    private void drawlineChart()
    {
        numData = editTextDataNum.getText().toString().trim();
        if(numData.equals(""))
            numData = "10";
        List<Pair<String,Float>> list = databaseOperator.queryLog(module_name,numData);
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < list.size(); ++i)
        {
            entries.add(new Entry(i,list.get(i).second));
        }
        LineDataSet lineDataSet = new LineDataSet(entries,"data");
        lineDataSet.setColor(Color.RED);
        //设置每个点的颜色
        lineDataSet.setCircleColor(Color.YELLOW);
        //设置该线的宽度
        lineDataSet.setLineWidth(1f);
        //设置每个坐标点的圆大小
        //lineDataSet.setCircleRadius(1f);
        //设置是否画圆
        lineDataSet.setDrawCircles(false);
        // 设置平滑曲线模式
        //  lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //设置线一面部分是否填充颜色
        lineDataSet.setDrawFilled(true);
        //设置填充的颜色
        lineDataSet.setFillColor(Color.BLUE);
        //设置是否显示点的坐标值
        lineDataSet.setDrawValues(false);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}


