package xyz.qscftyjm.board;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import postutil.AsynTaskUtil;
import pub.devrel.easypermissions.EasyPermissions;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;


public class AddMsgFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks {

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImageView img_add_pic;
    private ImageView[] pics;
    private Button bt_submit;
    private EditText et_content;
    private boolean isHasPic=false;
    private String userid,token;
    private boolean[] haspics=new boolean[]{false,false,false};

    SQLiteDatabase database;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( dm.widthPixels,  getDialog().getWindow().getAttributes().height );
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_add_msg, container);
        database = BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
        pics=new ImageView[3];
        img_add_pic=view.findViewById(R.id.add_msg_add_img);
        pics[0]=view.findViewById(R.id.add_msg_img0);
        pics[1]=view.findViewById(R.id.add_msg_img1);
        pics[2]=view.findViewById(R.id.add_msg_img2);
        bt_submit=view.findViewById(R.id.add_msg_submit);
        et_content=view.findViewById(R.id.add_msg_content);

        Cursor cursor=database.query("userinfo", new String[]{"userid", "token"}, null, null, null, null, "id desc", "0,1");

        if(cursor.moveToFirst()&&cursor.getCount()>0){
            userid=cursor.getString(0);
            token=cursor.getString(1);
        } else {
            Toast.makeText(getActivity(),"请登录账号！",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),LoginActivity.class));
            dismiss();
        }
        cursor.close();

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=et_content.getText().toString();

                if(!isHasPic){
                    // TODO 没有图片
                    if(content.length()<=0){ Toast.makeText(getActivity(),"留言不能为空！",Toast.LENGTH_LONG).show(); return; }
                    AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formSendMsg(userid, token, content, 0, null), new AsynTaskUtil.AsynNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj;
                            int code;
                            if(response!=null){
                                try {
                                    jsonObj=new JSONObject(response);
                                    code=jsonObj.optInt("code",-1);
                                    Logd(jsonObj.optString("msg","unknown error"));
                                    if(code==0){
                                        dismiss();
                                    } else if(code!=-1) {
                                        if(code==-101){
                                            Intent intent=new Intent(getActivity(),LoginActivity.class);
                                            Bundle bundle=new Bundle();
                                            bundle.putString("userid",userid);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            dismiss();
                                        } else if(code==-99){
                                            makeToast("留言发生错误");
                                        }
                                    } else {
                                        Logd("sever error");
                                    }
                                } catch (JSONException e) {
                                    Logd("服务器返回数据错误");
                                }
                            }
                        }
                    });
                } else {
                    // TODO 有图片

                }
            }
        });

        return view;
    }

    private void Logd(String log) {
        try {
            Log.d("AMF",log);
        } catch (Exception ignored){}

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void getPermission() {
        if (!EasyPermissions.hasPermissions(getActivity(), permissions)) {
            EasyPermissions.requestPermissions(this, "我们需要获取您的相册使用权限", 1, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getActivity(), "请同意相关权限，否则无法选择图片", Toast.LENGTH_SHORT).show();
    }

    public void makeToast(String str){
        try {
            Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
        } catch (Exception ignored){ }

    }
}
