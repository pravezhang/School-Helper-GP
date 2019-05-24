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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoticesActivity extends AppCompatActivity {

    private ListView notices_listview;
    private Adapter_Notices a_notices;
    private List<Item_Notices> notices_list=new ArrayList<Item_Notices>();
    private String[] notices;
    private ProgressDialog pw;
    private RadioButton notice_rb_all,notice_rb_sys,notice_rb_jwc;
    private RadioGroup notice_rg;
    private int selecttype=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);
        setTitle("通知公告");
        notices_listview=(ListView)findViewById(R.id.notices_listview);
        notice_rb_all=(RadioButton)findViewById(R.id.notice_rb_all);
        notice_rb_sys=(RadioButton)findViewById(R.id.notice_rb_sys);
        notice_rb_jwc=(RadioButton)findViewById(R.id.notice_rb_jwc);
        notice_rg=(RadioGroup)findViewById(R.id.notice_rg);


        pw=new ProgressDialog(NoticesActivity.this);
        pw.setMessage("请稍候……");
        pw.setCancelable(false);
        pw.setIndeterminate(true);
        pw.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //初次，获取所有通知 type=0
                    GetNotices(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        notices_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    OpenDetails(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        notice_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes final int checkedId) {
                switch (checkedId){
                    case R.id.notice_rb_all:{
                        selecttype=0;
                        break;
                    }
                    case R.id.notice_rb_sys:{
                        selecttype=2;
                        break;
                    }
                    case R.id.notice_rb_jwc:{
                        selecttype=1;
                        break;
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetNotices(selecttype);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

    }

    private void GetNotices(int type) throws Exception{
        String str="http://120.78.219.146/gp/getnotices.php?TYPE="+String.valueOf(type);
        Log.d("131",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] allres=null;
        allres=inputline.split("★");
        //allres[0]内存放所有结果的结合
        notices=allres[0].split("☆");
        //get done. call handler to fill the listview
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","fill");
        msg.setData(data);
        handler.sendMessage(msg);



    }

    private void FillList() throws Exception{
        JSONObject it=null;
        int length=notices.length,i=-1;
        notices_list.clear();
        while(i++<length-1) {
            Log.d("130","NOW NOTICE IS : "+notices[i]);
            it = new JSONObject(notices[i]);
            Item_Notices in=new Item_Notices();
            in.setNotice_time(it.getString("PUBLISHTIME"));
            in.setNotice_publisher(it.getString("PUBLISHER"));
            in.setNotice_title(it.getString("TITLE"));
            notices_list.add(in);
        }
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","fillfinish");
        msg.setData(data);
        handler.sendMessage(msg);

    }


    private void FillListView(){
        a_notices=new Adapter_Notices(this,notices_list);
        notices_listview.setAdapter(a_notices);

    }

    private void OpenDetails(int pos) throws Exception {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(NoticesActivity.this);
        View view=View.inflate(getApplicationContext(),R.layout.view_notice_detail,null);
        //things
        TextView notice_detail_title=(TextView)view.findViewById(R.id.notice_detail_title);
        TextView notice_detail_publisher=(TextView)view.findViewById(R.id.notice_detail_publisher);
        TextView notice_detail_publishtime=(TextView)view.findViewById(R.id.notice_detail_publishtime);
        TextView notice_detail_text=(TextView)view.findViewById(R.id.notice_detail_text);
        WebView notice_detail_web=(WebView)view.findViewById(R.id.notice_detail_web);
        JSONObject thisdata=new JSONObject(notices[pos]);
        String publisher=thisdata.getString("PUBLISHER");
        notice_detail_title.setText(thisdata.getString("TITLE"));
        notice_detail_publishtime.setText(thisdata.getString("PUBLISHTIME"));
        switch (publisher){
            case "[教务通知]":{
                notice_detail_publisher.setText("教务处");
                notice_detail_web.setVisibility(View.VISIBLE);
                notice_detail_web.loadUrl(thisdata.getString("URL"));
                WebSettings set=notice_detail_web.getSettings();
                set.setSupportZoom(true);
                set.setLoadWithOverviewMode(true);
                break;
            }
            case "[系统通知]":{
                notice_detail_publisher.setText("系统管理员");
                notice_detail_text.setVisibility(View.VISIBLE);
                notice_detail_text.setText("　　"+thisdata.getString("CONTENT"));
                break;
            }

        }
        normalDialog.setView(view);
        normalDialog.show();

    }


    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")){
                case "fill":{
                    pw.dismiss();
                    try {
                        FillList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "fillfinish":{
                    FillListView();
                    break;
                }
            }
        }
    };


}
