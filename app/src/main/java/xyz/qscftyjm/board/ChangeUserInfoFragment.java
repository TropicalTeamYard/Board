package xyz.qscftyjm.board;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import postutil.AsynTaskUtil;
import pub.devrel.easypermissions.EasyPermissions;
import tools.AlertDialogUtil;
import tools.ParamToString;
import tools.StringCollector;
import tools.TimeUtil;
import tools.UserUtil;


public class ChangeUserInfoFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks {
    private static int RESULT_LOAD_IMAGE=10;
    ImageView img_portrait;
    TextView tv_userid;
    EditText ed_nickname,ed_email;
    Button submit;
    Map<String, Object> userInfo;
    Bitmap bitmap;
    private String IMAGE_FILE_LOCATION_DIR;
//    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    String imagePath;
    private Uri imageUri; File temp_portrait;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public ChangeUserInfoFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( dm.widthPixels,  getDialog().getWindow().getAttributes().height );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.change_info_layout, container);

        img_portrait=view.findViewById(R.id.change_info_portrait);
        tv_userid=view.findViewById(R.id.change_info_userid);
        ed_nickname=view.findViewById(R.id.change_info_nickname);
        ed_email=view.findViewById(R.id.change_info_email);
        submit=view.findViewById(R.id.change_info_submit);
        userInfo= UserUtil.getUserInfo(getActivity());
        img_portrait.setImageBitmap((Bitmap) userInfo.get("portrait"));
        tv_userid.setText((String) userInfo.get("userid"));
        ed_nickname.setText((String) userInfo.get("nickname"));
        ed_email.setText((String) userInfo.get("email"));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Board", "Submit change info");
                SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
                Cursor cursor = database.query("userinfo", new String[]{"id", "userid", "nickname", "portrait", "email", "priority", "token"}, null, null, null, null, "id desc", "0,1");
                String token = null, userid = null;
                int id = 0;
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        Map<String,String> changeInfo=new HashMap<>();
                        do {
                            id = cursor.getInt(0);
                            userid = cursor.getString(1);
                            token = cursor.getString(6);
                        } while (cursor.moveToNext());

                        cursor.close();
                        final int finalId = id;

                        ContentValues values = new ContentValues();
                        if(ed_nickname.getText().toString().length()>=2&&ed_nickname.getText().toString().length()<=15){
                            values.put("nickname", ed_nickname.getText().toString());
                            changeInfo.put("nickname",ed_nickname.getText().toString());
                        } else {
                            Toast.makeText(getActivity(),"Nickname 长度应在2~15个字符",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(ed_email.getText().toString().length()>=6&&ed_email.getText().toString().length()<=30){
                            values.put("email", ed_email.getText().toString());
                            changeInfo.put("email",ed_email.getText().toString());
                        } else {
                            Toast.makeText(getActivity(),"Email 长度应在6~30个字符",Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if(bitmap!=null){
                            byte[] temp_pic=BitMapUtil.Bitmap2Bytes(bitmap);
                            values.put("portrait", BitMapUtil.Bitmap2Bytes(bitmap));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            changeInfo.put("portrait",BitmapIOUtils.byte2Base64StringFun(baos.toByteArray()));
                        }

                        AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formChangeUserInfo(userid, token, changeInfo), new AsynTaskUtil.AsynNetUtils.Callback() {
                            @Override
                            public void onResponse(String response) {
                                String result=response;
                                Log.d("Board",response);
                                JSONObject jsonObj;
                                if(response!=null) {
                                    try {
                                        jsonObj=new JSONObject(result);
                                        int code=jsonObj.optInt("code", -1);
                                        if(code==0) {

                                            //Toast.makeText(getActivity(), "用户数据修改成功", Toast.LENGTH_SHORT).show();

                                            SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
                                            Cursor cursor = database.query("userinfo", new String[]{"id", "userid", "nickname", "portrait", "email", "priority", "token"}, null, null, null, null, "id desc", "0,1");
                                            String token = null;//, userid = null;
                                            int id = 0;
                                            if (cursor.moveToFirst()) {
                                                if (cursor.getCount() > 0) {
                                                    Map<String, String> changeInfo = new HashMap<>();
                                                    do {
                                                        id = cursor.getInt(0);
                                                        //userid = cursor.getString(1);
                                                        //token = cursor.getString(6);
                                                    } while (cursor.moveToNext());

                                                    cursor.close();
                                                    final int finalId = id;
                                                    ContentValues values = new ContentValues();
                                                    token = jsonObj.optString("token", "00000000000000000000000000000000");
                                                    values.put("token", token);

                                                    database.update("userinfo", values, "id=?", new String[]{String.valueOf(finalId)});
                                                    //finish();
                                                }
                                            }

                                        } else if(code<0) {
                                            Toast.makeText(getActivity(),jsonObj.optString("msg","未知错误"),Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        database.update("userinfo", values, "id=?", new String[]{String.valueOf(finalId)});
                    }
                }
                dismiss();
            }
        });

        img_portrait.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("Board","Pick image");
                getPermission();
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//                getCropImage();
            }
        });

        return view;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO 打开存储权限
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePath = c.getString(columnIndex);
            bitmap= BitmapFactory.decodeFile(imagePath);
            img_portrait.setImageBitmap(bitmap);
            getCropImage(data.getData());
            c.close();

        } else if(requestCode==1&&resultCode==Activity.RESULT_OK){
            bitmap = BitmapFactory.decodeFile(temp_portrait.getPath());
            Log.d("Board","URI: "+temp_portrait.toString());

            //            Bundle bundle = data.getExtras();
//
//            if (bundle != null) {
//                //在这里获得了剪裁后的Bitmap对象，可以用于上传
//                Bitmap image = bundle.getParcelable("data");
//            }
//

            img_portrait.setImageBitmap(bitmap);
        } else {
            Log.d("Board","requestCode: "+requestCode+" resultCode: "+resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getCropImage(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);

//        intent.putExtra("return-data", true);

        intent.putExtra("return-data", false);
        IMAGE_FILE_LOCATION_DIR = getActivity().getExternalCacheDir()+ "/xyz.qscftyjm.board/";
        File temp_dir=new File(IMAGE_FILE_LOCATION_DIR);
        if(!temp_dir.exists()){
            temp_dir.mkdir();
        }
        temp_portrait=new File(temp_dir,"temp_portrait.jpg");
        try {
            if (temp_portrait.exists()) {
                temp_portrait.delete();
            }
            temp_portrait.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        imageUri = getImageContentUri(getActivity(),temp_portrait);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imageUri = FileProvider.getUriForFile(getActivity(),
//                    "xyz.qscftyjm.board", temp_portrait);
//        } else {
//            imageUri = Uri.fromFile(temp_portrait);
//        }

        imageUri=Uri.parse("file://"+IMAGE_FILE_LOCATION_DIR+"temp_portrait.jpg");
        Log.d("Board","PICURI: "+imageUri.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, 1);
    }

//    public static Uri getImageContentUri(Context context, File imageFile) {
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[]{MediaStore.Images.Media._ID},
//                MediaStore.Images.Media.DATA + "=? ",
//                new String[]{filePath}, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor
//                    .getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                return context.getContentResolver().insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
//    }

    private void getPermission() {
        if (EasyPermissions.hasPermissions(getActivity(), permissions)) {
            //Toast.makeText(getActivity(), "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
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
        //Toast.makeText(getActivity(), "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getActivity(), "请同意相关权限，否则无法选择图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        startActivity(new Intent(getActivity(),MoreInfoActivity.class));
    }
}
