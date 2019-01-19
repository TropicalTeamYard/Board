package tools;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ParamToString {

    public static String formLogin(String userid, String password) {
        //method=login&userid=10001&password=E10ADC3949BA59ABBE56E057F20F883E
        return "method=login" + "&userid=" + userid + "&password=" + MD5Util.getMd5(password);

    }

    public static String formAutoLogin(String userid, String token) {
        //method=autologin&userid=10001&token=f2b03e3a64c142519fd1066714fa217b
        return "method=autologin&userid=" + userid + "&token=" + token;
    }

    public static String formRegister(String nickname, String password) {
        //method=register&nickname=test&password=E10ADC3949BA59ABBE56E057F20F883E
        return "method=register" + "&nickname=" + nickname + "&password=" + MD5Util.getMd5(password);

    }

    public static String formChangePassword(String userid, String password, String newpassword) {
        //method=changepassword&userid=10003&password=1222211221212121&newpassword=E10ADC3949BA59ABBE56E057F20F883E
        return "method=changepassword&userid=" + userid + "&password=" + password + "&newpassword=" + newpassword;
    }


    public static String formChangeUserInfo(String userid, String token, Map<String, String> updateInfo) {
        //method=changeinfo&userid=10036&token=ffe2e588c0d34cceb82044acb6532f12&nickname=test10036&portrait=00000000&email=123456@gmail.com
        StringBuilder result = new StringBuilder("method=changeinfo&userid=" + userid + "&token=" + token);
        for (String key : updateInfo.keySet()) {
            result.append("&").append(key).append("=").append(updateInfo.get(key));
        }
        Log.d("Board", "result: " + result);
        return result.toString();
    }

    public static String formGetPublicInfo(String[] userids) {
        //method=getpublicinfo&userids=['10001','10041','100']
        Log.d("Board", "userids: " + Arrays.toString(userids));
        return "method=getpublicinfo&userids=" + userids.toString();
    }

    public static String formGetPublicInfo(String userids) {
        //method=getpublicinfo&userids=['10001','10041','100']
        //Log.d("Board","userids: "+userids);
        return "method=getpublicinfo&userids=" + userids;
    }

    public static String formGetUSerInfo(String userid, String token) {
        //method=getuserinfo&userid=10001&token=f0956e4857564917ba13008debcd6432
        return "method=getuserinfo&userid=" + userid + "&token=" + token;
    }

    public static String formGetMsg(String userid, String token, String id) {
        //Log.d("Board",result);
        return "method=checknew&userid=" + userid + "&token=" + token + "&msgid=" + id;
    }

    public static String formSendMsg(String userid, String token, String content, int haspics, String pics) {
        String result = "method=add&userid=" + userid + "&content=" + content + "&token=" + token + "&haspics=" + haspics + "&pics=" + pics;
        Log.d("SendMsg", result);
        return result;
    }

    public static String formDelMsg(String userid, String token, String msgid) {
        return "method=deletemsg&userid=" + userid + "&token=" + token + "&msgid=" + msgid;
    }

    public static String formDelMsg(String userid, String token, int msgid) {
        return "method=deletemsg&userid=" + userid + "&token=" + token + "&msgid=" + msgid;
    }

    public static String formUpdatePublicUserInfo(ArrayList<Map<String, String>> userInfoMd5Array) {
        JSONArray jsonArray = new JSONArray(userInfoMd5Array);
        //Log.d("UUI",result);
        return "method=updatepublicinfo&usermap=" + jsonArray.toString();
    }
}
