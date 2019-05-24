package com.prave.xjmu.gp.schoolhelper;

/**
 * Created by Prave Zhang on 2018/2/7.
 */

public class Item_Chatting  {
    private String name,level,word,time,id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private int ST;
    //ST=0 自己
    //ST=1 其他学生
    //ST=2 老师


    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setST(int ST) {
        this.ST = ST;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getWord() {
        return word;
    }

    public String getTime() {
        return time;
    }

    public int getST() {
        return ST;
    }
}
