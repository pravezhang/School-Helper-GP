package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity{

    MapView mapView;
    AMap aMap;
    String[] items,poss;
    public String factors;
    int isMapClear=1;
    ProgressDialog pw,po;
    //items 是用于往Dialog里填充的，只包含地点名字
    //poss 包含地点的所有信息，每一条都是一个JSONObject
    int findwhich=0;
    //findwhich 是指 最后的最后，要把哪个点加为marker
    public LatLng location=null;
    public ImageView map_locate,map_search;
    public AlertDialog.Builder position;
    private AlertDialog dadada;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    location=new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","ok");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");

        po=new ProgressDialog(MapActivity.this);
        po.setMessage("请稍候……");
        po.setCancelable(false);
        po.setIndeterminate(true);
        po.show();

        Timer tm=new Timer();
        tm.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value","dismiss");
                msg.setData(data);
                handler.sendMessage(msg);
            }
        },2000);
        mapView = (MapView) findViewById(R.id.map_map);
        map_locate=(ImageView)findViewById(R.id.map_locate);
        map_search=(ImageView)findViewById(R.id.map_search);

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        //LOCATE THINGS
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //定位设置
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        //启动定位，启动定位会自动添加一个点儿，上传一组数据
        mLocationClient.startLocation();

        //LayoutInflater layoutInflater = LayoutInflater.from(this);
        //View PositionSelect= layoutInflater.inflate(R.layout.view_map_select_position, null);
        position =
                new AlertDialog.Builder(MapActivity.this);
        position.setTitle("选择地点：");
        //position.setView(PositionSelect);
        position.setCancelable(false);
        map_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                mLocationClient.startLocation();
            }
        });
        map_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pw=new ProgressDialog(MapActivity.this);
                pw.setMessage("请稍候……");
                pw.setCancelable(false);
                pw.setIndeterminate(true);
                pw.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetPositions(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    public void OpenSearch(){
        position.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                findwhich = which;
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value","find");
                msg.setData(data);
                handler.sendMessage(msg);
                dadada.dismiss();
            }
        });
        dadada=position.show();
    }

    private void AddTargetPos() throws Exception{
        if(isMapClear==0) {
            aMap.clear();
            //好像自己的位置也被清空了，要再定位一次。

            isMapClear=1;
        }
        //如果已经标记过一次，这次把标记清空掉
        //
        JSONObject aaa=new JSONObject(poss[findwhich]);
        MarkerOptions pos=new MarkerOptions();
        LatLng p=new LatLng(aaa.getDouble("LAT"),aaa.getDouble("LNG"));
        pos.draggable(false);
        pos.title(aaa.getString("NAME"));
        pos.position(p);
        aMap.addMarker(pos);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(p));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        isMapClear=0;
    }

    private void UploadUser() throws Exception{

        JSONObject j=new JSONObject(factors);
        String uid=j.getString("USERID");
        String name=j.getString("NAME");
        double lat=location.latitude;
        double lng=location.longitude;
        String str="http://120.78.219.146/gp/storeuser.php?UID="
                +uid+"&LAT="
                +lat+"&LNG="
                +lng+"&NAME="
                +name;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();

    }


    public void  GetPositions(int type) throws Exception{
        String str="http://120.78.219.146/gp/getposition.php?TYPE="+
                String.valueOf(type);
        Log.d("128","START CONNECT SERVER");
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] allres=null;
        allres=inputline.split("。");
        //allres[0]内存放所有结果的结合
        poss=allres[0].split("；");
        int i=0;
        int n=poss.length;
        JSONObject singlepos;
        String s="";
        while (i<n) {
            Log.d("128","poss[i] is :"+poss[i]);
            Log.d("128","i is :"+String.valueOf(i));
            singlepos = new JSONObject(poss[i]);
            s+=singlepos.getString("NAME");
            s+="·";
            i++;
            Log.d("128","now s is :"+s);
        }
        items=s.split("·");
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","get");
        msg.setData(data);
        handler.sendMessage(msg);
    }

    private void AddMyPos(){
        MarkerOptions pos=new MarkerOptions();
        pos.draggable(false);
        pos.position(location);
        pos.title("我的位置");
        aMap.addMarker(pos);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(location));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));

    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")){
                case "ok":{
                    AddMyPos();
                    mLocationClient.stopLocation();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UploadUser();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
                }
                case "get":{
                    pw.dismiss();
                    OpenSearch();
                    break;
                }
                case "dismiss":{
                    po.dismiss();
                    break;
                }
                case "find":{
                    try {
                        AddTargetPos();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }
    };

}
