package xyz.qscftyjm.board;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import postutil.AsynTaskUtil;
import pub.devrel.easypermissions.EasyPermissions;
import tools.BitmapIOUtil;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;


public class AddMsgFragment extends DialogFragment implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 10;
    private static int RESULT_CROP_IMAGE = 20;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImageView img_add_pic;
    private ImageView[] pics;
    private Button bt_submit;
    private EditText et_content;
    private boolean isHasPic = false;
    private String userid, token;
    private LinearLayout add_msg_img_bar;
    private String[] imagePath = new String[]{null, null, null};
    private Bitmap[] bitmaps = new Bitmap[]{null, null, null};
    private boolean[] haspics = new boolean[]{false, false, false};
    private String IMAGE_FILE_LOCATION_DIR;
    private Uri[] imageUri = new Uri[]{null, null, null};
    private File[] temp_picture = new File[]{null, null, null};

    private SQLiteDatabase database;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, Objects.requireNonNull(getDialog().getWindow()).getAttributes().height);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_add_msg, container);
        database = BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
        pics = new ImageView[3];
        img_add_pic = view.findViewById(R.id.add_msg_add_img);
        add_msg_img_bar = view.findViewById(R.id.add_msg_img_bar);
        pics[0] = view.findViewById(R.id.add_msg_img0);
        pics[1] = view.findViewById(R.id.add_msg_img1);
        pics[2] = view.findViewById(R.id.add_msg_img2);
        bt_submit = view.findViewById(R.id.add_msg_submit);
        et_content = view.findViewById(R.id.add_msg_content);
        pics[0].setOnClickListener(this);
        pics[1].setOnClickListener(this);
        pics[2].setOnClickListener(this);

        Cursor cursor = database.query("userinfo", new String[]{"userid", "token"}, null, null, null, null, "id desc", "0,1");

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            userid = cursor.getString(0);
            token = cursor.getString(1);
        } else {
            Toast.makeText(getActivity(), "请登录账号！", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            dismiss();
        }
        cursor.close();

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    Toast.makeText(getActivity(), "留言不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }

                isHasPic = (haspics[0] || haspics[1] || haspics[2]);
                if (!isHasPic) {
                    // 没有图片
                    AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formSendMsg(userid, token, content, 0, null), new AsynTaskUtil.AsynNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj;
                            int code;
                            if (response != null) {
                                try {
                                    jsonObj = new JSONObject(response);
                                    code = jsonObj.optInt("code", -1);
                                    Logd(jsonObj.optString("msg", "unknown error"));
                                    if (code == 0) {
                                        dismiss();
                                    } else if (code != -1) {
                                        if (code == -101) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("userid", userid);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            dismiss();
                                        } else if (code == -99) {
                                            makeToast("留言发生错误");
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
                    // 有图片
                    Log.d("Pics", "HasPic");
                    ArrayList<Bitmap> sendPicArray = new ArrayList<>();
                    ArrayList<String> hexPic = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        if (haspics[i] && bitmaps[i] != null) {
                            sendPicArray.add(bitmaps[i]);
                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmaps[i].compress(Bitmap.CompressFormat.JPEG, 66, baos);
                                hexPic.add(BitmapIOUtil.bytesToHexString(baos.toByteArray()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (hexPic.size() > 0) {
                        JSONArray jsonArray = new JSONArray(hexPic);
                        AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formSendMsg(userid, token, content, hexPic.size(), jsonArray.toString()), new AsynTaskUtil.AsynNetUtils.Callback() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObj;
                                int code;
                                if (response != null) {
                                    try {
                                        jsonObj = new JSONObject(response);
                                        code = jsonObj.optInt("code", -1);
                                        Logd(jsonObj.optString("msg", "unknown error"));
                                        if (code == 0) {
                                            dismiss();
                                        } else if (code != -1) {
                                            if (code == -101) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("userid", userid);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                dismiss();
                                            } else if (code == -99) {
                                                makeToast("留言发生错误");
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
                        AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formSendMsg(userid, token, content, 0, null), new AsynTaskUtil.AsynNetUtils.Callback() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObj;
                                int code;
                                if (response != null) {
                                    try {
                                        jsonObj = new JSONObject(response);
                                        code = jsonObj.optInt("code", -1);
                                        Logd(jsonObj.optString("msg", "unknown error"));
                                        if (code == 0) {
                                            dismiss();
                                        } else if (code != -1) {
                                            if (code == -101) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("userid", userid);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                dismiss();
                                            } else if (code == -99) {
                                                makeToast("留言发生错误");
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
            Log.d("AMF", log);
        } catch (Exception ignored) {
        }

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

    private void makeToast(String str) {
        try {
            Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_msg_img0:
                ChoosePic(0);
                break;
            case R.id.add_msg_img1:
                if (haspics[0]) {
                    //选择图片
                    ChoosePic(1);
                }
                break;
            case R.id.add_msg_img2:
                if (haspics[1]) {
                    //选择图片\
                    ChoosePic(2);
                }
                break;
        }
    }

    private void ChoosePic(int i) {
        getPermission();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE + i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode <= RESULT_LOAD_IMAGE + 2 && requestCode >= RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = Objects.requireNonNull(getActivity()).getContentResolver().query(Objects.requireNonNull(selectedImage), filePathColumns, null, null, null);
            Objects.requireNonNull(c).moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePath[requestCode - 10] = c.getString(columnIndex);
            getCropImage(data.getData(), requestCode - 10);
            c.close();

        } else if (requestCode >= RESULT_CROP_IMAGE && requestCode <= RESULT_CROP_IMAGE + 2 && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(temp_picture[requestCode - 20].getPath());
            pics[requestCode - 20].setImageBitmap(bitmap);
            bitmaps[requestCode - 20] = bitmap;
            haspics[requestCode - 20] = true;
            if (requestCode < 22) {
                pics[requestCode - 19].setVisibility(View.VISIBLE);
            }
        } else {
            Log.d("AMF", "requestCode: " + requestCode + " resultCode: " + resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getCropImage(Uri uri, int i) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 512);
        intent.putExtra("outputY", 512);

        intent.putExtra("return-data", false);
        IMAGE_FILE_LOCATION_DIR = Objects.requireNonNull(getActivity()).getExternalCacheDir() + "/xyz.qscftyjm.board/";
        File temp_dir = new File(IMAGE_FILE_LOCATION_DIR);
        if (!temp_dir.exists()) {
            temp_dir.mkdir();
        }
        temp_picture[i] = new File(temp_dir, "temp_picture" + i + ".jpg");
        try {
            if (temp_picture[i].exists()) {
                temp_picture[i].delete();
            }
            temp_picture[i].createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageUri[i] = Uri.parse("file://" + IMAGE_FILE_LOCATION_DIR + "temp_picture" + i + ".jpg");
        Log.d("AMF", "PICURI " + i + " : " + imageUri[i].toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri[i]);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, RESULT_CROP_IMAGE + i);
    }

}
