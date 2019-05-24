package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.ListViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoggedActivity extends AppCompatActivity{

    private ImageButton navi_func,navi_forum,navi_info;
    private LinearLayout logged_content,logged_layout;
    private View V1,V2,V3;
    private long exitTime=0;
    private Thread GetChatsThread;
    private int MAXID=0;
    private char authed;
    private EditText chatting_new_word;
    private String nowname="";
    private int nowexp=0,nowST=-1;
    private ProgressDialog pw;
    private ListView chatting_lv;
    private Button chatting_new_submit;
    public String factors,savefolder;
    private String[] allchats;
    private List<Item_Chatting> chatstoput=new ArrayList<>();
    private Adapter_Chattings adapter_c;
    private LinearLayout logged_tipscontent;
    private TextView logged_tips,logged_navi_1,logged_navi_2,logged_navi_3;
    private ImageView logged_closetips;
    private JSONObject data;
    private Timer chatting_getchats=new Timer();

    private View.OnClickListener click1,click3,click2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        savefolder= Environment.getExternalStorageDirectory()+"/schoolhelper";
        navi_func=(ImageButton)findViewById(R.id.logged_navi_func);
        navi_forum=(ImageButton)findViewById(R.id.logged_navi_forum);
        navi_info=(ImageButton)findViewById(R.id.logged_navi_info);
        logged_content=(LinearLayout)findViewById(R.id.logged_content);
        logged_layout=(LinearLayout)findViewById(R.id.logged_layout);
        logged_tipscontent=(LinearLayout)findViewById(R.id.logged_tipscontent);
        logged_tips=(TextView)findViewById(R.id.logged_tips);
        logged_closetips=(ImageView)findViewById(R.id.logged_closetips);
        logged_navi_1=(TextView)findViewById(R.id.logged_navi_1);
        logged_navi_3=(TextView)findViewById(R.id.logged_navi_3);
        logged_navi_2=(TextView)findViewById(R.id.logged_navi_2);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        V1 = layoutInflater.inflate(R.layout.view_nav_function, null);
        V2 = layoutInflater.inflate(R.layout.view_nav_chatting, null);
        V3 = layoutInflater.inflate(R.layout.view_nav_information, null);
        logged_navi_1.setTextColor(Color.parseColor("#F66B3C"));
        logged_navi_2.setTextColor(Color.parseColor("#888888"));
        logged_navi_3.setTextColor(Color.parseColor("#888888"));
        logged_layout.setBackground(getResources().getDrawable(R.drawable.bg_blue_2));
        logged_content.addView(V1);

        click1=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击了“功能” 按钮
                navi_func.setBackground(getResources().getDrawable(R.drawable.navi_func_ing));
                navi_forum.setBackground(getResources().getDrawable(R.drawable.navi_forum_ed));
                navi_info.setBackground(getResources().getDrawable(R.drawable.navi_info_ed));
                logged_content.removeAllViews();
                logged_navi_1.setTextColor(Color.parseColor("#F66B3C"));
                logged_navi_2.setTextColor(Color.parseColor("#888888"));
                logged_navi_3.setTextColor(Color.parseColor("#888888"));
                logged_layout.setBackground(getResources().getDrawable(R.drawable.bg_blue_2));
                logged_content.addView(V1);
                setListeners(1);
                navi_func.setOnClickListener(null);

            }
        };


        click2=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击了“聊天/FORUM” 按钮
                navi_func.setBackground(getResources().getDrawable(R.drawable.navi_func_ed));
                navi_forum.setBackground(getResources().getDrawable(R.drawable.navi_forum_ing));
                navi_info.setBackground(getResources().getDrawable(R.drawable.navi_info_ed));
                logged_content.removeAllViews();
                logged_navi_1.setTextColor(Color.parseColor("#888888"));
                logged_navi_2.setTextColor(Color.parseColor("#BCDD41"));
                logged_navi_3.setTextColor(Color.parseColor("#888888"));
                logged_layout.setBackground(getResources().getDrawable(R.drawable.pica));
                logged_content.addView(V2);
                setListeners(2);
                navi_forum.setOnClickListener(null);
                navi_func.setOnClickListener(click1);
                navi_info.setOnClickListener(click3);

            }
        };

        click3=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击了“个人信息” 按钮
                navi_func.setBackground(getResources().getDrawable(R.drawable.navi_func_ed));
                navi_forum.setBackground(getResources().getDrawable(R.drawable.navi_forum_ed));
                navi_info.setBackground(getResources().getDrawable(R.drawable.navi_info_ing));
                logged_content.removeAllViews();
                logged_navi_1.setTextColor(Color.parseColor("#888888"));
                logged_navi_2.setTextColor(Color.parseColor("#888888"));
                logged_navi_3.setTextColor(Color.parseColor("#20618C"));
                logged_layout.setBackground(getResources().getDrawable(R.drawable.pic3));
                logged_content.addView(V3);
                setListeners(3);
                navi_info.setOnClickListener(null);
                navi_func.setOnClickListener(click1);
                navi_forum.setOnClickListener(click2);
            }
        };


        setListeners(1);

        //Exact view's onclicklisteners are put into setListeners()



        //now is tele auth tips
        try {
            data=new JSONObject(factors);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        authed='0';
        try {
            authed=data.getString("TELEAUTHED").toCharArray()[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(authed=='0') logged_tipscontent.setVisibility(View.VISIBLE);
        logged_tips.setText("您还没有验证并绑定手机号，快来绑定~  ");
        logged_closetips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logged_tipscontent.setVisibility(View.GONE);
            }
        });

        logged_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.AuthTelephoneActivity");
                String uid="";
                logged_tipscontent.setVisibility(View.GONE);
                try {
                    uid=data.getString("USERID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("uid",uid);//要注意 绑定手机号的时候传过去的是ID即学号或者职工号啥的，不是手机号
                startActivity(intent);
            }
        });


    }


    public  void setListeners(int vid){
        switch (vid){
            case 1:{
                navi_func.setOnClickListener(null);
                navi_forum.setOnClickListener(click2);
                navi_info.setOnClickListener(click3);
                //Declare views
                ImageView func_map,func_schoolcalendar,
                        func_twohand,func_lostfound,
                        func_notices,func_schedule;
                //find view at View function:
                func_map=(ImageView)V1.findViewById(R.id.func_map);
                func_schoolcalendar=(ImageView)V1.findViewById(R.id.func_schoolcalendar);
                func_twohand=(ImageView)V1.findViewById(R.id.func_twohand);
                func_lostfound=(ImageView)V1.findViewById(R.id.func_lostfound);
                func_notices=(ImageView)V1.findViewById(R.id.func_notices);
                func_schedule=(ImageView)V1.findViewById(R.id.func_schedule);

                //set listeners
                func_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("127","Got click ");
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),MapActivity.class);
                        intent.putExtra("factors",factors);
                        //map itself need not factors
                        //it is used to record who opened this page,and where exactly he is
                        startActivity(intent);
                    }
                });
                func_lostfound.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),LostFoundActivity.class);
                        intent.putExtra("factors",factors);
                        intent.putExtra("all",true);
                        startActivity(intent);
                    }
                });
                func_notices.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),NoticesActivity.class);
                        //intent.putExtra("factors",factors);
                        startActivity(intent);
                    }
                });
                func_schoolcalendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),SchoolCalendarActivity.class);
                        //intent.putExtra("factors",factors);
                        startActivity(intent);
                    }
                });
                func_twohand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),TwoHandActivity.class);
                        intent.putExtra("factors",factors);
                        intent.putExtra("all",true);
                        startActivity(intent);
                    }
                });
                func_schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),ScheduleActivity.class);
                        intent.putExtra("factors",factors);
                        startActivity(intent);
                    }
                });


                break;
            }
            case 2:{
                navi_func.setOnClickListener(click1);
                navi_forum.setOnClickListener(null);
                navi_info.setOnClickListener(click3);

                // Declare views
                // AND
                // find views at View forum:
                chatting_lv=(ListView)V2.findViewById(R.id.chatting_lv);
                chatting_new_word=(EditText)V2.findViewById(R.id.chatting_new_word);
                chatting_new_submit=(Button)V2.findViewById(R.id.chatting_new_submit);

                pw = new ProgressDialog(LoggedActivity.this);
                pw.setMessage("请稍候，正在更新消息……");
                pw.setCancelable(false);
                pw.setIndeterminate(true);
                pw.show();
                GetChatsThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetChats(666,"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                GetChatsThread.start();



                //refresh chattings per 1.5 s
                chatting_getchats.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            CheckChats();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },0,20000);
                //每20秒更新一次消息
                //set listeners

                chatting_new_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String words=chatting_new_word.getText().toString();
                        if(authed!='0'){
                            if(!words.equals("")) {
                                //禁止发言5秒
                                Message msg = new Message();
                                Bundle dataa = new Bundle();
                                dataa.putString("value","disable");
                                msg.setData(dataa);
                                handler.sendMessage(msg);
                                Timer tr=new Timer();
                                tr.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putString("value","enable");
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }
                                },5000);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            AddChats(words);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                Item_Chatting ic=new Item_Chatting();
                                ic.setST(0);
                                try {
                                    ic.setLevel("LV. "+CalculateLevel(data.getInt("EXP")));
                                    ic.setName(data.getString("NAME"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ic.setId("-1");
                                ic.setWord(words);
                                ic.setTime(nowtime());
                                chatstoput.add(ic);
                                msg = new Message();
                                Bundle dataq = new Bundle();
                                dataq.putString("value","updatelv");
                                msg.setData(dataq);
                                handler.sendMessage(msg);
                            }
                            else{
                                Empty();
                            }
                        }
                        else{
                            NotAuthed();
                        }
                    }
                });

                chatting_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent =new Intent();
                        String TEL="";
                        Item_Chatting item=chatstoput.get(position);
                        String ID=item.getId();
                        if(ID.equals("-1")) {
                            intent.putExtra("SELF",1);
                            try {
                                TEL = data.getString("TELEPHONE");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //这个position就是它在allchats里的positon
                        else{
                            intent.putExtra("SELF",0);
                            JSONObject js= null;
                            try {
                                js = new JSONObject(allchats[position]);
                                TEL=js.getString("TELEPHONE");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        intent.putExtra("TEL",TEL);
                        intent.setClass(getApplicationContext(),MyChatsActivity.class);
                        startActivity(intent);
                    }
                });
                chatting_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        final AlertDialog.Builder del=new AlertDialog.Builder(LoggedActivity.this);
                        del.setTitle("提示");
                        del.setMessage("是否删除这条消息？");
                        del.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatstoput.remove(position);
                                adapter_c=new Adapter_Chattings(LoggedActivity.this,chatstoput);
                                chatting_lv.setAdapter(adapter_c);
                            }
                        });
                        del.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        del.show();
                        return true;
                    }
                });

                break;

            }
            case 3:{
                navi_func.setOnClickListener(click1);
                navi_forum.setOnClickListener(click2);
                navi_info.setOnClickListener(null);
                //Declare views
                TextView info_gotoinfo,
                        info_name,
                        info_mylostfound,
                        info_mytwohand,
                        info_myforum,
                        info_about,
                        info_exit;

                //find views at View information:
                info_gotoinfo=(TextView)findViewById(R.id.info_gotoinfo);
                info_name=(TextView)findViewById(R.id.info_name);
                info_mylostfound=(TextView)findViewById(R.id.info_mylostfound);
                info_mytwohand=(TextView)findViewById(R.id.info_mytwohand);
                info_myforum=(TextView)findViewById(R.id.info_myforum);
                info_about=(TextView)findViewById(R.id.info_about);
                info_exit=(TextView)findViewById(R.id.info_exit);
                String name="";
                try {
                    name=data.getString("NAME");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(name.length()>4)
                    info_name.setText(name);
                else
                    info_name.setText("　"+name);


                //set listeners
                info_gotoinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //go to info edit
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),MyInfoActivity.class);
                        intent.putExtra("factors",factors);
                        startActivity(intent);

                    }
                });
                info_mylostfound.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),LostFoundActivity.class);
                        intent.putExtra("factors",factors);
                        intent.putExtra("all",false);
                        startActivity(intent);
                        //go to mylostfound
                    }
                });
                info_mytwohand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),TwoHandActivity.class);
                        intent.putExtra("factors",factors);
                        intent.putExtra("all",false);
                        startActivity(intent);
                        //go to mytwohand
                    }
                });
                info_myforum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),MyChatsActivity.class);
                        try {
                            intent.putExtra("TEL",data.getString("TELEPHONE"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("SELF",1);
                        startActivity(intent);
                        //go to myforum
                    }
                });
                info_about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //go to about
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),AboutActivity.class);
                        startActivity(intent);
                    }
                });
                info_exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askExit();
                    }
                });
                break;
            }
        }

    }


    private void CheckChats() throws Exception{
        String str="http://120.78.219.146/gp/checknewchat.php?ID="+String.valueOf(MAXID);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        if(inputline.toCharArray()[0]=='1') {
            if(!GetChatsThread.isAlive())//上次没更新完，就不执行更新
                GetChats(333,"");
        }
    }

    private void GetTargetNameAndExp(String tel)throws Exception{
        //start new application
        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String dburl = "jdbc:mysql://120.78.219.146:3306/w5txq47xzc_c";
        String user = "RemoteUser";
        String password = "uiGhGigi2uii33r";
        Class.forName(driver);
        //1.getConnection()方法，连接MySQL数据库！！
        con = DriverManager.getConnection(dburl,user,password);
        Statement statement = con.createStatement();
        String sql1 = "select * from gp_Students where TELEPHONE="+tel;
        String sql2 = "select * from gp_Teachers where TELEPHONE="+tel;
        ResultSet rs = statement.executeQuery(sql1);
        nowST=1;
        int flag=0;
        if(!rs.next()) {
            rs = statement.executeQuery(sql2);
            nowST = 2;
            flag=1;
        }
        if(flag==0) rs.previous();
        if(rs.next()){
            nowname=rs.getString("NAME");
            nowexp=rs.getInt("EXP");
        }
        //ends


        /*
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
        nowST=Integer.parseInt(res[0]);
        JSONObject target=new JSONObject(res[1]);
        if(target.getString("TELEPHONE").equals(data.getString("TELEPHONE"))) nowST=0;
        nowname=target.getString("NAME");
        nowexp=target.getInt("EXP");
        */
    }

    private void GetChats(int method ,String tel) throws Exception{
        /*NOW METHOD START


        //start new application
        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String dburl = "jdbc:mysql://120.78.219.146:3306/w5txq47xzc_c";
        String user = "RemoteUser";
        String password = "uiGhGigi2uii33r";
        Class.forName(driver);
        //1.getConnection()方法，连接MySQL数据库！！
        con = DriverManager.getConnection(dburl,user,password);
        Statement statement = con.createStatement();
        String sql = "select * from gp_chattings ";
        switch (method){
            case 0:{
                sql+="where TELEPHONE="+tel+" order by ID desc limit 0,25";
                break;
            }
            case 1:{
                sql+="where STATUS=1 and TELEPHONE="+tel+" order by ID desc limit 0,25";
                break;
            }
            default:{
                sql+="where STATUS=1 order by ID desc limit 0,25";
                break;
            }
        }
        ResultSet rs = statement.executeQuery(sql);
        int i=0;
        rs.last();
        Log.d("Trya1",String.valueOf(rs.getRow()));
        allchats=new String[rs.getRow()];

        rs.first();
        while(rs.next()){
            JSONObject js=new JSONObject();
            js.put("ID",rs.getString("ID"));
            js.put("TELEPHONE",rs.getString("TELEPHONE"));
            js.put("TIME",rs.getString("TIME"));
            js.put("WORD",rs.getString("WORD"));
            Log.d("NOW JSON IS : ",js.toString());
            allchats[i]=js.toString();
            i++;
        }
        rs.close();
        con.close();
        //new application end



        NEW METHOD END*/


        Log.d("FEB7","获取一次CHARS，此时MAXID="+String.valueOf(MAXID));
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
        allchats=allres[0].split("☆");
        //get done. call handler to fill the listview



        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","fill");
        msg.setData(data);
        handler.sendMessage(msg);
    }


    private void FillLV()throws Exception{
        JSONObject it=null;
        int length=allchats.length,i=length-1;
        chatstoput.clear();
        it = new JSONObject(allchats[0]);
        MAXID=it.getInt("ID");
        while(i>=0) {
            it = new JSONObject(allchats[i]);
            final String tel=it.getString("TELEPHONE");
            Item_Chatting ic=new Item_Chatting();
            Thread gets =new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GetTargetNameAndExp(tel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            gets.start();
            while(gets.isAlive());
            ic.setST(nowST);
            ic.setName(nowname);
            ic.setId(it.getString("ID"));
            ic.setTime(it.getString("TIME"));
            ic.setWord(it.getString("WORD"));
            ic.setLevel("Lv. "+CalculateLevel(nowexp));
            chatstoput.add(ic);
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

    private void AddChats(String words)throws Exception{
        String tel=data.getString("TELEPHONE");
        String str="http://120.78.219.146/gp/addchats.php?TEL="+tel
                +"&WORD="+words;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
    }

    public void NotAuthed(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoggedActivity.this);
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


    private String nowtime(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }


    private void askExit(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoggedActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您确定要注销账号吗？保存的密码将会被清除。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DeleteUser();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        normalDialog.show();
    }


    public void DeleteUser() throws IOException {
        File folder=new File(savefolder);
        File file=new File(savefolder+"/user.dat");
        if(file.exists()) file.delete();
        Intent intent=new Intent();
        intent.setClass(this,HelloViewActivity.class);
        startActivity(intent);
        finish();
    }


    public void Empty(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoggedActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("不可以发送空消息！请重试");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }




    //double click to exit
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")) {
                case "fill": {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FillLV();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                }
                case "disable":{
                    chatting_new_submit.setEnabled(false);
                    chatting_new_word.setText("");
                    break;
                }
                case "enable":{
                    chatting_new_submit.setEnabled(true);
                    break;
                }
                case "updatelv":{
                    if(pw.isShowing()) pw.dismiss();
                    adapter_c=new Adapter_Chattings(LoggedActivity.this,chatstoput);
                    chatting_lv.setAdapter(adapter_c);
                    chatting_lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    break;
                }
            }
        }
    };
}
