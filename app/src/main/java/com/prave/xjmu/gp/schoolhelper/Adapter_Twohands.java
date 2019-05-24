package com.prave.xjmu.gp.schoolhelper;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prave Zhang on 2018/1/31.
 */

public class Adapter_Twohands extends BaseAdapter {

    private List<Item_Twohands> data;
    private Context context;
    public Adapter_Twohands(Context context,List data){
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
        View view=View.inflate(context,R.layout.view_item_twohand,null);
        TextView item_twohand_type=(TextView)view.findViewById(R.id.item_twohand_type);
        TextView item_twohand_name=(TextView)view.findViewById(R.id.item_twohand_name);
        TextView item_twohand_price=(TextView)view.findViewById(R.id.item_twohand_price);
        Item_Twohands item=data.get(position);
        if(item.getStatus()==1)
            item_twohand_name.setText(item.getTwohand_name());
        else{
            item_twohand_name.setText("[删] "+item.getTwohand_name());
        }
        item_twohand_type.setText(item.getTwohand_type());
        item_twohand_price.setText("￥"+item.getTwohand_price());
        return view;
    }
}
