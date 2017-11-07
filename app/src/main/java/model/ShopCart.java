package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Hau on 2017/9/20.
 */
public class ShopCart implements Parcelable{
    private int shoppingAccount;//商品总数
    private double shoppingTotalPrice;//商品总价钱
    private Map<ShopModel,Integer> shoppingSingle;//单个物品的总价价钱

    public ShopCart(){
        this.shoppingAccount = 0;
        this.shoppingTotalPrice = 0;
        this.shoppingSingle = new HashMap<>();
    }



    public int getShoppingAccount() {
        return shoppingAccount;
    }

    public double getShoppingTotalPrice() {
        return shoppingTotalPrice;
    }

    public Map<ShopModel, Integer> getShoppingSingleMap() {
        return shoppingSingle;
    }

    public boolean addShoppingSingle(ShopModel shopModel){
        int remain = shopModel.getRemain();
        if(remain<=0)
            return false;
        shopModel.setRemain(--remain);
        int num = 0;
        if(shoppingSingle.containsKey(shopModel)){
            num = shoppingSingle.get(shopModel);
        }
        num+=1;
        shoppingSingle.put(shopModel,num);
        Log.e("TAG", "addShoppingSingle: "+shoppingSingle.get(shopModel));

        shoppingTotalPrice += shopModel.getPrice();
        shoppingAccount++;
        return true;
    }

    public boolean subShoppingSingle(ShopModel shopModel){
        int num = 0;
        if(shoppingSingle.containsKey(shopModel)){
            num = shoppingSingle.get(shopModel);
        }
        if(num<=0) return false;
        num--;
        int remain = shopModel.getRemain();
        shopModel.setRemain(++remain);
        shoppingSingle.put(shopModel,num);
        if (num ==0) shoppingSingle.remove(shopModel);

        shoppingTotalPrice -= shopModel.getPrice();
        shoppingAccount--;
        return true;
    }

    public int getModelAccount() {
        return shoppingSingle.size();
    }

    public void clear(){
        this.shoppingAccount = 0;
        this.shoppingTotalPrice = 0;
        restore();
        this.shoppingSingle.clear();
    }

    public void restore(){
        for(Map.Entry<ShopModel,Integer> entry:shoppingSingle.entrySet()){
                ShopModel sm = entry.getKey();
                int num = entry.getValue();
                int remainder = sm.getRemain();
                sm.setRemain(num+remainder);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

         dest.writeInt(shoppingAccount);
         dest.writeDouble(shoppingTotalPrice);
         dest.writeMap(shoppingSingle);
    }
    protected ShopCart(Parcel in) {
         shoppingAccount = in.readInt();
         shoppingTotalPrice = in.readDouble();
         shoppingSingle = in.readHashMap(Map.class.getClassLoader());
    }

    public static final Creator<ShopCart> CREATOR = new Creator<ShopCart>() {
        @Override
        public ShopCart createFromParcel(Parcel in) {

            return new ShopCart(in);
        }

        @Override
        public ShopCart[] newArray(int size) {
            return new ShopCart[size];
        }
    };
}
