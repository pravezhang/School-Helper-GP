package com.prave.xjmu.gp.schoolhelper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prave Zhang on 2018/2/6.
 */

public class Adapter_Exact_Lesson extends BaseAdapter {

    private Context context;
    private List<String> data;

    public Adapter_Exact_Lesson(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data==null) return 0;
        else
            return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String now=data.get(position);
        String[] res=null;
        View view;
        if(position<8)
            view=View.inflate(context,R.layout.view_item_schedule_weekname,null);
        else {
            switch (now.toCharArray()[0]) {
                case '%': {
                    view = View.inflate(context, R.layout.view_item_schedule_classtimename, null);
                    res = now.split("%");
                    now = res[1];
                    break;
                }
                default: {
                    view = View.inflate(context, R.layout.view_item_schedule_lesson_single, null);
                }
            }
        }
        TextView con=(TextView)view.findViewById(R.id.schedule_gv_con);
        con.setText(now);
        return view;
    }
}
