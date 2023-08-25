package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *  Created by tunb on 05/04/2023.
 */

public class NhanHieuXeObject implements Serializable {
    @SerializedName("ma_nhomxe")
    private String ma_nhomxe;

    @SerializedName("ten_nhomxe")
    private String ten_nhomxe;


    public NhanHieuXeObject(String ma_nhomxe, String ten_nhomxe) {
        this.ma_nhomxe = ma_nhomxe;
        this.ten_nhomxe = ten_nhomxe;
    }

    public String getMa_nhomxe() {
        return ma_nhomxe;
    }

    public String getTen_nhomxe() {
        return ten_nhomxe;
    }
}
