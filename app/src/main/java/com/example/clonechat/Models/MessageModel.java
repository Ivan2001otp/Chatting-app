package com.example.clonechat.Models;

import androidx.annotation.NonNull;

public class MessageModel {
    private String unique_Uid,sms_message;
    private Long time;
    private String message_id;

    public MessageModel(){}

    public MessageModel( String uid, String sms_message, Long time) {
        this.unique_Uid = uid;
        this.sms_message = sms_message;
        this.time = time;
    }

    public MessageModel(@NonNull String uid, String sms_message) {
        this.unique_Uid = uid;
        this.sms_message = sms_message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getUnique_Uid() {
        return this.unique_Uid;
    }

    public void setUid(String uid) {
        unique_Uid = uid;
    }

    public String getSms_message() {
        return sms_message;
    }

    public void setSms_message(String sms_message) {
        this.sms_message = sms_message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
