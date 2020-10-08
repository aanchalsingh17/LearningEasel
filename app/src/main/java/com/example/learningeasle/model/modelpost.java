package com.example.learningeasle.model;

import android.net.Uri;

public class modelpost {
    String pId, pTitle, pDesc, pTime, pImage,uName,url;

    public modelpost(String pId, String pImage, String pTitle, String pDesc, String pTime, String uName, String url) {
        this.pId = pId;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pDesc = pDesc;
        this.pTime = pTime;
        this.uName=uName;
        this.url=url;
    }

    public String getuImage(){
        return url;
    }

    public String getuName(){
        return uName;
    }
    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

}
