package com.example.luming.iis;
import android.app.*;
import android.content.DialogInterface;
import android.os.*;
import android.view.*;
import android.widget.*;
public class MainActivity extends Activity
{
    private TableLayout tableLayout;
    private Button btnAdd;
    private int num = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.content_main);

        tableLayout = (TableLayout) this.findViewById(R.id.tableLayout);
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
                        String a = name.getText().toString().trim();
                        String b = ip.getText().toString().trim();
                        String c = port.getText().toString().trim();
                        // 打印出来
                        Toast.makeText(MainActivity.this, "名称: " + a + ", IP地址: " + b + "端口"+c, Toast.LENGTH_SHORT).show();

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

    private void addRow()
    {
        TableRow tableRow = new TableRow(this);
        TextView textView = new TextView(this);
        Button button = new Button(this);

        textView.setText(String.valueOf(num));
        button.setText("删除");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TableRow tableRow = (TableRow) view.getParent();
                tableLayout.removeView(tableRow);
            }
        });
        tableRow.addView(textView);
        tableRow.addView(button);

        tableLayout.addView(tableRow);
    }
}