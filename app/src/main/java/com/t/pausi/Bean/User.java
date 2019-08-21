package com.t.pausi.Bean;

public class User {

    public String name;
    public String receiver_name;
    public String receiver_profile;
    public String email;
    public String profileUrl;
    public String token;
    public String profilePicLink;
    public String key;
    public String receiver_key;
    public String message;
    public String prop_id;
    public String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRead;

    public String getProp_id() {
        return prop_id;
    }

    public void setProp_id(String prop_id) {
        this.prop_id = prop_id;
    }



    public String getReceiver_key() {
        return receiver_key;
    }

    public void setReceiver_key(String receiver_key) {
        this.receiver_key = receiver_key;
    }



    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_profile() {
        return receiver_profile;
    }

    public void setReceiver_profile(String receiver_profile) {
        this.receiver_profile = receiver_profile;
    }



    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }




    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfilePicLink() {
        return profilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        this.profilePicLink = profilePicLink;
    }

    public User() {
    }

    public User(String name, String email, String profileUrl, String token, String profilePicLink) {
        this.name = name;
        this.email = email;
        this.profileUrl = profileUrl;
        this.token = token;
        this.profilePicLink = profilePicLink;
    }
}
