package com.example.luming.iis.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.luming.iis.R;
import com.example.luming.iis.database.DatabaseOperator;

/**
 * Created by TukulHX on 2019/4/4
 *
 */
public class AddDeviceDialog extends DialogFragment implements View.OnClickListener {


    private Button bt_left, bt_right;
    private EditText et_name, et_ip, et_port;
    private OnAddDeviceListener listener;
    private static DatabaseOperator dbOperator;
    public static final String TAG = "AddDeviceDialog";

    public static AddDeviceDialog newInstance(Context context) {
        AddDeviceDialog dialog = new AddDeviceDialog();
        dbOperator = DatabaseOperator.getInstance(context);
//        Bundle args = new Bundle();
//        args.putString(HEAD, header);
//        args.putString(HEAD_DESC, headerDesc);
//        args.putString(LEFT_BTN, leftBtn);
//        args.putString(RIGHT_BTN, rightBtn);
//        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Dialog);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_device, null);
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;

        et_name = view.findViewById(R.id.et_name);
        et_ip = view.findViewById(R.id.et_ip);
        et_port = view.findViewById(R.id.et_port);
        bt_left = view.findViewById(R.id.bt_left);
        bt_right = view.findViewById(R.id.bt_right);

        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        //设置宽度只占屏幕宽度的80%
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    @Override
    public void dismiss() {
        if (null != getActivity() && !getActivity().isFinishing()) {
            super.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_left) {
            dismiss();
        }
        if (v.getId() == R.id.bt_right) {
            if (listener != null) {
                //获取输入内容
                String name = et_name.getText().toString().trim();
                String ip = et_ip.getText().toString().trim();
                String port = et_port.getText().toString().trim();
                if (name.equals("") || ip.equals("") || port.equals(""))
                    return;
                listener.getDevice(name, ip, port);
            }
            dismiss();
        }
    }

    public AddDeviceDialog setOnAddDeviceListener(OnAddDeviceListener listener) {
        this.listener = listener;
        return this;
    }

    public void showDialog(FragmentActivity activity) {
        try {
            show(activity.getSupportFragmentManager(), TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnAddDeviceListener {
        void getDevice(String name, String ip, String port);

//        void onRightButtonClick(View v);
    }


}
