package xyz.qscftyjm.board;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import postutil.AsynTaskUtil;
import tools.MD5Util;
import tools.ParamToJSON;
import tools.StringCollector;

public class RegisterActivity extends AppCompatActivity {

    private EditText set_nickname;
    private EditText set_password;
    private EditText confirm_password;
    private Button submit_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        set_nickname=(EditText)findViewById(R.id.register_nickname);
        set_password=(EditText)findViewById(R.id.register_password);
        confirm_password=(EditText)findViewById(R.id.register_confirm_password);
        submit_request=(Button)findViewById(R.id.register_submit);

        submit_request.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String username=set_nickname.getText().toString();
                String password=set_password.getText().toString();
                String confirm=confirm_password.getText().toString();
                if(!username.equals("")&&!password.equals("")&&!confirm.equals("")) {
                    if(password.equals(confirm)) {
                        if(username.length()<4||username.length()>15) {
                            Toast.makeText(RegisterActivity.this, "用户名长度应该在4~15位，请重新修改用户名长度", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(password.length()<6||password.length()>18) {
                            Toast.makeText(RegisterActivity.this, "密码长度应该在6~18位，请重新修改密码强度", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(RegisterActivity.this, password, Toast.LENGTH_SHORT).show();

                        AsynTaskUtil.AsynNetUtils.post(StringCollector.getServer(), ParamToJSON.formRegisterJson(username, MD5Util.getMd5(password)), new AsynTaskUtil.AsynNetUtils.Callback() {

                            @Override
                            public void onResponse(String response) {
                                // TODO Auto-generated method stub
                                String result=response;
                                JSONObject jsonObj=null;
                                if(response!=null) {
                                    try {
                                        jsonObj=new JSONObject(result);
                                        int status=jsonObj.optInt("Status", -1);
                                        if(status==0) {
                                            JSONObject data=jsonObj.getJSONObject("Data");
                                            Toast.makeText(RegisterActivity.this, "注册成功，即将跳转登录界面", Toast.LENGTH_SHORT).show();
                                            AlertDialogUtil.makeRegisterResultDialog(RegisterActivity.this, data.optString("Account","null"), data.optString("UserName", "null"));
                                            //finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "网络或服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }else {
                        Toast.makeText(RegisterActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                        set_password.setText("");
                        confirm_password.setText("");
                    }

                }else {
                    Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
