package com.taobao.arthas.core.command.monitor200.test;

import com.taobao.arthas.core.GlobalOptions;
import com.taobao.arthas.core.advisor.AdviceListener;
import com.taobao.arthas.core.command.Constants;
import com.taobao.arthas.core.command.monitor200.EnhancerCommand;
import com.taobao.arthas.core.shell.cli.Completion;
import com.taobao.arthas.core.shell.cli.CompletionUtils;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.arthas.core.util.SearchUtils;
import com.taobao.arthas.core.util.matcher.Matcher;
import com.taobao.middleware.cli.annotations.Description;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Option;
import com.taobao.middleware.cli.annotations.Summary;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Name("test")
@Summary("test")
@Description(Constants.EXPRESS_DESCRIPTION + "\nExamples:\n" +
        "  test key abc\n" +
        Constants.WIKI + Constants.WIKI_HOME + "test")
public class TestCommand extends EnhancerCommand {
    private static String className;
    private static String methodName;
    static {
        className = "cn.chinapost.jdpt.delivery.svc1.common.domain.dao.impl.DlvWaybillBaseDao";
        methodName = "saveByVo";
    }

    private String key;
    private SecretKey secretKey;

    public String getKey() {
        return key;
    }
    @Option(shortName = "key", longName = "key")
    @Description("key secret")
    public void setKey(String key) throws NoSuchAlgorithmException {
        this.key = key;

        this.secretKey = EncryptionUtil.generateAESKey(key);
    }

    public SecretKey getSecretKeySpec() {
        return secretKey;
    }

    @Override
    protected Matcher getClassNameMatcher() {
        if (classNameMatcher == null) {
            classNameMatcher = SearchUtils.classNameMatcher(className, false);
        }
        return classNameMatcher;
    }

    @Override
    protected Matcher getClassNameExcludeMatcher() {
        return classNameExcludeMatcher;
    }

    @Override
    protected Matcher getMethodNameMatcher() {
        if (methodNameMatcher == null) {
            methodNameMatcher = SearchUtils.classNameMatcher(methodName, false);
        }
        return methodNameMatcher;
    }

    @Override
    protected AdviceListener getAdviceListener(CommandProcess process) {
        return new TestListener(this, process, GlobalOptions.verbose || this.verbose);
    }

    @Override
    protected void completeArgument3(Completion completion) {
        CompletionUtils.complete(completion, Arrays.asList(EXPRESS_EXAMPLES));
    }
}
