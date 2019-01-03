package xyz.qscftyjm.board;

import android.graphics.Bitmap;

class Msg {

    private int id;
    private String nickname;
    private String time;
    private String content;
    private Bitmap portrait;
    private boolean hasPic;
    private Bitmap[] picture=null;

    public Msg(int id, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture) {
        this.id = id;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = hasPic;
        this.picture = picture;
    }

    public Msg(int id, String nickname, String time, String content, Bitmap portrait) {
        this.id = id;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = false;
    }

    public Msg(String nickname, String time, String content, Bitmap portrait) {
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = false;
    }

    public Msg(String nickname, String time, String content) {
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.hasPic = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getPortrait() {
        return portrait;
    }

    public void setPortrait(Bitmap portrait) {
        this.portrait = portrait;
    }

    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }

    public Bitmap[] getPicture() {
        return picture;
    }

    public void setPicture(Bitmap[] picture) {
        this.picture = picture;
        this.hasPic = (picture==null);
    }
}
