package tools;

import android.graphics.Bitmap;

public class Msg {

    private int id;
    private String userid;
    private String nickname;
    private String time;
    private String content;
    private Bitmap portrait;
    private int hasPic;
    private Bitmap[] picture = null;

    public Msg() {
        this.hasPic = 0;
    }

    public Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, int hasPic, Bitmap[] picture) {
        this.id = id;
        this.userid = userid;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = hasPic;
        this.picture = picture;
    }

    public Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait) {
        this.id = id;
        this.userid = userid;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = 0;
    }

    public Msg(String userid, String nickname, String time, String content, Bitmap portrait) {
        this.userid = userid;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.portrait = portrait;
        this.hasPic = 0;
    }

    public Msg(String userid, String nickname, String time, String content) {
        this.userid = userid;
        this.nickname = nickname;
        this.time = time;
        this.content = content;
        this.hasPic = 0;
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

    public int getHasPic() {
        return hasPic;
    }

    public void setHasPic(int hasPic) {
        this.hasPic = hasPic;
    }

    public Bitmap[] getPicture() {
        return picture;
    }

    public void setPicture(Bitmap[] picture) {
        this.picture = picture;
        this.hasPic = (picture != null) ? picture.length : 0;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
