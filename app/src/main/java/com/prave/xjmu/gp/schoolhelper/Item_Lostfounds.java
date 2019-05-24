package com.prave.xjmu.gp.schoolhelper;

/**
 * Created by Prave Zhang on 2018/2/2.
 */

public class Item_Lostfounds {
    //放进来的时候 time 已包含 AMPM
    private String lf_time,lf_thingname;
    private int status;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {

        return status;
    }

    public String getLf_time() {
        return lf_time;
    }

    public String getLf_thingname() {
        return lf_thingname;
    }


    public void setLf_time(String lf_time) {
        this.lf_time = lf_time;
    }

    public void setLf_thingname(String lf_thingname) {
        this.lf_thingname = lf_thingname;
    }

}
