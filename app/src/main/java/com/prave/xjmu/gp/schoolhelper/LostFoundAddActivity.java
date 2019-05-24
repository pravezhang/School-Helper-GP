package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.ETC1;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LostFoundAddActivity extends AppCompatActivity {

    private String factors;
    private ProgressDialog pw;
    private String dada;
    private EditText lostfound_add_name,lostfound_add_time,lostfound_add_describe    ;
    private RadioGroup lostfound_add_rg;
    private RadioButton lostfound_add_rb_found,lostfound_add_rb_lost;
    private Button lostfound_add_publish,lostfound_add_cancel;
    private Spinner lostfound_add_ampm;
    private String date,name,desc;
    int lostfound=0;
    private ArrayList<String> ampms=new ArrayList<>();
    private ArrayAdapter<String> spinner_aa;
    private int selectedampm=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_add);
        setTitle("发布失物招领/寻物启事消息");
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        lostfound_add_name=(EditText)findViewById(R.id.lostfound_add_name);
        lostfound_add_time=(EditText)findViewById(R.id.lostfound_add_time);
        lostfound_add_time.setText("2018-01-23");
        lostfound_add_describe=(EditText)findViewById(R.id.lostfound_add_describe);
        lostfound_add_rg=(RadioGroup)findViewById(R.id.lostfound_add_rg);
        lostfound_add_rb_found=(RadioButton)findViewById(R.id.lostfound_add_rb_found);
        lostfound_add_rb_lost=(RadioButton)findViewById(R.id.lostfound_add_rb_lost);
        lostfound_add_publish=(Button)findViewById(R.id.lostfound_add_publish);
        lostfound_add_cancel=(Button)findViewById(R.id.lostfound_add_cancel);
        lostfound_add_ampm=(Spinner)findViewById(R.id.lostfound_add_ampm);
        ampms.add("-");
        ampms.add("上午");
        ampms.add("下午");
        spinner_aa=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,ampms);
        lostfound_add_ampm.setAdapter(spinner_aa);
        lostfound_add_ampm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedampm=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        lostfound_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WannaCancel();
            }
        });

        lostfound_add_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date=lostfound_add_time.getText().toString();
                name=lostfound_add_name.getText().toString();
                desc=lostfound_add_describe.getText().toString();

                if(lostfound_add_rb_found.isChecked()){
                    lostfound=1;
                }
                else if(lostfound_add_rb_lost.isChecked()){
                    lostfound=2;
                }

                try {
                    if(!IsNull(date)&&!IsNull(name)&&!IsNull(desc)&&isDateVaild(date)){


                        pw=new ProgressDialog(LostFoundAddActivity.this);
                        pw.setMessage("请稍候……");
                        pw.setCancelable(false);
                        pw.setIndeterminate(true);
                        pw.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    UploadTW(name,date,selectedampm,desc,lostfound);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    else{
                        InVaildInput();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    InVaildInput();

                }

            }
        });



    }

    private boolean isDateVaild(String date) throws Exception{
        String[] ymd=date.split("-");
        int y=Integer.parseInt(ymd[0]);
        int m=Integer.parseInt(ymd[1]);
        int d=Integer.parseInt(ymd[2]);
        if(y<2018) return false;
        if(m>12|| m<0) return false;
        if(d>31 || d<0) return false;
        return true;

    }

    private boolean IsNull(String s){
        return s.equals("");
    }

    public void InVaildInput(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundAddActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您输入的内容格式不正确，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            WannaCancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void UploadTW(String name,String date,int ampm,String desc,int type) throws Exception {
        JSONObject js = new JSONObject(factors);
        String tel = js.getString("TELEPHONE");
        String str = "http://120.78.219.146/gp/addlostfounds.php?TEL=" + tel
                + "&NAME=" + name
                + "&AMPM=" + ampm
                + "&DAT=" + date
                + "&DESCRIBE=" + desc
                + "&TYPE=" + String.valueOf(type);

        Log.d("Feb4",str);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        JSONObject ob = new JSONObject();
        ob.put("NAME", name);
        ob.put("LF",lostfound);
        switch (ampm){
            case 0:{
                ob.put("TIME",date);
                break;
            }
            case 1:{
                ob.put("TIME",date+ " 上午");
                break;
            }
            case 2:{
                ob.put("TIME",date+" 下午");
            }
            default:{}
        }
        dada=ob.toString();
        if(inputline.toCharArray()[0]=='1'){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","success");
            msg.setData(data);
            handler.sendMessage(msg);
        }

    }



    public void WannaCancel(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(LostFoundAddActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您确定要退出吗？所做的改动不会被保存。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),LostFoundActivity.class);
                        setResult(555,intent);
                        finish();
                    }
                });
        normalDialog.setCancelable(false);
        normalDialog.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        normalDialog.show();
    }

    private void Finish(){
        Intent intent =new Intent();
        intent.putExtra("data",dada);
        setResult(888,intent);
        finish();
    }


    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.getString("value")=="success") {
                pw.dismiss();
                Finish();
            }
        }
    };

}
