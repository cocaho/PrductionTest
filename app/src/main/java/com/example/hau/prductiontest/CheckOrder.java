package com.example.hau.prductiontest;

import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adapter.SuperAdapter;
import holder.DataHolder;
import holder.SuperViewHolder;
import model.ShopModel;
import util.LeanUtil;
import model.ItemModel;
import wiget.LayoutWrapper;

/**
 * Created by Hau on 2017/10/29.
 */

public class CheckOrder extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  SuperAdapter adapter;
    private Toolbar toolbar;
    private DataHolder<String> topHolder;
    private DataHolder<ItemModel> itemHolder;
    private DataHolder<Integer> buttonHolder;
    final List<LayoutWrapper> data = new ArrayList<>();

    private int HANDLING = 0;
    private int HANDLED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_check);

        init();
        createHolder();
        initAdapter();


    }

    private void initAdapter() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int[] layoutIds = {R.layout.item_top, R.layout.item_check, R.layout.item_finished};
        adapter = new SuperAdapter(this, layoutIds);
        recyclerView.setAdapter(adapter);
        getOrder();
    }

    public  void getOrder() {

        final AVUser user = AVUser.getCurrentUser();



        AVQuery<AVObject> statusQuery1 = new AVQuery<>("Order");
        statusQuery1.whereEqualTo("state", HANDLING);

        AVQuery<AVObject> statusQuery2 = new AVQuery<>("Order");
        statusQuery2.whereEqualTo("state", HANDLED);

        AVQuery<AVObject> nameQuery = new AVQuery<>("Order");
        nameQuery.whereStartsWith("username", user.get("username").toString());

        AVQuery<AVObject> statusQuery = AVQuery.or(Arrays.asList(statusQuery1, statusQuery2));
        final AVQuery<AVObject> query = AVQuery.and(Arrays.asList(nameQuery, statusQuery));

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size()==0){
                    Toast.makeText(getApplicationContext(),"暂无记录",Toast.LENGTH_SHORT).show();
                }
                else{
                    Set<Integer> set = new HashSet();

                    for (AVObject av : list) {
                        int order_id = av.getInt("order_id");
                        set.add(order_id);
                    }

                    Iterator<Integer> iterator = set.iterator();
                    data.clear();

                    while(iterator.hasNext()){
                        final int id = iterator.next();

                        AVQuery<AVObject> idQuery = new AVQuery<>("Order");
                        idQuery.whereEqualTo("order_id", id);
                        AVQuery<AVObject> query1 = AVQuery.and(Arrays.asList(query,idQuery));

                        query1.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {

                                String top_time = LeanUtil.parseDate(list.get(0).getCreatedAt().toString());
                                data.add(new LayoutWrapper(R.layout.item_top,top_time,topHolder));

                                for (AVObject avObject:list){
                                    final String name = avObject.getString("productName");
                                    final String time = LeanUtil.parseDate(avObject.getCreatedAt().toString());
                                    final int price = avObject.getInt("price");
                                    final int count = avObject.getInt("count");
                                    final int status = avObject.getInt("state");
                                    data.add(new LayoutWrapper(R.layout.item_check,new ItemModel(name,count,price,time,status) , itemHolder));
                                }
                                data.add(new LayoutWrapper(R.layout.item_finished,id,buttonHolder));

                                adapter.setData(data);
                                adapter.notifyDataSetChanged();


                            }
                        });
                    }
                }




            }
        });
    }

    private  void createHolder(){

        topHolder = new DataHolder<String>() {
            @Override
            public void bind(Context context, SuperViewHolder holder, String item, int position) {
                TextView top_time = holder.getView(R.id.top_time);
                top_time.setText(item);
            }
        };

       itemHolder = new DataHolder<ItemModel>(){

            @Override
            public void bind(Context context, SuperViewHolder holder, ItemModel item, int position) {

                TextView name = holder.getView(R.id.check_name);
                TextView count = holder.getView(R.id.check_count);
                TextView time = holder.getView(R.id.check_date);
                TextView price = holder.getView(R.id.check_price);
                TextView status = holder.getView(R.id.check_status);
                final ImageView imageView = holder.getView(R.id.check_image);

                status.setVisibility(View.VISIBLE);
                time.setVisibility(View.INVISIBLE);


                name.setText(item.getProductName());
                count.setText("x"+item.getCount());
                price.setText("￥"+item.getPrice());
                status.setText(item.getStatus());
                LeanUtil.setOrderImage(imageView,item.getProductName());


            }
        };

        buttonHolder = new DataHolder<Integer>() {
            @Override
            public void bind(final Context context, SuperViewHolder holder, Integer item, int position) {

                final int orderId = item;

                Button button = holder.getView(R.id.finished_confirm);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setTitle("确认收货");
                        builder.setMessage("落子无悔，三思而后行。");
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                        AVQuery<AVObject> idQuery1 = new AVQuery<>("Order");
                        idQuery1.whereEqualTo("order_id", orderId);
                        idQuery1.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                for(AVObject avObject:list){
                                    String id = avObject.getObjectId();
                                    AVQuery.doCloudQueryInBackground("update Order set state=2"+"  where objectId='"+id+"'", new CloudQueryCallback<AVCloudQueryResult>() {
                                        @Override
                                        public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                                            // 如果 e 为空，说明保存成功

                                                getOrder();


                                        }
                                    });
                                }
                            }
                        });


                        Toast.makeText(CheckOrder.this,"确认收货",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();

                    }
                });
            }
        };
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.history_recyclerView);
        toolbar = (Toolbar) findViewById(R.id.history_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("订单状态");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
