package datas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.okhttp.internal.Util;
import com.example.hau.prductiontest.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.ShopModel;
/**
 * Created by Hau on 2017/9/20.
 */

public class AppConfig {


    public static List<ShopModel> factoryGoods() {


        final List<ShopModel> datas = new ArrayList<>();

        AVQuery<AVObject> query = new AVQuery<>("Product");
        query.selectKeys(Arrays.asList("title","price","reserve","description","image"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                for (AVObject avObject : list) {
                    final String name = avObject.getString("title");
                    final int price = avObject.getInt("price");
                    final int remain = avObject.getInt("reserve");
                    final String description = avObject.getString("description");
                    final AVFile file = avObject.getAVFile("image");


                    file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                // bytes 就是文件的数据流
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                datas.add(new ShopModel(name, description, bitmap, price, remain));
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {
                                // 下载进度数据，integer 介于 0 和 100。
                            }
                        });




                }
            }
        });

        return datas;

    }



}
