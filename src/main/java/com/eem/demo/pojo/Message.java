package com.eem.demo.pojo;

/**
 * 信息结构体
 * @author Administrator
 */
public class Message {
    /**
     * 发送人名字
     */
    private String from;
    /**
     * 发送人id
     */
    private String fromId;
    /**
     * 接收人名字
     */
    private String to;
    /**
     * 接收人id
     */
    private String toId;
    /**
     * 信息的内容
     */
    private String msg;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
