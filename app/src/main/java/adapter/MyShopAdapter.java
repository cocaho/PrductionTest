package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.hau.prductiontest.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import imp.ShopCartImp;
import model.ShopCart;
import model.ShopModel;

/**
 * Created by Hau on 2017/9/20.
 */

public class MyShopAdapter extends RecyclerView.Adapter  {

    private List<ShopModel>  mShopList;
    Context context;
    ShopCart shopCart;
    ShopCartImp shopCartImp;

    public ShopCartImp getShopCartImp() {
        return shopCartImp;
    }

    public void setShopCartImp(ShopCartImp shopCartImp) {
        this.shopCartImp = shopCartImp;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productionImg;
        ImageView reduce;
        ImageView add;
        TextView productionName;
        TextView productionPrice;
        TextView productionRemain;
        TextView chooseNums;
        LinearLayout itemLayout;

        public ViewHolder(View view){
            super(view);
            productionImg = (ImageView) view.findViewById(R.id.productionImg);
            reduce = (ImageView) view.findViewById(R.id.reduce);
            add = (ImageView) view.findViewById(R.id.add);
            productionName = (TextView) view.findViewById(R.id.productionName);
            productionPrice = (TextView) view.findViewById(R.id.productionPrice);
            productionRemain = (TextView) view.findViewById(R.id.productionRemain);
            chooseNums = (TextView) view.findViewById(R.id.choose_nums);
            itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
        }


    }

    public MyShopAdapter(Context context,List<ShopModel> list,ShopCart shopCart){

                 mShopList = list;
                 this.context = context;
                 this.shopCart = shopCart;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ViewHolder viewHolder = (ViewHolder)holder;
        final ShopModel model = mShopList.get(position);
        viewHolder.productionName.setText(model.getName());
        viewHolder.productionPrice.setText("￥:"+model.getPrice()+"");
        viewHolder.productionRemain.setText("剩余量:"+model.getRemain());
        viewHolder.productionImg.setImageBitmap(model.getPath());
       //Glide.with(context).load(model.getUrl()).into(viewHolder.productionImg);

        int count = 0;
        if(shopCart.getShoppingSingleMap().containsKey(model)){
            count = shopCart.getShoppingSingleMap().get(model);
        }
        if(count>=0){
            viewHolder.chooseNums.setText(count+"");
        }

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shopCart.addShoppingSingle(model)) {
                    notifyItemChanged(position);
                    if(shopCartImp!=null)
                        shopCartImp.add(view,position);

                }
            }
        });

        viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shopCart.subShoppingSingle(model)) {
                    notifyItemChanged(position);
                    if(shopCartImp!=null)
                        shopCartImp.remove(view,position);

                }
            }
        });

        viewHolder.productionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.item_show);
                ImageView imageView = (ImageView) window.findViewById(R.id.production_Img);
                imageView.setImageBitmap(model.getPath());
                TextView textView = (TextView) window.findViewById(R.id.production_Name);
                textView.setText(model.getIntroduction());


            }
        });


    }



    @Override
    public int getItemCount() {
        return mShopList.size()== 0 ? 0 : mShopList.size() ;
    }
}
