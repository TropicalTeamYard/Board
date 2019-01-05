package xyz.qscftyjm.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MainMsgFragment extends Fragment implements View.OnClickListener {

    final static String TAG = "Board";

    private View view;
    private ListView lv_msg;

    public MainMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_main_msg, container, false);

        lv_msg=view.findViewById(R.id.msg_list);
        MsgListAdapter adapter=new MsgListAdapter(MsgDataOperator.getTestMsgData(getContext()),getContext());
        lv_msg.setAdapter(adapter);


        return  view;
    }


    @Override
    public void onClick(View v) {

    }
}
