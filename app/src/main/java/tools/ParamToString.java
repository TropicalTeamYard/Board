package tools;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public class ParamToString {

    public static String formLogin(String userid, String password) {
        userid=StringUtil.toURLEncoded(userid);
        return "method=login" + "&userid=" + userid + "&password=" + MD5Util.getMd5(password);

    }

    public static String formAutoLogin(String userid, String token) {
        userid=StringUtil.toURLEncoded(userid);
        return "method=autologin&userid=" + userid + "&token=" + token;
    }

    public static String formRegister(String nickname, String password) {
        nickname=StringUtil.toURLEncoded(nickname);
        return "method=register" + "&nickname=" + nickname + "&password=" + MD5Util.getMd5(password);

    }

    public static String formChangePassword(String userid, String password, String newpassword) {
        userid=StringUtil.toURLEncoded(userid);
        return "method=changepassword&userid=" + userid + "&password=" + password + "&newpassword=" + newpassword;
    }

    public static String formChangeUserInfo(String userid, String token, Map<String, String> updateInfo) {
        StringBuilder result = new StringBuilder("method=changeinfo&userid=" + StringUtil.toURLEncoded(userid) + "&token=" + token);
        for (String key : updateInfo.keySet()) {
            result.append("&").append(key).append("=").append(StringUtil.toURLEncoded(updateInfo.get(key)));
        }
        return result.toString();
    }

    public static String formGetPublicInfo(String userids) {
        return "method=getpublicinfo&userids=" + userids;
    }

    public static String formGetUSerInfo(String userid, String token) {
        return "method=getuserinfo&userid=" + userid + "&token=" + token;
    }

    public static String formGetMsg(String userid, String token, String id) {
        return "method=checknew&userid=" + userid + "&token=" + token + "&msgid=" + id;
    }

    public static String formSendMsg(String userid, String token, String content, int haspics, String pics) {
        userid=StringUtil.toURLEncoded(userid);
        content=StringUtil.toURLEncoded(content);
        return "method=add&userid=" + userid + "&content=" + content + "&token=" + token + "&haspics=" + haspics + "&pics=" + pics;
    }

    public static String formDelMsg(String userid, String token, String msgid) {
        return "method=deletemsg&userid=" + userid + "&token=" + token + "&msgid=" + msgid;
    }

    public static String formDelMsg(String userid, String token, int msgid) {
        return "method=deletemsg&userid=" + userid + "&token=" + token + "&msgid=" + msgid;
    }

    public static String formUpdatePublicUserInfo(ArrayList<Map<String, String>> userInfoMd5Array) {
        JSONArray jsonArray = new JSONArray(userInfoMd5Array);
        return "method=updatepublicinfo&usermap=" + jsonArray.toString();
    }

    public static String formAddComment(String msgid, String userid, String token, String comment){
        String result="method=comment&targetmsgid="+msgid+"&userid="+StringUtil.toURLEncoded(userid)+"&token="+StringUtil.toURLEncoded(token)+"&comment="+StringUtil.toURLEncoded(comment);
        return result;
    }

    public static String formCheckNewComment(){
        return null;
    }

    public static String formDelComment(){
        return null;
    }

}
