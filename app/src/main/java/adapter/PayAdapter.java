package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hau.prductiontest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.ShopCart;
import model.ShopModel;

/**
 * Created by Hau on 2017/10/14.
 */

public class PayAdapter extends RecyclerView.Adapter {

    private  Context context;
    private  ShopCart shopCart;
    private  int itemCount;
    private  ArrayList<ShopModel> modelList;

    public PayAdapter(Context context, ShopCart shopCart){
        this.context = context;
        this.shopCart = shopCart;
        this.itemCount = shopCart.getModelAccount();
        this.modelList = new ArrayList<>();
        modelList.addAll(shopCart.getShoppingSingleMap().keySet());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        MyHolder viewHolder = new MyHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyHolder viewHolder = (MyHolder) holder;
        final ShopModel shopModel = getDishByPosition(position);
        if (shopModel!=null){
            viewHolder.reduce.setVisibility(View.INVISIBLE);
            viewHolder.add.setVisibility(View.INVISIBLE);
            viewHolder.productionName.setText(shopModel.getName());
            viewHolder.productionPrice.setText("￥"+shopModel.getPrice());
            viewHolder.productionImg.setImageBitmap(shopModel.getPath());
            int num = shopCart.getShoppingSingleMap().get(shopModel);
            viewHolder.chooseNums.setText("x"+num);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }

    public ShopModel getDishByPosition(int position){
        return modelList.get(position);
    }

    private  class  MyHolder extends RecyclerView.ViewHolder{
        ImageView productionImg;
        ImageView reduce,add;
        TextView productionName;
        TextView productionPrice;
        TextView chooseNums;

        public MyHolder(View view) {
            super(view);

            productionImg = (ImageView) view.findViewById(R.id.productionImg);
            reduce = (ImageView) view.findViewById(R.id.reduce);
            add = (ImageView) view.findViewById(R.id.add);
            productionPrice = (TextView) view.findViewById(R.id.productionPrice);
            productionName = (TextView) view.findViewById(R.id.productionName);
            chooseNums = (TextView) view.findViewById(R.id.choose_nums);
        }
    }



}
