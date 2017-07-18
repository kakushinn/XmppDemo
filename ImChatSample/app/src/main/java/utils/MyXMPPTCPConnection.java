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
            //设置连接超时的最大时间
            builder.setConnectTimeout(10000);
            //设置登录openfire的用户名和密码
            builder.setUsernameAndPassword("guochen", "guochen");
            //设置安全模式
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            builder.setResource("Android");
            //设置服务器名称
            builder.setServiceName("192.168.0.245");
            //设置主机地址
            builder.setHost("192.168.0.244");
            //设置端口号
            builder.setPort(5222);
            //是否查看debug日志
            builder.setDebuggerEnabled(true);
            connection = new MyXMPPTCPConnection(builder.build());
            connection.setPacketReplyTimeout(10000);
        }
        return connection;
    }
}
