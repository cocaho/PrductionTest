package com.example.hau.prductiontest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;

import java.util.List;

import util.LeanUtil;
import model.ItemModel;

/**
 * Created by Hau on 2017/10/29.
 */

public class CheckHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AVUser user;
    private List<ItemModel> itemList;
    private int FINISHED_STATUS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_check);

        init();
        getHistory();

    }

    private void getHistory() {

        if(user!=null){

            LeanUtil.getOrderHistory(getApplicationContext(),recyclerView);


        }else
        {
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
        }


    }

    private void init() {
        user = AVUser.getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.history_recyclerView);
        toolbar = (Toolbar) findViewById(R.id.history_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("消费历史");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public static String parseDate(String date){
        String[] dates = date.split(" ");
        int len = dates.length;
        String parseDate = dates[len-1]+"-";
        switch(dates[1]){
            case "Jan": parseDate+="01"+"-";break;
            case "Feb": parseDate+="02"+"-";break;
            case "Mar": parseDate+="03"+"-";break;
            case "Apr": parseDate+="04"+"-";break;
            case "May": parseDate+="05"+"-";break;
            case "Jun": parseDate+="06"+"-";break;
            case "Jul": parseDate+="07"+"-";break;
            case "Aug": parseDate+="08"+"-";break;
            case "Sept": parseDate+="09"+"-";break;
            case "Oct": parseDate+="10"+"-";break;
            case "Nov": parseDate+="11"+"-";break;
            case "Dec": parseDate+="12"+"-";break;
        }
        parseDate+=dates[2]+" " +dates[3];
        return parseDate;
    }
}
