package com.example.administrator.imchatsample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import adapter.ChatAdapter;
import model.ChatMessage;
import utils.MyXMPPTCPConnection;

/**
 * Created by Administrator on 2017/7/14.
 */
public class ChatActivity extends AppCompatActivity implements ChatManagerListener, ChatMessageListener, View.OnClickListener {
    private ListView chatListView;
    private EditText et_chat;
    private Button sendBtn;
    private MyXMPPTCPConnection connection;
    private ChatManager chatManager;
    private List<ChatMessage> messageList;
    private String friendJid;
    private Chat chat;
    private ChatAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    ChatMessage chatMessage = new ChatMessage((String) msg.obj, 1);
                    messageList.add(chatMessage);
                    adapter.notifyDataSetChanged();
                    chatListView.setSelection(messageList.size() - 1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        messageList = new ArrayList<ChatMessage>();
        friendJid = getIntent().getStringExtra("friend_jid");
        initView();
        initListener();
        initChatManager();
        initChat();
        adapter = new ChatAdapter(ChatActivity.this, messageList);
        chatListView.setAdapter(adapter);
        chatListView.setSelection(messageList.size() - 1);
    }

    private void initView(){
        chatListView = (ListView) findViewById(R.id.chatListView);
        et_chat = (EditText) findViewById(R.id.chatEditText);
        sendBtn = (Button) findViewById(R.id.sendBtn);
    }

    private void initListener(){
        sendBtn.setOnClickListener(this);
    }

    private void initChatManager(){
        connection = MyXMPPTCPConnection.getInstance();
        if(connection != null){
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(this);
        }
    }

    private void initChat(){
        if(chatManager != null){
            //第一个参数是 用户的ID
            //第二个参数是 ChatMessageListener，我们这里传null就好了
            chat = chatManager.createChat(friendJid, null);
        }
    }

    private void sendChatMessage(String msgContent){
        ChatMessage chatMessage = new ChatMessage(msgContent, 0);
        messageList.add(chatMessage);
        if(chat != null){
            try {
                //发送消息，参数为发送的消息内容
                chat.sendMessage(msgContent);
                et_chat.setText("");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        chatListView.setSelection(messageList.size() - 1);
    }

    //ChatListener中需要重写的方法
    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        //在这里面给chat对象添加ChatMessageListener
        chat.addMessageListener(this);
    }

    //ChatMessageListener中需要重写的方法
    //当接收到对方发来的消息的时候，就会回调processMessage方法
    @Override
    public void processMessage(Chat chat, Message message) {
        if(message.getType().equals(org.jivesoftware.smack.packet.Message.Type.chat) || message.getType().equals(org.jivesoftware.smack.packet.Message.Type.normal)){
            if(message.getBody() != null){
                android.os.Message msg = android.os.Message.obtain();
                msg.what = 0;
                msg.obj = message.getBody();
                handler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendBtn:
                if(!TextUtils.isEmpty(et_chat.getText().toString())){
                    sendChatMessage(et_chat.getText().toString());
                }else{
                    Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
