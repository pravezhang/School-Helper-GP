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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private GridView schedule_gv;
    private Button schedule_add;
    private String factors;
    private JSONObject j=null;
    private String[] schedules;
    //这个schedules是从服务器获取的所有列
    private int addid,delid;
    //这个addid是真正服务器里的ID，delid是List里的
    private String addname="";
    private AlertDialog NowDialog;
    private List<HashMap<String, Object>> schedule_list = new ArrayList<HashMap<String, Object>>();
    //jiade

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        try {
            j=new JSONObject(factors);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        schedule_gv=(GridView)findViewById(R.id.schedule_gv);
        schedule_add=(Button)findViewById(R.id.schedule_add);
        schedule_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewScheduleDialog();
            }
        });

        schedule_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),ScheduleExactActivity.class);
                intent.putExtra("factors",factors);
                intent.putExtra("id",(String) schedule_list.get(position).get("ID"));
                //这个ID是真正服务器里的ID
                startActivity(intent);
            }
        });

        schedule_gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delid=position;
                DelScheduleDialog();
                return true;
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetSchedulesFromServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void AddNewScheduleDialog(){
        //make a dialog
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleActivity.this);
        View view=View.inflate(getApplicationContext(),R.layout.view_schedule_add_schedule,null);
        final EditText schedule_add_name=(EditText)view.findViewById(R.id.schedule_add_name);
        Button schedule_add_confirm=(Button)view.findViewById(R.id.schedule_add_confirm);
        normalDialog.setView(view);
        NowDialog= normalDialog.show();
        schedule_add_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sname=schedule_add_name.getText().toString();
                if(!sname.equals("")){
                    addname=sname;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddScheduleToServer(sname);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else{
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value","x");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        });




    }

    private void AddScheduleToServer(String name) throws Exception{
        //关掉这个输入提示
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","dismiss");
        msg.setData(data);
        handler.sendMessage(msg);

        String str="http://120.78.219.146/gp/addschedule.php?TEL="+j.getString("TELEPHONE")
                +"&NAME="+name;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] dd=inputline.split("；");
        addid=Integer.parseInt(dd[0]);
        msg = new Message();
        data = new Bundle();
        data.putString("value","addok");
        msg.setData(data);
        handler.sendMessage(msg);
        //添加成功了 返回ID
    }

    private void GetSchedulesFromServer() throws Exception{
        String tel=j.getString("TELEPHONE");
        String str="http://120.78.219.146/gp/getschedule.php?TEL="+tel;
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
        schedules=allres[0].split("☆");
        //get done. call handler to fill the gridview
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","got");
        msg.setData(data);
        handler.sendMessage(msg);

    }

    private void FillGridView() throws Exception{
        Log.d("FEB6","开始填充");
        JSONObject it=null;
        int length=schedules.length,i=-1;
        schedule_list.clear();
        /*
        while(i++<length-1) {
            Log.d("FEB6","现在处理  "+schedules[i]);
            it = new JSONObject(schedules[i]);
            String[] in=new String[2];
            in[0]=it.getString("ID");
            in[1]=it.getString("NAME");
            datas.add(in);
        }
        */

        while(i++<length-1) {
            it = new JSONObject(schedules[i]);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ID",it.getString("ID"));
            map.put("NAME",it.getString("NAME"));
            schedule_list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, schedule_list, R.layout.view_item_schedule_single,
                new String[] { "ID", "NAME" }, new int[] { R.id.schedule_id,
                R.id.schedule_name });

        schedule_gv.setAdapter(adapter);
    }

    private void DelScheduleDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要删除这个课程表吗？操作不可恢复。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DelScheduleFromServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        normalDialog.show();



    }

    private void DelScheduleFromServer() throws Exception{
        String RealID=(String)schedule_list.get(delid).get("ID");
        String str="http://120.78.219.146/gp/updatesdstatus.php?ID="+RealID;
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
            data.putString("value","delok");
            msg.setData(data);
            handler.sendMessage(msg);
        }

    }


    private void RefreshGV(int method){
        switch (method){
            case 1:{
                String[] in=new String[2];
                in[0]=String.valueOf(addid);
                in[1]=addname;
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ID",String.valueOf(addid));
                map.put("NAME",addname);
                schedule_list.add(map);

                SimpleAdapter adapter = new SimpleAdapter(this, schedule_list, R.layout.view_item_schedule_single,
                        new String[] { "ID", "NAME" }, new int[] { R.id.schedule_id,
                        R.id.schedule_name });

                schedule_gv.setAdapter(adapter);
                //获取从服务器拿到的ID，然后把这个ID和名字加到GridView里
                break;
            }
            case 2:{
                //从List删除对应ID的内容，然后重新赋给Adapter
                schedule_list.remove(delid);
                SimpleAdapter adapter = new SimpleAdapter(this, schedule_list, R.layout.view_item_schedule_single,
                        new String[] { "ID", "NAME" }, new int[] { R.id.schedule_id,
                        R.id.schedule_name });

                schedule_gv.setAdapter(adapter);
                break;
            }
        }
    }


    private void InVaildInput(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您输入的格式不正确，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void AddOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("添加课程表成功！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }


    private void DelOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("删除课程表成功！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")) {
                case "got":{
                    try {
                        FillGridView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "addok":{
                    AddOK();
                    RefreshGV(1);
                    break;
                }
                case "delok":{
                    DelOK();
                    RefreshGV(2);
                    break;
                }
                case "dismiss":{
                    NowDialog.dismiss();
                    break;
                }
                case "x":{
                    InVaildInput();
                    break;
                }

            }
        }
    };


}
