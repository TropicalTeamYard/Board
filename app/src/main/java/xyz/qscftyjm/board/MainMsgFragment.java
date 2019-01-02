package xyz.qscftyjm.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainMsgFragment extends Fragment implements View.OnClickListener {

    final static String TAG = "Board";

    private View view;

    public MainMsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_main_msg, container, false);



        return  view;
    }


    @Override
    public void onClick(View v) {

    }
}
