
package com.riddhikakadia.brunchy2.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

    @SerializedName("sane")
    @Expose
    private List<Object> sane = null;
    @SerializedName("to")
    @Expose
    private List<String> to = null;
    @SerializedName("q")
    @Expose
    private List<String> q = null;
    @SerializedName("app_id")
    @Expose
    private List<String> appId = null;
    @SerializedName("app_key")
    @Expose
    private List<String> appKey = null;
    @SerializedName("from")
    @Expose
    private List<String> from = null;

    public List<Object> getSane() {
        return sane;
    }

    public void setSane(List<Object> sane) {
        this.sane = sane;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getQ() {
        return q;
    }

    public void setQ(List<String> q) {
        this.q = q;
    }

    public List<String> getAppId() {
        return appId;
    }

    public void setAppId(List<String> appId) {
        this.appId = appId;
    }

    public List<String> getAppKey() {
        return appKey;
    }

    public void setAppKey(List<String> appKey) {
        this.appKey = appKey;
    }

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

}
