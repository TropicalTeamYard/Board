package xyz.qscftyjm.board;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import postutil.AsyncTaskUtil;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;
import tools.TimeUtil;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Board";

    private Button login_btn, register_btn, forget_password_btn;
    private EditText userid_et, password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        forget_password_btn = findViewById(R.id.forget_password_btn);
        userid_et = findViewById(R.id.login_account);
        password_et = findViewById(R.id.login_password);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.containsKey("userid")) {
            userid_et.setText(bundle.getString("userid"));
            Toast.makeText(LoginActivity.this, "请重新登录您的账号", Toast.LENGTH_LONG).show();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input_account = userid_et.getText().toString();
                String input_password = password_et.getText().toString();
                if (!input_account.equals("") && !input_password.equals("")) {

                    final String account = input_account;


                    AsyncTaskUtil.AsyncNetUtils.post(StringCollector.getUserServer(), ParamToString.formLogin(account, input_password), new AsyncTaskUtil.AsyncNetUtils.Callback() {

                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObj;
                            if (response != null) {
                                Log.d(TAG, response);
                                try {
                                    jsonObj = new JSONObject(response);
                                    int code = jsonObj.optInt("code", -1);
                                    if (code == 0) {
                                        Toast.makeText(LoginActivity.this, "欢迎 " + jsonObj.optString("nickname") + " ！正在跳转到主界面......", Toast.LENGTH_SHORT).show();
                                        BoardDBHelper sqLiteHelper = BoardDBHelper.getMsgDBHelper(LoginActivity.this);
                                        SQLiteDatabase database = sqLiteHelper.getWritableDatabase();

                                        ContentValues values = new ContentValues();
                                        values.put("userid", account);
                                        values.put("nickname", jsonObj.optString("nickname", "null"));
                                        values.put("token", jsonObj.optString("token", "null"));
                                        values.put("email", jsonObj.optString("email", "youremail@server.com"));
                                        values.put("checktime", TimeUtil.getTime());
                                        values.put("portrait", jsonObj.optString("portrait", "00000000").getBytes());
                                        values.put("priority", jsonObj.optInt("priority", -1));
                                        database.insert("userinfo", null, values);
                                        Log.d(TAG, "添加账号数据 " + account);

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        finish();
                                    } else if (code < 0) {
                                        Toast.makeText(LoginActivity.this, jsonObj.optString("msg", "未知错误"), Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "网络或服务器错误", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forget_password_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "忘记密码 该功能将在后续推出，敬请期待", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
