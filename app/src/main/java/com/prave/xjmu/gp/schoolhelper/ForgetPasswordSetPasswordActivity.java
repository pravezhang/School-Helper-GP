package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgetPasswordSetPasswordActivity extends AppCompatActivity {

    public String tel;
    public TextView forget_set_tele;
    public EditText forget_password1,forget_password2;
    public Button forget_changepassword;
    private ProgressDialog pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_set_password);
        setTitle("设置新密码");
        Bundle b=getIntent().getExtras();
        tel=b.getString("tele");

        forget_set_tele=(TextView)findViewById(R.id.forget_set_tele);
        forget_password1=(EditText)findViewById(R.id.forget_password1);
        forget_password2=(EditText)findViewById(R.id.forget_password2);
        forget_changepassword=(Button)findViewById(R.id.forget_changepassword);
        forget_set_tele.setText("您好， "+tel);
        forget_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String p1=forget_password1.getText().toString();
                final String p2=forget_password2.getText().toString();
                if(!IsPWDVaild(p1)||!IsPWDVaild(p2)) PWDInvaild();
                else if(!p1.equals(p2))
                    NoSame();
                else{


                    pw=new ProgressDialog(ForgetPasswordSetPasswordActivity.this);
                    pw.setMessage("请稍候……");
                    pw.setCancelable(false);
                    pw.setIndeterminate(true);
                    pw.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ChangePWD(p1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
        });

    }

    public boolean IsPWDVaild(String s){
        return s.length()>=6;
    }


    public void ChangePWD(String pwd) throws  Exception{
        String str="http://120.78.219.146/gp/changepassword.php?"+
                "TEL="+tel+
                "&PWD="+pwd;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        int res=Integer.parseInt(inputline);
        if(res==1){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","s");
            msg.setData(data);
            handler.sendMessage(msg);
        }

    }


    public void NoSame(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ForgetPasswordSetPasswordActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("两次输入的密码不一致！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }
    public void PWDInvaild(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ForgetPasswordSetPasswordActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("密码格式不正确！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }
    public void Successful(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ForgetPasswordSetPasswordActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("密码修改完成，请牢记您的新密码。");
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
            String ans = data.getString("value");
            if(ans=="s"){pw.dismiss();  Successful();}
        }
    };


}
