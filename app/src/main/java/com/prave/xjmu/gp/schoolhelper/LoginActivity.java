package com.prave.xjmu.gp.schoolhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    /*
    *  这里由于是Android版本大于6.0 必须要主动申请权限
    *  以下标记了//&&&//的都是在凡是要调用权限的地方都要用到的
    *  标记以供再次使用。
    * */
    public AutoCompleteTextView login_userid;
    public EditText login_password;
    public CheckBox login_checksave;
    private ProgressDialog pw;
    public Button login_login;
    public TextView login_forgetpassword;
    public String userid,savefolder,factors[];

    private long exitTime = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        savefolder= Environment.getExternalStorageDirectory()+"/schoolhelper";
        login_userid=(AutoCompleteTextView)findViewById(R.id.login_userid);
        login_password=(EditText) findViewById(R.id.login_password);
        login_login=(Button)findViewById(R.id.login_login);
        login_checksave=(CheckBox)findViewById(R.id.login_checksave);
        login_forgetpassword=(TextView)findViewById(R.id.login_forgetpassword);

        login_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.ForgetPasswordGetCodeActivity");
                startActivity(intent);


            }
        });

        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid=login_userid.getText().toString();
                String pwd=login_password.getText().toString();
                int uidtype=isUserIDVaild(uid);
                boolean pwdvaild=isPasswordValid(pwd);
                if(uidtype==0||!pwdvaild) InVaildInput();
                else{


                    pw=new ProgressDialog(LoginActivity.this);
                    pw.setMessage("请稍候……");
                    pw.setCancelable(false);
                    pw.setIndeterminate(true);
                    pw.show();
                    final String url="http://120.78.219.146/gp/login.php?" +
                            "UID="+uid+
                            "&PWD="+pwd+
                            "&UIDTYPE="+uidtype;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AttemptLogin(url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });

    }



    public void WriteUser(String s) throws IOException {
        File folder=new File(savefolder);
        if(!folder.exists())  folder.mkdirs();
        File file=new File(savefolder+"/user.dat");
        if(file.exists()) file.delete();
        if(!file.exists()) file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        byte[] bytes=s.getBytes("UTF-8");
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }


    public int isUserIDVaild(String s){
        int i=0;
        if(s.length()==15) i=1;
        if(s.length()==11) i=2;
        return i;
    }


    public boolean isPasswordValid(String s){
        return s.length()>=4;
    }

    public void AttemptLogin(String str) throws Exception{
        Log.d("123",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        Log.d("123",inputline);
        if(inputline.toCharArray()[0]!='0')         //验证成功
        {
            factors=inputline.split("；");
            JSONObject jsonObject=new JSONObject(factors[1]);
            userid=jsonObject.getString("USERID");
            if(login_checksave.isChecked()){
                try {
                    WriteUser(userid+"；"+factors[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    WriteUser("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","ok");
            msg.setData(data);
            handler.sendMessage(msg);
            Intent intent=new Intent();
            intent.setClassName("com.prave.xjmu.gp.schoolhelper","com.prave.xjmu.gp.schoolhelper.LoggedActivity");//shandiaole
            intent.putExtra("factors",factors[1]);//这个也传的是factors
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

    public void InVaildInput(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoginActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您输入的账号/密码格式不正确，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    public void InVaildCombination(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LoginActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您输入的账号/密码不正确，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }







    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.getString("value")=="wrong")
                InVaildCombination();
            else if(data.getString("value")=="ok")
                pw.dismiss();
        }
    };



}
