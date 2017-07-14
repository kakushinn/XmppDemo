package utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by kakushinn on 2017/7/14.
 */

public class MyXMPPTCPConnection extends XMPPTCPConnection {
    private static MyXMPPTCPConnection connection;

    private MyXMPPTCPConnection(XMPPTCPConnectionConfiguration config) {
        super(config);
    }

    public static synchronized MyXMPPTCPConnection getInstance(){
        //初始化XMPPTCPConnection相关配置
        if(connection == null){
            XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
            builder.setConnectTimeout(10000);
            builder.setUsernameAndPassword("guochen", "guochen");
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            builder.setResource("Android");
            builder.setServiceName("192.168.0.245");
            builder.setHost("192.168.0.245");
            builder.setPort(5222);
            builder.setDebuggerEnabled(true);
            connection = new MyXMPPTCPConnection(builder.build());
        }
        return connection;
    }
}
