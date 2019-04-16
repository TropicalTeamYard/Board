package tools;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getTime() {
        String time;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        time = sdf.format(date);
        System.out.println(time);

        return time;
    }

    public static boolean checkIsOverTime(String lastchecketime) {
        boolean flag = true;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date lastdate = sdf.parse(lastchecketime);
            Date nowdate = new Date();
            long delta = nowdate.getTime() - lastdate.getTime();
            Log.d("Calendar", "delta time : " + delta);
            if (delta >= 0 && delta <= 1000 * 3600 * 24 * 7) {
                //一个星期不进行登录，判断用户登录过期
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }


}