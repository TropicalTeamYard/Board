package xyz.qscftyjm.board;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AlertDialogUtil {

    public static void makeRegisterResultDialog(final Context context, String userid, String nickname){

        final TextView ac = new TextView(context);
        ac.setHeight(120);
        ac.setLines(2);
        ac.setText("你的账号为 ： "+userid+"\n后续登录账号仅以该账号和密码登录有效");
        new AlertDialog.Builder(context).setTitle("注册成功，欢迎 "+nickname+" ！")
                .setView(ac)
                .setPositiveButton("跳转登录界面", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }).show();

    }

    public static int makeAddItemChoiceDialog(final Context context){

        final TextView tv = new TextView(context);
        int clickItem=-1;
        tv.setHeight(120);
        tv.setLines(1);
        tv.setText("12345");
        ListView listView=new ListView(context, null);
        ListAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" });
        listView.setAdapter(adapter);
        new AlertDialog.Builder(context).setTitle("title")
                .setView(tv).setView(listView).show().getWindow().setLayout(600, 800);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

            }
        });

        return clickItem;

    }

}