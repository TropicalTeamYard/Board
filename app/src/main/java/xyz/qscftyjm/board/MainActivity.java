package xyz.qscftyjm.board;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import postutil.AsynTaskUtil;
import tools.BoardDBHelper;
import tools.MainFragmentpagerAdapter;
import tools.ParamToString;
import tools.StringCollector;

public class MainActivity extends AppCompatActivity implements MsgReceiver.Message {

    private BottomNavigationView bottomNavigationView;
    MainMsgFragment mainMsgFragment;
    MainChatFragment mainChatFragment;
    MainUserFragment mainUserFragment;

    ArrayList<Fragment> mainFragList;
    ViewPager mainViewPager;

    BoardDBHelper boardDBHelper;
    SQLiteDatabase database;
    MsgSyncService msgSyncService;
    MsgReceiver msgReceiver;

    private static String TAG = "Board";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boardDBHelper=BoardDBHelper.getMsgDBHelper(this);
        database=boardDBHelper.getWritableDatabase();

        bottomNavigationView=findViewById(R.id.main_bottom_navigation);
        mainViewPager=findViewById(R.id.main_parent_frag);

        mainMsgFragment=new MainMsgFragment();
        mainChatFragment=new MainChatFragment();
        mainUserFragment=new MainUserFragment();
        mainFragList = new ArrayList<>();
        mainFragList.add(mainMsgFragment);
        mainFragList.add(mainChatFragment);
        mainFragList.add(mainUserFragment);

        MainFragmentpagerAdapter fragmentAdapter = new MainFragmentpagerAdapter(getSupportFragmentManager(), mainFragList);
        mainViewPager.setAdapter(fragmentAdapter);
        mainViewPager.setCurrentItem(0);
        setListener();

//        msgReceiver=new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("xyz.qscftyjm.board.HAS_NEW_MSG");
//        registerReceiver(msgReceiver, intentFilter);
//        msgReceiver.setMessage(MainActivity.this);
//
//        Intent startMsgSyncService=new Intent(MainActivity.this, MsgSyncService.class);
//        startService(startMsgSyncService);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //startActivity(new Intent(MainActivity.this,MsgDetailActivity.class));

//        /**
//         * http://localhost:8080/board/user?method=login&userid=10001&password=E10ADC3949BA59ABBE56E057F20F883E
//         * http://localhost:8080/board/user?method=autologin&userid=10001&token=02bdf3327cd94f2bace333f35e11fd04
//         * http://localhost:8080/board/user?method=register&nickname=10001&password=E10ADC3949BA59ABBE56E057F20F883E
//         * http://localhost:8080/board/user?method=changeinfo&userid=10001&token=270cc92204de4bb48d11e137695e6604&portrait=00000000
//         * http://localhost:8080/board/user?method=changepassword&userid=10003&password=1222211221212121&newpassword=E10ADC3949BA59ABBE56E057F20F883E
//         * http://localhost:8080/board/user?method=getpublicinfo&userids=['10001','10002','100']
//         * http://localhost:8080/board/user?method=getuserinfo&userid=10001&token=f0956e4857564917ba13008debcd6432
//         */

//        AsynTaskUtil.AsynNetUtils.post(StringCollector.LOCAL_USER, "method=getpublicinfo&userids=['10001','10002','100']", new AsynTaskUtil.AsynNetUtils.Callback() {
//            @Override
//            public void onResponse(String response) {
//                if(response!=null){Log.d(TAG,response);}
//            }
//        });

        /**
         * Check the account
         */
        Cursor cursor=database.query("userinfo",new String[]{"id","userid","nickname","portrait","email","priority","token"},null,null,null,null,"id desc","0,1");
        String token=null,userid=null;int id=0;
        if(cursor.moveToFirst()){
            if(cursor.getCount()>0){

                do{
                    id=cursor.getInt(0);
                    userid=cursor.getString(1);
                    token=cursor.getString(6);
                }while (cursor.moveToNext());

                cursor.close();
                final int finalId = id;
                AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formAutoLogin(userid, token), new AsynTaskUtil.AsynNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObj;
                        if (response != null) {
                            String result = response;
                            Log.d(TAG, result);
                            try {
                                jsonObj = new JSONObject(response);
                                int code = jsonObj.optInt("code", -1);
                                if (code == 0) {
                                    String newToken=jsonObj.optString("token","00000000000000000000000000000000");
                                    Log.d("Board","newToken: "+newToken);
                                    ContentValues values=new ContentValues();
                                    values.put("token",newToken);
                                    database.update("userinfo",values,"id=?",new String[]{String.valueOf(finalId)});
                                    //Toast.makeText(MainActivity.this,"自动登录成功",Toast.LENGTH_SHORT).show();
                                    msgReceiver=new MsgReceiver();
                                    IntentFilter intentFilter = new IntentFilter();
                                    intentFilter.addAction("xyz.qscftyjm.board.HAS_NEW_MSG");
                                    registerReceiver(msgReceiver, intentFilter);
                                    msgReceiver.setMessage(MainActivity.this);

                                    Intent startMsgSyncService=new Intent(MainActivity.this, MsgSyncService.class);
                                    if(!isServiceRunning("xyz.qscftyjm.board.MsgSyncService")){
                                        Log.d("MA","StartService");
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(startMsgSyncService);
                                        }else{
                                            startService(startMsgSyncService);
                                        }
                                    } else {
                                        Log.d("MA","Serviec is running");
                                    }

                                } else if (code < 0) {
                                    Toast.makeText(MainActivity.this,jsonObj.optString("msg","未知错误"),Toast.LENGTH_LONG).show();
                                    // TODO 不同error code的处理
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(MainActivity.this,"服务器或网络异常",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        } else {
            Toast.makeText(this,"请登录您的账号",Toast.LENGTH_SHORT).show();
        }

        



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListener() {
        mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.tab_msg);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.tab_chat);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.tab_user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_msg:
                        mainViewPager.setCurrentItem(0);
                        setTitle("留言板");
                        Log.d(TAG, "MSG");
                        break;
                    case R.id.tab_chat:
                        mainViewPager.setCurrentItem(1);
                        setTitle("对话");
                        Log.d(TAG, "CHAT");
                        break;
                    case R.id.tab_user:
                        mainViewPager.setCurrentItem(2);
                        setTitle("个人中心");
                        Log.d(TAG, "USER");
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        //stopService(new Intent(MainActivity.this, MsgSyncService.class));
    }

    @Override
    public void getMsg(String str) {
        Log.d("MainA","get broadcast");
    }

    /**
     * 判断服务是否运行
     */
    private boolean isServiceRunning(final String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }
}
