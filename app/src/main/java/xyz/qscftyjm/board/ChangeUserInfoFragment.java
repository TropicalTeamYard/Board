package xyz.qscftyjm.board;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import tools.UserUtil;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

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
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
                Log.d("Board","Submit change info");
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
//            Log.d("Board",data.getData().toString());
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(getImageContentUri(getActivity(),temp_portrait)));

            } catch (FileNotFoundException e) {
                Toast.makeText(getActivity(),"文件存储权限未打开",Toast.LENGTH_LONG).show();
                bitmap=BitMapUtil.getDefaultPortrait(getActivity());
                e.printStackTrace();
            }
//            Bundle bundle = data.getExtras();
//
//            if (bundle != null) {
//                //在这里获得了剪裁后的Bitmap对象，可以用于上传
//                Bitmap image = bundle.getParcelable("data");
//            }
//
//            img_portrait.setImageBitmap(bitmap);
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);

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
        //imageUri = getImageContentUri(getActivity(),temp_portrait);

        imageUri = getImageContentUri(getActivity(),temp_portrait);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imageUri = FileProvider.getUriForFile(getActivity(),
//                    "xyz.qscftyjm.board", temp_portrait);
//        } else {
//            imageUri = Uri.fromFile(temp_portrait);
//        }
        Log.d("Board","PICURI: "+imageUri.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, 1);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void getPermission() {
        if (EasyPermissions.hasPermissions(getActivity(), permissions)) {
            //已经打开权限
            Toast.makeText(getActivity(), "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(getActivity(), "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getActivity(), "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}