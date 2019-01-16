package tools;

public class StringCollector {

    public static String LOCAL_USER = "http://192.168.31.97:8080/board/user";
    public static String LOCAL_MSG = "http://192.168.31.97:8080/board/msg";
    public static String SERVER_USER = "http://101.132.122.143:8080/board/user";
    public static String SERVER_MSG = "http://101.132.122.143:8080/board/msg";

    public static String getUserServer(){
        return SERVER_USER;
    }

    public static String getMsgServer(){
        return SERVER_MSG;
    }

}
