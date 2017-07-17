package com.example.administrator.imchatsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendAdapter;
import model.Friend;
import utils.MyXMPPTCPConnection;

/**
 * Created by Administrator on 2017/7/14.
 */
public class FriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private List<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_friends);
        initView();
        initListener();
        friendList = getMyFriends();
        listView.setAdapter(new FriendAdapter(this, friendList));
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.friends);
    }

    private void initListener(){
        listView.setOnItemClickListener(this);
    }

    private List<Friend> getMyFriends(){
        MyXMPPTCPConnection connection = MyXMPPTCPConnection.getInstance();
        List<Friend> friends = new ArrayList<Friend>();
        //通过Roster.getInstanceFor(connection)获取Roast对象;
        //通过Roster对象的getEntries()获取Set，遍历该Set就可以获取好友的信息了;
        for(RosterEntry entry : Roster.getInstanceFor(connection).getEntries()){
            Friend friend = new Friend(entry.getUser(), entry.getName());
            friends.add(friend);
        }
        return friends;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
        intent.putExtra("friend_jid", friendList.get(position).getJid());
        startActivity(intent);
    }
}
