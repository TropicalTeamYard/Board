package tools;

public class StringCollector {

    public static String LOCAL_USER = "http://192.168.31.97:8080/user";
    public static String SERVER_USER = "http://39.108.120.239/api/user";

    public static String SERVER_MSG = "http://39.108.120.239/api/msgboard";

    public static String getUserServer(){
        return SERVER_USER;
    }

    public static String getMsgServer(){
        return SERVER_MSG;
    }

}
