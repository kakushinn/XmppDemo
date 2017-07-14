package model;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ChatMessage {
    private String msgContent;
    private int from;

    public ChatMessage(String msgContent, int from) {
        this.msgContent = msgContent;
        this.from = from;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}
