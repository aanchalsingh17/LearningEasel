package com.example.learningeasle.model;

public class modelpost {
    String pId, pTitle, pDesc, pTime, pImage,uImage;

    public modelpost(String pId, String pImage, String pTitle, String pDesc, String pTime,String uImage) {
        this.pId = pId;
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pDesc = pDesc;
        this.pTime = pTime;
        this.uImage=uImage;
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
