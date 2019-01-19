package xyz.qscftyjm.board;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import tools.BitmapUtil;
import tools.BoardDBHelper;

public class MsgDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public String comment;
    private SQLiteDatabase database;
    private String msgid, userid, nickname, time, content;
    private int haspic = 0;
    private TextView tv_time, tv_nickname, tv_content;
    private Cursor cursor;
    private ImageView[] pics = new ImageView[]{null, null, null};
    private ImageView portrait, big_pic;
    private ArrayList<Bitmap> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = BoardDBHelper.getMsgDBHelper(this).getWritableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        Bundle bundle = this.getIntent().getExtras();
        pics[0] = findViewById(R.id.msg_detail_pic_0);
        pics[1] = findViewById(R.id.msg_detail_pic_1);
        pics[2] = findViewById(R.id.msg_detail_pic_2);
        big_pic = findViewById(R.id.msg_detail_big_pic);
        portrait = findViewById(R.id.msg_detail_head_portrait);
        tv_nickname = findViewById(R.id.msg_detail_nickname);
        tv_time = findViewById(R.id.msg_detail_time);
        tv_content = findViewById(R.id.msg_detail_content);

        for (int i = 0; i < 3; i++) {
            pics[i].setOnClickListener(this);
        }

        pictures = new ArrayList<>();
        if (bundle != null && bundle.containsKey("msgid")) {
            msgid = String.valueOf(bundle.getInt("msgid"));
            cursor = database.query("msg", new String[]{"id", "userid", "time", "content", "haspic", "picture", "comment"}, "id=?", new String[]{msgid}, null, null, null, null);
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                userid = cursor.getString(1);
                time = cursor.getString(2);
                content = cursor.getString(3);
                tv_time.setText(time);
                tv_content.setText(content);
                haspic = cursor.getInt(4);
                if (haspic > 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(new String(cursor.getBlob(5)));
                        haspic = jsonArray.length();
                        Log.d("MDA", "pic num: " + haspic);
                        for (int i = 0; i < haspic; i++) {
                            pictures.add(BitmapUtil.getHexBitmap(MsgDetailActivity.this, jsonArray.optString(i)));
                            pics[i].setVisibility(View.VISIBLE);
                            pics[i].setImageBitmap(pictures.get(i));
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }
            cursor.close();
            cursor = database.query("publicinfo", new String[]{"userid", "nickname", "portrait"}, "userid=?", new String[]{userid}, null, null, null, null);
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                nickname = cursor.getString(1);
                tv_nickname.setText(nickname);
                portrait.setImageBitmap(BitmapUtil.getHexBitmap(MsgDetailActivity.this, new String(cursor.getBlob(2))));
            }
        } else {
            finish();
        }
        database.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_detail_pic_0:
                if (big_pic.getVisibility() != View.VISIBLE) {
                    big_pic.setVisibility(View.VISIBLE);
                }
                if (haspic < 1 && pics[0].getDrawable() == null) {
                    break;
                }
                big_pic.setImageDrawable(pics[0].getDrawable());
                break;
            case R.id.msg_detail_pic_1:
                if (big_pic.getVisibility() != View.VISIBLE) {
                    big_pic.setVisibility(View.VISIBLE);
                }
                if (haspic < 2 && pics[1].getDrawable() == null) {
                    break;
                }
                big_pic.setImageDrawable(pics[1].getDrawable());
                break;
            case R.id.msg_detail_pic_2:
                if (big_pic.getVisibility() != View.VISIBLE) {
                    big_pic.setVisibility(View.VISIBLE);
                }
                if (haspic < 3 && pics[2].getDrawable() == null) {
                    break;
                }
                big_pic.setImageDrawable(pics[2].getDrawable());
                break;
            default:
                Log.d("MDA", "NOT DEFINED CLICK");
        }
    }
}
