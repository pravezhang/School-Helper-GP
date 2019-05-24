package com.prave.xjmu.gp.schoolhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prave Zhang on 2018/2/7.
 */

public class Adapter_Chattings extends BaseAdapter {

    private Context context;
    private List<Item_Chatting> data;

    public Adapter_Chattings(Context context, List<Item_Chatting> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data==null) return 0;
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
        Item_Chatting item=data.get(position);
        View view=null;
        switch (item.getST()){
            case 0:{
                view=View.inflate(context,R.layout.view_item_chatting_me,null);
                break;
            }
            case 1:{
                view=View.inflate(context,R.layout.view_item_chatting_you,null);
                break;
            }
            case 2:{
                view=View.inflate(context,R.layout.view_item_chatting_teacher,null);
                break;
            }
            default:{
                return null;
            }
        }
        TextView chatting_time=(TextView)view.findViewById(R.id.chatting_time);
        TextView chatting_name=(TextView)view.findViewById(R.id.chatting_name);
        TextView chatting_level=(TextView)view.findViewById(R.id.chatting_level);
        TextView chatting_word=(TextView)view.findViewById(R.id.chatting_word);
        chatting_time.setText(item.getTime());
        chatting_name.setText(item.getName());
        chatting_level.setText(item.getLevel());
        chatting_word.setText(item.getWord());

        return view;
    }
}
