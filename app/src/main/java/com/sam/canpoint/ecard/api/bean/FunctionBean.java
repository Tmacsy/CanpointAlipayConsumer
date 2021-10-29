package com.sam.canpoint.ecard.api.bean;

public class FunctionBean {
    private String number;//序号
    private String name;//名称

    public FunctionBean(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
