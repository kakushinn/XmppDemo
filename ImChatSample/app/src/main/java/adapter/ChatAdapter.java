package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.imchatsample.R;

import java.util.List;

import model.ChatMessage;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessage> chatMessageList;
    private LayoutInflater layoutInflater;
    private int type = 0;

    public ChatAdapter(Context context, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).getFrom() == 0){
            type = 0;
        }else{
            type = 1;
        }
        return type;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        if(convertView == null){
            if(type == 0){
                viewHolder2 = new ViewHolder2();
                convertView = layoutInflater.inflate(R.layout.item_my_chat, parent, false);
                viewHolder2.myTextView = (TextView) convertView.findViewById(R.id.tv_my_chat);
                convertView.setTag(viewHolder2);
            }else{
                viewHolder1 = new ViewHolder1();
                convertView = layoutInflater.inflate(R.layout.item_other_chat, parent, false);
                viewHolder1.otherTextView = (TextView) convertView.findViewById(R.id.tv_other_chat);
                convertView.setTag(viewHolder1);
            }
        }else{
            if(type == 0){
                viewHolder2 = (ViewHolder2) convertView.getTag();
            }else{
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
        }

        if(type == 0){
            viewHolder2.myTextView.setText(chatMessageList.get(position).getMsgContent());
        }else{
            viewHolder1.otherTextView.setText(chatMessageList.get(position).getMsgContent());
        }
        return convertView;
    }

    class ViewHolder1{
        TextView otherTextView;
    }

    class ViewHolder2{
        TextView myTextView;
    }
}
