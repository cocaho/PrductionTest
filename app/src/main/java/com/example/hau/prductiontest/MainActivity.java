package com.example.hau.prductiontest;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import adapter.MyShopAdapter;
import adapter.PayAdapter;

import datas.AppConfig;
import imp.ShopCartImp;
import model.ShopCart;
import model.ShopModel;
import util.LeanUtil;
import wiget.FakeAddImageView;
import wiget.PointFTypeEvaluator;
import wiget.ShopCartDialog;

/**
 * Created by Hau on 2017/9/20.
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,ShopCartImp,ShopCartDialog.ShopCartDialogImp{


    private RecyclerView shopList,payList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ShopModel> list;
    private MyShopAdapter adapter;
    private ShopCart shopCart;
    private Boolean isHide = false;//fragment是否覆盖在activity上

    private ImageView shoppingCartView;
    private TextView totalPriceTextView;
    private TextView totalPriceNumTextView;
    private TextView payTv;
    private RelativeLayout relativeLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private FrameLayout shoppingCartLayout;
    private ImageView userImg;
    private TextView userTv;
    private Toolbar toolbar;

    private Handler handler = new Handler();
    private long exitTime=0;
    private static int THRESHOLD_OFFSET = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AVOSCloud.initialize(this,"cwbL3KxL2nst7Jjbij5Ap50x-gzGzoHsz","8vYasv6zVkxozxXvPToD1XrX");

        initView();
        initShop();
        initNav();
    }


    private void initShop() {

        swipeRefreshLayout.setProgressViewEndTarget(false,600);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable(){
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        swipeRefreshLayout.postDelayed(new Runnable(){
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);

            }
        },2000);

        shopList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = LeanUtil.getData(getApplicationContext(),handler);
        shopCart = new ShopCart();
        adapter = new MyShopAdapter(getApplicationContext(),list,shopCart);
        shopList.setAdapter(adapter);
        adapter.setShopCartImp(this);

        shopList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean controlVisible = true;
            int scrollDistance = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (controlVisible && scrollDistance > THRESHOLD_OFFSET){
                    //手指上滑即Scroll向下滚动的时候，dy为正
                    animationHide();

                    //重置使下面的if语句起作用
                    controlVisible = false;
                    scrollDistance = 0;
                }else if (!controlVisible && scrollDistance < -THRESHOLD_OFFSET){
                    //手指下滑即Scroll向上滚动的时候，dy为负
                    animationShow();
                    controlVisible = true;
                    scrollDistance = 0;
                }

                //当scrollDistance累计到隐藏（显示)ToolBar之后，如果Scroll向下（向上）滚动，则停止对scrollDistance的累加
                //直到Scroll开始往反方向滚动，再次启动scrollDistance的累加
                if ((controlVisible && dy > 0) || (!controlVisible && dy < 0)){
                    scrollDistance += dy;
                }
            }
        });

        shoppingCartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCart(view);
            }
        });

        payTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(shopCart!=null && shopCart.getShoppingAccount()>0) {

                    isHide =true;

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,new PayFragment())
                            .addToBackStack(null)
                            .commit();

                }
                else{
                    Toast.makeText(getApplicationContext(),"请先购物",Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void initView() {
        payTv = (TextView) findViewById(R.id.sl_submit_area);
        shoppingCartView = (ImageView) findViewById(R.id.sl_shopcart_img);
        totalPriceTextView = (TextView) findViewById(R.id.sl_money_text);
        totalPriceNumTextView = (TextView) findViewById(R.id.sl_count_text);
        relativeLayout = (RelativeLayout) findViewById(R.id.sl_main_layout);
        shoppingCartLayout = (FrameLayout) findViewById(R.id.sl_show_layout);
        shopList = (RecyclerView) findViewById(R.id.sl_rv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sl_refresh);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.mian_toolbar);
        navigationView = (NavigationView)findViewById(R.id.main_nav);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.meun_3);
        }


    }

    private void initNav() {

        final AVUser currentUser = AVUser.getCurrentUser();
        navigationView.setCheckedItem(R.id.one);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.one:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.two:
                        Intent intent = new Intent(MainActivity.this,CheckHistory.class);
                        startActivity(intent);
                        break;
                    case R.id.three:
                        Intent intent1 = new Intent(MainActivity.this,CheckOrder.class);
                        startActivity(intent1);
                        break;
                    case R.id.four:
                        Toast.makeText(getApplicationContext(),"我好像听到有人在说我帅。",Toast.LENGTH_SHORT).show();
                        break;


                }
                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        userImg = (ImageView) headerView.findViewById(R.id.UserImage);
        userTv = (TextView) headerView.findViewById(R.id.UserName);

        if (currentUser!=null) {
            LeanUtil.setUserImage(userImg);
            userTv.setText(currentUser.getString("username"));
        }

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    // 跳转到个人信息
                    Intent intent = new Intent(MainActivity.this,UserInfo.class);
                    startActivity(intent);
                } else {
                    //缓存用户对象为空时，可打开用户注册界面…
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return  true;
    }

    //实现添加的贝塞尔曲线动画
    @Override
    public void add(View view, int position) {
        int[] addLocation = new int[2];
        int[] cartLocation = new int[2];
        int[] recycleLocation = new int[2];
        view.getLocationInWindow(addLocation);
        shoppingCartView.getLocationInWindow(cartLocation);
        shopList.getLocationInWindow(recycleLocation);

        PointF startP = new PointF();
        PointF endP = new PointF();
        PointF controlP = new PointF();

        startP.x = addLocation[0];
        startP.y = addLocation[1]-recycleLocation[1];
        endP.x = cartLocation[0];
        endP.y = cartLocation[1]-recycleLocation[1];
        controlP.x = endP.x;
        controlP.y = startP.y;

        final FakeAddImageView fakeAddImageView = new FakeAddImageView(this);
        relativeLayout.addView(fakeAddImageView);
        fakeAddImageView.setImageResource(R.mipmap.ic_add_circle_blue_700_36dp);
        fakeAddImageView.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.item_goods_circle_size);
        fakeAddImageView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.item_goods_circle_size);
        fakeAddImageView.setVisibility(View.VISIBLE);
        ObjectAnimator addAnimator = ObjectAnimator.ofObject(fakeAddImageView, "mPointF",
                                                              new PointFTypeEvaluator(controlP), startP, endP);
        addAnimator.setInterpolator(new AccelerateInterpolator());
        addAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fakeAddImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fakeAddImageView.setVisibility(View.GONE);
                mDrawerLayout.removeView(fakeAddImageView);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        ObjectAnimator scaleAnimatorX = new ObjectAnimator().ofFloat(shoppingCartView,"scaleX", 0.6f, 1.0f);
        ObjectAnimator scaleAnimatorY = new ObjectAnimator().ofFloat(shoppingCartView,"scaleY", 0.6f, 1.0f);
        scaleAnimatorX.setInterpolator(new AccelerateInterpolator());
        scaleAnimatorY.setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleAnimatorX).with(scaleAnimatorY).after(addAnimator);
        animatorSet.setDuration(800);
        animatorSet.start();

        showTotalPrice();
    }

    @Override
    public void remove(View view, int position) {
        showTotalPrice();
    }

    private void showTotalPrice(){
        if(shopCart!=null && shopCart.getShoppingTotalPrice()>0){
            totalPriceTextView.setVisibility(View.VISIBLE);
            totalPriceTextView.setText("￥ "+shopCart.getShoppingTotalPrice());
            totalPriceNumTextView.setVisibility(View.VISIBLE);
            totalPriceNumTextView.setText(""+shopCart.getShoppingAccount());

        }else {
            totalPriceTextView.setVisibility(View.GONE);
            totalPriceNumTextView.setVisibility(View.GONE);
        }
    }

    //结算小界面
    private void showCart(View view) {
        if(shopCart!=null && shopCart.getShoppingAccount()>0){
            ShopCartDialog dialog = new ShopCartDialog(this,shopCart,R.style.cartdialog);
            Window window = dialog.getWindow();
            dialog.setShopCartDialogImp( this);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.show();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
        }
    }

    @Override
    public void dialogDismiss() {
        showTotalPrice();
        adapter.notifyDataSetChanged();
    }

    //toolBar显示
    private void animationShow() {
        toolbar.animate()
                .setInterpolator(new AccelerateInterpolator(1))
                .setDuration(180)
                .translationY(0);
    }

    //toolBar隐藏
    private void animationHide(){

        toolbar.animate()
                .translationY(-toolbar.getBottom())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180);

    }

    //按返回两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!isHide){
            if (keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
                if((System.currentTimeMillis()-exitTime)>2000){

                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                }else {
                    finish();
                }

                return true;
            }
        }
        isHide = false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNav();
    }

    public ShopCart getShopCart(){
        return shopCart;
    }

    public void setisHide(Boolean isHide){
        this.isHide = isHide;
    }

    public void resetShopCart(){
                shopCart.clear();
                totalPriceNumTextView.setText("0");
                totalPriceTextView.setText("￥0.0");
    }

    @Override
    public void onRefresh() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                LeanUtil.refresh(list);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
