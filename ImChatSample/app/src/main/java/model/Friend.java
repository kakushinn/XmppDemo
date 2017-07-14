package model;

/**
 * Created by Administrator on 2017/7/14.
 */

public class Friend {
    private String jid;
    private String name;

    public Friend() {
    }

    public Friend(String jid, String name) {
        this.jid = jid;
        this.name = name;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
