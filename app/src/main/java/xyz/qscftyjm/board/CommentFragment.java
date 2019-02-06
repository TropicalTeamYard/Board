package xyz.qscftyjm.board;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tools.BoardDBHelper;


public class CommentFragment extends DialogFragment {

    private Context mContext;
    private EditText et_comment_content;
    private Button bt_submit;
    private SQLiteDatabase database;

    public CommentFragment() {
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
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=et_comment_content.getText().toString();
                if(comment.equals("")||comment.length()<1){
                    Toast.makeText(mContext,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                } else {

                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

}
