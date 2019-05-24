package com.prave.xjmu.gp.schoolhelper;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyInfoActivity extends AppCompatActivity {

    public String factors;

    JSONObject data;
    public EditText info_info_name,info_info_userid,
            info_info_tel,info_info_institute,
            info_info_classs,info_info_birth;
    public RadioButton info_info_gender_male,info_info_gender_female;
    public TextView info_info_topic_userid,info_info_topic_classs;
    public Button info_info_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        setTitle("我的个人信息");
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        info_info_name=(EditText)findViewById(R.id.info_info_name);
        info_info_userid=(EditText)findViewById(R.id.info_info_userid);
        info_info_tel=(EditText)findViewById(R.id.info_info_tel);
        info_info_institute=(EditText)findViewById(R.id.info_info_institute);
        info_info_classs=(EditText)findViewById(R.id.info_info_classs);
        info_info_birth=(EditText)findViewById(R.id.info_info_birth);
        info_info_gender_male=(RadioButton)findViewById(R.id.info_info_gender_male);
        info_info_gender_female=(RadioButton)findViewById(R.id.info_info_gender_female);
        info_info_topic_userid=(TextView)findViewById(R.id.info_info_topic_userid);
        info_info_topic_classs=(TextView)findViewById(R.id.info_info_topic_classs);
        info_info_update=(Button)findViewById(R.id.info_info_update);
        try {
            FillBlanks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //只允许更改电话及生日

        info_info_tel.setOnClickListener(Click);
        info_info_birth.setOnClickListener(Click);
        info_info_name.setOnClickListener(Click);
        info_info_gender_male.setOnClickListener(Click);
        info_info_gender_female.setOnClickListener(Click);
        info_info_userid.setOnClickListener(Click);
        info_info_classs.setOnClickListener(Click);
        info_info_institute.setOnClickListener(Click);
        info_info_update.setOnClickListener(Click);


    }


    View.OnClickListener Click =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.info_info_birth:{
                    info_info_birth.setEnabled(true);
                    info_info_update.setVisibility(View.VISIBLE);
                    break;
                }
                case R.id.info_info_tel:{
                    info_info_tel.setEnabled(true);
                    info_info_update.setVisibility(View.VISIBLE);
                    break;
                }
                case R.id.info_info_update:{
                    //update
                    boolean change=false;
                    try {
                        change=isChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(!change) {
                        NoChanged();
                    }
                    else{
                        final String ntel=info_info_tel.getText().toString();
                        final String nbirth=info_info_birth.getText().toString();
                        boolean can=false;
                        try {
                            can=isNewBirthdayVaild(nbirth)&&isNewTelephoneVaild(ntel);
                        } catch (Exception e) {
                            can=false;
                        }
                        if(!can){
                            NoVaild();
                        }
                        else{
                            //update to server
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UpdateToServer(ntel,nbirth);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    break;
                }
                default:{
                    NoChange();
                }
            }
        }
    };

    boolean isChanged() throws Exception{
        boolean change=false;
        if(data.getString("BRITHDAY")!=info_info_birth.getText().toString())
            change=true;
        if(data.getString("TELEPHONE")!=info_info_tel.getText().toString())
            change=true;
        return change;
    }

    boolean isNewBirthdayVaild(String nb) throws Exception{
        String[] ymd=nb.split("-");
        if(ymd[0].length()!=4)
            return false;
        else {
            int m=Integer.parseInt(ymd[1]);
            if (m>12||m<0) return false;
            else{
                int d=Integer.parseInt(ymd[2]);
                if(d>31||d<0) return false;
            }
        }
        return true;
    }

    boolean isNewTelephoneVaild(String nt){
        return nt.length()==11;
    }


    private void UpdateToServer(String tel,String birthday) throws Exception{
        String str="http://120.78.219.146/gp/updateinfo.php?TEL="+
                tel+"&BDAY="+birthday+"&USERID="+data.getString("USERID");
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
            data.putString("value","updated");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    private void NoChange(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyInfoActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("该内容无法修改，如果有误，请联系管理员。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();

    }

    private void NoChanged(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyInfoActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您没有修改内容，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void NoVaild(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyInfoActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您的输入格式有误，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();

    }

    private void Updated(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyInfoActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您的信息已经修改完成。\n（请注意，手机号修改后需要重新验证。）");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        normalDialog.show();

    }

    private void FillBlanks() throws Exception{
        data=new JSONObject(factors);
        int flag=0;
        String cache="";
        try {
            cache=data.getString("CLASSS");
        } catch (JSONException e) {
            flag=1;
        }
        int gender=data.getInt("GENDER");
        String birthd=data.getString("BRITHDAY");
        Log.d("129","gender:"+String.valueOf(gender));
        Log.d("129","birth:"+birthd);
        switch (flag){
            case 1:{
                //this is teacher
                info_info_name.setText(data.getString("NAME"));
                info_info_userid.setText(data.getString("USERID"));
                info_info_tel.setText(data.getString("TELEPHONE"));
                info_info_institute.setText(data.getString("INSTITUTE"));
                info_info_classs.setText(data.getString("ROOM"));
                info_info_birth.setText(birthd);
                info_info_topic_userid.setText("工号：　");
                info_info_topic_classs.setText("教研室：");
                break;
            }
            default:{
                //this is student
                info_info_name.setText(data.getString("NAME"));
                info_info_userid.setText(data.getString("USERID"));
                info_info_tel.setText(data.getString("TELEPHONE"));
                info_info_institute.setText(data.getString("INSTITUTE"));
                info_info_classs.setText(data.getString("MAJOR")+"\n"+data.getString("CLASSS"));
                info_info_birth.setText(birthd);
                info_info_topic_userid.setText("学号：　");
                info_info_topic_classs.setText("班级：　");
            }
        }
        switch (gender){
            case 0:{
                info_info_gender_female.setEnabled(true);
                info_info_gender_male.setChecked(false);
                info_info_gender_female.setChecked(true);
                break;
            }
            case 1:{
                info_info_gender_female.setChecked(false);
                info_info_gender_male.setChecked(true);
                info_info_gender_male.setEnabled(true);
                break;
            }
        }
    }



    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.getString("value")=="updated")
                Updated();
        }
    };


}
