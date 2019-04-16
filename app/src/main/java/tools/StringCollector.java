package tools;

public class StringCollector {

    private static final String LOCAL_USER = "http://192.168.31.97:8080/board/user";
    private static final String LOCAL_MSG = "http://192.168.31.97:8080/board/msg";
    private static final String SERVER_USER = "http://101.132.122.143:8080/board/user";
    private static final String SERVER_MSG = "http://101.132.122.143:8080/board/msg";

    private static String getSeverMode() {
        return "LOCAL";
    }

    public static String getUserServer() {
        if (getSeverMode().equals("SERVER")) {
            return SERVER_USER;
        } else {
            return LOCAL_USER;
        }

    }

    public static String getMsgServer() {

        if (getSeverMode().equals("SERVER")) {
            return SERVER_MSG;
        } else {
            return LOCAL_MSG;
        }
    }

}
