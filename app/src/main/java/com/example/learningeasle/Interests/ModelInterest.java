package com.example.learningeasle.Interests;

public class ModelInterest {
    String channelName,value;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ModelInterest(String channelName, String value) {
        this.channelName = channelName;
        this.value = value;
    }
}
