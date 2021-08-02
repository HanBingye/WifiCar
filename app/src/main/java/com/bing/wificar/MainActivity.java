package com.bing.wificar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt_action)
    Button btAction;
    @BindView(R.id.bt_duoji)
    Button btDuoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.bt_action, R.id.bt_duoji})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_action:
                startActivity(new Intent(MainActivity.this,ActionActivity.class));
                finish();
                break;
            case R.id.bt_duoji:
                startActivity(new Intent(MainActivity.this,DuojiActivity.class));
                finish();
                break;
        }
    }
}