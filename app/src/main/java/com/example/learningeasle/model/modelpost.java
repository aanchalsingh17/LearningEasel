package com.example.learningeasle.model;

import android.net.Uri;

public class modelpost {
    String pId, pTitle, pDesc, pTime, pImage,pName,url,pLikes,pComments,pType;

    public modelpost(String pId, String pImage, String pTitle, String pDesc, String pTime, String uName,
                     String url,String pLikes,String pComments,String pType) {
        this.pId = pId;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pDesc = pDesc;
        this.pTime = pTime;
        this.pName=uName;
        this.url=url;
        this.pLikes=pLikes;
        this.pComments=pComments;
        this.pType=pType;
    }

    public String getpComments() {
        return pComments;
    }

    public String getpType(){
        return pType;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getuImage(){
        return url;
    }

    public String getpName(){
        return pName;
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
