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

import imp.ShopCartImp;
import model.ShopCart;
import model.ShopModel;

/**
 * Created by Hau on 2017/9/20.
 */

public class PopupAdapter extends RecyclerView.Adapter {

    private ShopCart shopCart;
    private Context context;
    private int itemCount;
    private ArrayList<ShopModel> modelList;
    private ShopCartImp shopCartImp;

    public PopupAdapter(Context context, ShopCart shopCart){
           this.shopCart = shopCart;
           this.context = context;
           this.itemCount = shopCart.getModelAccount();
           this.modelList = new ArrayList<>();
           modelList.addAll(shopCart.getShoppingSingleMap().keySet());

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final ShopModel shopModel = getDishByPosition(position);
        if(shopModel!=null) {
            viewHolder.productionName.setText(shopModel.getName());
            viewHolder.productionPrice.setText(shopModel.getPrice()+"");
            viewHolder.productionImg.setImageBitmap(shopModel.getPath());
            int num = shopCart.getShoppingSingleMap().get(shopModel);
            viewHolder.chooseNums.setText(num+"");

            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(shopCart.addShoppingSingle(shopModel)) {
                        notifyItemChanged(position);
                        if(shopCartImp!=null)
                            shopCartImp.add(view,position);
                    }
                }
            });

            viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(shopCart.subShoppingSingle(shopModel)){
                        modelList.clear();
                        modelList.addAll(shopCart.getShoppingSingleMap().keySet());
                        itemCount = shopCart.getModelAccount();
                        notifyDataSetChanged();
                        if(shopCartImp!=null)
                            shopCartImp.remove(view,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }

    public ShopModel getDishByPosition(int position){
        return modelList.get(position);
    }

    public ShopCartImp getShopCartImp() {
        return shopCartImp;
    }

    public void setShopCartImp(ShopCartImp shopCartImp){
        this.shopCartImp = shopCartImp;
    }

    private  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productionImg;
        ImageView reduce;
        ImageView add;
        TextView productionName;
        TextView productionPrice;
        TextView chooseNums;


        public ViewHolder(View view) {
            super(view);
            productionImg = (ImageView) view.findViewById(R.id.productionImg);
            reduce = (ImageView) view.findViewById(R.id.reduce);
            add = (ImageView) view.findViewById(R.id.add);
            productionName = (TextView) view.findViewById(R.id.productionName);
            productionPrice = (TextView) view.findViewById(R.id.productionPrice);
            chooseNums = (TextView) view.findViewById(R.id.choose_nums);
        }
    }
}
