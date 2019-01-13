package tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;

import xyz.qscftyjm.board.LoginActivity;
import xyz.qscftyjm.board.R;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class AlertDialogUtil {

    private static int RESULT_LOAD_IMAGE=10;

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

    public static void makeChangeUserInfoDialog(final Context context){

        Bitmap bitmap;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.change_info_layout, null);
        builder.setView(view);

        builder.setCancelable(true);


        ImageView img_portrait=view.findViewById(R.id.change_info_portrait);
        TextView tv_userid=view.findViewById(R.id.change_info_userid);
        EditText ed_nickname=view.findViewById(R.id.change_info_nickname);
        EditText ed_email=view.findViewById(R.id.change_info_email);
        Button submit=view.findViewById(R.id.change_info_submit);
        Map<String, Object> userInfo=UserUtil.getUserInfo(context);
        img_portrait.setImageBitmap((Bitmap) userInfo.get("portrait"));
        tv_userid.setText((String) userInfo.get("userid"));
        ed_nickname.setText((String) userInfo.get("nickname"));
        ed_email.setText((String) userInfo.get("email"));
        final AlertDialog dialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Board","Submit change info");
                dialog.dismiss();
            }
        });

        img_portrait.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("Board","Pick image");
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult((Activity)context,intent, RESULT_LOAD_IMAGE,null);
            }
        });

        dialog.show();
    }



}