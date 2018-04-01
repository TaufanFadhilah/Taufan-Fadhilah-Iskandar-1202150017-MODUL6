package com.example.apple.firebase;

/**
 * Created by apple on 01/04/18.
 */

public class User
{
    private String uid;

    public User(String uid) {
        this.uid = uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
