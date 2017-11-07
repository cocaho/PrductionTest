package com.example.hau.prductiontest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;


import util.KeyboardAnimation;
import util.LeanUtil;
import wiget.KeyboardWatcher;
import wiget.KeyboardWatcher.SoftKeyboardStateListener;


/**
 * Created by Hau on 2017/10/9.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, SoftKeyboardStateListener{

    private KeyboardWatcher keyboardWatcher;

    private TextView logo;
    private EditText et_mobile,et_password,et_message,et_person;
    private Button register,getNumber;
    private ImageView close;

    private int screenHeight = 0;//屏幕高度
    private float scale = 0.8f; //logo缩放比例
    private View  body;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initListener();

        keyboardWatcher = new KeyboardWatcher(findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);

    }

    private void initView() {
        body = findViewById(R.id.register_body);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        logo = (TextView) findViewById(R.id.register_logo);
        et_mobile = (EditText) findViewById(R.id.register_et_mobile);
        et_password = (EditText) findViewById(R.id.register_et_password);
        et_message = (EditText) findViewById(R.id.register_et_message);
        et_person = (EditText) findViewById(R.id.register_et_person);
        register = (Button) findViewById(R.id.btn_register);
        getNumber = (Button) findViewById(R.id.register_getNumber);
        close = (ImageView) findViewById(R.id.register_close);
    }

    private void initListener() {

        close.setOnClickListener(this);

        register.setOnClickListener(this);

        getNumber.setOnClickListener(this);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardSize) {
        Log.e("Hau", "----->show" + keyboardSize);
        int[] location = new int[2];
        body.getLocationOnScreen(location); //获取body在屏幕中的坐标,控件左上角
        int x = location[0];
        int y = location[1];
        Log.e("Hau","y = "+y+",x="+x);
        int bottom = screenHeight - (y+body.getHeight()) ;
        Log.e("Hau","bottom = "+bottom);
        Log.e("Hau","con-h = "+body.getHeight());
        if (keyboardSize > bottom){
            ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(body, "translationY", 0.0f, -(keyboardSize - bottom));
            mAnimatorTranslateY.setDuration(300);
            mAnimatorTranslateY.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimatorTranslateY.start();
            KeyboardAnimation.zoomIn(logo, keyboardSize - bottom);

        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        Log.e("Hau", "----->hide");
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(body, "translationY", body.getTranslationY(), 0);
        mAnimatorTranslateY.setDuration(300);
        mAnimatorTranslateY.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimatorTranslateY.start();
        KeyboardAnimation.zoomOut(logo);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        keyboardWatcher.removeSoftKeyboardStateListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String userName = null;
        String phoneNumber = null;
        String passWard = null;

        switch (id){
            case R.id.register_close:

                finish();

                break;
            case  R.id.btn_register:

                userName = et_person.getText().toString();
                phoneNumber = et_mobile.getText().toString();
                passWard = et_password.getText().toString();
                String message = et_message.getText().toString();

                if (phoneNumber.length() != 11 || passWard.length() != 8 || message.length() == 0||userName.length()==0) {
                    Toast.makeText(getApplicationContext(), "信息错误，无效注册", Toast.LENGTH_SHORT).show();
                }else{

                    final String finalUserName = userName;
                    final String finalPassWard = passWard;
                    final String finalPhoneNumber = phoneNumber;

                    LeanUtil.signUpOrLoginByMobilePhoneInBackground(getApplicationContext(),phoneNumber,message,finalUserName,finalPassWard,finalPhoneNumber);

                }
                break;
            case R.id.register_getNumber:

                 phoneNumber = et_mobile.getText().toString();
                 passWard = et_password.getText().toString();

                if (phoneNumber.length() != 11) {
                    Toast.makeText(getApplicationContext(), "请输入11位有效手机号码", Toast.LENGTH_SHORT).show();
                } else if (passWard.length() != 8) {
                    Toast.makeText(getApplicationContext(), "请输入8位有效密码", Toast.LENGTH_SHORT).show();
                } else {
                    LeanUtil.cloudRequestLoginSmsCodeInBackground(getApplicationContext(),getNumber,phoneNumber);

                }
                break;
        }
    }
}
