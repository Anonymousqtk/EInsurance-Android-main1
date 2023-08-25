package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tunb on 05/04/2023.
 */

public class LoaiXeObject implements Serializable {
    @SerializedName("ma_phi")
    private String ma_phi;

    @SerializedName("ten_loaixe")
    private String ten_loaixe;


    public LoaiXeObject(String ma_phi, String ten_loaixe) {
        this.ma_phi = ma_phi;
        this.ten_loaixe = ten_loaixe;
    }

    public String getMa_phi() {
        return ma_phi;
    }

    public String getTen_loaixe() {
        return ten_loaixe;
    }
}
