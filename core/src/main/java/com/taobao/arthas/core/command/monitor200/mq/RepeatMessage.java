package com.taobao.arthas.core.command.monitor200.mq;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RepeatMessage {
    private String msgId;
    private String topic;
    private AtomicInteger count = new AtomicInteger(0);

    List<JSONObject> repeatInfos = new ArrayList<>();

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<JSONObject> getRepeatInfos() {
        return repeatInfos;
    }

    public void setRepeatInfos(List<JSONObject> repeatInfos) {
        this.repeatInfos = repeatInfos;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }
}
