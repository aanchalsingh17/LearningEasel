package com.example.learningeasle.chats;

public class ModelChat {
    String message,receiver,sender,timestamp,isSeen;



    public ModelChat(String message, String receiver, String sender, String timestamp, String isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String isSeen() {
        return isSeen;
    }

    public void setSeen(String seen) {
        isSeen = seen;
    }
}
