package tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.qscftyjm.board.R;

public class MsgListAdapter extends BaseAdapter {

    ArrayList<Msg> msgList;
    Context context;
    ViewHolder viewHolder;

    public MsgListAdapter(ArrayList<Msg> msgList, Context context){
        this.context=context;this.msgList=msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.msg_card, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.portrait.setImageBitmap(msgList.get(position).getPortrait());
        viewHolder.time.setText(msgList.get(position).getTime());
        viewHolder.nickname.setText(msgList.get(position).getNickname());
        viewHolder.content.setText(msgList.get(position).getContent());
        if(msgList.get(position).isHasPic()){
            viewHolder.picture.setVisibility(View.VISIBLE);
            viewHolder.picture.setImageBitmap(msgList.get(position).getPicture()[0]);
        }


        return convertView;
    }

    class ViewHolder{
        ImageView portrait, picture;
        TextView time, nickname, content;

        public ViewHolder(View view) {
            portrait=view.findViewById(R.id.msg_head_portrait);
            picture=view.findViewById(R.id.msg_picture);
            time=view.findViewById(R.id.msg_time);
            nickname=view.findViewById(R.id.msg_nickname);
            content=view.findViewById(R.id.msg_content);
        }
    }


}
