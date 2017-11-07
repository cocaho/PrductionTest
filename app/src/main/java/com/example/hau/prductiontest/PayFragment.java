package com.example.hau.prductiontest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import adapter.PayAdapter;
import model.ShopCart;
import model.ShopModel;
import util.LeanUtil;


/**
 * Created by Hau on 2017/10/24.
 */

public class PayFragment extends Fragment {

    private ShopCart shopCart;
    private PayAdapter payAdapter;
    private static MainActivity mainActivity;


    private RecyclerView recyclerView;
    private TextView pay_tv;
    private EditText phone_ed,address_ed,tip_ed;
    private ImageView close;
    private Button pay;

    private  String phone,address,tip;

    private AVUser avUser;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pay,container,false);
        mainActivity = (MainActivity) getActivity();
        shopCart = mainActivity.getShopCart();

        avUser = AVUser.getCurrentUser();

        initView(root);
        initEvent(root);

        return  root;
    }



    private void initEvent(View root) {

        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        payAdapter = new PayAdapter(root.getContext(),shopCart);
        recyclerView.setAdapter(payAdapter);

        pay_tv.setText("￥"+shopCart.getShoppingTotalPrice());

        if (avUser!=null){
            address_ed.setText(avUser.get("address").toString());
            phone_ed.setText(avUser.getMobilePhoneNumber());
        }


        phone = phone_ed.getText().toString();
        address = address_ed.getText().toString();
        tip = tip_ed.getText().toString();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avUser = AVUser.getCurrentUser();//更新

                if (avUser==null){
                    Toast.makeText(mainActivity.getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mainActivity.getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }else {


                        LeanUtil.updataProduct(shopCart);
                        LeanUtil.setOrder(shopCart,phone,address,tip);
                        mainActivity.resetShopCart();
                        closeFragment();
                        Toast.makeText(mainActivity.getApplicationContext(),"购买成功",Toast.LENGTH_SHORT).show();


                }

            }
        });
    }

    private void initView(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.pay_recyclerView);
        pay_tv = (TextView) root.findViewById(R.id.pay_total_price);
        phone_ed = (EditText) root.findViewById(R.id.pay_phone);
        address_ed = (EditText) root.findViewById(R.id.pay_address);
        tip_ed = (EditText) root.findViewById(R.id.pay_tip);
        close = (ImageView) root.findViewById(R.id.pay_close);
        pay = (Button) root.findViewById(R.id.pay);



    }

    public  static void closeFragment(){
        mainActivity.onBackPressed();
        mainActivity.setisHide(false);
    }

}
