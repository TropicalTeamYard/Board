package xyz.qscftyjm.board;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MsgReceiver extends BroadcastReceiver {

    private Message message;

    public MsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "有新的留言", Toast.LENGTH_SHORT).show();
        String len = intent.getStringExtra("msg");
        message.getMsg(len);
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    interface Message {
        void getMsg(String str);
    }
}
