package com.example.apple.firebase;

/**
 * Created by apple on 01/04/18.
 */

public class Post {
    private String user_id, title, caption;

    public Post(String title, String caption) {
        this.title = title;
        this.caption = caption;
    }

    public Post(String user_id, String title, String caption) {
        this.user_id = user_id;
        this.title = title;
        this.caption = caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_id() {
        return user_id;
    }
}
