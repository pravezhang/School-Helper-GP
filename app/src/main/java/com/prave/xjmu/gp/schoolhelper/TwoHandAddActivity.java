package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TwoHandAddActivity extends AppCompatActivity {

    private Button twohand_add_cancel,twohand_add_publish;
    private EditText twohand_add_name,twohand_add_price,twohand_add_describe;
    private Spinner twohand_add_type;
    private List<String> spinner_list=new ArrayList<>();
    private int nowtype=0;
    private ProgressDialog pw;
    private ArrayAdapter<String> spinner_aa;
    private String factors;
    private String dada;
    double price=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_hand_add);
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        setTitle("发布二手物品");
        twohand_add_cancel=(Button)findViewById(R.id.twohand_add_cancel);
        twohand_add_publish=(Button)findViewById(R.id.twohand_add_publish);
        twohand_add_name=(EditText)findViewById(R.id.twohand_add_name);
        twohand_add_price=(EditText)findViewById(R.id.twohand_add_price);
        twohand_add_describe=(EditText)findViewById(R.id.twohand_add_describe);
        twohand_add_type=(Spinner)findViewById(R.id.twohand_add_type);
        spinner_list.add("[书籍/资料]");
        spinner_list.add("[电子/电器]");
        spinner_list.add("[日用/出行]");
        spinner_list.add("[ 其他 ]");
        spinner_aa=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,spinner_list);
        twohand_add_type.setAdapter(spinner_aa);
        twohand_add_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nowtype=position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        twohand_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WannaCancel();
            }
        });
        twohand_add_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sprice=twohand_add_price.getText().toString();

                try {
                    price=GetDemicalPrice(sprice);
                } catch (Exception e) {
                    InVaildInput();
                }
                final String name=twohand_add_name.getText().toString().trim();
                final String describe=twohand_add_describe.getText().toString();
                if(!isEmpty(name)&&!isEmpty(sprice)&&!isEmpty(describe)&&!isZero(nowtype)){


                    pw=new ProgressDialog(TwoHandAddActivity.this);
                    pw.setMessage("请稍候……");
                    pw.setCancelable(false);
                    pw.setIndeterminate(true);
                    pw.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UploadTW(name,String.valueOf(price),describe,nowtype);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else{
                    InVaildInput();
                }
            }
        });
    }

    private double GetDemicalPrice(String price) throws Exception{
        double p= Double.valueOf(price);
        BigDecimal bg = new BigDecimal(p).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }

    private void UploadTW(String name,String price,String desc,int type) throws Exception{
        JSONObject js=new JSONObject(factors);
        String tel=js.getString("TELEPHONE");
        String str="http://120.78.219.146/gp/addtwohands.php?TEL="+tel
                +"&NAME="+name
                +"&PRICE="+price
                +"&DESCRIBE="+desc
                +"&TYPE="+String.valueOf(type);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        JSONObject ob=new JSONObject();
        ob.put("name",name);
        ob.put("price",price);
        ob.put("describe",desc);
        ob.put("type",type);
        ob.put("tel",tel);
        dada=ob.toString();

        if(inputline.toCharArray()[0]=='1'){
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value","success");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }


    private boolean isEmpty(String s){
        return s.equals("");
    }

    private boolean isZero(int i){
        return i==0;
    }

    public void InVaildInput(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandAddActivity.this);
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


    private void Finish(){
        Intent intent =new Intent();
        intent.putExtra("data",dada);
        setResult(888,intent);
        finish();
    }

    private void Cancel(){
        Intent intent =new Intent();
        setResult(555,intent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            WannaCancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void WannaCancel(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(TwoHandAddActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您确定要退出吗？所做的改动不会被保存。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(),TwoHandAddActivity.class);
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
