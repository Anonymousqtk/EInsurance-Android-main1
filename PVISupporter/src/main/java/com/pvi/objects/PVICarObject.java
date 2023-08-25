package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tuyenpt on 24/05/2016.
 */
public class PVICarObject implements Serializable {

    @SerializedName("so_seri")
    private String serial;

    @SerializedName("ten_khach")
    private String name;

    @SerializedName("bien_ksoat")
    private String license;

    @SerializedName("ngay_dau_seri")
    private String firstDay;

    @SerializedName("ngay_cuoi_seri")
    private String lastDay;

    @SerializedName("tyle_phi")
    private String ratio;

    @SerializedName("ma_nhomdl")
    private String group;

    @SerializedName("ghi_chu")
    private String note;

    public PVICarObject(String serial, String name, String license, String firstDay, String lastDay, String ratio, String group, String note) {
        this.serial = serial;
        this.name = name;
        this.license = license;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.ratio = ratio;
        this.group = group;
        this.note = note;
    }

    public String getSerial() {
        return serial;
    }

    public String getName() {
        return name;
    }

    public String getLicense() {
        return license;
    }

    public String getFirstDay() {
        return firstDay;
    }

    public String getLastDay() {
        return lastDay;
    }

    public String getRatio() {
        return ratio;
    }

    public String getGroup() {
        return group;
    }

    public String getNote() {
        return note;
    }
}
