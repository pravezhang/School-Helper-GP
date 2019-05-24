package com.prave.xjmu.gp.schoolhelper;

/**
 * Created by Prave Zhang on 2018/1/31.
 */

public class Item_Twohands {
    public String getTwohand_type() {
        return twohand_type;
    }

    public void setTwohand_type(String twohand_type) {
        this.twohand_type = twohand_type;
    }

    public void setTwohand_name(String twohand_name) {
        this.twohand_name = twohand_name;
    }

    public void setTwohand_price(String twohand_price) {
        this.twohand_price = twohand_price;
    }

    public String getTwohand_name() {
        return twohand_name;
    }

    public String getTwohand_price() {
        return twohand_price;
    }

    private String twohand_type,twohand_name,twohand_price;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

}
