package com.prave.xjmu.gp.schoolhelper;

import android.content.DialogInterface;
import android.opengl.ETC1;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleExactActivity extends AppCompatActivity {

    private String factors,id;
    private int adderweek=0,add_start=0,add_stop=0;
    private String[] lessons=new String[20];
    //lessons里每一个String都是一个JSONObject
    private GridView schedule_exact_gv;
    private boolean sdflag=true;//单周为true 双周为false
    private boolean add_sflag=false,add_dflag=false;
    private RadioButton schedule_exact_single_week,schedule_exact_double_week;
    private ImageButton schedule_exact_add;
    private String[] classtime={"%第一节",
            "%第二节","%第三节","%第四节",
            "%第五节","%第六节","%第七节",
            "%第八节","%第九节","%第十节",
            "%第一节",
            "%第二节","%第三节","%第四节",
            "%第五节","%第六节","%第七节",
            "%第八节","%第九节","%第十节"
    };
    private String[] weeeks={"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
    private List<HashMap<String, Object>> schedule_exact_list = new ArrayList<HashMap<String, Object>>();

    private List<String> filldata=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_exact);
        Bundle b=getIntent().getExtras();
        factors=b.getString("factors");
        id=b.getString("id");
        schedule_exact_gv=(GridView)findViewById(R.id.schedule_exact_gv);
        schedule_exact_single_week=(RadioButton)findViewById(R.id.schedule_exact_single_week);
        schedule_exact_double_week=(RadioButton)findViewById(R.id.schedule_exact_double_week);
        schedule_exact_add=(ImageButton)findViewById(R.id.schedule_exact_add);
        schedule_exact_gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View gvview, final int position, long id) {
                if(position<8) return  false;
                if(position%8==0) return false;
                int sdint=0;
                if(!sdflag) sdint=1;
                String nowprocessc="";
                final int clstm;
                if(position<16){
                    clstm=1;
                    nowprocessc=lessons[0+10*sdint];
                }
                else if(position<24){
                    clstm=2;
                    nowprocessc=lessons[1+10*sdint];
                }
                else if(position<32){
                    clstm=3;
                    nowprocessc=lessons[2+10*sdint];
                }
                else if(position<40){

                    clstm=4;
                    nowprocessc=lessons[3+10*sdint];
                }
                else if(position<48){
                    clstm=5;
                    nowprocessc=lessons[4+10*sdint];
                }
                else if(position<56){

                    clstm=6;
                    nowprocessc=lessons[5+10*sdint];
                }
                else if(position<64){

                    clstm=7;
                    nowprocessc=lessons[6+10*sdint];
                }
                else if(position<72){
                    clstm=8;
                    nowprocessc=lessons[7+10*sdint];
                }
                else if(position<80){
                    clstm=9;
                    nowprocessc=lessons[8+10*sdint];
                }
                else {
                    clstm=10;
                    nowprocessc=lessons[9+10*sdint];
                }
                final int week=position%8;
                try {
                    JSONObject nowj=new JSONObject(nowprocessc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final AlertDialog.Builder ab=new AlertDialog.Builder(ScheduleExactActivity.this);
                View view=View.inflate(getApplicationContext(),R.layout.view_schedule_replace_lesson,null);
                TextView schedule_exact_replace_weekday=(TextView)view.findViewById(R.id.schedule_exact_replace_weekday);
                EditText schedule_exact_replace_class_time=(EditText)view.findViewById(R.id.schedule_exact_replace_class_time);
                EditText schedule_exact_replace_old_class_name=(EditText)view.findViewById(R.id.schedule_exact_replace_old_class_name);
                final EditText schedule_exact_replace_new_class_name=(EditText)view.findViewById(R.id.schedule_exact_replace_new_class_name);
                CheckBox schedule_exact_replace_cb_single_week=(CheckBox) view.findViewById(R.id.schedule_exact_replace_cb_single_week);
                CheckBox schedule_exact_replace_cb_double_week=(CheckBox)view.findViewById(R.id.schedule_exact_replace_cb_double_week);
                schedule_exact_replace_weekday.setText(weeeks[week-1]);
                schedule_exact_replace_class_time.setText(String.valueOf(clstm));
                schedule_exact_replace_cb_single_week.setChecked(sdflag);
                schedule_exact_replace_cb_double_week.setChecked(!sdflag);
                schedule_exact_replace_cb_single_week.setVisibility(sdflag?View.VISIBLE:View.INVISIBLE);
                schedule_exact_replace_cb_double_week.setVisibility(!sdflag?View.VISIBLE:View.INVISIBLE);
                schedule_exact_replace_old_class_name.setText(filldata.get(position).equals(" ")?"( 空 )":filldata.get(position));
                ab.setView(view);
                ab.setCancelable(false);
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final  String rep=schedule_exact_replace_new_class_name.getText().toString();
                        if(!rep.equals("")){
                            Thread t=new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ReplaceAtServer(week,clstm-1,rep,1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                            while(t.isAlive());
                            Replace(position,rep);
                            Adapter_Exact_Lesson adapter=new Adapter_Exact_Lesson(ScheduleExactActivity.this,filldata);
                            schedule_exact_gv.setAdapter(adapter);
                        }
                        else{
                            InVailName();
                        }
                    }
                });
                ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                ab.setTitle("修改课程");
                ab.show();
                return true;
            }
        });
        if(schedule_exact_single_week.isChecked()) sdflag=true;
        View.OnClickListener onc=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sdflag!=schedule_exact_single_week.isChecked()){
                    if(schedule_exact_single_week.isChecked()){
                        try {
                            FillGridView(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sdflag=true;
                    }
                    else if(schedule_exact_double_week.isChecked()){
                        try {
                            FillGridView(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sdflag=false;
                    }
                }
            }
        };
        schedule_exact_single_week.setOnClickListener(onc);
        schedule_exact_double_week.setOnClickListener(onc);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetLessonsByID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        schedule_exact_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viee) {

                // open dialog
                final AlertDialog.Builder abadd=new AlertDialog.Builder(ScheduleExactActivity.this);
                List<String> adap_weeks=new ArrayList<String>();
                adap_weeks.add("星期一");
                adap_weeks.add("星期二");
                adap_weeks.add("星期三");
                adap_weeks.add("星期四");
                adap_weeks.add("星期五");
                adap_weeks.add("星期六");
                adap_weeks.add("星期日");
                ArrayAdapter sp_a=new ArrayAdapter(ScheduleExactActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,adap_weeks);

                View view=View.inflate(ScheduleExactActivity.this,R.layout.view_schedule_exact_add_lesson,null);
                final Spinner schedule_exact_add_choose_weekday_spinner=(Spinner)view.findViewById(R.id.schedule_exact_add_choose_weekday_spinner);
                final EditText schedule_exact_add_class_time=(EditText)view.findViewById(R.id.schedule_exact_add_class_time);
                final EditText schedule_exact_add_class_name=(EditText)view.findViewById(R.id.schedule_exact_add_class_name);
                final CheckBox schedule_exact_add_cb_single_week=(CheckBox)view.findViewById(R.id.schedule_exact_add_cb_single_week);
                final CheckBox schedule_exact_add_cb_double_week=(CheckBox)view.findViewById(R.id.schedule_exact_add_cb_double_week);
                schedule_exact_add_choose_weekday_spinner.setAdapter(sp_a);
                schedule_exact_add_choose_weekday_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        adderweek=position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                abadd.setCancelable(false);
                abadd.setTitle("添加课程");
                abadd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        add_sflag=false;
                        add_dflag=false;
                        final String time=schedule_exact_add_class_time.getText().toString();
                        final String name=schedule_exact_add_class_name.getText().toString();
                        final int  sdweek;
                        //0=单 1=双 2=单双
                        int s=0,d=0;
                        if(schedule_exact_add_cb_single_week.isChecked()) {s=1;add_sflag=true;}
                        if(schedule_exact_add_cb_double_week.isChecked()) {d=1;add_dflag=true;}
                        sdweek=s+d;
                        boolean timecorrect=false;
                        try {
                            timecorrect=IsTimeCorrect(time);
                        } catch (Exception e) {
                            InVaildTime();
                        }
                        if(sdweek==0)
                            InVaildSD();
                        else if(!IsNameCorrect(name))
                            InVailName();
                        else if(!timecorrect)
                            InVaildTime();
                        else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    for(int i=add_start-1;i<add_stop;i++)
                                        try {
                                            ReplaceAtServer(adderweek+1,i,name,2);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                }
                            }).start();
                            //add_start和add_stop 是从1开始，到10结束
                            //adderweek 从0开始，6结束
                            if(sdflag){
                                if(add_sflag){
                                    //当前页面为单周，且添加的有单周
                                    for(int i=add_start;i<=add_stop;i++) {
                                        int pos=i*8+adderweek+1;
                                        Replace(pos, name);
                                    }
                                    Adapter_Exact_Lesson adapter=new Adapter_Exact_Lesson(ScheduleExactActivity.this,filldata);
                                    schedule_exact_gv.setAdapter(adapter);
                                }
                            }
                            else {
                                if(add_dflag){
                                    //当前页面为双周，且添加的有双周
                                    for(int i=add_start;i<=add_stop;i++) {
                                        int pos=i*8+adderweek+1;
                                        Replace(pos, name);
                                    }
                                    Adapter_Exact_Lesson adapter=new Adapter_Exact_Lesson(ScheduleExactActivity.this,filldata);
                                    schedule_exact_gv.setAdapter(adapter);
                                }

                            }
                        }

                    }
                });
                abadd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                abadd.setView(view);
                abadd.show();

            }
        });

    }

    private void ReplaceAtServer(int week,int clstm,String name,int method)throws Exception{
        //week是星期，从1开始，到7结束
        //clstm是课节，从0开始，到9结束
        //name是课程名
        //这个函数替换服务器，更改list，但是不更新gridview
        //method=1 时，更新一个，method=2时，更新多个
        if (method==1) {
            String itematserver,gotoprocess;
            if(sdflag) {
                gotoprocess=lessons[clstm];
                itematserver = "SCLASS" + String.valueOf(clstm+1);
            }
            else {
                gotoprocess=lessons[clstm+10];
                itematserver="DCLASS"+String.valueOf(clstm+1);
            }
            JSONObject jsonObject;
            try {
                jsonObject=new JSONObject(gotoprocess);
            } catch (JSONException e) {
                Log.d("FEB6","向服务器更新数据的时候，解析lessons[i]出错");
                e.printStackTrace();
                //这个错误发生在一个object都没有的情况，如果有一个 则不会出这个错
                jsonObject=new JSONObject();
                try {
                    jsonObject.put("1"," ");
                    jsonObject.put("2"," ");
                    jsonObject.put("3"," ");
                    jsonObject.put("4"," ");
                    jsonObject.put("5"," ");
                    jsonObject.put("6"," ");
                    jsonObject.put("7"," ");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            jsonObject.remove(String.valueOf(week));
            jsonObject.put(String.valueOf(week),name);
            String result=jsonObject.toString();
            int t=sdflag?0:10;
            lessons[clstm+t]=result;
            String str="http://120.78.219.146/gp/updatesdlesson.php?ID="+id
                    +"&CLA="+itematserver+"&LES="+Encode(result);
            Log.d("LOG",str);
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
                data.putString("value","updateok");
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }
        else {
            String itematserver,gotoprocess="";
            if(add_sflag) {
                //单周存在
                gotoprocess=lessons[clstm];
                itematserver = "SCLASS" + String.valueOf(clstm+1);

            JSONObject jsonObject;
            try {
                jsonObject=new JSONObject(gotoprocess);
            } catch (JSONException e) {
                Log.d("FEB6","向服务器更新数据的时候，解析lessons[i]出错");
                e.printStackTrace();
                //这个错误发生在一个object都没有的情况，如果有一个 则不会出这个错
                jsonObject=new JSONObject();
                try {
                    jsonObject.put("1"," ");
                    jsonObject.put("2"," ");
                    jsonObject.put("3"," ");
                    jsonObject.put("4"," ");
                    jsonObject.put("5"," ");
                    jsonObject.put("6"," ");
                    jsonObject.put("7"," ");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            jsonObject.remove(String.valueOf(week));
            jsonObject.put(String.valueOf(week),name);
            String result=jsonObject.toString();
            int t=0;
            lessons[clstm+t]=result;
            String str="http://120.78.219.146/gp/updatesdlesson.php?ID="+id
                    +"&CLA="+itematserver+"&LES="+Encode(result);
            Log.d("LOG",str);
            StringBuffer sb = new StringBuffer(str);
            //System.out.println("URL:"+sb);
            URL url = new URL(sb.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String inputline = in.readLine();

        }


            if(add_dflag) {
                gotoprocess=lessons[clstm+10];
                itematserver = "DCLASS" + String.valueOf(clstm+1);

                JSONObject jsonObject;
                try {
                    jsonObject=new JSONObject(gotoprocess);
                } catch (JSONException e) {
                    Log.d("FEB6","向服务器更新数据的时候，解析lessons[i]出错");
                    e.printStackTrace();
                    //这个错误发生在一个object都没有的情况，如果有一个 则不会出这个错
                    jsonObject=new JSONObject();
                    try {
                        jsonObject.put("1"," ");
                        jsonObject.put("2"," ");
                        jsonObject.put("3"," ");
                        jsonObject.put("4"," ");
                        jsonObject.put("5"," ");
                        jsonObject.put("6"," ");
                        jsonObject.put("7"," ");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                jsonObject.remove(String.valueOf(week));
                jsonObject.put(String.valueOf(week),name);
                String result=jsonObject.toString();
                int t=10;
                lessons[clstm+t]=result;
                String str="http://120.78.219.146/gp/updatesdlesson.php?ID="+id
                        +"&CLA="+itematserver+"&LES="+Encode(result);
                Log.d("LOG",str);
                StringBuffer sb = new StringBuffer(str);
                //System.out.println("URL:"+sb);
                URL url = new URL(sb.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        url.openStream()));
                String inputline = in.readLine();

            }

        }
    }

    private String Replace(int position,String str){
        //单纯更改List，不改数据库
        String r=filldata.remove(position);
        filldata.add(position,str);
        return r;
    }

    private boolean IsTimeCorrect(String time)throws Exception{
        String[] ss=time.split("-");
        int t1=Integer.parseInt(ss[0]);
        int t2=Integer.parseInt(ss[1]);
        if(t1>10||t2>10) return false;
        if(t1>=t2) return false;
        add_stop=t2;
        add_start=t1;
        return true;
    }

    private boolean IsNameCorrect(String name){
        return !name.equals("");
    }


    public void InVailName(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleExactActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("您输入的课程名称不正确，请重试。");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void InVaildTime(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleExactActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请输入正确的课程时间！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void InVaildSD(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleExactActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请选择单周/双周！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    private void InVaildWeek(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleExactActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请选择课程在星期几！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }


    public void UpdateOK(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ScheduleExactActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("课程更新成功！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }


    private void GetLessonsByID() throws Exception {
        String str="http://120.78.219.146/gp/getlessonbyid.php?ID="+id;
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        String[] res=inputline.split("★");
        String lesson=res[0];
        JSONObject jsonlessons=new JSONObject(lesson);
        int i=0;
        while(i<10){
            //这里初次给lessons赋值
            lessons[i]=jsonlessons.getString("SCLASS"+String.valueOf(i+1));
            lessons[i+10]=jsonlessons.getString("DCLASS"+String.valueOf(i+1));
            i++;
        }
        for(i=0;i<20;i++)
            lessons[i]=Decode(lessons[i]);

        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","got");
        msg.setData(data);
        handler.sendMessage(msg);
    }

    private String Encode(String s){
        char[] chars=s.toCharArray();
        int l=s.length();
        for(int i=0;i<l;i++) {
            if(chars[i]=='{') chars[i]='｛';
            if(chars[i]=='}') chars[i]='｝';
            if(chars[i]==':') chars[i]='：';
            if(chars[i]=='=') chars[i]='＝';
            if(chars[i]==';') chars[i]='；';
            if(chars[i]=='"') chars[i]='”';
            if(chars[i]==' ') chars[i]='　';
            if(chars[i]=='&') chars[i]='＆';
        }
        return String.valueOf(chars);
    }

    private String Decode(String s){
        char[] chars=s.toCharArray();
        int l=s.length();
        for(int i=0;i<l;i++) {
            if(chars[i]=='｛'  ) chars[i]='{';
            if(chars[i]=='｝') chars[i]='}';
            if(chars[i]=='：') chars[i]=':';
            if(chars[i]=='＝') chars[i]='=';
            if(chars[i]=='；') chars[i]=';';
            if(chars[i]=='”') chars[i]='"';
            if(chars[i]=='　') chars[i]=' ';
            if(chars[i]=='＆') chars[i]='&';

        }
        return String.valueOf(chars);
    }


    private void FillGridView(int singledouble) {
        //single=0;double=1

        int sd=singledouble*10;
        /*
        schedule_exact_list.clear();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("NAME","星期/节次");
        schedule_exact_list.add(map);
        map = new HashMap<String, Object>();
        map.put("NAME","星期一");
        schedule_exact_list.add(map);
           */
        JSONObject nowprocessc=null;
        filldata.clear();
        filldata.add("星期/节次");
        filldata.add("星期一");
        filldata.add("星期二");
        filldata.add("星期三");
        filldata.add("星期四");
        filldata.add("星期五");
        filldata.add("星期六");
        filldata.add("星期日");
        //外层for
        for(int i=sd;i<10+sd;i++){
            filldata.add(classtime[i]);
            try {
                nowprocessc=new JSONObject(lessons[i]);
                //如果一个元素都没有，则出JSONException
            } catch (JSONException e) {
                nowprocessc=new JSONObject();
                try {
                    nowprocessc.put("1"," ");
                    nowprocessc.put("2"," ");
                    nowprocessc.put("3"," ");
                    nowprocessc.put("4"," ");
                    nowprocessc.put("5"," ");
                    nowprocessc.put("6"," ");
                    nowprocessc.put("7"," ");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
            for(int j=0;j<7;j++)
                try {
                    filldata.add(nowprocessc.getString(String.valueOf(j+1)));
                    //某个元素没有，添加为“ ”，但是实际数组中还没有变，还是不存在这个get的数字
                } catch (JSONException e) {
                    filldata.add(" ");
                }
        }
        Adapter_Exact_Lesson adapter=new Adapter_Exact_Lesson(this,filldata);
        schedule_exact_gv.setAdapter(adapter);
    }










    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getString("value")) {
                case "got": {
                    try {
                        FillGridView(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "updateok":{
                    UpdateOK();
                    break;
                }
            }
        }
    };
}