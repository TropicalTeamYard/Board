package tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import xyz.qscftyjm.board.LoginActivity;

public class AlertDialogUtil {

    private static final int RESULT_LOAD_IMAGE = 10;

    public static void makeRegisterResultDialog(final Context context, String userid, String nickname) {

        final TextView ac = new TextView(context);
        ac.setHeight(120);
        ac.setLines(2);
        ac.setText("你的账号为 ： " + userid + "\n后续登录账号仅以该账号和密码登录有效");
        new AlertDialog.Builder(context).setTitle("注册成功，欢迎 " + nickname + " ！")
                .setView(ac)
                .setPositiveButton("跳转登录界面", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }).show();

    }

}