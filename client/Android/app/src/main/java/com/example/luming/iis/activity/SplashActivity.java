package com.example.luming.iis.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luming.iis.R;
import com.example.luming.iis.utils.SharedPreferenceUtils;
import com.example.luming.iis.utils.TipDialogUtils;
import com.example.luming.iis.utils.WebService;
import com.example.luming.iis.widgets.FullScreenVideoView;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.luming.iis.activity.RegisterActivity.USER_PASSWORD;
import static com.qmuiteam.qmui.widget.dialog.QMUITipDialog.Builder.ICON_TYPE_FAIL;

public class SplashActivity extends FragmentActivity implements View.OnClickListener {

    public static final String TAG = "SplashActivity";
    private FullScreenVideoView videoView;
    private EditText et_password;
    private EditText et_name;
    private LinearLayout root;
    private Button bt_login;
    private TextView tv_register, tv_tourist;

    private static final int LOGIN_FAILED = 0;
    private static final int LOGIN_SUCCESS = 1;

    private static final int REGISTER_FAILED = 2;
    private static final int REGISTER_SUCCESS = 3;
    private static final int NET_ERROR = 4;


    public static final String CONFIG = "config";
    public static final String USER_ID = "user_id";
    public static final String JSON = "json";
    public static final String LOGIN_NULL = "NULL";
    public static final String LOGIN_INFO = "LoginInfo";
    public static final String IS_LOGIN = "isLogin";


    private boolean isLogin = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    isLogin = true;
                    // TODO 1、登录成功后检测网络状态，有网络则从服务器获取设备信息并加载设备界面
                    // TODO 2、无网络则直接加载本地数据即可
                    //保存登录信息
                    String loginInfo = msg.obj.toString();
                    System.out.println("登录信息:" + loginInfo);
                    SharedPreferenceUtils.saveString(getApplicationContext(), LOGIN_INFO, loginInfo);
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), IS_LOGIN, isLogin);
//                    TipDialogUtils.getInstance(SplashActivity.this, ICON_TYPE_SUCCESS, "登录成功", handler);
                    Intent intent = new Intent(SplashActivity.this, DeviceActivity.class);
                    intent.putExtra(LOGIN_INFO, loginInfo);
                    intent.putExtra(IS_LOGIN, isLogin);
                    startActivity(intent);
                    finish();
                    break;

                case LOGIN_FAILED:
                    isLogin = false;
                    SharedPreferenceUtils.saveBoolean(getApplicationContext(), IS_LOGIN, isLogin);
                    Toast.makeText(SplashActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

                case NET_ERROR:
                    System.out.println("msg.obj：" + msg.obj.toString());
                    Toast.makeText(SplashActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_FAILED:
                    TipDialogUtils.getInstance(SplashActivity.this, ICON_TYPE_FAIL, "注册失败", handler);
                    break;
            }
        }
    };


    public static void ToSplashActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        isLogin = SharedPreferenceUtils.getBoolean(getApplicationContext(), IS_LOGIN, false);
        System.out.println("登录标记：" + isLogin);
        if (isLogin) {
            DeviceActivity.ToDeviceActivity(this);
        }
        initView();
        playBackgroundVideo();
    }


    private void initView() {
        root = findViewById(R.id.splash_root);
        videoView = findViewById(R.id.videoview);
        et_name = findViewById(R.id.et_name);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        tv_register = findViewById(R.id.tv_register);
        tv_tourist = findViewById(R.id.tv_tourist);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra(USER_ID);
        String user_password = intent.getStringExtra(USER_PASSWORD);
        et_name.setText(user_id);
        et_password.setText(user_password);

        bt_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_tourist.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                //do login
                String name = et_name.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (name.equals("") && password.equals("")) {
                    Toast.makeText(this, "登录信息不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(name, password);
                break;

            case R.id.tv_register:
                //do register TODO 这里无法使用TextInputLayout，将在后续进行注册界面重制
                RegisterActivity.ToRegisterActivity(this);
//                LoginDialog.newInstance("注册新用户").setOnLoginListener(new LoginDialog.OnLoginListener() {
//                    @Override
//                    public void getLoginInfo(String name, String password) {
//                        //do Register TODO 后续添加跳转逻辑
//                        register(name, password);
//                    }
//                }).showDialog(this);
                break;
            case R.id.tv_tourist:
                //do tourist
                DeviceActivity.ToDeviceActivity(SplashActivity.this);
                break;
        }
    }

    /**
     * 登录
     */
    private void login(final String name, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String info = WebService.httpLogin(name, password);
                System.out.println("点击了登录:" + info);
                if (info.equals("false")) {
                    message.what = NET_ERROR;
                    message.obj = "请检查网络";
                    handler.sendMessage(message);
                } else if (info.equals(LOGIN_NULL)) {
                    message.what = LOGIN_FAILED;
                    message.obj = "登陆失败,请检查账号密码";
                    handler.sendMessage(message);
                } else {
                    message.what = LOGIN_SUCCESS;
                    //TODO 这里的info为user表的id值，为自增字段，保存在DeviceActivity中
                    message.obj = info;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

//    /**
//     * 注册
//     */
//    private void register(final String name, final String password) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                String info = WebService.executeHttpGet("RegLet", name, password);
//                if (info.equals(SP_NULL)) {
//                    message.what = REGISTER_FAILED;
//                    message.obj = "注册失败";
//                } else {
//                    message.what = REGISTER_SUCCESS;
//                    message.obj = "注册成功";
//                }
//                handler.sendMessage(message);
//            }
//        }).start();
//    }

    /**
     * 播放背景视频
     */
    private void playBackgroundVideo() {
        //1、设置播放路径
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login));
        //2、播放前准备
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //静音后开始播放
                mp.setVolume(0f, 0f);
                videoView.start();
                root.setVisibility(View.VISIBLE);
                startAnimation();
            }
        });
        //3、循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    /**
     * 动画效果,由下向上
     */
    private void startAnimation() {
        int startX = 0;
        float startY = 0f;
        System.out.println("startX = " + startX + "   startY = " + startY);
        TranslateAnimation animation = new TranslateAnimation(startX, startX, startY + 300f, startY);
        animation.setDuration(1000);
        root.setAnimation(animation);
        animation.startNow();
//        ObjectAnimator animatorY = new ObjectAnimator().ofFloat(root, "translationY", 300f, startY);
//        animatorY.setDuration(1500);
//        animatorY.start();

    }

    @Override
    protected void onStop() {
        videoView.stopPlayback();
        root.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        playBackgroundVideo();
        startAnimation();
        super.onRestart();
    }

    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    private void exit() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //2000ms内按第二次则退出
            finish();
            System.exit(0);
        }
    }
}
