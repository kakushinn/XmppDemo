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

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adapter.FriendAdapter;
import model.Friend;
import utils.MyXMPPTCPConnection;

/**
 * Created by Administrator on 2017/7/14.
 */
public class FriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView listView;
    private List<Friend> friendList;
    private Button mucButton, confirmButton;
    private boolean isInInvitationState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_friends);
        initView();
        initListener();
        friendList = getMyFriends();
        listView.setAdapter(new FriendAdapter(this, friendList));
        addInvitationListener();
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.friends);
        mucButton = (Button) findViewById(R.id.invite_muc_btn);
        confirmButton = (Button) findViewById(R.id.confirmBtn);
    }

    private void initListener(){
        listView.setOnItemClickListener(this);
        mucButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
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

    /**
     * 创建聊天室
     */
    private void createMucRoom(){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPPTCPConnection.getInstance());
        MultiUserChat muc = manager.getMultiUserChat("room01@conference.192.168.0.245");
        try {
            //queen为昵称
            muc.create("queen");
            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();

            for (Iterator fields = form.getFields().iterator(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                    // Sets the default value as the answer
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            List list =  new ArrayList();
            list.add("20");
            List owners = new ArrayList();
            owners.add("guochen@192.168.0.245");
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            submitForm.setAnswer("muc#roomconfig_maxusers", list);
            submitForm.setAnswer("muc#roomconfig_roomname", "room01");
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            muc.sendConfigurationForm(submitForm);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入聊天室
     */
    private void joinChatRoom(){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPPTCPConnection.getInstance());
        MultiUserChat muc = manager.getMultiUserChat("room01@conference.192.168.0.245");
        try {
            muc.join("TestUser01");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 邀请进入聊天室
     */
    private void inviteToTalkRoom(){
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPPTCPConnection.getInstance());
        MultiUserChat muc = manager.getMultiUserChat("room01@conference.192.168.0.245");
        try {
            muc.join("invitor");
            muc.addInvitationRejectionListener(new InvitationRejectionListener() {
                @Override
                public void invitationDeclined(String invitee, String reason) {
                    Toast.makeText(FriendsActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            });
            muc.invite("jaychou@192.168.0.245", "Come on,It's a party!");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加被邀请监听
     */
    private void addInvitationListener(){
        final MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPPTCPConnection.getInstance());
        manager.addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
                try {
                    manager.decline(room.getRoom(), inviter, "Sorry ! I'm busy right now");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
        intent.putExtra("friend_jid", friendList.get(position).getJid());
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.invite_muc_btn:
                isInInvitationState = true;
//                createMucRoom();
//                joinChatRoom();
                inviteToTalkRoom();
                break;
            case R.id.confirmBtn:
                isInInvitationState = false;
                break;
            default:
                break;
        }
    }
}
