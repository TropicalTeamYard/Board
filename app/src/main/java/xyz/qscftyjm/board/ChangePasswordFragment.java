package xyz.qscftyjm.board;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import postutil.AsyncTaskUtil;
import tools.MD5Util;
import tools.ParamToString;
import tools.StringCollector;

public class ChangePasswordFragment extends DialogFragment {
    private String userid, oldpassword, newpassword;
    private EditText ed_userid, ed_old_password, ed_new_password, ed_confirm_password;
    private Button bt_sunmit;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, Objects.requireNonNull(getDialog().getWindow()).getAttributes().height);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.change_password_layout, container);
        ed_userid = view.findViewById(R.id.change_password_userid);
        ed_old_password = view.findViewById(R.id.change_password_old_password);
        ed_new_password = view.findViewById(R.id.change_password_new_password);
        ed_confirm_password = view.findViewById(R.id.change_password_confirm_password);
        bt_sunmit = view.findViewById(R.id.change_password_submit);

        bt_sunmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid = ed_userid.getText().toString();
                oldpassword = ed_old_password.getText().toString();
                newpassword = ed_new_password.getText().toString();
                if (userid.equals("") || oldpassword.equals("") || newpassword.equals("")) {
                    Toast.makeText(getActivity(), "用户ID或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpassword.equals(ed_confirm_password.getText().toString())) {
                    Toast.makeText(getActivity(), "新密码与确认密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newpassword.length() < 6 || newpassword.length() > 18 || userid.equals("")) {
                    Toast.makeText(getActivity(), "密码长度应该在6~18位", Toast.LENGTH_SHORT).show();
                    return;
                }
                AsyncTaskUtil.AsyncNetUtils.post(StringCollector.getUserServer(), ParamToString.formChangePassword(userid, MD5Util.getMd5(oldpassword), MD5Util.getMd5(newpassword)), new AsyncTaskUtil.AsyncNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CPF", response);
                        JSONObject jsonObj;
                        if (response != null) {
                            try {
                                jsonObj = new JSONObject(response);
                                int code = jsonObj.optInt("code", -1);
                                if (code == 0) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("userid", userid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    dismiss();
                                } else if (code == 107) {
                                    try {
                                        Toast.makeText(getActivity(), "账号密码验证失败", Toast.LENGTH_SHORT).show();
                                    } catch (Exception ignored) {
                                        Log.d("CPF", "java,lang.Exception");
                                    }

                                } else {
                                    return;
                                }
                            } catch (JSONException ignored) {
                            }
                        }
                    }
                });
            }
        });

        return view;
    }
}
