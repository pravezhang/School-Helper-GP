package com.prave.xjmu.gp.schoolhelper;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class HelloViewActivity extends AppCompatActivity {


    public String userid,utype,savefolder,factors;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hello_view);
        Log.d("AAA","AAA");
        savefolder= Environment.getExternalStorageDirectory()+"/schoolhelper";
        Timer timer=new Timer();
        int flag=1;//flag=0表新用户，1表老用户
        try {
            ReadUser();
        } catch (Exception e) {
            e.printStackTrace();
            flag=0;
        }
        Intent intent=new Intent();
        if(flag==0) {
            Log.d("AAA","flag0");
            //jump to login
            intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.LoginActivity");
            startActivity(intent);
            finish();
        }
        else{
            if(userid.equals("")) {
                Log.d("AAA","uid0");
                //null userid
                intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.LoginActivity");
                startActivity(intent);
                finish();
            }
            else{
                //login as old user
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetInfo("http://120.78.219.146/gp/login_old.php?UID="+userid+"&UTYPE="+utype+"&AASFAFSA=");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //intent
            }
        },1000);

    }


    public void ReadUser() throws Exception{
        File file=new File(savefolder+"/user.dat");
        FileInputStream fileInputStream=new FileInputStream(file);
        int length = fileInputStream.available();
        byte[] bytes =new byte[length];
        fileInputStream.read(bytes);
        String all=new String(bytes,"UTF-8");
        String[] string=all.split("；");
        userid=string[0];
        utype=string[1];
        fileInputStream.close();
        Log.d("AAA","read uid "+userid+",utype="+utype);
    }

    public void GetInfo(String str) throws Exception{
        Log.d("ASAFAF",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        factors=inputline;
        Log.d("AAA","factors="+inputline);
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","got");
        msg.setData(data);
        handler.sendMessage(msg);
    }



    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.getString("value")=="got"){
                Intent intent=new Intent();
                intent.putExtra("factors",factors);//跳转到登陆后界面，传送信息过去
                Log.d("AAAA",factors);
                intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.LoggedActivity");
                startActivity(intent);
                finish();
            }
        }
    };





}
