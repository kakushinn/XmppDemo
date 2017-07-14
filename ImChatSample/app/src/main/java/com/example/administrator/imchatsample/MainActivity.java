package com.example.administrator.imchatsample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ChatMessageListener, ChatManagerListener {

    private XMPPTCPConnection connection;
    private Button connectBtn, loginBtn, logoutBtn, sendMsgBtn;
    private TextView msgTxt;
    private boolean connectionState;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    connectionState = true;
                    Toast.makeText(MainActivity.this,"已连接",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    connectionState = false;
                    Toast.makeText(MainActivity.this,"连接出现故障",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"已登录",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this,"已注销",Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    msgTxt.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView(){
        connectBtn = (Button) findViewById(R.id.connectBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        sendMsgBtn = (Button) findViewById(R.id.sendMsgBtn);
        msgTxt = (TextView) findViewById(R.id.msgTxt);
    }

    private void initListener(){
        connectBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
    }

    private void connServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
                configBuilder.setUsernameAndPassword("guochen", "guochen");
                configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                configBuilder.setResource("Android");
                configBuilder.setServiceName("192.168.0.245");
                configBuilder.setHost("192.168.0.245");
                configBuilder.setPort(5222);
                configBuilder.setDebuggerEnabled(true);
                try {
                    connection = new XMPPTCPConnection(configBuilder.build());
                    ChatManager.getInstanceFor(connection).addChatListener(MainActivity.this);
                    connection.connect();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    private void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(connectionState){
                    try {
                        if(connection != null && connection.isConnected()){
                            connection.login("guochen", "guochen");
                            handler.sendEmptyMessage(2);
                        }else{
                            connServer();
                            connection.login("guochen", "guochen");
                            handler.sendEmptyMessage(2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(3);
                    }
                }
            }
        }).start();
    }

    private void logout(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(connection != null){
                    connection.disconnect();
                    handler.sendEmptyMessage(4);
                }
            }
        }).start();
    }

    private void sendMessage(){
        Chat chat = ChatManager.getInstanceFor(connection).createChat("admin@192.168.0.245", null);
        try {
            chat.sendMessage("Hello!IM GuoChen~~");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connectBtn:
                connServer();
                break;
            case R.id.loginBtn:
                login();
                break;
            case R.id.logoutBtn:
                logout();
                break;
            case R.id.sendMsgBtn:
                sendMessage();
                break;
            default:
                break;
        }
    }

    @Override
    public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
       if(message.getType().equals(org.jivesoftware.smack.packet.Message.Type.chat) || message.getType().equals(org.jivesoftware.smack.packet.Message.Type.normal)){
           if(message.getBody() != null){
               String from = message.getFrom();
               Message msg = Message.obtain();
               msg.what = 6;
               msg.obj = message.getBody();
               handler.sendMessage(msg);
           }
       }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(this);
    }
}
