package xyz.qscftyjm.board;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import postutil.AsynTaskUtil;
import tools.BoardDBHelper;
import tools.Msg;
import tools.MsgDataOperator;
import tools.MsgListAdapter;
import tools.ParamToString;
import tools.PublicUserInfo;
import tools.StringCollector;

public class MainMsgFragment extends Fragment implements View.OnClickListener,MsgReceiver.Message {

    final static String TAG = "Board";
    private ListView lv_msg;
    MsgListAdapter adapter;
    MsgReceiver msgReceiver;
    ArrayList<Msg> msgData;
    Map<String, PublicUserInfo> userInfoMap;
    SQLiteDatabase database;
    public MainMsgFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_msg, container, false);

        msgData=new ArrayList<>();
        userInfoMap=new HashMap<>();

        msgReceiver=new MsgReceiver();
        database= BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("xyz.qscftyjm.board.HAS_NEW_MSG");
        getContext().registerReceiver(msgReceiver, intentFilter);
        msgReceiver.setMessage(this);

        lv_msg= view.findViewById(R.id.msg_list);
        MsgDataOperator.getMsgData(getActivity(),msgData,userInfoMap);
        adapter=new MsgListAdapter(msgData,userInfoMap,getActivity());
        lv_msg.setAdapter(adapter);

        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logd("ItemClick: "+position);
                Intent intent0=new Intent(getActivity(),MsgDetailActivity.class);
                Bundle bundle0=new Bundle();
                bundle0.putInt("msgid",msgData.get(position).getId());
                bundle0.putString("content",msgData.get(position).getContent());
                bundle0.putString("nickname",msgData.get(position).getNickname());
                bundle0.putString("time",msgData.get(position).getTime());
                bundle0.putInt("haspic",msgData.get(position).getHasPic());
                intent0.putExtras(bundle0);
                startActivity(intent0);
            }
        });

        lv_msg.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("更多操作");
                menu.add(0, 0, 0, "查看详细内容");
                menu.add(0, 1, 0, "复制内容到剪切板");
                menu.add(0, 2, 0, "查看TA的所有留言");
                menu.add(0, 3, 0, "回复这条留言");
                menu.add(0, 4, 0, "删除这条留言");
            }
        });

        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        String index=String.valueOf(info.id);
        Toast.makeText(getActivity(), "长按点击了第"+index+"条的第"+menuItem.getItemId()+"项", Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case 0:
                Intent intent0=new Intent(getActivity(),MsgDetailActivity.class);
                Bundle bundle0=new Bundle();
                bundle0.putInt("msgid",msgData.get(info.position).getId());
                bundle0.putString("content",msgData.get(info.position).getContent());
                bundle0.putString("nickname",msgData.get(info.position).getNickname());
                bundle0.putString("time",msgData.get(info.position).getTime());
                bundle0.putInt("haspic",msgData.get(info.position).getHasPic());
                intent0.putExtras(bundle0);
                startActivity(intent0);
                break;

            case 1:
                ClipboardManager manager=(ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("content:",msgData.get(info.position).getContent());
                manager.setPrimaryClip(data);
                Toast.makeText(getActivity(),"留言内容已经复制到剪切板",Toast.LENGTH_SHORT).show();
                break;

            case 2:
                // TODO 查看TA的所有留言
                makeToast("功能暂未开放");
                break;

            case 3:
                // TODO 回复留言
                makeToast("功能暂未开放");
                break;

            case 4:
                // TODO 删除留言
                Cursor cursor=database.query("userinfo", new String[]{"userid", "token"}, null, null, null, null, "id desc", "0,1");

                String userid,token;
                if(cursor.moveToFirst()&&cursor.getCount()>0){
                    userid=cursor.getString(0);
                    token=cursor.getString(1);
                    AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formDelMsg(userid,token,msgData.get(info.position).getId()), new AsynTaskUtil.AsynNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj;
                            if(response!=null){
                                try {
                                    jsonObj=new JSONObject(response);
                                    int code=jsonObj.optInt("code",-1);
                                    if(code==0){
                                        makeToast("删除成功");
                                    } else if(code==-104){
                                        Logd("delete failed, userid and token not match");
                                        makeToast("删除失败，请重新登录");
                                    } else if(code==-105){
                                        Logd("delete msg failed, data error");
                                        makeToast("删除失败");
                                    } else if (code==-106){
                                        Logd("delete msg failed, user not found");
                                        makeToast("删除失败，用户不存在");
                                    } else if(code==-107) {
                                        Logd("delete msg failed, permission not allowed");
                                        makeToast("删除失败，权限不足");
                                    } else {
                                        Logd(jsonObj.optString("msg","未知错误"));
                                    }
                                } catch (JSONException e) {
                                    makeToast("删除失败，网络错误");
                                }
                            } else {
                                makeToast("删除失败，网络错误");
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(),"请登录账号！",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                cursor.close();

                Logd("4");
                break;

            default:
                break;

        }
        return super.onContextItemSelected(menuItem);

    }

    private void makeToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {

    }

    void Logd(String msg){
        Log.d("MsgFt",msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getContext().unregisterReceiver(msgReceiver);
            Log.d("MMF","Broadcast closed successfully");
        } catch (IllegalArgumentException e) {
            Log.d("MMF","Broadcast closed failed");
        }


    }

    @Override
    public void getMsg(String str) {
        MsgDataOperator.getMsgData(getActivity(),msgData,userInfoMap);
        Logd("get broadcast: "+str);
        lv_msg.invalidate();
        adapter.notifyDataSetChanged();
        lv_msg.invalidate();
    }
}
