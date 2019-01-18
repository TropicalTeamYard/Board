package xyz.qscftyjm.board;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tools.BitmapUtil;
import tools.BoardDBHelper;

public class MoreInfoActivity extends AppCompatActivity {

    public final static String TAG = "Board";

    private TextView tv_userid,tv_nickname,tv_email;
    private ImageView img_portrait;
    private Button bt_change_info,bt_change_password;
    String userid,nickname,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        tv_userid=findViewById(R.id.user_info_userid);
        tv_nickname=findViewById(R.id.user_info_nickname);
        tv_email=findViewById(R.id.user_info_email);
        img_portrait=findViewById(R.id.user_info_portrait);
        bt_change_info=findViewById(R.id.user_info_change_info);
        bt_change_password=findViewById(R.id.user_info_change_password);

        final int count=setUserInfo(tv_userid,tv_nickname,tv_email,img_portrait);


        bt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Change Info");
                // TODO 弹出窗口更改信息
                if(count>0){
                    ChangePasswordFragment fragment=new ChangePasswordFragment();
                    fragment.show(getSupportFragmentManager(),"changepassword");
                } else {
                    Toast.makeText(MoreInfoActivity.this,"请登录账号",Toast.LENGTH_LONG).show();
                }
            }
        });

        bt_change_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Change Info");
                // TODO 弹出窗口更改信息
                if(count>0){
                    ChangeUserInfoFragment fragment=new ChangeUserInfoFragment();
                    fragment.show(getFragmentManager(),"changeinfo");
                } else {
                    Toast.makeText(MoreInfoActivity.this,"请登录账号",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public int setUserInfo(TextView tv_userid, TextView tv_nickname,TextView tv_email, ImageView img_portrait){
        SQLiteDatabase db= BoardDBHelper.getMsgDBHelper(MoreInfoActivity.this).getWritableDatabase();
        Cursor cursor=db.query("userinfo",new String[]{"userid","nickname","portrait","email","priority","token"},null,null,null,null,"id desc","0,1");
        String token;int count=0;
        if(cursor.moveToFirst()){
            count=cursor.getCount();
            if(count>0){
                do{
                    this.userid=cursor.getString(0);
                    tv_userid.setText(this.userid);
                    this.nickname=cursor.getString(1);
                    tv_nickname.setText(nickname);

                    img_portrait.setImageBitmap(BitmapUtil.getHexBitmap(MoreInfoActivity.this,new String(cursor.getBlob(2))));

                    this.email=cursor.getString(3);
                    tv_email.setText(this.email);
                    token=cursor.getString(5);
                }while (cursor.moveToNext());
            }
        }
        cursor.close();
        return count;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo(tv_userid, tv_nickname,tv_email, img_portrait);
    }

}
