package com.prave.xjmu.gp.schoolhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class AuthTelephoneActivity extends AppCompatActivity {

    String uid;
    public AutoCompleteTextView authtele_tele;
    public ImageView authtele_sendcode;
    public TextView authtele_timer_toresend,authtele_tip;
    public EditText authtele_code;
    public Button authtele_confirmcode;
    public int code,t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_telephone);
        setTitle("验证手机号码");
        Bundle b=getIntent().getExtras();
        uid=b.getString("uid");
        authtele_tele=(AutoCompleteTextView)findViewById(R.id.authtele_tele);
        authtele_sendcode=(ImageView) findViewById(R.id.authtele_sendcode);
        authtele_timer_toresend=(TextView) findViewById(R.id.authtele_timer_toresend);
        authtele_code=(EditText) findViewById(R.id.authtele_code);
        authtele_confirmcode=(Button) findViewById(R.id.authtele_comfirmcode);
        authtele_tip=(TextView)findViewById(R.id.authtele_tip);
        authtele_tip.setText("您好， "+uid +"\n 您正在绑定手机号。");
        authtele_sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teles=authtele_tele.getText().toString();
                if(teles.length()!=11)
                    NoTele();
                else
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Send();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
            }
        });


        authtele_confirmcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String input=authtele_code.getText().toString();
                final String teles=authtele_tele.getText().toString();
                if(input.length()==0||teles.length()==0) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","nocode");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                else{
                    if(Integer.parseInt(input)==code){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Auth(uid,teles);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    else{
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("value","wrong");
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }
            }
        });
    }

    public void Send() throws Exception {

        String tele=authtele_tele.getText().toString();
        code=(int)(100000+Math.random()*(999999-100000+1));
        SendCode(uid,tele,code);
    }

    public void Auth(String uid,String tel) throws Exception{
        String str="http://120.78.219.146/gp/authtele.php?"+
                "UID="+uid+
                "&TEL="+tel;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        if(inputline.toCharArray()[0]=='1') {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","success");
            msg.setData(data);
            handler.sendMessage(msg);
        }

    }

    public void SendCode(String uid,String tele,int code) throws Exception{
        String str="http://api.sms.cn/sms/" +
                "?ac=send&uid=z534849692&pwd=a0f10c1b1567696f2ca15874725fcf60" +
                "&template=417420" +
                "&mobile="+tele+"&content={\"code\":\""+
                String.valueOf(code)+"\",\"num\":\""+uid+"\"}";

        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        JSONObject result=new JSONObject(inputline);
        int resnum=result.getInt("stat");
        if(resnum==100){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","sent");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }



    public void NoAuthed(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AuthTelephoneActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您的手机号尚未验证，请联系管理员更改密码。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        normalDialog.show();
    }
    public void NoTele(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AuthTelephoneActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请输入正确的手机号码！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }
    public void NoCode(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AuthTelephoneActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请输入验证码！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }
    public void Wrong(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AuthTelephoneActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("验证码不正确，请重试！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    public void SendSuccess(){
        authtele_sendcode.setVisibility(View.GONE);
        authtele_timer_toresend.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                t=60;
                Timer timer=new Timer();
                //call for minus timer
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        t--;
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("value","--");
                        msg.setData(data);
                        handler.sendMessage(msg);
                        if (t==0) {
                            Message msg2 = new Message();
                            Bundle data2 = new Bundle();
                            data2.putString("value","over");
                            msg2.setData(data2);
                            handler.sendMessage(msg2);
                            this.cancel();
                        }
                    }
                },0,1000);

                //call for set visibility

            }
        }).start();
    }

    public void Minus(){
        authtele_timer_toresend.setText(String.valueOf(t));
    }

    public void TimeUp(){
        authtele_sendcode.setVisibility(View.VISIBLE);
        authtele_timer_toresend.setVisibility(View.GONE);
    }

    public void AuthOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AuthTelephoneActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("手机号验证完成，以后就可以使用手机号登陆了。");
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

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String ans=data.getString("value");
            switch (ans){
                case "na":{     //×
                    NoAuthed();break;
                }
                case "sent":{   // √
                    SendSuccess();break;
                }
                case "notele":{
                    NoTele();break;
                }
                case "nocode":{ // √
                    NoCode();break;
                }
                case "wrong":{  // √
                    Wrong();break;
                }
                case "--":{     // √
                    Minus();break;
                }
                case "over":{   // √
                    TimeUp();break;
                }
                case "success":{
                    AuthOK();break;
                }
                default:{

                }

            }

        }
    };



}
