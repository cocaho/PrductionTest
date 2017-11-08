package util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.example.hau.prductiontest.LoginActivity;
import com.example.hau.prductiontest.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import adapter.ConsumeAdapter;
import model.ItemModel;
import model.ShopCart;
import model.ShopModel;

/**
 * Created by Hau on 2017/11/3.
 */

public class LeanUtil {

    static int FINISHED_STATUS = 2;

    public static void setOrderImage(final ImageView imageView, String orderName) {
        AVQuery<AVObject> iQuery = new AVQuery<>("Product");
        iQuery.whereStartsWith("title", orderName);
        iQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()>0){
                    final AVFile file = list.get(0).getAVFile("image");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            // bytes 就是文件的数据流
                            Bitmap iBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(iBitmap);
                        }
                    });
                }

            }
        });
    }

    public static void setUserImage(final ImageView image){
        final AVUser user = AVUser.getCurrentUser();

        AVQuery<AVObject> query = new AVQuery<>("_File");
        final String[] id = {null};
        query.whereStartsWith("name", user.getString("username"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                if (list.size() > 0) {
                    id[0] = list.get(list.size() - 1).getString("url");
                    AVFile file = new AVFile(user.getString("username"), id[0], new HashMap<String, Object>());

                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            // bytes 就是文件的数据流
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            image.setImageBitmap(bitmap);
                        }

                    });
                }
            }
        });
    }

    public static void getOrderHistory(final Context context, final RecyclerView recyclerView) {
        final AVUser user = AVUser.getCurrentUser();
        final List<ItemModel> itemList = new ArrayList<>();


        AVQuery<AVObject> nameQuery = new AVQuery<>("Order");
        nameQuery.whereStartsWith("username", user.get("username").toString());


        AVQuery<AVObject> stateQuery = new AVQuery<>("Order");
        stateQuery.whereEqualTo("state",FINISHED_STATUS);


        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(nameQuery, stateQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                if (list.size()>0){

                    for( AVObject ao:list){


                        String time = parseDate(ao.getCreatedAt().toString());
                        ItemModel itemModel = new ItemModel(ao.getString("productName"),ao.getInt("count"),ao.getInt("price"),time);
                        itemList.add(itemModel);


                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    ConsumeAdapter ca = new ConsumeAdapter(itemList);
                    recyclerView.setAdapter(ca);

                }else{

                    Toast.makeText(context,"暂无记录",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public static void setOrder(final ShopCart shopCart , final String phone, final String address, final String tip){
        final AVUser user = AVUser.getCurrentUser();

        final ArrayList<ShopModel> modelList = new ArrayList<>();
        final ArrayList<Integer> countList = new ArrayList<>();

        modelList.addAll(shopCart.getShoppingSingleMap().keySet());
        for (ShopModel shopModel:modelList){
            countList.add(shopCart.getShoppingSingleMap().get(shopModel));
        }

        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.selectKeys(Arrays.asList("order_id"));
        query.orderByDescending("order_id");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                int order_id = list.get(0).getInt("order_id")+1;

                for (int i=0;i<modelList.size();i++){
                    AVObject avObject = new AVObject("Order");
                    avObject.put("username",user.get("username"));
                    avObject.put("productName",modelList.get(i).getName());
                    avObject.put("count",countList.get(i));
                    avObject.put("price",modelList.get(i).getPrice());
                    avObject.put("phone",phone);
                    avObject.put("address",address);
                    avObject.put("tip",tip);
                    avObject.put("state",0);
                    avObject.put("order_id",order_id);
                    avObject.saveInBackground();
                }

            }
        });


    }

    public static void updataProduct(ShopCart shopCart){

        ArrayList<ShopModel> modelList = new ArrayList<>();
        modelList.addAll(shopCart.getShoppingSingleMap().keySet());


        AVQuery<AVObject> query = new AVQuery<>("Product");
        final String[] id = {null};

        for (ShopModel sm:modelList){

            final int newRemain = sm.getRemain();
            query.whereStartsWith("title", sm.getName());
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {

                    id[0] = list.get(0).getObjectId();
                    AVQuery.doCloudQueryInBackground("update Product set reserve="+newRemain+"  where objectId='"+id[0]+"'", new CloudQueryCallback<AVCloudQueryResult>() {
                        @Override
                        public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                            // 如果 e 为空，说明保存成功

                        }
                    });


                }
            });


        }



    }

    public static void refresh(final List<ShopModel> list){

        AVQuery<AVObject> query = new AVQuery<>("Product");
        query.selectKeys(Arrays.asList("title","price","reserve","description","image"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> Alist, AVException e) {

                list.clear();
                for (AVObject avObject : Alist) {
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
                            list.add(new ShopModel(name, description, bitmap, price, remain));
                        }
                    });




                }
            }
        });
    }

    public static void signUpOrLoginByMobilePhoneInBackground(final Context context, String phoneNumber, String message){

                AVUser.signUpOrLoginByMobilePhoneInBackground(phoneNumber, message, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (avUser!=null){
                            Log.i("smile","用户登陆成功");
                            Intent intent = new Intent(context,MainActivity.class);
                            context.startActivity(intent);
                        }else {
                            Toast.makeText(context, "用户未注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public static void signUpOrLoginByMobilePhoneInBackground(final Context context, String phoneNumber, String message, final String finalUserName, final String finalPassWard, final String finalPhoneNumber){

        AVUser.signUpOrLoginByMobilePhoneInBackground(phoneNumber, message, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser user, AVException e) {
                // 如果 e 为空就可以表示登录成功了，并且 user 是一个全新的用户
                if (e==null){
                    user.setUsername(finalUserName);
                    user.setPassword(finalPassWard);
                    user.setMobilePhoneNumber(finalPhoneNumber);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(context, "注册成功:", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else {
                    Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void userRequestLoginSmsCodeInBackground(final Context context,final Button get_number,String phoneNumber){
        AVUser.requestLoginSmsCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    get_number.setClickable(false);
                    get_number.setBackgroundColor(Color.GRAY);
                    Toast.makeText(context, "请稍候", Toast.LENGTH_SHORT).show();
                    new CountDownTimer(60000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            get_number.setText(millisUntilFinished / 1000 + "秒");
                        }

                        @Override
                        public void onFinish() {
                            get_number.setBackgroundColor(Color.RED);
                            get_number.setClickable(true);
                            get_number.setText("重新发送");

                        }
                    }.start();
                    Log.e("MESSAGE:", "4");
                } else {
                    Toast.makeText(context, "该手机号未注册，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void cloudRequestLoginSmsCodeInBackground(final Context context,final Button getNumber,String phoneNumber){

        AVOSCloud.requestSMSCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            // 发送失败可以查看 e 里面提供的信息
                            if (e == null) {
                                getNumber.setClickable(false);
                                getNumber.setBackgroundColor(Color.GRAY);
                                Toast.makeText(context, "请稍候", Toast.LENGTH_SHORT).show();
                                new CountDownTimer(60000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        getNumber.setText(millisUntilFinished / 1000 + "秒");
                                    }

                                    @Override
                                    public void onFinish() {
                                        getNumber.setBackgroundColor(Color.RED);
                                        getNumber.setClickable(true);
                                        getNumber.setText("重新发送");

                                    }
                                }.start();
                                Log.e("MESSAGE:", "4");
                            } else{
                                Toast.makeText(context, "验证码发送失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    public static void loginByMobilePhoneNumberInBackground(final Context context,String phoneNumber,String passWard){
        AVUser.loginByMobilePhoneNumberInBackground(phoneNumber, passWard, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if(e==null){
                    Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context,"请输入正确信息",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void resetPasswordBySmsCodeInBackground(final Context context,String message,String passWard){
        AVUser.resetPasswordBySmsCodeInBackground(message, passWard, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(context, "重置成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                } else {
                    e.printStackTrace();
                    Toast.makeText(context, "重置失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void uploadImage(Bitmap photo){
        final AVUser user = AVUser.getCurrentUser();

        AVQuery<AVObject> query = new AVQuery<>("_File");
        query.whereStartsWith("name", user.getString("username"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject avObject:list){
                    avObject.deleteInBackground();
                }
            }
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        AVFile file = new AVFile(user.getString("username"),datas);
        file.saveInBackground();

    }

    public static List<ShopModel> getData(Context context,Handler handler) {


        final List<ShopModel> datas = new ArrayList<>();

           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
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
                                                        }
                               );
                           }
                       }
                   });

               }
           },1500);

        return datas;

    }

    public static String parseDate(String date){
        String[] dates = date.split(" ");
        int len = dates.length;
        String parseDate = dates[len-1]+"-";
        switch(dates[1]){
            case "Jan": parseDate+="01"+"-";break;
            case "Feb": parseDate+="02"+"-";break;
            case "Mar": parseDate+="03"+"-";break;
            case "Apr": parseDate+="04"+"-";break;
            case "May": parseDate+="05"+"-";break;
            case "Jun": parseDate+="06"+"-";break;
            case "Jul": parseDate+="07"+"-";break;
            case "Aug": parseDate+="08"+"-";break;
            case "Sept": parseDate+="09"+"-";break;
            case "Oct": parseDate+="10"+"-";break;
            case "Nov": parseDate+="11"+"-";break;
            case "Dec": parseDate+="12"+"-";break;
        }
        parseDate+=dates[2]+" " +dates[3];
        return parseDate;
    }

   public static List<ShopModel> getData2(){


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
                   String url = file.getUrl();
                   datas.add(new ShopModel(name, description, url, price, remain));

               }
           }
       });



       return datas;

   }
}