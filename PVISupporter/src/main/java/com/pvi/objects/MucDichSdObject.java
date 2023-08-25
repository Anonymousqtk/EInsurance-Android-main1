package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tunb on 05/04/2023.
 */

public class MucDichSdObject implements Serializable {
    @SerializedName("ma_nhphi")
    private String ma_nhphi;

    @SerializedName("ten_nhphi")
    private String ten_nhphi;


    public MucDichSdObject(String ma_nhphi, String ten_nhphi) {
        this.ma_nhphi = ma_nhphi;
        this.ten_nhphi = ten_nhphi;
    }

    public String getMa_nhphi() {
        return ma_nhphi;
    }

    public String getTen_nhphi() {
        return ten_nhphi;
    }
}
