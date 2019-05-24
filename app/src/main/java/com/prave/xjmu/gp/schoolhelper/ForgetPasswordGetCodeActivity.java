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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ForgetPasswordGetCodeActivity extends AppCompatActivity {


    public AutoCompleteTextView forget_tele;
    public ImageView forget_sendcode;
    private ProgressDialog pw;
    public TextView forget_timer_toresend;
    public EditText forget_code;
    public Button forget_confirmcode;
    public int code,t;
    public String tele;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_get_code);
        Bundle b=getIntent().getExtras();
        forget_tele=(AutoCompleteTextView)findViewById(R.id.forget_tele);
        forget_sendcode=(ImageView)findViewById(R.id.forget_sendcode);
        forget_timer_toresend=(TextView)findViewById(R.id.forget_timer_toresend);
        forget_code=(EditText)findViewById(R.id.forget_code);
        forget_confirmcode=(Button)findViewById(R.id.forget_comfirmcode);
        if(b!=null){
            forget_tele.setText(b.getString("tel"));
        }//用于当用户已经登录的时候，想要修改密码，可以直接跳到这里，简化步骤
        forget_sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tele = forget_tele.getText().toString();
                if (tele.length()!=11) {

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","notele");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                else {
                    pw=new ProgressDialog(ForgetPasswordGetCodeActivity.this);
                    pw.setMessage("请稍候……");
                    pw.setCancelable(false);
                    pw.setIndeterminate(true);
                    pw.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CheckIfTeleAuthed(tele);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        forget_confirmcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=forget_code.getText().toString();
                String teles=forget_tele.getText().toString();
                if(input.length()==0||teles.length()==0) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","nocode");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                else{
                    if(Integer.parseInt(input)==code){
                        //go to change password
                        Intent intent =new Intent();
                        intent.putExtra("tele",tele);
                        intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.ForgetPasswordSetPasswordActivity");
                        startActivity(intent);
                        finish();
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

    public void CheckIfTeleAuthed(String s) throws  Exception{
        String str="http://120.78.219.146/gp/checkauthed.php?TEL="
                +s;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        int res=Integer.parseInt(inputline);
        if(res==0) { //该手机号没有验证过
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","na");
            msg.setData(data);
            handler.sendMessage(msg);
        }
        else{
            code=(int)(100000+Math.random()*(999999-100000+1));
            SendCode(tele,code);
        }

    }

    public void SendCode(String tele,int code) throws Exception{
        String str="http://api.sms.cn/sms/" +
                "?ac=send&uid=z534849692&pwd=a0f10c1b1567696f2ca15874725fcf60" +
                "&template=417419" +
                "&mobile="+tele+"&content={\"code\":\""+
                String.valueOf(code)+"\"}";

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
                new AlertDialog.Builder(ForgetPasswordGetCodeActivity.this);
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
                new AlertDialog.Builder(ForgetPasswordGetCodeActivity.this);
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
                new AlertDialog.Builder(ForgetPasswordGetCodeActivity.this);
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
                new AlertDialog.Builder(ForgetPasswordGetCodeActivity.this);
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
        forget_sendcode.setVisibility(View.GONE);
        forget_timer_toresend.setVisibility(View.VISIBLE);
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
        forget_timer_toresend.setText(String.valueOf(t));
    }

    public void TimeUp(){
        forget_sendcode.setVisibility(View.VISIBLE);
        forget_timer_toresend.setVisibility(View.GONE);
    }



    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String ans=data.getString("value");
            switch (ans){
                case "na":{
                    pw.dismiss();
                    NoAuthed();break;
                }
                case "sent":{
                    pw.dismiss();
                    SendSuccess();break;
                }
                case "notele":{
                    NoTele();break;
                }
                case "nocode":{
                    NoCode();break;
                }
                case "wrong":{
                    Wrong();break;
                }
                case "--":{
                    Minus();break;
                }
                case "over":{
                    TimeUp();break;
                }

            }

        }
    };

}
