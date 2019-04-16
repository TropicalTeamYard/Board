package xyz.qscftyjm.board;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import postutil.AsyncTaskUtil;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;


public class CommentFragment extends DialogFragment {

    private Context mContext;
    private EditText et_comment_content;
    private Button bt_submit;
    private SQLiteDatabase database;
    int msgid=-1;
    public CommentFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, Objects.requireNonNull(getDialog().getWindow()).getAttributes().height);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_comment, container, false);
        et_comment_content=view.findViewById(R.id.comment_msg_content);
        bt_submit=view.findViewById(R.id.comment_msg_submit);
        database= BoardDBHelper.getMsgDBHelper(mContext).getWritableDatabase();
        Bundle bundle=this.getArguments();
        if(bundle!=null&&bundle.containsKey("msgid")){
            msgid=bundle.getInt("msgid");
        } else {
            dismiss();
        }

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgid<0){return;}
                String comment=et_comment_content.getText().toString();
                if(comment.equals("")||comment.length()<1){
                    Toast.makeText(mContext,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Cursor cursor = database.query("userinfo", new String[]{"userid", "token"}, null, null, null, null, "id desc", "0,1");
                    if (cursor.moveToFirst() && cursor.getCount() > 0) {
                        final String userid = cursor.getString(0);
                        String token = cursor.getString(1);
                        AsyncTaskUtil.AsyncNetUtils.post(StringCollector.getMsgServer(), ParamToString.formAddComment(String.valueOf(msgid), userid, token, comment), new AsyncTaskUtil.AsyncNetUtils.Callback() {
                            @Override
                            public void onResponse(String response) {
                                //Toast.makeText(mContext,"comment : "+response,Toast.LENGTH_SHORT).show();
                                JSONObject jsonObj;
                                int code;
                                if (response != null) {
                                    try {
                                        jsonObj = new JSONObject(response);
                                        code = jsonObj.optInt("code", -1);
                                        Logd(jsonObj.optString("msg", "unknown error"));
                                        if (code == 0) {
                                            makeToast("评论成功");
                                            dismiss();
                                        } else if (code != -1) {
                                            if (code == -101) {
                                                Intent intent = new Intent(mContext, LoginActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("userid", userid);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                dismiss();
                                            } else if (code == -99) {
                                                makeToast("评论发生错误");
                                            } else if(code==-102||code==-103) {
                                                makeToast("该留言不存在，可能已被删除");
                                            }
                                        } else {
                                            Logd("sever error");
                                        }
                                    } catch (JSONException e) {
                                        Logd("服务器返回数据错误");
                                        makeToast("服务器返回数据错误");
                                    }
                                }
                            }
                        });
                    } else {
                        //TODO 登录
                    }
                }
            }
        });
        return view;
    }

    private void makeToast(String info) {
        if(mContext==null||info==null||info.length()==0){return;}
        Toast.makeText(mContext,info,Toast.LENGTH_SHORT).show();
    }

    private void Logd(String log) {
        if(log==null){return;}
        Log.d("CF",log);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

}
