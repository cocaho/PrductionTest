package com.example.hau.prductiontest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;

import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.HashMap;
import java.util.List;


import util.LeanUtil;
import wiget.SelectPicPopupWindow;

/**
 * Created by Hau on 2017/10/14.
 */

public class UserInfo extends AppCompatActivity implements View.OnClickListener{

    private ImageView head;
    private TextView nickName,address,phoneNumber;
    private Button logout,reset;
    private Toolbar toolbar;
    private AVUser user;
    private SelectPicPopupWindow menuWindow;

    private static final int REQUESTCODE_TAKE = 0;
    private static final int REQUESTCODE_PICK =1;
    private  static final int REQUESTCODE_CUTTING = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        File destDir = new File(Environment.getExternalStorageDirectory() + "/AndroidPersonal_icon");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        user = AVUser.getCurrentUser();

        initView();
        initLoad();
        initListener();
    }

    private void initLoad() {
        if (user!=null) {
            String name = user.getString("username");
            String phone = user.getMobilePhoneNumber();
            String myaddress = user.getString("address");
            
            nickName.setText(name);
            phoneNumber.setText(phone);
            address.setText(myaddress);


            LeanUtil.setUserImage(head);

        }
    }

    private void initListener() {

        logout.setOnClickListener(this);

        head.setOnClickListener(this);

        nickName.setOnClickListener(this);

        phoneNumber.setOnClickListener(this);

        address.setOnClickListener(this);

        reset.setOnClickListener(this);

    }

    private void initView() {
        head = (ImageView) findViewById(R.id.head);
        nickName = (TextView) findViewById(R.id.nickName);
        phoneNumber = (TextView) findViewById(R.id.user_phone);
        address = (TextView) findViewById(R.id.address);
        logout = (Button) findViewById(R.id.btn_logout);
        reset = (Button) findViewById(R.id.btn_reset);
        toolbar = (Toolbar) findViewById(R.id.info_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("个人资料");
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset:
                Intent intent1 = new Intent(UserInfo.this,ResetPwdActivity.class);
                startActivity(intent1);
                break;

            case R.id.btn_logout:
                AVUser.logOut();// 清除缓存用户对象
                AVUser currentUser = AVUser.getCurrentUser();
                Intent intent = new Intent(UserInfo.this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.head:
                menuWindow = new SelectPicPopupWindow(UserInfo.this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.userRelative),
                        Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);


                break;

            case R.id.nickName:
                final EditText inputServer = new EditText(UserInfo.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                builder.setTitle("修改信息").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        nickName.setText( inputServer.getText().toString());
                        user.put("username",inputServer.getText().toString());
                        user.saveInBackground();
                    }
                });
                builder.show();
                break;
            case R.id.address:
                final EditText inputServer1 = new EditText(UserInfo.this);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(UserInfo.this);
                builder1.setTitle("修改信息").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer1)
                        .setNegativeButton("取消", null);
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        address.setText( inputServer1.getText().toString());
                        user.put("address",inputServer1.getText().toString());
                        user.saveInBackground();
                    }
                });
                builder1.show();
                break;

            case R.id.user_phone:
                Toast.makeText(this,"很抱歉，暂时不支持更改手机号",Toast.LENGTH_SHORT).show();
                break;


        }

    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (hasSdcard()) {
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory() ,"AndroidPersonal_icon")));
                    }
                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    pickIntent.setType("image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REQUESTCODE_TAKE:
                if (hasSdcard()) {
                    File temp = new File(Environment.getExternalStorageDirectory(), "AndroidPersonal_icon");
                    startPhotoZoom(Uri.fromFile(temp));
                }else
                {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            LeanUtil.uploadImage(photo);
            head.setImageBitmap(photo);
        }
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
                 if (state.equals(Environment.MEDIA_MOUNTED)) {
                        // 有存储的SDCard
                         return true;
                  } else {
                        return false;
                   }
             }
}
