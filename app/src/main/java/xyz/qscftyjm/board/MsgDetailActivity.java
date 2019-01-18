package xyz.qscftyjm.board;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import tools.BitmapUtil;
import tools.BoardDBHelper;

public class MsgDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase database;
    private String msgid,userid,nickname,time,content;
    private int haspic=0;
    public String comment;
    private TextView tv_time,tv_nickname,tv_content;
    private Cursor cursor;
    private ImageView[] pics=new ImageView[]{null,null,null};
    private ImageView portrait;
    private ArrayList<Bitmap> pictures;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database= BoardDBHelper.getMsgDBHelper(this).getWritableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        Bundle bundle=this.getIntent().getExtras();
        pics[0]=findViewById(R.id.msg_detail_pic_0);
        pics[1]=findViewById(R.id.msg_detail_pic_1);
        pics[2]=findViewById(R.id.msg_detail_pic_2);
        portrait=findViewById(R.id.msg_detail_head_portrait);
        tv_nickname=findViewById(R.id.msg_detail_nickname);
        tv_time=findViewById(R.id.msg_detail_time);
        tv_content=findViewById(R.id.msg_detail_content);

        pictures=new ArrayList<>();
        if(bundle!=null&&bundle.containsKey("msgid")){
            msgid=String.valueOf(bundle.getInt("msgid"));
            cursor=database.query("msg",new String[]{"id","userid","time","content","haspic","picture","comment"}, "id=?", new String[]{msgid}, null, null, null, null);
            if(cursor.moveToFirst()&&cursor.getCount()>0){
                userid=cursor.getString(1);
                time=cursor.getString(2);
                content=cursor.getString(3);
                tv_time.setText(time);
                tv_content.setText(content);
                haspic=cursor.getInt(4);
                if(haspic>0){
                    try {
                        JSONArray jsonArray=new JSONArray(new String(cursor.getBlob(5)));
                        haspic=jsonArray.length();
                        Log.d("MDA","pic num: "+haspic);
                        for(int i=0;i<haspic;i++){
                            pictures.add(BitmapUtil.getHexBitmap(MsgDetailActivity.this,jsonArray.optString(i)));
                            pics[i].setVisibility(View.VISIBLE);
                            pics[i].setImageBitmap(pictures.get(i));
                        }
                    } catch (JSONException ignored) { }
                }
            }
            cursor.close();
            cursor=database.query("publicinfo",new String[]{"userid","nickname","portrait"}, "userid=?", new String[]{userid}, null, null, null, null);
            if(cursor.moveToFirst()&&cursor.getCount()>0){
                nickname=cursor.getString(1);
                tv_nickname.setText(nickname);
                portrait.setImageBitmap(BitmapUtil.getHexBitmap(MsgDetailActivity.this,new String(cursor.getBlob(2))));
            }
        } else {finish();}
        database.close();
    }

    @Override
    public void onClick(View v) {

    }
}
