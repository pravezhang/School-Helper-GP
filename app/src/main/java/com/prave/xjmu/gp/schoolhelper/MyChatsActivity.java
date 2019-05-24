package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyChatsActivity extends AppCompatActivity {

    private String TEL;
    private int nowST=0,nowexp;
    private String nowname;
    private Adapter_Chattings ac;
    private String[] servergets;
    private List<Item_Chatting> mychats=new ArrayList<>();
    private ListView chatting_mychat_lv;
    private ProgressDialog pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);
        chatting_mychat_lv=(ListView)findViewById(R.id.chatting_mychat_lv);
        Bundle b=getIntent().getExtras();
        TEL=b.getString("TEL");
        nowST=b.getInt("SELF");
        Log.d("FEB7",TEL);

        pw = new ProgressDialog(MyChatsActivity.this);
        pw.setMessage("请稍候，正在更新消息……");
        pw.setCancelable(false);
        pw.setIndeterminate(true);
        pw.show();

        Thread gets =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetTargetNameAndExp(TEL);
                } catch (Exception e) {
                    if(pw.isShowing()) pw.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","noinfo");
                    msg.setData(data);
                   handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        });
        gets.start();
        while(gets.isAlive());
        if(nowST==0)
            setTitle("我的发言");
        else
            setTitle(nowname+"的发言");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetChats(nowST,TEL);
                } catch (Exception e) {
                    if(pw.isShowing()) pw.dismiss();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","noinfo");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void GetChats(int method ,String tel) throws Exception{
        //method=0  查自己的
        //method=1  查某人的
        //method=其他 查所有60条
        String str="http://120.78.219.146/gp/getchattings.php?ALL="+String.valueOf(method)
                +"&TEL="+tel;
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
        servergets=allres[0].split("☆");
        //get done. call handler to fill the listview
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","fill");
        msg.setData(data);
        handler.sendMessage(msg);
    }


    private void FillLV()throws Exception{
        JSONObject it=null;
        int length=servergets.length,i=length-1;
        mychats.clear();
        it = new JSONObject(servergets[0]);
        while(i>=0) {
            it = new JSONObject(servergets[i]);
            final String tel=it.getString("TELEPHONE");
            Item_Chatting ic=new Item_Chatting();
            ic.setST(nowST);
            ic.setName(nowname);
            ic.setId(it.getString("ID"));
            ic.setTime(it.getString("TIME"));
            ic.setWord(it.getString("WORD"));
            ic.setLevel("Lv. "+CalculateLevel(nowexp));
            mychats.add(ic);
            i--;
        }
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","updatelv");
        msg.setData(data);
        handler.sendMessage(msg);
    }


    private String CalculateLevel(int exp){
        if(exp==0) return "0";
        else if(exp<10) return "1";
        else if(exp<100) return "2";
        else if(exp<1000) return "3";
        else if(exp<10000) return "4";
        else if(exp<100000) return "5";
        else if(exp<1000000) return "6";
        else return "888";

    }

    public void NoInfo(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyChatsActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("暂时没有更多信息，请稍后再试！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        normalDialog.show();
    }

    private void GetTargetNameAndExp(String tel)throws Exception{
        String str="http://120.78.219.146/gp/getusernamebytel.php?TEL="
                +tel;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] res=inputline.split("；");
        if(nowST!=1) //不是自己
        nowST=Integer.parseInt(res[0]);
        else nowST=0;
        JSONObject target=new JSONObject(res[1]);
        nowname=target.getString("NAME");
        nowexp=target.getInt("EXP");
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")) {
                case "fill": {
                    try {
                        FillLV();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "updatelv":{
                    pw.dismiss();
                    ac=new Adapter_Chattings(MyChatsActivity.this,mychats);
                    chatting_mychat_lv.setAdapter(ac);
                    chatting_mychat_lv.setTranscriptMode(ListView.OVER_SCROLL_ALWAYS);
                    break;
                }
                case "noinfo":{
                    NoInfo();
                    break;
                }


            }
        }
    };

}
