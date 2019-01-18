package xyz.qscftyjm.board;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import postutil.AsynTaskUtil;
import pub.devrel.easypermissions.EasyPermissions;
import tools.BitmapIOUtil;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;


public class AddMsgFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int RESULT_LOAD_IMAGE=10;
    private static int RESULT_CROP_IMAGE=20;
    private ImageView img_add_pic;
    private ImageView[] pics;
    private Button bt_submit;
    private EditText et_content;
    private boolean isHasPic=false;
    private String userid,token;
    private LinearLayout add_msg_img_bar;
    private String[] imagePath=new String[]{null,null,null};
    private Bitmap[] bitmaps=new Bitmap[]{null,null,null};
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
        add_msg_img_bar=view.findViewById(R.id.add_msg_img_bar);
        pics[0]=view.findViewById(R.id.add_msg_img0);
        pics[1]=view.findViewById(R.id.add_msg_img1);
        pics[2]=view.findViewById(R.id.add_msg_img2);
        bt_submit=view.findViewById(R.id.add_msg_submit);
        et_content=view.findViewById(R.id.add_msg_content);
        pics[0].setOnClickListener(this);
        pics[1].setOnClickListener(this);
        pics[2].setOnClickListener(this);

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
                if(content.length()<=0){ Toast.makeText(getActivity(),"留言不能为空！",Toast.LENGTH_LONG).show(); return; }

                isHasPic=(haspics[0]||haspics[1]||haspics[2]);
                if(!isHasPic){
                    // TODO 没有图片
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
                    Log.d("Pics","HasPic");
                    ArrayList<Bitmap> sendPicArray=new ArrayList<>();
                    ArrayList<String> hexPic=new ArrayList<>();
                    for (int i=0;i<2;i++){
                        if(haspics[i]&&bitmaps[i]!=null){
                            sendPicArray.add(bitmaps[i]);
                            try {
                                hexPic.add(BitmapIOUtil.bytesToHexString(BitmapIOUtil.ReadImage(imagePath[i])));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (hexPic.size()>0){
                        JSONArray jsonArray=new JSONArray(hexPic);
                        AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formSendMsg(userid, token, content, hexPic.size(), jsonArray.toString()), new AsynTaskUtil.AsynNetUtils.Callback() {
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
                    }
                }
            }
        });

        img_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_add_pic.setVisibility(View.GONE);
                add_msg_img_bar.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_msg_img0:
                ChoosePic(0);
                break;
            case R.id.add_msg_img1:
                if(haspics[0]){
                    //选择图片
                    ChoosePic(1);
                }
                break;
            case R.id.add_msg_img2:
                if(haspics[1]){
                    //选择图片\
                    ChoosePic(2);
                }
                break;
        }
    }

    private void ChoosePic(int i){
        getPermission();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE+i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO 打开存储权限
        if (requestCode <= RESULT_LOAD_IMAGE+2 && requestCode >= RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePath[requestCode-10] = c.getString(columnIndex);
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath[requestCode-10]);
            pics[requestCode-10].setImageBitmap(bitmap);
            bitmaps[requestCode-10]=bitmap;
            haspics[requestCode-10]=true;
            if(requestCode<12){
                pics[requestCode-9].setVisibility(View.VISIBLE);
            }

            c.close();

        } else if(requestCode==RESULT_CROP_IMAGE&&resultCode==Activity.RESULT_OK){
//            bitmap = BitmapFactory.decodeFile(temp_portrait.getPath());
//            //Log.d("Board","URI: "+temp_portrait.toString());
//            img_portrait.setImageBitmap(bitmap);
        } else {
            Log.d("AMF","requestCode: "+requestCode+" resultCode: "+resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
