package com.prave.xjmu.gp.schoolhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TwoHandActivity extends AppCompatActivity {

    private ListView twohand_listview;
    private Adapter_Twohands a_twohands;
    private ProgressDialog pw;
    private Spinner twohand_search_spinner;
    private EditText twohand_search_name;
    private ImageButton twohand_search_search;
    private Button twohand_publish;
    private List<Item_Twohands> twohandslist=new ArrayList<>();
    private String[] twohands;
    private String factors;
    private boolean all=false;
    private String UN="";
    private List<String> spinner_list=new ArrayList<>();
    private ArrayAdapter<String> spinner_aa;
    private int nowtype=0,opened=-1;
    //默认不来自Logged，logged的传过来的是true
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_hand);
        Bundle b=getIntent().getExtras();
        all=b.getBoolean("all");
        factors=b.getString("factors");
        if(all) setTitle("二手市场 - 所有物品");
        else setTitle("二手市场 - 我的发布");
        twohand_listview=(ListView)findViewById(R.id.twohand_listview);
        twohand_search_spinner=(Spinner)findViewById(R.id.twohand_search_spinner);
        twohand_search_name=(EditText)findViewById(R.id.twohand_search_name);
        twohand_search_search=(ImageButton)findViewById(R.id.twohand_search_search);
        twohand_publish=(Button)findViewById(R.id.twohand_publish);
        spinner_list.add("[所有物品]");
        spinner_list.add("[书籍/资料]");
        spinner_list.add("[电子/电器]");
        spinner_list.add("[日用/出行]");
        spinner_list.add("[ 其他 ]");
        spinner_aa=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,spinner_list);
        twohand_search_spinner.setAdapter(spinner_aa);
        twohand_search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nowtype=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nowtype=0;
            }
        });


        pw=new ProgressDialog(TwoHandActivity.this);
        pw.setMessage("请稍候……");
        pw.setCancelable(false);
        pw.setIndeterminate(true);
        pw.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(all)
                        GetTwoHands(nowtype,"",0);
                    else
                        GetTwoHands(nowtype,"",1);
                } catch (Exception e) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","noinfo");
                    msg.setData(data);
                    handler.sendMessage(msg);
                    e.printStackTrace();
                    e.printStackTrace();
                }
            }
        }).start();

        twohand_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                opened=position;
                try {
                    OpenDetails(position);
                } catch (Exception e) {
                    e.printStackTrace();
                    PleaseWait();
                }
            }
        });

        twohand_search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String neirong=twohand_search_name.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(all)
                                GetTwoHands(nowtype,neirong,0);
                            else
                                GetTwoHands(nowtype,neirong,1);
                        } catch (Exception e) {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value","noinfo");
                            msg.setData(data);
                            handler.sendMessage(msg);
                            e.printStackTrace();
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        twohand_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int istelauthed=0;
                try {
                    JSONObject j=new JSONObject(factors);
                    istelauthed=j.getInt("TELEAUTHED");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (istelauthed==1) {
                    Intent intent =new Intent();
                    intent.setClass(getApplicationContext(),TwoHandAddActivity.class);
                    intent.putExtra("factors",factors);
                    startActivityForResult(intent,666);
                }
                else{
                    NotAuthed();
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
        else if(resultCode==888){
            SuccessAdd();
            Bundle bundle=data.getExtras();
            String get=bundle.getString("data");
            String name="",type="",des="",price="",tel="";
            int te=0;
            JSONObject j=null;
            try {
                j=new JSONObject(get);
                te=j.getInt("type");
                name=j.getString("name");
                des=j.getString("describe");
                price=j.getString("price");
                tel=j.getString("tel");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (te){
                case 1:{
                    type="[书籍/资料]";
                    break;
                }
                case 2:{
                    type="[电子/电器]";
                    break;
                }
                case 3:{
                    type="[日用/出行]";
                    break;
                }
                case 4:{
                    type="[ 其他 ]";
                }
            }

            AddtoList(type,name,price);
        }

    }

    public void NoInfo(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
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
    private void OpenDetails(int pos) throws Exception {
        UN="";
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.view_twohand_detail, null);
        TextView twohand_detail_type_and_name=(TextView)view.findViewById(R.id.twohand_detail_type_and_name);
        TextView twohand_detail_time=(TextView)view.findViewById(R.id.twohand_detail_time);
        TextView twohand_detail_describe=(TextView)view.findViewById(R.id.twohand_detail_describe);
        TextView twohand_detail_price=(TextView)view.findViewById(R.id.twohand_detail_price);
        TextView twohand_detail_tel=(TextView)view.findViewById(R.id.twohand_detail_tel);
        TextView twohand_detail_uname=(TextView)view.findViewById(R.id.twohand_detail_uname);
        LinearLayout selflayout=(LinearLayout)view.findViewById(R.id.twohand_detail_selflayout);
        Button self_update=(Button)view.findViewById(R.id.twohand_detail_self_update);
        Button self_delete=(Button)view.findViewById(R.id.twohand_detail_self_delete);
        if(!all) {
            JSONObject js=new JSONObject(twohands[opened]);
            int status=js.getInt("STATUS");
            if(status==1){
                selflayout.setVisibility(View.VISIBLE);
                self_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    UpdateTime(opened);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                self_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DeleteThis(opened);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        }
        JSONObject thisdata=new JSONObject(twohands[pos]);
        final String tel=thisdata.getString("TELEPHONE");
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetUserName(tel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        while(t.isAlive());
        //UN GOT
        String typetext="";
        switch (thisdata.getInt("TYPE")){
            case 1:{
                typetext="[书籍/资料]";
                break;
            }
            case 2:{
                typetext="[电子/电器]";
                break;
            }
            case 3:{
                typetext="[日用/出行]";
                break;
            }
            case 4:{
                typetext="[ 其他 ]";
                break;
            }
        }
        twohand_detail_type_and_name.setText(typetext+"\n"+thisdata.getString("NAME"));
        twohand_detail_time.setText(thisdata.getString("TIME"));
        twohand_detail_describe.setText(thisdata.getString("DESCRIBE"));
        twohand_detail_price.setText("￥"+thisdata.getString("PRICE"));
        twohand_detail_tel.setText(tel);
        twohand_detail_uname.setText(UN);
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



    public void PleaseWait(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
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





    private void GetTwoHands(int type,String name,int self) throws Exception{
        //type : 0=all,1=books,2=eletronic,3=daily,4=others
        //name : search name

        //for the first get,type=0,name=""
        //for the further search,type and name are useful
        String str="";
        if(self!=1)
            str="http://120.78.219.146/gp/gettwohands.php?TYPE="+String.valueOf(type)
                      +"&NAME="+name+"&SELF="+String.valueOf(self);
        else {
            JSONObject js=new JSONObject(factors);
            String tel=js.getString("TELEPHONE");
            str = "http://120.78.219.146/gp/gettwohands.php?TYPE=" + String.valueOf(type)
                    + "&NAME=" + name + "&SELF=" + String.valueOf(self) + "&TEL="+tel;
        }
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
        if(allres.length==0){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","empty");
            msg.setData(data);
            handler.sendMessage(msg);
        }
        else {
            twohands = allres[0].split("☆");
            //get done. call handler to fill the listview
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "fill");
            msg.setData(data);
            handler.sendMessage(msg);
        }



    }

    private void FillList() throws Exception{
        JSONObject it=null;
        int length=twohands.length,i=-1;
        twohandslist.clear();
        while(i++<length-1) {
            Log.d("131","NOW 2hand IS : "+twohands[i]);
            it = new JSONObject(twohands[i]);
            Item_Twohands in=new Item_Twohands();
            in.setTwohand_name(it.getString("NAME"));
            in.setTwohand_price(it.getString("PRICE"));
            in.setStatus(it.getInt("STATUS"));
            switch (it.getInt("TYPE")){
                case 1:{
                    in.setTwohand_type("[书籍/资料]");
                    break;
                }
                case 2:{
                    in.setTwohand_type("[电子/电器]");
                    break;
                }
                case 3:{
                    in.setTwohand_type("[日用/出行]");
                    break;
                }
                case 4:{
                    in.setTwohand_type("[ 其他 ]");
                    break;
                }
            }
            twohandslist.add(in);
        }
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","fillfinish");
        msg.setData(data);
        handler.sendMessage(msg);

    }


    private void FillListView(){
        a_twohands=new Adapter_Twohands(this,twohandslist);
        twohand_listview.setAdapter(a_twohands);
    }


    public void NoThings(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("没有更多二手物品可展示，请稍候再来。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    public void CancelAdd(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您取消了发布二手物品。");
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
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("成功发布一条二手物品，请稍后查看！。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void AddtoList(String type,String name,String price){
        Item_Twohands add=new Item_Twohands();
        add.setStatus(1);
        add.setTwohand_type(type);
        add.setTwohand_price(price);
        add.setTwohand_name(name);
        twohandslist.add(add);
        a_twohands=new Adapter_Twohands(getApplicationContext(),twohandslist);
        twohand_listview.setAdapter(a_twohands);
    }


    public void NotAuthed(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您还没有验证手机号，请验证后再尝试发布二手物品。");
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

    private void UpdateOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("时间更新成功！请稍后查看。");
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

    private void DeleteOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("成功删除这个物品！请稍后查看。");
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

    private void UpdateTime(int IDatList) throws Exception{
        JSONObject jjj=new JSONObject(twohands[IDatList]);
        int ID=jjj.getInt("ID");
        String str="http://120.78.219.146/gp/updatetwtime.php?ID="+String.valueOf(ID);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        if(inputline.toCharArray()[0]=='1'){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","updatetime");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    }


    private void DeleteThis(int IDatList) throws Exception{
        JSONObject jjj=new JSONObject(twohands[IDatList]);
        int ID=jjj.getInt("ID");
        String str="http://120.78.219.146/gp/updatetwstatus.php?ID="+String.valueOf(ID);
        Log.d("131",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        if(inputline.toCharArray()[0]=='1'){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","delete");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")){
                case "fill":{
                    try {
                        FillList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "fillfinish":{
                    pw.dismiss();
                    FillListView();
                    break;
                }
                case "empty":{
                    NoThings();
                    break;
                }
                case "updatetime":{
                    UpdateOK();
                    break;
                }
                case "delete":{
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
