package xyz.qscftyjm.board;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import postutil.AsyncTaskUtil;
import tools.BoardDBHelper;
import tools.Msg;
import tools.MsgDataOperator;
import tools.MsgListAdapter;
import tools.ParamToString;
import tools.PublicUserInfo;
import tools.StringCollector;

public class MainMsgFragment extends Fragment implements View.OnClickListener, MsgReceiver.Message {

    final static String TAG = "Board";
    private ListView lv_msg;
    private MsgListAdapter adapter;
    private MsgReceiver msgReceiver;
    private ArrayList<Msg> msgData;
    private Map<String, PublicUserInfo> userInfoMap;
    private SQLiteDatabase database;

    public MainMsgFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_msg, container, false);

        msgData = new ArrayList<>();
        userInfoMap = new HashMap<>();

        msgReceiver = new MsgReceiver();
        database = BoardDBHelper.getMsgDBHelper(getActivity()).getWritableDatabase();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("xyz.qscftyjm.board.HAS_NEW_MSG");
        try {
            Objects.requireNonNull(getContext()).getApplicationContext().registerReceiver(msgReceiver, intentFilter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        msgReceiver.setMessage(this);

        lv_msg = view.findViewById(R.id.msg_list);
        MsgDataOperator.getMsgData(getActivity(), msgData, userInfoMap);
        adapter = new MsgListAdapter(msgData, userInfoMap, getActivity());
        lv_msg.setAdapter(adapter);

        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logd("ItemClick: " + position);
                Intent intent0 = new Intent(getActivity(), MsgDetailActivity.class);
                Bundle bundle0 = new Bundle();
                bundle0.putInt("msgid", msgData.get(position).getId());
                bundle0.putString("content", msgData.get(position).getContent());
                bundle0.putString("nickname", msgData.get(position).getNickname());
                bundle0.putString("time", msgData.get(position).getTime());
                bundle0.putInt("haspic", msgData.get(position).getHasPic());
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
                menu.add(0, 2, 0, "查看TA的所有留言(敬请期待)");
                menu.add(0, 3, 0, "评论这条留言");
                menu.add(0, 4, 0, "删除这条留言");
            }
        });

        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        String index = String.valueOf(info.id);
        //Toast.makeText(getActivity(), "长按点击了第"+index+"条的第"+menuItem.getItemId()+"项", Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case 0:
                Intent intent0 = new Intent(getActivity(), MsgDetailActivity.class);
                Bundle bundle0 = new Bundle();
                bundle0.putInt("msgid", msgData.get(info.position).getId());
                bundle0.putString("content", msgData.get(info.position).getContent());
                bundle0.putString("nickname", msgData.get(info.position).getNickname());
                bundle0.putString("time", msgData.get(info.position).getTime());
                bundle0.putInt("haspic", msgData.get(info.position).getHasPic());
                intent0.putExtras(bundle0);
                startActivity(intent0);
                break;

            case 1:
                ClipboardManager manager = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("content:", msgData.get(info.position).getContent());
                manager.setPrimaryClip(data);
                Toast.makeText(getActivity(), "留言内容已经复制到剪切板", Toast.LENGTH_SHORT).show();
                break;

            case 2:
                // TODO 查看TA的所有留言
                makeToast("功能暂未开放");
                break;

            case 3:
                // TODO 评论留言
                CommentFragment fragment=new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("msgid", msgData.get(info.position).getId());
                fragment.setArguments(bundle);
                fragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "添加留言");
                break;

            case 4:
                // TODO 删除留言
                Cursor cursor = database.query("userinfo", new String[]{"userid", "token"}, null, null, null, null, "id desc", "0,1");

                String userid, token;
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    userid = cursor.getString(0);
                    token = cursor.getString(1);
                    AsyncTaskUtil.AsyncNetUtils.post(StringCollector.getMsgServer(), ParamToString.formDelMsg(userid, token, msgData.get(info.position).getId()), new AsyncTaskUtil.AsyncNetUtils.Callback() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj;
                            if (response != null) {
                                try {
                                    jsonObj = new JSONObject(response);
                                    int code = jsonObj.optInt("code", -1);
                                    if (code == 0) {
                                        makeToast("删除成功，下一次刷新后会消失");
                                    } else if (code == -104) {
                                        Logd("delete failed, userid and token not match");
                                        makeToast("删除失败，请重新登录");
                                    } else if (code == -105) {
                                        Logd("delete msg failed, data error");
                                        makeToast("删除失败");
                                    } else if (code == -106) {
                                        Logd("delete msg failed, user not found");
                                        makeToast("删除失败，用户不存在");
                                    } else if (code == -107) {
                                        Logd("delete msg failed, permission not allowed");
                                        makeToast("删除失败，权限不足");
                                    } else {
                                        makeToast(jsonObj.optString("msg", "未知错误"));
                                        Logd(jsonObj.optString("msg", "未知错误"));
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
                    Toast.makeText(getActivity(), "请登录账号！", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                cursor.close();

                //Logd("4");
                break;

            default:
                break;

        }
        return super.onContextItemSelected(menuItem);

    }

    private void makeToast(String msg) {
        try {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        } catch (Exception ignore) {
        }

    }


    @Override
    public void onClick(View v) {

    }

    private void Logd(String msg) {
        Log.d("MMF", msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Objects.requireNonNull(getContext()).getApplicationContext().unregisterReceiver(msgReceiver);
            Log.d("MMF", "Broadcast closed successfully");
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            Log.d("MMF", "Broadcast closed failed");
        }

    }

    @Override
    public void getMsg(String str) {
        MsgDataOperator.getMsgData(getActivity(), msgData, userInfoMap);
        Logd("get broadcast: " + str);
        adapter.notifyDataSetChanged();
        lv_msg.invalidate();
    }
}
