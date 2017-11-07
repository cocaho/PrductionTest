package com.example.hau.prductiontest;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.avos.avoscloud.LogInCallback;

import util.KeyboardAnimation;
import util.LeanUtil;
import wiget.KeyboardWatcher;

/**
 * Created by Hau on 2017/9/24.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener{

    private TextView logo;
    private EditText et_mobile;
    private EditText et_password;
    private ImageView clean_phone;
    private ImageView clean_password;
    private ImageView close;
    private ImageView show_pwd;
    private Button btn_login;
    private TextView forget_pwd,register;

    private int screenHeight = 0;//屏幕高度
    private View service, body;
    private KeyboardWatcher keyboardWatcher;

    private View root;

    private boolean flag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

        keyboardWatcher = new KeyboardWatcher(findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);
    }

    private void initListener() {
        clean_phone.setOnClickListener(this);
        clean_password.setOnClickListener(this);
        show_pwd.setOnClickListener(this);
        close.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        register.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);

        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && clean_phone.getVisibility() == View.GONE) {
                    clean_phone.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    clean_phone.setVisibility(View.GONE);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && clean_password.getVisibility() == View.GONE) {
                    clean_password.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    clean_password.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    Toast.makeText(LoginActivity.this, "请输入字母或数字", Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());
                    et_password.setSelection(s.length());
                }
            }
        });






    }

    private void initView() {
        logo = (TextView) findViewById(R.id.login_logo);
        et_mobile = (EditText) findViewById(R.id.login_et_mobile);
        et_password = (EditText) findViewById(R.id.login_et_password);
        clean_phone = (ImageView) findViewById(R.id.login_clean_phone);
        clean_password = (ImageView) findViewById(R.id.login_clean_password);
        show_pwd = (ImageView) findViewById(R.id.login_show_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        register = (TextView) findViewById(R.id.login_regist);
        forget_pwd = (TextView) findViewById(R.id.login_forget_password);
        service = findViewById(R.id.login_service);
        body = findViewById(R.id.login_body);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        root = findViewById(R.id.login_root);
        close = (ImageView) findViewById(R.id.login_close);
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

        switch (id) {
            case R.id.login_clean_phone:
                et_mobile.setText("");
                break;
            case R.id.login_clean_password:
                et_password.setText("");
                break;
            case R.id.login_close:
                finish();
                break;
            case R.id.login_show_pwd:
                if(flag == true){
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_pwd.setImageResource(R.mipmap.pass_gone);
                    flag = false;
                }else{
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_pwd.setImageResource(R.mipmap.pass_visuable);
                    flag = true;
                }
                String pwd = et_password.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    et_password.setSelection(pwd.length());
                break;
            case  R.id.btn_login:
                phoneNumber = et_mobile.getText().toString();
                passWard = et_password.getText().toString();

                if(phoneNumber==null||passWard==null)
                {
                    Toast.makeText(LoginActivity.this,"无效登录",Toast.LENGTH_SHORT).show();
                }else {

                    LeanUtil.loginByMobilePhoneNumberInBackground(getApplicationContext(),phoneNumber,passWard);
                }
                break;
            case R.id.login_regist:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case  R.id.login_forget_password:
                Intent intent1 = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
