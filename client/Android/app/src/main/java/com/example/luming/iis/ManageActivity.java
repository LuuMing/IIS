package com.example.luming.iis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ManageActivity extends AppCompatActivity {
    private JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket socket = mSocket.getInstance();
        setContentView(R.layout.activity_manage);
        Intent intent = getIntent();
        try {
            jsonObject = new JSONObject(intent.getStringExtra("json"));
            Toast.makeText(ManageActivity.this, jsonObject.toString() , Toast.LENGTH_SHORT).show();
            Toast.makeText(ManageActivity.this, socket.toString() , Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
