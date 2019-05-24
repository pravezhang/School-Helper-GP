package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LostFoundActivity extends AppCompatActivity {

    private ListView lostfound_lost_listview,lostfound_found_listview;
    private ArrayList<Item_Lostfounds> losts_list=new ArrayList<>(),
                                        founds_list=new ArrayList<>();
    private Adapter_Lostfounds a_losts,a_founds;
    private TabHost tabHost;
    private ProgressDialog pw;
    private Button lostfound_publish;
    private String factors,UN;
    private boolean all=false;
    private String[] losts,founds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);
        Bundle b=getIntent().getExtras();
        all=b.getBoolean("all");
        factors=b.getString("factors");
        tabHost=(TabHost)findViewById(R.id.tabhost);
        lostfound_publish=(Button)findViewById(R.id.lostfound_publish);



        pw=new ProgressDialog(LostFoundActivity.this);
        pw.setMessage("请稍候……");
        pw.setCancelable(false);
        pw.setIndeterminate(true);
        pw.show();
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("found").setIndicator("失物招领").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("lost").setIndicator("寻物启事").setContent(R.id.tab2));
        tabHost.setCurrentTab(0);
        lostfound_lost_listview=(ListView)findViewById(R.id.lostfound_lost_listview);
        lostfound_found_listview=(ListView)findViewById(R.id.lostfound_found_listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetLostFoundsFromServer();
                } catch (Exception e) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","noinfo");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
        lostfound_found_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {
                    OpenDetail(2,position);
                } catch (Exception e) {
                    e.printStackTrace();
                    PleaseWait();
                }
            }
        });
        lostfound_lost_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {
                    OpenDetail(1,position);
                } catch (Exception e) {
                    e.printStackTrace();
                    PleaseWait();
                }
            }
        });

        lostfound_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int authed=0;
                try {
                    JSONObject jsonObject=new JSONObject(factors);
                    authed=jsonObject.getInt("TELEAUTHED");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(authed==0){
                    NotAuthed();
                }
                else{
                    Intent intent =new Intent();
                    intent.setClass(getApplicationContext(),LostFoundAddActivity.class);
                    intent.putExtra("factors",factors);
                    startActivityForResult(intent,666);
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==555){
            CancelAdd();
        }
        else if(resultCode==888) {
            SuccessAdd();
            Bundle bundle=data.getExtras();
            String get=bundle.getString("data");
            int lostfound=1;
            String thingname="",time="";
            JSONObject j=null;
            try {
                j=new JSONObject(get);
                thingname=j.getString("NAME");
                time=j.getString("TIME");
                lostfound=j.getInt("LF");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            AddtoList(thingname,time,lostfound);

        }
    }


    private void AddtoList(String thingname,String time,int lostfound){
        Item_Lostfounds add=new Item_Lostfounds();
        add.setStatus(1);
        add.setLf_thingname(thingname);
        add.setLf_time(time);
        if(lostfound==1) {
            losts_list.add(add);
            a_losts = new Adapter_Lostfounds(losts_list, getApplicationContext());
            lostfound_lost_listview.setAdapter(a_losts);
        }
        else{
            founds_list.add(add);
            a_founds=new Adapter_Lostfounds(founds_list,getApplicationContext());
            lostfound_found_listview.setAdapter(a_founds);
        }
    }

    public void CancelAdd(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您取消了发布消息。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }


    public void SuccessAdd(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("成功发布一条消息，请稍后查看！。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }



    public void PleaseWait(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("列表同步尚未完成，请稍后再尝试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        normalDialog.show();
    }

    public void NoInfo(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
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

    public void NotAuthed(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您还没有验证手机号，请验证后再尝试发布消息。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),AuthTelephoneActivity.class);
                        String uid="";
                        try {
                            JSONObject js=new JSONObject(factors);
                            uid=js.getString("USERID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                        finish();
                    }
                });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        normalDialog.show();
    }
    private void OpenDetail(int lostfound,int pos) throws Exception{
        UN="";
        String title_lostfoundtime="";
        JSONObject ja=null;
        switch (lostfound){
            case 1:{    //lost
                title_lostfoundtime="丢失时间：";
                ja=new JSONObject(losts[pos]);
                break;
            }
            case 2:{    //found
                title_lostfoundtime="捡到时间：";
                ja=new JSONObject(founds[pos]);
                break;
            }
        }
        final int id=ja.getInt("ID");
        final String tel=ja.getString("TELEPHONE");
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.view_lostfound_detail, null);
        TextView lostfound_detail_thingname=(TextView) view.findViewById(R.id.lostfound_detail_thingname);
        TextView lostfound_detail_uname=(TextView) view.findViewById(R.id.lostfound_detail_uname);
        TextView lostfound_detail_tel=(TextView) view.findViewById(R.id.lostfound_detail_tel);
        TextView lostfound_detail_time_title=(TextView) view.findViewById(R.id.lostfound_detail_time_title);
        TextView lostfound_detail_time=(TextView) view.findViewById(R.id.lostfound_detail_time);
        TextView lostfound_detail_describe=(TextView) view.findViewById(R.id.lostfound_detail_describe);
        LinearLayout lostfound_detail_selflayout=(LinearLayout)view.findViewById(R.id.lostfound_detail_selflayout);
        Button lostfound_detail_self_delete=(Button)view.findViewById(R.id.lostfound_detail_self_delete);
        if(!all) {
            int status=ja.getInt("STATUS");
            if(status==1) {
                lostfound_detail_selflayout.setVisibility(View.VISIBLE);
                lostfound_detail_self_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteConfirm(id);
                    }
                });
                lostfound_detail_thingname.setText(ja.getString("NAME"));
            }
            else{
                lostfound_detail_thingname.setText("[删]"+ja.getString("NAME"));
            }
        }
        else{
            lostfound_detail_thingname.setText(ja.getString("NAME"));
        }
        //get username
        Thread get=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetUserName(tel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        get.start();
        while(get.isAlive()) ;
        lostfound_detail_uname.setText(UN);
        lostfound_detail_tel.setText(tel);
        lostfound_detail_time_title.setText(title_lostfoundtime);
        switch (ja.getInt("AMPM")){
            case 1:{
                lostfound_detail_time.setText(ja.getString("DATE")+" 上午");
                break;
            }
            case 2:{
                lostfound_detail_time.setText(ja.getString("DATE")+" 下午");
                break;
            }
            default:{
                lostfound_detail_time.setText(ja.getString("DATE"));
            }
        }
        lostfound_detail_describe.setText(ja.getString("DESCRIBE"));

        normalDialog.setView(view);
        normalDialog.show();


    }

    private void GetUserName(String tel) throws Exception{
        String str="http://120.78.219.146/gp/getusernamebytel.php?TEL="+tel;
        Log.d("131",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] aaa=inputline.split("；");
        JSONObject j=new JSONObject(aaa[1]);
        UN=j.getString("NAME");
    }



    private void DeleteConfirm(final int id){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要删除这条消息吗？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Delete(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        normalDialog.show();
    }

    private void Delete(int id) throws Exception{
        String str="http://120.78.219.146/gp/updatelfstatus.php?ID="+String.valueOf(id);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","del");
        msg.setData(data);
        handler.sendMessage(msg);
    }



    private void GetLostFoundsFromServer() throws Exception{
        String str;
        if(all){
            str="http://120.78.219.146/gp/getlostfounds.php?SELF=0";
        }
        else{
            JSONObject jb=new JSONObject(factors);
            String tel=jb.getString("TELEPHONE");
            str="http://120.78.219.146/gp/getlostfounds.php?SELF=1&TEL="+tel;
        }
        Log.d("Feb3",str);
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
        losts=allres[0].split("☆");
        founds=allres[1].split("☆");
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","got");
        msg.setData(data);
        handler.sendMessage(msg);
    }


    private void FillList() throws Exception{
        JSONObject it=null;
        int llength=losts.length,i=-1;
        losts_list.clear();
        while(i++<llength-1) {
            it=new JSONObject(losts[i]);
            Item_Lostfounds il=new Item_Lostfounds();
            il.setLf_thingname(it.getString("NAME"));
            il.setStatus(it.getInt("STATUS"));
            switch (it.getInt("AMPM")){
                case 1:{
                    il.setLf_time(it.getString("DATE")+" 上午");
                    break;
                }
                case 2:{
                    il.setLf_time(it.getString("DATE")+" 下午");
                    break;
                }
                default:{
                    il.setLf_time(it.getString("DATE"));
                }
            }
            losts_list.add(il);
        }
        a_losts=new Adapter_Lostfounds(losts_list,getApplicationContext());
        lostfound_lost_listview.setAdapter(a_losts);



        int flength=founds.length;
        i=-1;
        founds_list.clear();
        while(i++<flength-1) {
            it=new JSONObject(founds[i]);
            Item_Lostfounds il=new Item_Lostfounds();
            il.setLf_thingname(it.getString("NAME"));
            il.setStatus(it.getInt("STATUS"));
            switch (it.getInt("AMPM")){
                case 1:{
                    il.setLf_time(it.getString("DATE")+" 上午");
                    break;
                }
                case 2:{
                    il.setLf_time(it.getString("DATE")+" 下午");
                    break;
                }
                default:{
                    il.setLf_time(it.getString("DATE"));
                }
            }
            founds_list.add(il);
        }
        a_founds=new Adapter_Lostfounds(founds_list,getApplicationContext());
        lostfound_found_listview.setAdapter(a_founds);
    }

    private void DeleteOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("成功删除这条消息！请稍后查看。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        normalDialog.setCancelable(false);
        normalDialog.show();

    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")) {
                case "got":{
                    pw.dismiss();
                    try {
                        FillList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "del":{
                    DeleteOK();
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
