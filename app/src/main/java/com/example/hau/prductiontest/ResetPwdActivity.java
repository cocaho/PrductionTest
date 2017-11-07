package com.example.hau.prductiontest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

import util.KeyboardAnimation;
import util.LeanUtil;
import wiget.KeyboardWatcher;

/**
 * Created by Hau on 2017/10/17.
 */

public class ResetPwdActivity extends AppCompatActivity implements View.OnClickListener,KeyboardWatcher.SoftKeyboardStateListener {
    private KeyboardWatcher keyboardWatcher;

    private TextView logo;
    private EditText et_mobile,et_password,et_message;
    private Button confirm,getNumber;
    private ImageView close;


    private int screenHeight = 0;//屏幕高度
    private float scale = 0.8f; //logo缩放比例
    private View  body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_passward);

        initView();
        initListener();

        keyboardWatcher = new KeyboardWatcher(findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);
    }

    private void initListener() {

        close.setOnClickListener(this);


         getNumber.setOnClickListener(this);


        confirm.setOnClickListener(this);
    }

    private void initView() {
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        close = (ImageView) findViewById(R.id.rp_close);
        body = findViewById(R.id.rp_body);
        logo = (TextView) findViewById(R.id.rp_logo);
        et_mobile = (EditText) findViewById(R.id.rp_et_mobile);
        et_password = (EditText) findViewById(R.id.rp_et_password);
        et_message = (EditText) findViewById(R.id.rp_et_message);
        confirm = (Button) findViewById(R.id.btn_confirm);
        getNumber = (Button) findViewById(R.id.rp_getNumber);


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
        String phoneNumber = null;
        String passWard = null;
        String message = null;

        switch (id){
            case R.id.rp_close:
                finish();
                break;
            case R.id.rp_getNumber:
                phoneNumber = et_mobile.getText().toString();
                passWard = et_password.getText().toString();

                if (phoneNumber.length() != 11) {
                    Toast.makeText(getApplicationContext(), "请输入11位有效手机号码", Toast.LENGTH_SHORT).show();
                } else if (passWard.length() != 8) {
                    Toast.makeText(getApplicationContext(), "请输入8位有效密码", Toast.LENGTH_SHORT).show();
                } else {

                    LeanUtil.userRequestLoginSmsCodeInBackground(getApplicationContext(),getNumber,phoneNumber);

                }
                break;
            case R.id.btn_confirm:
                phoneNumber = et_mobile.getText().toString();
                passWard = et_password.getText().toString();
                message = et_message.getText().toString();
                if (phoneNumber.length() != 11 || passWard.length() != 8 || message.length() == 0) {
                    Toast.makeText(getApplicationContext(), "信息错误，无效重置", Toast.LENGTH_SHORT).show();
                }else{

                    LeanUtil.resetPasswordBySmsCodeInBackground(getApplicationContext(),message,passWard);

                }
                break;
        }
    }
}
