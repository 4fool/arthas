package com.taobao.arthas.core.command.monitor200.test;

import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;
import com.taobao.arthas.core.advisor.AdviceListenerAdapter;
import com.taobao.arthas.core.advisor.ArthasMethod;
import com.taobao.arthas.core.shell.command.CommandProcess;

import java.lang.reflect.Method;

class TestListener extends AdviceListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final Logger testLogger = LoggerFactory.getLogger("test");

    private TestCommand command;
    private CommandProcess process;

    private Method getSender;
    private Method getSenderLinker;
    private Method getSenderMobile;
    private Method getSenderAddr;
    private Method getSenderProvinceName;
    private Method getSenderCityName;
    private Method getSenderCountyName;
    private Method getSenderIdNo;
    private Method getSenderGis;

    private Method getReceiver;
    private Method getReceiverLinker;
    private Method getReceiverMobile;
    private Method getReceiverAddr;
    private Method getReceiverProvinceName;
    private Method getReceiverCityName;
    private Method getReceiverCountyName;
    private Method getReceiverIdNo;
    private Method getReceiverGis;

    private Method getEcommerceNo;

    public TestListener(TestCommand command, CommandProcess process, boolean verbose) {
        this.command = command;
        this.process = process;
        super.setVerbose(verbose);

        try{
            String data = EncryptionUtil.encryptWithAES("test",this.command.getSecretKeySpec());
        }catch (Exception e){
            logger.error("encryptAES error",e);
        }
    }

    private void init(ClassLoader classLoader){
        try{
            if(this.getEcommerceNo == null){
                synchronized (TestListener.class){
                    if(this.getEcommerceNo != null){
                        return;
                    }
                    Class cls =
                            classLoader.loadClass("cn.chinapost.jdpt.delivery.api.common.domain.dto.DlvWaybillBase");

                    this.getSender = cls.getDeclaredMethod("getSender");
                    this.getSenderLinker = cls.getDeclaredMethod("getSenderLinker");
                    this.getSenderMobile = cls.getDeclaredMethod("getSenderMobile");
                    this.getSenderAddr = cls.getDeclaredMethod("getSenderAddr");
                    this.getSenderProvinceName = cls.getDeclaredMethod("getSenderProvinceName");
                    this.getSenderCityName = cls.getDeclaredMethod("getSenderCityName");
                    this.getSenderCountyName = cls.getDeclaredMethod("getSenderCountyName");
                    this.getSenderIdNo = cls.getDeclaredMethod("getSenderIdNo");
                    this.getSenderGis = cls.getDeclaredMethod("getSenderGis");

                    this.getReceiver = cls.getDeclaredMethod("getReceiver");
                    this.getReceiverLinker = cls.getDeclaredMethod("getReceiverLinker");
                    this.getReceiverMobile = cls.getDeclaredMethod("getReceiverMobile");
                    this.getReceiverAddr = cls.getDeclaredMethod("getReceiverAddr");
                    this.getReceiverProvinceName = cls.getDeclaredMethod("getReceiverProvinceName");
                    this.getReceiverCityName = cls.getDeclaredMethod("getReceiverCityName");
                    this.getReceiverCountyName = cls.getDeclaredMethod("getReceiverCountyName");
                    this.getReceiverIdNo = cls.getDeclaredMethod("getReceiverIdNo");
                    this.getReceiverGis = cls.getDeclaredMethod("getReceiverGis");

                    this.getEcommerceNo = cls.getDeclaredMethod("getEcommerceNo");
                }
            }
        }catch (Exception e){
            logger.error("",e);
        }
    }
    @Override
    public void before(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args)
            throws Throwable {
        try {
            Object arg = args[0];
            init(arg.getClass().getClassLoader());

            String ecommerceNo = (String) getEcommerceNo.invoke(arg);

            String sender = (String) getSender.invoke(arg);
            String senderLinker= (String) getSenderLinker.invoke(arg);
            String senderMobile = (String) getSenderMobile.invoke(arg);
            String senderAddr = (String) getSenderAddr.invoke(arg);
            String senderProvinceName = (String) getSenderProvinceName.invoke(arg);
            String senderCityName = (String) getSenderCityName.invoke(arg);
            String senderCountyName = (String) getSenderCountyName.invoke(arg);
            String senderIdNo = (String) getSenderIdNo.invoke(arg);
            String senderGis = (String) getSenderGis.invoke(arg);
            log(ecommerceNo,sender,senderLinker,senderMobile,senderAddr,senderProvinceName,senderCityName,
                    senderCountyName,senderIdNo,senderGis);

            String receiver = (String) getReceiver.invoke(arg);
            String receiverLinker = (String) getReceiverLinker.invoke(arg);
            String receiverMobile = (String) getReceiverMobile.invoke(arg);
            String receiverAddr = (String) getReceiverAddr.invoke(arg);
            String receiverProvinceName = (String) getReceiverProvinceName.invoke(arg);
            String receiverCityName = (String) getReceiverCityName.invoke(arg);
            String receiverCountyName = (String) getReceiverCountyName.invoke(arg);
            String receiverIdNo = (String) getReceiverIdNo.invoke(arg);
            String receiverGis = (String) getReceiverGis.invoke(arg);

            log(ecommerceNo,receiver,receiverLinker,receiverMobile,receiverAddr,receiverProvinceName
                ,receiverCityName,receiverCountyName,receiverIdNo,receiverGis);
        }catch (Throwable t){
            logger.error("{}",args[0],t);
        }
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Object returnObject) throws Throwable {

    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Throwable throwable) throws Throwable {

    }

    private void log(String ecommerceNo,String name,String linker,String mobile,String addr,
                     String prov,String city,String country,String idNo,String gis){
        StringBuilder msg = new StringBuilder();
        msg.append(trim(ecommerceNo)).append("|")
                .append(trim(name)).append("|")
                .append(trim(linker)).append("|")
                .append(trim(mobile)).append("|")
                .append(trim(addr)).append("|")
                .append(trim(prov)).append("|")
                .append(trim(city)).append("|")
                .append(trim(country)).append("|")
                .append(trim(idNo)).append("|")
                .append(trim(gis));

        try{
            String m = EncryptionUtil.encryptWithAES(msg.toString(),this.command.getSecretKeySpec());
            testLogger.info(m);
        }catch (Exception e){
            logger.error("",e);
        }
    }

    private String trim(String val){
        return val == null ? "" : val;
    }
}
