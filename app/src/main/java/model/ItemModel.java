package model;

import android.graphics.Bitmap;

/**
 * Created by Hau on 2017/10/29.
 */

public class ItemModel {
    private String productName;
    private int count;
    private int price;
    private String date;
    private int status;




    public ItemModel(String productName, int count,String date){
        this.productName = productName;
        this.count = count;
        this.date = date;
    }
    public ItemModel(String productName, int count,int price,String date,int status){
        this.productName = productName;
        this.count = count;
        this.price = price;
        this.date = date;
        this.status = status;
    }

    public ItemModel(String productName, int count,int price,String date){
        this.productName = productName;
        this.count = count;
        this.date = date;
        this.price = price;

    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        switch (status){
            case 0:
                return "未处理";
            case 1:
                return "已发货";
            case 2:
                return "完成";
        }
        return  null;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
