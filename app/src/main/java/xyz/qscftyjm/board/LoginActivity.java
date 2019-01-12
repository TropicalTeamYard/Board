package xyz.qscftyjm.board;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import tools.TimeUtil;

public class LoginActivity extends AppCompatActivity {

    static String TAG ="Board";

    private Button login_btn,register_btn,forget_password_btn;
    private EditText account_et,password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_login);
        login_btn=findViewById(R.id.login_btn);
        register_btn=findViewById(R.id.register_btn);
        forget_password_btn=findViewById(R.id.forget_password_btn);
        account_et=findViewById(R.id.login_account);
        password_et=findViewById(R.id.login_password);

        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String input_account=account_et.getText().toString();
                String input_password=password_et.getText().toString();
                if(!input_account.equals("")&&!input_password.equals("")) {

                    //input_password= MD5Util.getMd5(input_password);
                    //Toast.makeText(LoginActivity.this, "Account : "+input_account+" Password : "+input_password, Toast.LENGTH_SHORT).show();
                    final String account =input_account;
                    final String password=input_password;


                    AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToJSON.formLoginJson(account, MD5Util.getMd5(password)), new AsynTaskUtil.AsynNetUtils.Callback() {

                        @Override
                        public void onResponse(String response) {

                            //Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObj;
                            if(response!=null) {
                                String result=response;
                                Log.d(TAG,result);
                                try {
                                    jsonObj=new JSONObject(response);
                                    if(result!=null) {
                                        int code=jsonObj.optInt("code",-1);
                                        //Toast.makeText(LoginActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                                        if(code==200) {
                                            JSONObject data=jsonObj.optJSONObject("data");

                                            Toast.makeText(LoginActivity.this, "欢迎 "+data.optString("nickname")+" ！正在跳转到主界面......", Toast.LENGTH_SHORT).show();
                                            BoardDBHelper sqLiteHelper=BoardDBHelper.getMsgDBHelper(LoginActivity.this);
                                            SQLiteDatabase database=sqLiteHelper.getWritableDatabase();
//                                            boolean isExist=false;
//                                            Cursor cursor = database.query("userinfo", new String[] {"userid","nickname"}, "userid = ?", new String[] { account }, null, null, null, null);
//                                            int count;
//                                            if(cursor.moveToFirst()) {
//                                                count=cursor.getCount();
//                                                if(count>0) {
//                                                    //更新数据
//                                                    isExist=true;
//                                                    ContentValues values=new ContentValues();
//                                                    values.put("token", data.optString("credit","null"));
//                                                    values.put("nickname",data.optString("nickname","null"));
//                                                    values.put("checktime", TimeUtil.getTime());
//                                                    database.update("userinfo", values, "userid = ?", new String[] { account });
//                                                    Log.d(TAG, "更新账号数据 "+account);
//
//                                                }
//                                            }
//                                            cursor.close();
//                                            if(!isExist) {
                                                //插入数据
                                                ContentValues values = new ContentValues();
                                                values.put("userid",account);
                                                values.put("nickname",data.optString("nickname","null"));
                                                values.put("token",data.optString("credit","null"));
                                                values.put("checktime", TimeUtil.getTime());
                                                database.insert("userinfo",null,values);

                                                Log.d(TAG, "添加账号数据 "+account);
//                                            }

                                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else if(code!=-1) {
                                            JSONObject data = jsonObj.optJSONObject("data");

                                            Toast.makeText(LoginActivity.this,jsonObj.optString("msg"),Toast.LENGTH_LONG).show();

                                        }else {
                                            Toast.makeText(LoginActivity.this, "未知错误1，请稍后再试", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "未知错误0，请稍后再试", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                Toast.makeText(LoginActivity.this, "网络错误，请检查你的网络连接", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(LoginActivity.this, "注册账号-正在跳转...", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forget_password_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(LoginActivity.this, "忘记密码-该功能将在后续推出，敬请期待......", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
