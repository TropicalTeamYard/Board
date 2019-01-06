package tools;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ParamToJSON {

    public static String formLoginJson(String userid, String password) {
        String result="method=login"+"&username="+userid+"&password="+password+"&devicetype=mobile";

        return result;

    }

    public static String formRegisterJson(String nickname, String password) {

        String result="method=register2"+"&nickname="+nickname+"&password="+password;
        return result;

    }

    public static String formCangePasswordJson(String account, String oldPassword, String newPassword) {
        String result=null;

        Map<String, Object> UserInfo=new HashMap<String, Object>();
        UserInfo.put("Type", "user");
        UserInfo.put("Method", "changepassword");
        Map<String, Object> Data=new HashMap<String, Object>();
        Map<String, Object> InnerData=new HashMap<String, Object>();

        oldPassword=MD5Util.getMd5(oldPassword);
        newPassword=MD5Util.getMd5(newPassword);

        InnerData.put("NewPassword", newPassword);
        InnerData.put("OldPassword", oldPassword);
        Map<String, Object> User=new HashMap<String, Object>();
        User.put("PassWord", oldPassword);
        User.put("ID", -1);
        User.put("Account", account);
        User.put("UserName", "");
        User.put("Priority", 0);
        Data.put("InnerData", InnerData);
        Data.put("User", User);
        UserInfo.put("Data", Data);

        result="json="+new JSONObject(UserInfo).toString();

        return result;

    }

    public static String formGetGlobalMsgJson(int lastId) {
        String result=null;
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("Type", "message");
        map.put("Method", "globalget");
        map.put("Data", lastId);
        result="json="+new JSONObject(map).toString();
        return result;
    }

    public static String formSendGlobalMsgJson(String Account, String Password, String Content) {
        String result=null;
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("Type", "message");
        map.put("Method", "globalsend");
        Map<String, Object> data=new HashMap<String, Object>();
        Map<String, Object> user=new HashMap<String, Object>();
        data.put("InnerData", Content);
        user.put("PassWord", Password);
        user.put("Account", Account);
        user.put("ID", -1);
        user.put("Priority", 0);
        user.put("UserName", "");
        data.put("User", user);
        map.put("Data", data);
        result="json="+new JSONObject(map).toString();
        return result;
    }

}
