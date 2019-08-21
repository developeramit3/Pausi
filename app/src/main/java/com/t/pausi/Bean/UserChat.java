package com.t.pausi.Bean;

import java.sql.Timestamp;

public class UserChat {

    public String content;
    public String senderName;
    public String receiverName;
    public String senderProfile;
    public String receiverProfile;
    public String fromID;
    public String toID;
    public String type;
    public boolean isRead;
    public int timestamp;
    public String location;
    public String node;
    public String property_id;

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }


    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getReceiverProfile() {
        return receiverProfile;
    }

    public void setReceiverProfile(String receiverProfile) {
        this.receiverProfile = receiverProfile;
    }


    public UserChat(String content, String senderName, String receiverName, String senderProfile, String receiverProfile, String fromID, String toID, String type, boolean isRead, int timestamp, String property_id) {
        this.content = content;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderProfile = senderProfile;
        this.receiverProfile = receiverProfile;
        this.fromID = fromID;
        this.toID = toID;
        this.type = type;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.property_id = property_id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(String senderProfile) {
        this.senderProfile = senderProfile;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public UserChat() {

    }
}
