package adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hau.prductiontest.R;


import java.util.List;

import util.LeanUtil;
import model.ItemModel;

/**
 * Created by Hau on 2017/10/29.
 */

public class ConsumeAdapter extends RecyclerView.Adapter {

    private List<ItemModel>  itemModelList;

    public  ConsumeAdapter(List<ItemModel>  itemModelList){
        this.itemModelList = itemModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check, parent, false);
        ConsumeAdapter.MyHolder viewHolder = new ConsumeAdapter.MyHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ConsumeAdapter.MyHolder viewHolder = (ConsumeAdapter.MyHolder) holder;
        ItemModel itemModel = itemModelList.get(position);
        if (itemModel!=null){

            viewHolder.name.setText(itemModel.getProductName());
            viewHolder.count.setText("x"+itemModel.getCount());
            viewHolder.time.setText(itemModel.getDate());
            viewHolder.price.setText("￥"+itemModel.getPrice());

            LeanUtil.setOrderImage(viewHolder.imageView,itemModel.getProductName());


        }
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    private  class  MyHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView count;
        private TextView time;
        private TextView price;
        private ImageView imageView;

        public MyHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.check_name);
            count = (TextView) view.findViewById(R.id.check_count);
            time = (TextView) view.findViewById(R.id.check_date);
            price = (TextView) view.findViewById(R.id.check_price);
            imageView = (ImageView) view.findViewById(R.id.check_image);

        }

    }

}
