package com.taobao.arthas.core.command.klass100;

import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;
import com.taobao.arthas.core.command.Constants;
import com.taobao.arthas.core.command.model.RowAffectModel;
import com.taobao.arthas.core.shell.cli.Completion;
import com.taobao.arthas.core.shell.cli.CompletionUtils;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.arthas.core.util.*;
import com.taobao.arthas.core.util.affect.RowAffect;
import com.taobao.middleware.cli.annotations.*;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.instrument.Instrumentation;
import java.util.*;

@Name("hessian")
@Summary("HSF Hessian Test")
@Description(Constants.EXAMPLE +
        "  hessian cn.chinapost.jdpt.tms.api.transport.dto.ContractCollectVolumnCapacity \n" +
        Constants.WIKI + Constants.WIKI_HOME + "hessian")
public class HSFHessianCommand extends AnnotatedCommand {
    private static final Logger logger = LoggerFactory.getLogger(HSFHessianCommand.class);
    private String classPattern;
    @Argument(argName = "class-pattern", index = 0)
    @Description("Class name pattern, use either '.' or '/' as separator")
    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    @Override
    public void process(final CommandProcess process) {
        RowAffect affect = new RowAffect();
        Instrumentation inst = process.session().getInstrumentation();

        String cp = "com.taobao.hsf.com.caucho.hessian.io.ContextSerializerFactory";
        boolean isRegEx = false;
        String hashCode = null;
        List<Class<?>> matchedClasses = new ArrayList<Class<?>>(SearchUtils.searchClass(inst, cp, isRegEx, hashCode));
        Class contextSerializerFactoryCls = matchedClasses.get(0);
        logger.info(contextSerializerFactoryCls + ",classLoader is " + contextSerializerFactoryCls.getClassLoader());


        matchedClasses = new ArrayList<Class<?>>(SearchUtils.searchClass(inst, classPattern, isRegEx, hashCode));
        Class ccvcCls = matchedClasses.get(0);
        ClassLoader ccvcCll = ccvcCls.getClassLoader();

        logger.info(ccvcCls + ",classLoader is " + ccvcCll);
        Object contextSerializserFactory = null;
        try {
            contextSerializserFactory = MethodUtils.invokeStaticMethod(contextSerializerFactoryCls,"create",ccvcCll);
        }catch (Exception e){
            logger.error("",e);
        }

        try{
            Object serializer = MethodUtils.invokeMethod(contextSerializserFactory,"getCustomSerializer",ccvcCls);
            logger.info("=========== serializer " + serializer);
        }catch (Exception e){
            logger.error("ContextSerializserFactory.getCustomSerializer",e);
        }


        Class<?> serClass = null;
        String clsName = ccvcCls.getName() + "HessianSerializer";
        String __clsName = ccvcCls.getName() + "a";
        try{
            serClass = Class.forName(__clsName, false, ccvcCll);
        }catch (ClassNotFoundException e){
            logger.error( "0_" + __clsName,e);
        } catch (NullPointerException e){
            logger.error("0_" + __clsName,e);
        } catch (Exception e){
            logger.error("0_" + __clsName,e);
        }

        try{
            serClass = Class.forName(clsName, false, ccvcCll);
        }catch (ClassNotFoundException e){
            logger.error( "1_" + clsName,e);
        } catch (NullPointerException e){
            logger.error("1_" + clsName,e);
        } catch (Exception e){
            logger.error("1_" + clsName,e);
        }

        try {
            serClass = ccvcCls.getClassLoader().loadClass(clsName);
        } catch (ClassNotFoundException e) {
            logger.error("2_" + clsName,e);
        } catch (NullPointerException e){
            logger.error("2_" + clsName,e);
        } catch (Exception e){
            logger.error("2_" + clsName,e);
        }

        try{
            serClass = Class.forName(clsName, false, contextSerializerFactoryCls.getClassLoader());
        }catch (ClassNotFoundException e){
            logger.error( "3_" + clsName,e);
        } catch (NullPointerException e){
            logger.error("3_" + clsName,e);
        } catch (Exception e){
            logger.error("3_" + clsName,e);
        }

        if(serClass != null){
            try{
                Object ser = serClass.newInstance();
                logger.info( clsName + " instance is " + ser);
            }catch (Exception e){
                logger.error("newInstance error",e);
            }
        }

        affect.rCnt(matchedClasses.size());
        process.appendResult(new RowAffectModel(affect));
        process.end();
    }

    @Override
    public void complete(Completion completion) {
        if (!CompletionUtils.completeClassName(completion)) {
            super.complete(completion);
        }
    }
}
