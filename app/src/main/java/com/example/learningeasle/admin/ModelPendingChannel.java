package com.example.learningeasle.admin;

public class ModelPendingChannel {
    String ChannelName,Des,Imageurl;

    public ModelPendingChannel(String channelName, String des, String imageurl) {
        ChannelName = channelName;
        Des = des;
        Imageurl = imageurl;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}
