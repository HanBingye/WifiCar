package com.bing.wificar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.MotionEvent;
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

import java.io.PrintWriter;


public class DuojiActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "MY_TAG";
    private Button bt_change2;
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
    private Button bt_add1;
    private Button bt_add2;
    private Button bt_add3;
    private Button bt_add4;
    private Button bt_add5;
    private Button bt_reduce1;
    private Button bt_reduce2;
    private Button bt_reduce3;
    private Button bt_reduce4;
    private Button bt_reduce5;


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
    final  Handler handler = new Handler() {
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

        setContentView(R.layout.activity_duoji);
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
        bt_change2 = findViewById(R.id.change2);
        bt_connect = findViewById(R.id.connect2);
        et_ip = findViewById(R.id.ip2);
        et_port = findViewById(R.id.port2);
        cb_remember = findViewById(R.id.remember2);
        wifi_switch = findViewById(R.id.wifi2);
        tv_status = findViewById(R.id.status2);
        bt_up = findViewById(R.id.Up2);
        bt_down = findViewById(R.id.Down2);
        bt_left = findViewById(R.id.Left2);
        bt_right = findViewById(R.id.Right2);
        bt_left_move = findViewById(R.id.bt_left_move2);
        bt_right_move = findViewById(R.id.bt_right_move2);
        bt_stop = findViewById(R.id.stop2);


        bt_add1 = findViewById(R.id.add1);
        bt_add2 = findViewById(R.id.add2);
        bt_add3 = findViewById(R.id.add3);
        bt_add4 = findViewById(R.id.add4);
        bt_add5 = findViewById(R.id.add5);
        bt_reduce1 = findViewById(R.id.reduce1);
        bt_reduce2 = findViewById(R.id.reduce2);
        bt_reduce3 = findViewById(R.id.reduce3);
        bt_reduce4 = findViewById(R.id.reduce4);
        bt_reduce5 = findViewById(R.id.reduce5);
        bt_left_rotate = findViewById(R.id.bt_left_rotate2);
        bt_right_rotate = findViewById(R.id.bt_right_rotate2);
        bt_left_front = findViewById(R.id.bt_left_front2);
        bt_right_front = findViewById(R.id.bt_right_front2);

        bt_change2.setOnClickListener(this);
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


        bt_add1.setOnTouchListener(this);
        bt_add2.setOnTouchListener(this);
        bt_add3.setOnTouchListener(this);
        bt_add4.setOnTouchListener(this);
        bt_add5.setOnTouchListener(this);
        bt_reduce1.setOnTouchListener(this);
        bt_reduce2.setOnTouchListener(this);
        bt_reduce3.setOnTouchListener(this);
        bt_reduce4.setOnTouchListener(this);
        bt_reduce5.setOnTouchListener(this);
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
        pd = new ProgressDialog(DuojiActivity.this);
        pd.setTitle("WifiCar");
        pd.setMessage("正在连接小车，请等待...");
        pd.setCancelable(false);// 设置对话框能否用back键取消
        pd.show();
    }


    //触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.add1:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('V').start();
                                        Log.d(TAG, "舵机1+ 长按");
                                    }


                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('V').start();
                            Log.d(TAG, "舵机1+ 单击");
                        } else {

                            showToast("请先连接小车");
                        }

                    }

                }

                break;
            case R.id.reduce1:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('v').start();
                                        Log.d(TAG, "舵机1- 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('v').start();
                            Log.d(TAG, "舵机1- 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }
                break;
            case R.id.add2:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('W').start();
                                        Log.d(TAG, "舵机2+ 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('W').start();
                            Log.d(TAG, "舵机2+ 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }

                break;
            case R.id.reduce2:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('w').start();
                                        Log.d(TAG, "舵机2- 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('w').start();
                            Log.d(TAG, "舵机2- 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }
                break;
            case R.id.add3:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('X').start();
                                        Log.d(TAG, "舵机3+ 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('X').start();
                            Log.d(TAG, "舵机3+ 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }

                break;
            case R.id.reduce3:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('x').start();
                                        Log.d(TAG, "舵机3- 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('x').start();
                            Log.d(TAG, "舵机3- 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }
                break;
            case R.id.add4:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('Y').start();
                                        Log.d(TAG, "舵机4+ 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('Y').start();
                            Log.d(TAG, "舵机4+ 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }

                break;
            case R.id.reduce4:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('y').start();
                                        Log.d(TAG, "舵机4- 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('y').start();
                            Log.d(TAG, "舵机4- 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }
                break;
            case R.id.add5:

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('Z').start();
                                        Log.d(TAG, "舵机5+ 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('Z').start();
                            Log.d(TAG, "舵机5+ 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }

                break;
            case R.id.reduce5:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    onBtTouch = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (onBtTouch) {
                                thisTime = System.currentTimeMillis();
                                if (thisTime - downTime >= 500) {
                                    if (pw != null) {
                                        new SendThread('z').start();
                                        Log.d(TAG, "舵机5- 长按");
                                    }

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }).start();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    onBtTouch = false;
                    if (thisTime - downTime < 500) {
                        if (pw != null) {
                            new SendThread('z').start();
                            Log.d(TAG, "舵机5- 单击");
                        } else {
                            showToast("请先连接小车");
                        }

                    }

                }
                break;
        }


        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect2:
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
            case R.id.Up2:
                if (pw != null) {
                    new SendThread('1').start();

                } else {
                    showToast("请先连接小车");
                }

                break;
            case R.id.Down2:
                if (pw != null) {
                    new SendThread('2').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.Left2:
                if (pw != null) {
                    new SendThread('3').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.Right2:
                if (pw != null) {
                    new SendThread('4').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_move2:
                if (pw != null) {
                    new SendThread('5').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_move2:
                if (pw != null) {
                    new SendThread('6').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_front2:
                if (pw != null) {
                    new SendThread('7').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_front2:
                if (pw != null) {
                    new SendThread('8').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_left_rotate2:
                if (pw != null) {
                    new SendThread('0').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.bt_right_rotate2:
                if (pw != null) {
                    new SendThread('9').start();
                } else {
                    showToast("请先连接小车");
                }
                break;


            case R.id.stop2:
                if (pw != null) {
                    new SendThread('S').start();
                } else {
                    showToast("请先连接小车");
                }
                break;
            case R.id.change2:
                startActivity(new Intent(DuojiActivity.this, ActionActivity.class));
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