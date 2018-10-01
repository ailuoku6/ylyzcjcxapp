package com.yz.dzq.ailuoku6;

import org.litepal.crud.LitePalSupport;

public class Student extends LitePalSupport {
    private String name;
    private String xuehao;

    public Student(String name,String xuehao){
        this.name = name;
        this.xuehao = xuehao;
    }

    public String getName(){
        return this.name;
    }

    public String getXuehao(){
        return this.xuehao;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setXuehao(String xuehao){
        this.xuehao = xuehao;
    }

}
