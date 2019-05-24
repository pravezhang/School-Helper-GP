package com.prave.xjmu.gp.schoolhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prave Zhang on 2018/2/2.
 */

public class Adapter_Lostfounds extends BaseAdapter {


    private List<Item_Lostfounds> data;
    private Context context;


    public Adapter_Lostfounds(List<Item_Lostfounds> data, Context context) {
        this.data = data;
        this.context = context;
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
        View view=View.inflate(context,R.layout.view_item_lostfound,null);
        TextView item_lostfound_thingname=(TextView)view.findViewById(R.id.item_lostfound_thingname);
        TextView item_lostfound_time=(TextView)view.findViewById(R.id.item_lostfound_time);
        Item_Lostfounds item=data.get(position);
        switch (item.getStatus()){
            case 1:{
                item_lostfound_thingname.setText(item.getLf_thingname());
                break;
            }
            case 2:{
                item_lostfound_thingname.setText("[删]"+item.getLf_thingname());
                break;
            }
        }
        item_lostfound_time.setText(item.getLf_time());

        return view;
    }
}
