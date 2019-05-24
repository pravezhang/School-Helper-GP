package com.prave.xjmu.gp.schoolhelper;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SchoolCalendarActivity extends AppCompatActivity {

    private CalendarView calendar;
    private TextView calendar_day,calendar_syear,calendar_sweek;
    private  int cy,cm,cd,cw,flag;
    private ProgressDialog pw;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_calendar);
        calendar=(CalendarView)findViewById(R.id.calendar);
        calendar_day=(TextView)findViewById(R.id.calendar_day);
        calendar_syear=(TextView)findViewById(R.id.calendar_syear);
        calendar_sweek=(TextView)findViewById(R.id.calendar_sweek);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        c.set(year,month,dayOfMonth);
        c.setTimeZone(TimeZone.getTimeZone("Etc/GMT+8"));
        cy=c.getTime().getYear()+1900;
        cm=c.getTime().getMonth();
        cd=c.getTime().getDate();
        cm++;
        cd--;
        if(cd==0) {
            cm--;
            switch (cm){
                case 1:{
                    cd=31;
                    break;
                }
                case 2:{
                    cd=28;
                    break;
                }
                case 3:{
                    cd=31;
                    break;
                }
                case 4:{
                    cd=30;
                    break;
                }
                case 5:{
                    cd=31;
                    break;
                }
                case 6:{
                    cd=30;
                    break;
                }
                case 7:{
                    cd=31;
                    break;
                }
                case 8:{
                    cd=31;
                    break;
                }
                case 9:{
                    cd=30;
                    break;
                }
                case 10:{
                    cd=31;
                    break;
                }
                case 11:{
                    cd=30;
                    break;
                }
                case 12:{
                    cd=31;
                    break;
                }
            }
        }
        calendar_sweek.setText("");


        pw=new ProgressDialog(SchoolCalendarActivity.this);
        pw.setMessage("请稍候……");
        pw.setCancelable(false);
        pw.setIndeterminate(true);
        pw.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetSWeek(cy,cm,cd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                c = Calendar.getInstance();
                c.set(year,month,dayOfMonth);
                c.setTimeZone(TimeZone.getTimeZone("Etc/GMT+8"));
                cy=c.getTime().getYear()+1900;
                cm=c.getTime().getMonth();
                cd=c.getTime().getDate();
                cm++;
                cd--;
                if(cd==0) {
                    cm--;
                    switch (cm){
                        case 1:{
                            cd=31;
                            break;
                        }
                        case 2:{
                            cd=28;
                            break;
                        }
                        case 3:{
                            cd=31;
                            break;
                        }
                        case 4:{
                            cd=30;
                            break;
                        }
                        case 5:{
                            cd=31;
                            break;
                        }
                        case 6:{
                            cd=30;
                            break;
                        }
                        case 7:{
                            cd=31;
                            break;
                        }
                        case 8:{
                            cd=31;
                            break;
                        }
                        case 9:{
                            cd=30;
                            break;
                        }
                        case 10:{
                            cd=31;
                            break;
                        }
                        case 11:{
                            cd=30;
                            break;
                        }
                        case 12:{
                            cd=31;
                            break;
                        }
                    }
                }
                setView();
                calendar_sweek.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GetSWeek(cy,cm,cd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

        });
        calendar_day.setText(String.valueOf(cy)+"年"+
            String.valueOf(cm)+"月"+
                String.valueOf(cd)+"日");
        if(cm<=2){
            //小于2月必然是去年到今年的第一学期
            calendar_syear.setText("校历 "+String.valueOf(cy-1)+"-"+
                    String.valueOf(cy)+" 第一学期"
            );
            flag=1;

        }
        else if(cm>8){//大于8月必然是今年到明年的第一学期
            calendar_syear.setText("校历 "+String.valueOf(cy)+"-"+
                    String.valueOf(cy+1)+" 第一学期"
            );
            flag=1;

        }

        else {
            //2月到8月，是去年到今年的第二学期
            calendar_syear.setText("校历 "+String.valueOf(cy-1)+"-"+
                    String.valueOf(cy)+" 第二学期"
            );
            flag=2;
        }
    }

    void setView(){
        calendar_day.setText(String.valueOf(cy)+"年"+
                String.valueOf(cm)+"月"+
                String.valueOf(cd)+"日");
        if(cm<=2)
            //小于2月必然是去年到今年的第一学期
            calendar_syear.setText("校历 "+String.valueOf(cy-1)+"-"+
                    String.valueOf(cy)+" 第一学期"
            );
        else if(cm>8)
            //大于8月必然是今年到明年的第一学期
            calendar_syear.setText("校历 "+String.valueOf(cy)+"-"+
                    String.valueOf(cy+1)+" 第一学期"
            );
        else //2月到8月，是去年到今年的第二学期
            calendar_syear.setText("校历 "+String.valueOf(cy-1)+"-"+
                    String.valueOf(cy)+" 第二学期"
            );

    }

    void GetSWeek(int y,int m,int d) throws Exception{
        String str="http://120.78.219.146/gp/getsweek.php?Y="+String.valueOf(y)+
                "&M="+String.valueOf(m)+
                "&D="+String.valueOf(d)+
                "&FLAG="+String.valueOf(flag);
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("URL:"+sb);
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        String inputline = in.readLine();
        try {
            cw=Integer.parseInt(inputline);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value","got");
        msg.setData(data);
        handler.sendMessage(msg);

    }

    void UpdateWeek(){
        if(cw<20)
            calendar_sweek.setText("第 "+ String.valueOf(cw)+" 周");
        else
            calendar_sweek.setText(" 假期中 ");
    }


    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data.getString("value")=="got") {
                pw.dismiss();
                UpdateWeek();
            }
        }
    };

}
