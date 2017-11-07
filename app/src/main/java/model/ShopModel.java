package model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hau on 2017/9/20.
 */
public class ShopModel implements Parcelable {


    private String name;

    private  String introduction;

    private Bitmap path;

    private int price;

    private int remain;

    private  String url;



    public ShopModel(String name, String introduction, Bitmap path, int price, int remain) {
        this.name = name;
        this.introduction = introduction;
        this.path = path;
        this.price = price;
        this.remain = remain;
    }

    public ShopModel(String name, String introduction,String url, int price,int remain) {
        this.name = name;
        this.introduction = introduction;
        this.url = url;
        this.price = price;
        this.remain = remain;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPath() {
        return path;
    }

    public void setPath(Bitmap path) {
        this.path = path;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRemain() {return remain;}

    public void setRemain(int remain) {this.remain = remain;}


    @Override
    public int describeContents() {
        return 0;
    }




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.introduction);
        path.writeToParcel(dest,0);
        dest.writeInt(this.price);
        dest.writeInt(this.remain);

    }

    public ShopModel() {

    }

    protected ShopModel(Parcel in) {
        this.name = in.readString();
        this.introduction = in.readString();
        this.path = Bitmap.CREATOR.createFromParcel(in);
        this.price = in.readInt();
        this.remain = in.readInt();
    }

    public static final Parcelable.Creator<ShopModel> CREATOR = new Parcelable.Creator<ShopModel>() {
        @Override
        public ShopModel createFromParcel(Parcel source) {
            return new ShopModel(source);
        }

        @Override
        public ShopModel[] newArray(int size) {
            return new ShopModel[size];
        }
    };
}
