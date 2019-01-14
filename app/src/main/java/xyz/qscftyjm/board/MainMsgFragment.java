package xyz.qscftyjm.board;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import tools.Msg;
import tools.MsgDataOperator;
import tools.MsgListAdapter;

public class MainMsgFragment extends Fragment implements View.OnClickListener,MsgReceiver.Message {

    final static String TAG = "Board";

    private View view;
    private ListView lv_msg;
    MsgListAdapter adapter;

    MsgReceiver msgReceiver;

    ArrayList<Msg> msgData;
    LocalBroadcastManager broadcastManager;

    public MainMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_main_msg, container, false);

        broadcastManager=LocalBroadcastManager.getInstance(getActivity());
        msgData=new ArrayList<>();

        msgReceiver=new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("xyz.qscftyjm.board.HAS_NEW_MSG");
        getContext().registerReceiver(msgReceiver, intentFilter);
        msgReceiver.setMessage(this);

        lv_msg=view.findViewById(R.id.msg_list);
        MsgDataOperator.getMsgData(getActivity(),msgData);
        adapter=new MsgListAdapter(msgData,getActivity());
        lv_msg.setAdapter(adapter);

        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logd("ItemClick: "+position);
            }
        });

        return  view;
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
        //msgReceiver.abortBroadcast();
    }

    @Override
    public void getMsg(String str) {
        MsgDataOperator.getMsgData(getActivity(),msgData);
        Logd("get broadcast: "+str);
        adapter.notifyDataSetChanged();
    }
}
