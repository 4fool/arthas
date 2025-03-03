package com.taobao.arthas.core.command.monitor200.mq;

import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.taobao.arthas.core.advisor.AdviceListenerAdapter;
import com.taobao.arthas.core.advisor.ArthasMethod;
import com.taobao.arthas.core.shell.command.CommandProcess;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ProducerAdviceListener extends AdviceListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ProducerAdviceListener.class);
    private static final Logger testLogger = LoggerFactory.getLogger("test");
    private ProducerCommand command;
    private CommandProcess process;

    private Cache<String, RepeatMessage> cache;

    private AtomicInteger c = new AtomicInteger(0);
    public ProducerAdviceListener(ProducerCommand command, CommandProcess process, boolean verbose) {
        this.command = command;
        this.process = process;
        super.setVerbose(verbose);

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .concurrencyLevel(64)
                .build();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args)
            throws Throwable {
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args,
                               Object returnObject) throws Throwable {
        try {
            p(args,null);
        }catch (Throwable t){
            logger.error("afterReturning",t);
        }
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args,
                              Throwable throwable) {
        try {
            p(args,throwable);
        }catch (Throwable t){
            logger.error("afterThrowing",t);
        }
    }

    private void p( Object[] args,Throwable t) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object mq = args[1];
        String topic = (String) MethodUtils.invokeMethod(mq,true,"getTopic");
        String brokerName = (String)  MethodUtils.invokeMethod(mq, true, "getBrokerName");
        Integer queueId = (Integer) MethodUtils.invokeMethod(mq, true, "getQueueId");

        Object msg = args[0];
        String msgId = (String) MethodUtils.invokeMethod(msg,true,"getProperty","UNIQ_KEY");
        testLogger.info(msgId);
        RepeatMessage value = this.cache.getIfPresent(msgId);
        if(value == null){
            value = new RepeatMessage();
            value.setMsgId(msgId);
            value.setTopic(topic);
            this.cache.put(msgId,value);
        }else {
            logger.error("repeat message id " + msgId);
        }
        JSONObject object = new JSONObject();
        object.put("brokerName",brokerName);
        object.put("queueId",queueId);
        Throwable throwable = new Throwable();
        String stack = ExceptionUtils.getStackTrace(throwable);
        object.put("stack",stack);
        if(t != null){
            object.put("error",ExceptionUtils.getStackTrace(t));
        }
        value.getRepeatInfos().add(object);

        int count = value.getCount().incrementAndGet();
        if(count == 2){
            logger.info("{} {} {} {}",msgId,topic,count, JSON.toJSONString(value.getRepeatInfos()));
        }else if(count > 2){
            logger.info("{} {} {} {}",msgId,topic,count,JSON.toJSONString(object));
        }
    }
}
