package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tunb on 05/04/2023.
 */

public class DongXeObject implements Serializable {
    @SerializedName("ma_dongxe")
    private String ma_dongxe;

    @SerializedName("ten_dongxe")
    private String ten_dongxe;


    public DongXeObject(String ma_dongxe, String ten_dongxe) {
        this.ma_dongxe = ma_dongxe;
        this.ten_dongxe = ten_dongxe;
    }

    public String getMa_dongxe() {
        return ma_dongxe;
    }

    public String getTen_dongxe() {
        return ten_dongxe;
    }
}
