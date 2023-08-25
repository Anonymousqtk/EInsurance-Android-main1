package com.pvi.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by tuyenpt on 21/04/2017.
 */

public class LossHistoryObject implements Serializable {
    @SerializedName("ten_dttt")
    private String lisencePlate;

    @SerializedName("ten_khach")
    private String name;

    @SerializedName("so_seri")
    private String serial;

    @SerializedName("ngay_dau_seri")
    private String firstDay;

    @SerializedName("ngay_cuoi_seri")
    private String lastDay;

    @SerializedName("tyle")
    private String lossRatio;

    @SerializedName("so_vu")
    private String numberOfLoss;

    @SerializedName("ten_donvi")
    private String unit;

    @SerializedName("blacklist")
    private String blacklist;

    public LossHistoryObject(String lisencePlate, String name, String serial, String firstDay, String lastDay, String lossRatio, String numberOfLoss, String unit, String blacklist) {
        this.lisencePlate = lisencePlate;
        this.name = name;
        this.serial = serial;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.lossRatio = lossRatio;
        this.numberOfLoss = numberOfLoss;
        this.unit = unit;
        this.blacklist = blacklist;
    }

    public String getLisencePlate() {
        return lisencePlate;
    }

    public String getName() {
        return name;
    }

    public String getBlacklist() {
        return blacklist;
    }

    public String getSerial() {
        return serial;
    }

    public String getFirstDay() {
        return firstDay;
    }

    public String getLastDay() {
        return lastDay;
    }

    public String getLossRatio() {
        return lossRatio;
    }

    public String getNumberOfLoss() {
        return numberOfLoss;
    }

    public String getUnit() {
        return unit;
    }
}
