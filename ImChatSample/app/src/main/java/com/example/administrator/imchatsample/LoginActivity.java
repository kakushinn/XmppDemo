package com.example.administrator.imchatsample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utils.MyXMPPTCPConnection;

/**
 * Created by Administrator on 2017/7/14.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ConnectionListener, RosterListener {

    private MyXMPPTCPConnection connection;
    private EditText et_account, et_password;
    private Button btn_login;
    private Roster roster;
    private Boolean isLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initView();
        initListener();
        initXMPPTCPConnection();
    }

    private void initView(){
        et_account = (EditText) findViewById(R.id.account);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.login);
    }

    private void initListener(){
        btn_login.setOnClickListener(this);
    }

    private void initXMPPTCPConnection(){
        connection = MyXMPPTCPConnection.getInstance();
        connection.addConnectionListener(this);
        roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                List<String> loginList = new ArrayList<String>();
                loginList.add(et_account.getText().toString());
                loginList.add(et_password.getText().toString());
                new loginTask().execute(loginList);
                break;
            default:
                break;
        }
    }

    //ConnectionListener
    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }

    //RosterListener
    @Override
    public void entriesAdded(Collection<String> addresses) {

    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {

    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {

    }

    @Override
    public void presenceChanged(Presence presence) {

    }

    private class loginTask extends AsyncTask<List<String>, Object, Short>{

        @Override
        protected Short doInBackground(List<String>... params) {
            if(connection != null){
                try{
                    //如果没有连接openfire服务器，则连接；若已连接openfire服务器则跳过。
                    if(!connection.isConnected()){
                        connection.connect();
                    }

                    if(TextUtils.isEmpty(params[0].get(0))){
                        return 0;
                    }else if(TextUtils.isEmpty(params[0].get(1))){
                        return 1;
                    }else{
                        if(isLogin){
                            return 2;
                        }else{
                            if(connection.isConnected()){
                                connection.login(params[0].get(0), params[0].get(1));
                                return 2;
                            }
                        }
                    }
                }catch (Exception e){
                    return 3;
                }
            }
            return 3;
        }

        @Override
        protected void onPostExecute(Short state) {
            switch (state){
                case 0:
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    isLogin = true;
                    startActivity(new Intent(LoginActivity.this, FriendsActivity.class));
                    break;
                case 3:
                    isLogin = false;
                    Toast.makeText(LoginActivity.this, "登录出现错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    }
}
