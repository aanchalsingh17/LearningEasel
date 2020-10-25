package com.example.learningeasle.model;

public class ModelComment {
    String cId,comment,timestamp,uid,uDp,uName,postId;

    public ModelComment(String cId, String comment, String timestamp, String uid, String uDp, String uName,String postId) {
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.uDp = uDp;
        this.uName = uName;
        this.postId = postId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
