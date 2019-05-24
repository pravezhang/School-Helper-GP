package com.prave.xjmu.gp.schoolhelper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prave Zhang on 2018/1/30.
 */

public class Adapter_Notices extends BaseAdapter {
    private List<Item_Notices> data;
    private Context context;

    public Adapter_Notices(Context context,List data){
        this.context=context;
        this.data=data;
    }

    @Override
    public int getCount() {
        if (data==null)
            return 0;
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
        View view=View.inflate(context,R.layout.view_item_notices,null);
        TextView item_notice_time=(TextView)view.findViewById(R.id.item_notice_time);
        TextView item_notice_publisher=(TextView)view.findViewById(R.id.item_notice_publisher);
        TextView item_notice_title=(TextView)view.findViewById(R.id.item_notice_title);
        Item_Notices item=data.get(position);
        item_notice_time.setText(item.getNotice_time());
        item_notice_publisher.setText(item.getNotice_publisher());
        if(!item.getNotice_publisher().equals("[教务通知]")){
            item_notice_publisher.setTextColor(Color.parseColor("#370D49"));
        }
        item_notice_title.setText(item.getNotice_title());
        return view;
    }
}
