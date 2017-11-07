package com.example.hau.prductiontest;

import android.animation.ObjectAnimator;

import android.os.Bundle;
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

import util.KeyboardAnimation;
import util.LeanUtil;
import wiget.KeyboardWatcher;


/**
 * Created by Hau on 2017/10/10.
 */

public class ForgetActivity extends AppCompatActivity implements KeyboardWatcher.SoftKeyboardStateListener, View.OnClickListener{

    private KeyboardWatcher keyboardWatcher;

    private TextView logo;
    private EditText et_mobile,et_message;
    private Button get_number,register;
    private ImageView close;

    private int screenHeight = 0;//屏幕高度
    private View body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initView();
        initListener();

        keyboardWatcher = new KeyboardWatcher(findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);

    }

    private void initView() {
        body = findViewById(R.id.forget_body);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        logo = (TextView) findViewById(R.id.forget_logo);
        et_mobile = (EditText) findViewById(R.id.forget_et_mobile);
        et_message = (EditText) findViewById(R.id.forget_et_message);
        get_number = (Button) findViewById(R.id.forget_getNumber);
        register = (Button) findViewById(R.id.forget_btn_register);
        close = (ImageView) findViewById(R.id.forget_close);
    }

    private void initListener(){

        close.setOnClickListener(this);

        get_number.setOnClickListener(this);

        register.setOnClickListener(this);
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
        String message = null;

        switch (id){
            case R.id.forget_close:
                finish();
                break;
            case R.id.forget_btn_register:
                phoneNumber = et_mobile.getText().toString();
                message = et_message.getText().toString();
                if (phoneNumber.length()!=11){
                    Toast.makeText(getApplicationContext(), "请输入11位有效手机号码", Toast.LENGTH_SHORT).show();
                }else if (message.length()==0){
                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                }else{
                    LeanUtil.signUpOrLoginByMobilePhoneInBackground(getApplicationContext(),phoneNumber,message);
                }
                break;
            case R.id.forget_getNumber:
                phoneNumber = et_mobile.getText().toString();

                if (phoneNumber.length()!=11){
                    Toast.makeText(getApplicationContext(), "请输入11位有效手机号码", Toast.LENGTH_SHORT).show();
                }else {

                        LeanUtil.userRequestLoginSmsCodeInBackground(getApplicationContext(),get_number,phoneNumber);

                }
                break;
        }
    }
}
