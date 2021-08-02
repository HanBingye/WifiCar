package com.bing.wificar;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.PrintWriter;

public class ActionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MY_TAG";
    private Button bt_change;
    private Button bt_connect;
    private Switch wifi_switch;
    private Button bt_up;
    private Button bt_down;
    private Button bt_left;
    private Button bt_right;
    private Button bt_left_move;
    private Button bt_right_move;
    private Button bt_stop;
    private Button bt_left_front;
    private Button bt_right_front;
    private Button bt_left_rotate;
    private Button bt_right_rotate;

    private Button bt_ll1_1;
    private Button bt_ll1_2;
    private Button bt_ll1_3;
    private Button bt_ll1_4;
    private Button bt_ll1_5;
    private Button bt_ll1_6;
    private Button bt_ll2_1;
    private Button bt_ll2_2;
    private Button bt_ll2_3;
    private Button bt_ll2_4;
    private Button bt_ll2_5;
    private Button bt_ll2_6;
    private Button bt_ll3_1;
    private Button bt_ll3_2;
    private Button bt_ll3_3;
    private Button bt_ll3_4;
    private Button bt_ll3_5;
    private Button bt_ll3_6;
    private Button bt_ll4_1;
    private Button bt_ll4_2;
    private Button bt_ll4_3;
    private Button bt_ll4_4;
    private Button bt_ll4_5;
    private Button bt_ll4_6;


    private TextView tv_status;
    private ConnectThread connectThread;
    private Toast mToast;
    private ProgressDialog pd;
    static PrintWriter pw;
    private WifiManager mWifi;
    private EditText et_ip;
    private EditText et_port;
    private String ip;
    private String port;
    private CheckBox cb_remember;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private long downTime = 0;//button被按下的时间
    private long thisTime = 0;//while每次循环的时间
    private boolean onBtTouch = false;//button是否被按下
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    pw = connectThread.getPw();
                    pd.dismiss();
                    tv_status.setText("已连接小车");
                    bt_connect.setBackgroundResource(R.drawable.connect);
                    showToast("小车连接成功");
                    break;
                case 0:
                    pd.dismiss();
                    tv_status.setText("未连接小车");
                    bt_connect.setBackgroundResource(R.drawable.discinnect);
                    showToast("小车连接失败");
                    break;
                default:
                    break;

            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (action) {
                case WifiManager.WIFI_STATE_ENABLED:

                    wifi_switch.setChecked(true);
                    showToast("wifi已打开");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:

                    wifi_switch.setChecked(false);
                    tv_status.setText("未连接小车");
                    bt_connect.setBackgroundResource(R.drawable.discinnect);
                    pw = null;
                    showToast("wifi未打开");
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_action);
        initView();

        /*
           限制输入ip时的格式
         */
        et_ip.setInputType(InputType.TYPE_CLASS_NUMBER);
        String digits = "0123456789.";
        et_ip.setKeyListener(DigitsKeyListener.getInstance(digits));

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("isRemember", false);
        if (isRemember) {
            String remember_ip = pref.getString("ip", "");
            String remember_port = pref.getString("port", "");
            et_ip.setText(remember_ip);
            et_port.setText(remember_port);
            cb_remember.setChecked(true);
        }

        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!wifi_switch.isPressed()) {
                    return;
                }

                if (isChecked) {
                    mWifi.setWifiEnabled(true);

                    if (!mWifi.isWifiEnabled()) {
                        wifi_switch.setChecked(false);
                    }
                } else {
                    mWifi.setWifiEnabled(false);
                }

            }
        });

        mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            wifi_switch.setChecked(true);
        } else {
            wifi_switch.setChecked(false);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);


    }

    private void initView() {
        bt_change = findViewById(R.id.change);
        bt_connect = findViewById(R.id.connect);
        et_ip = findViewById(R.id.ip);
        et_port = findViewById(R.id.port);
        cb_remember = findViewById(R.id.remember);
        wifi_switch = findViewById(R.id.wifi);
        tv_status = findViewById(R.id.status);
        bt_up = findViewById(R.id.Up);
        bt_down = findViewById(R.id.Down);
        bt_left = findViewById(R.id.Left);
        bt_right = findViewById(R.id.Right);
        bt_left_move = findViewById(R.id.bt_left_move);
        bt_right_move = findViewById(R.id.bt_right_move);
        bt_stop = findViewById(R.id.stop);


        bt_left_rotate = findViewById(R.id.bt_left_rotate);
        bt_right_rotate = findViewById(R.id.bt_right_rotate);
        bt_left_front = findViewById(R.id.bt_left_front);
        bt_right_front = findViewById(R.id.bt_right_front);

        bt_ll1_1 = findViewById(R.id.button_ll1_1);
        bt_ll1_2 = findViewById(R.id.button_ll1_2);
        bt_ll1_3 = findViewById(R.id.button_ll1_3);
        bt_ll1_4 = findViewById(R.id.button_ll1_4);
        bt_ll1_5 = findViewById(R.id.button_ll1_5);
        bt_ll1_6 = findViewById(R.id.button_ll1_6);

        bt_ll2_1 = findViewById(R.id.button_ll2_1);
        bt_ll2_2 = findViewById(R.id.button_ll2_2);
        bt_ll2_3 = findViewById(R.id.button_ll2_3);
        bt_ll2_4 = findViewById(R.id.button_ll2_4);
        bt_ll2_5 = findViewById(R.id.button_ll2_5);
        bt_ll2_6 = findViewById(R.id.button_ll2_6);

        bt_ll3_1 = findViewById(R.id.button_ll3_1);
        bt_ll3_2 = findViewById(R.id.button_ll3_2);
        bt_ll3_3 = findViewById(R.id.button_ll3_3);
        bt_ll3_4 = findViewById(R.id.button_ll3_4);
        bt_ll3_5 = findViewById(R.id.button_ll3_5);
        bt_ll3_6 = findViewById(R.id.button_ll3_6);

        bt_ll4_1 = findViewById(R.id.button_ll4_1);
        bt_ll4_2 = findViewById(R.id.button_ll4_2);
        bt_ll4_3 = findViewById(R.id.button_ll4_3);
        bt_ll4_4 = findViewById(R.id.button_ll4_4);
        bt_ll4_5 = findViewById(R.id.button_ll4_5);
        bt_ll4_6 = findViewById(R.id.button_ll4_6);

        bt_change.setOnClickListener(this);
        bt_connect.setOnClickListener(this);
        bt_up.setOnClickListener(this);
        bt_down.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_left_move.setOnClickListener(this);
        bt_right_move.setOnClickListener(this);

        bt_left_rotate.setOnClickListener(this);
        bt_right_rotate.setOnClickListener(this);
        bt_left_front.setOnClickListener(this);
        bt_right_front.setOnClickListener(this);

        bt_ll1_1.setOnClickListener(this);
        bt_ll1_2.setOnClickListener(this);
        bt_ll1_3.setOnClickListener(this);
        bt_ll1_4.setOnClickListener(this);
        bt_ll1_5.setOnClickListener(this);
        bt_ll1_6.setOnClickListener(this);

        bt_ll2_1.setOnClickListener(this);
        bt_ll2_2.setOnClickListener(this);
        bt_ll2_3.setOnClickListener(this);
        bt_ll2_4.setOnClickListener(this);
        bt_ll2_5.setOnClickListener(this);
        bt_ll2_6.setOnClickListener(this);

        bt_ll3_1.setOnClickListener(this);
        bt_ll3_2.setOnClickListener(this);
        bt_ll3_3.setOnClickListener(this);
        bt_ll3_4.setOnClickListener(this);
        bt_ll3_5.setOnClickListener(this);
        bt_ll3_6.setOnClickListener(this);

        bt_ll4_1.setOnClickListener(this);
        bt_ll4_2.setOnClickListener(this);
        bt_ll4_3.setOnClickListener(this);
        bt_ll4_4.setOnClickListener(this);
        bt_ll4_5.setOnClickListener(this);
        bt_ll4_6.setOnClickListener(this);


    }
