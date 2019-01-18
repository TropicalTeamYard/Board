package xyz.qscftyjm.board;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tools.BitmapUtil;
import tools.BoardDBHelper;


public class MainUserFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "Board";

    private int priority,id;
    private String userid="",nickname="",email="";
    private Bitmap bitmap_portrait;
    SQLiteDatabase database;
    private View view;
    private Button bt_login_info,bt_user_info;
    private TextView tv_nickname,tv_userid;
    private ImageView img_head_portrait;


    public MainUserFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_user, container, false);

        database= BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();

        bt_login_info=view.findViewById(R.id.user_bt_login_info);
        bt_user_info=view.findViewById(R.id.user_bt_more);
        tv_nickname=view.findViewById(R.id.user_nickname);
        tv_userid=view.findViewById(R.id.user_userid);
        img_head_portrait=view.findViewById(R.id.user_img_portrait);
        bt_login_info.setOnClickListener(this);
        bt_user_info.setOnClickListener(this);

        setUserInfo(tv_userid,tv_nickname,img_head_portrait,bt_login_info,database);

        Log.d(TAG, "userid "+userid+" nickname "+nickname+" email "+email+" priority "+priority);
        Log.d(TAG,tv_userid.getText().toString());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_bt_login_info:
                startActivity(new Intent(this.getActivity(),LoginActivity.class));
                break;
            case R.id.user_bt_more:
                Log.d(TAG,"MORE");
                Intent intent=new Intent(getActivity(),MoreInfoActivity.class);
                startActivity(intent);
                break;
                default:
                    Log.d(TAG,"Button Not Defined");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo(tv_userid,tv_nickname,img_head_portrait,bt_login_info,database);
    }

    public void setUserInfo(TextView tv_userid, TextView tv_nickname, ImageView img_portrait, Button login_info, SQLiteDatabase db){
        Cursor cursor=db.query("userinfo",new String[]{"userid","nickname","portrait","email","priority","token"},null,null,null,null,"id desc","0,1");
        if(cursor.moveToFirst()&&cursor.getCount()>0){
            do{
                this.userid=cursor.getString(0);
                tv_userid.setText("ID : "+this.userid);
                this.nickname=cursor.getString(1);
                tv_nickname.setText("Hi, "+this.nickname);

                img_portrait.setImageBitmap(BitmapUtil.getHexBitmap(getActivity(),new String(cursor.getBlob(2))));

                this.email=cursor.getString(3);
                this.priority=cursor.getInt(4);

            }while (cursor.moveToNext());
        }
        cursor.close();
    }
    
}
