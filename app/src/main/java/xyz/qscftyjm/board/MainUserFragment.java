package xyz.qscftyjm.board;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainUserFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "Board";

    private View view;

    private Button bt_login_info,bt_user_info;


    public MainUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main_user, container, false);

        bt_login_info=view.findViewById(R.id.user_bt_login_info);
        bt_user_info=view.findViewById(R.id.user_bt_more);
        bt_login_info.setOnClickListener(this);
        bt_user_info.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_bt_login_info:
                startActivity(new Intent(this.getActivity(),RegisterActivity.class));
                break;

                default:
                    Log.d(TAG,"Button No Response");
        }

    }
    
}