//    //沉浸式模式
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    private void initProgressDialog() {
        pd = new ProgressDialog(ActionActivity.this);
        pd.setTitle("WifiCar");
        pd.setMessage("正在连接小车，请等待...");
        pd.setCancelable(false);// 设置对话框能否用back键取消
        pd.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                if (!mWifi.isWifiEnabled()) {
                    showToast("请先打开wifi");

                } else {
                    if (pw == null) {

                        ip = et_ip.getText().toString().trim();
                        port = et_port.getText().toString().trim();
                        if ("".equals(ip) || "".equals(port) || ip == null || port == null) {
                            showToast("请输入IP和端口号");

                        } else {
                            edit = pref.edit();
                            if (cb_remember.isChecked()) {

                                edit.putBoolean("isRemember", true);
                                edit.putString("ip", ip);
                                edit.putString("port", port);

                            } else {
                                edit.clear();
                            }
                            edit.apply();
                            initProgressDialog();
                            connectThread = new ConnectThread(ip, Integer.parseInt(port), handler);
                            connectThread.start();
                        }
                    } else {
                        showToast("已连接");
                    }
                }
                break;
            case R.id.Up:
                if (pw != null) {
                    new SendThread('1').start();

                } else {
                    showToast("请先连接小车");
                }

                break;
            case R.id.Down:
                if (pw != null) {
                    new SendThread('2').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.Left:
                if (pw != null) {
                    new SendThread('3').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.Right:
                if (pw != null) {
                    new SendThread('4').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_move:
                if (pw != null) {
                    new SendThread('5').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_move:
                if (pw != null) {
                    new SendThread('6').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_front:
                if (pw != null) {
                    new SendThread('7').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_front:
                if (pw != null) {
                    new SendThread('8').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_rotate:
                if (pw != null) {
                    new SendThread('0').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_rotate:
                if (pw != null) {
                    new SendThread('9').start();
                } else {
                    showToast("请先连接小车");
                }
                break;


            case R.id.stop:
                if (pw != null) {
                    new SendThread('S').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_4:
                if (pw != null) {
                    new SendThread('A').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_5:
                if (pw != null) {
                    new SendThread('B').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_6:
                if (pw != null) {
                    new SendThread('C').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_4:
                if (pw != null) {
                    new SendThread('a').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_5:
                if (pw != null) {
                    new SendThread('b').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_6:
                if (pw != null) {
                    new SendThread('c').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_1:
                if (pw != null) {
                    new SendThread('D').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_2:
                if (pw != null) {
                    new SendThread('E').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll1_3:
                if (pw != null) {
                    new SendThread('F').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_1:
                if (pw != null) {
                    new SendThread('d').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_2:
                if (pw != null) {
                    new SendThread('e').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll2_3:
                if (pw != null) {
                    new SendThread('f').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_1:
                if (pw != null) {
                    new SendThread('G').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_2:
                if (pw != null) {
                    new SendThread('H').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_3:
                if (pw != null) {
                    new SendThread('I').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_1:
                if (pw != null) {
                    new SendThread('g').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_2:
                if (pw != null) {
                    new SendThread('h').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_3:
                if (pw != null) {
                    new SendThread('i').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_4:
                if (pw != null) {
                    new SendThread('K').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_5:
                if (pw != null) {
                    new SendThread('L').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll3_6:
                if (pw != null) {
                    new SendThread('M').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_4:
                if (pw != null) {
                    new SendThread('k').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_5:
                if (pw != null) {
                    new SendThread('l').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.button_ll4_6:
                if (pw != null) {
                    new SendThread('m').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.change:
                startActivity(new Intent(ActionActivity.this, DuojiActivity.class));
                finish();
                break;


        }

    }


    private void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);

        mToast.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        if (pw != null) {
            pw.close();
            pw = null;

        }
        Log.d(TAG, "onDestroy");
    }


}


