package xyz.qscftyjm.board;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import postutil.AsynTaskUtil;
import tools.ParamToJSON;
import tools.StringCollector;
import tools.TimeUtil;

public class MainMsgFragment extends Fragment implements View.OnClickListener {

    final static String TAG = "Board";

    private View view;
    private ListView lv_msg;
    BoardDBHelper boardDBHelper;
    SQLiteDatabase database;


    public MainMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_main_msg, container, false);
        boardDBHelper=BoardDBHelper.getMsgDBHelper(getContext());
        database=boardDBHelper.getWritableDatabase();
        lv_msg=view.findViewById(R.id.msg_list);
//        Cursor cursor;
//        cursor = database.query("userinfo", new String[] {"token"}, null, null, null, null, "id desc", "0,1");
//        int count = 0;String token;
//        while (cursor.moveToFirst()){
//            count=cursor.getCount();
//            break;
//        }
//        if(count>0){
//            do {
//                token=cursor.getString(0);
//                Log.d(TAG, token);
//            }while (cursor.moveToNext());
//            cursor.close();
//            AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToJSON.formAutoLoginJson(token), new AsynTaskUtil.AsynNetUtils.Callback() {
//                @Override
//                public void onResponse(String response) {
//                    if(response!=null){
//                        JSONObject jsonObj;
//                        Log.d(TAG, response);
//
//                        try {
//                            jsonObj=new JSONObject(response);
//                            int code=jsonObj.optInt("code",-1);
//                            if(code==200){
//                                JSONObject data=jsonObj.optJSONObject("data");
//                                if(data!=null){
//                                    ContentValues values=new ContentValues();
//                                    values.put("token", data.optString("credit","null"));
//                                    //values.put("username",data.optString("username","null"));
//                                    values.put("nickname",data.optString("nickname","null"));
//                                    values.put("checktime", TimeUtil.getTime());
//                                    String userid=data.optString("username","null");
//                                    database.update("userinfo", values, "userid = ?", new String[] { userid });
//                                    Log.d(TAG, "更新账号数据 "+userid);
//                                }
//
//                            } else if(code!=-1) {
//                                Log.d(TAG,"token 过期");
//                                Log.d(TAG,jsonObj.optString("msg","null"));
//                            } else {
//                                Log.d(TAG,"返回错误");
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.d(TAG,"服务器错误");
//                    }
//                }
//            });
//
//        }
        MsgDataOperator.setTestMsgData(getContext(),lv_msg);
//        MsgListAdapter adapter=new MsgListAdapter(,),getContext());
//        lv_msg.setAdapter(adapter);


        return  view;
    }


    @Override
    public void onClick(View v) {

    }
}
