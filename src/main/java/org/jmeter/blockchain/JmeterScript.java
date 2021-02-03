/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmeter.blockchain;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bcia.julongchain.sdk.SDK;
import org.bcia.julongchain.sdk.beans.JulongChainResponseMessage;

/**
 * @author zhangmingyang
 * @Date: 2019/2/26
 * @company Dingxuan
 */
public class JmeterScript implements JavaSamplerClient {

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("groupID", "myGroup");
        params.addArgument("scName", "mycc");
        params.addArgument("invokMethodName", "move");
        params.addArgument("acountNameA", "a");
        params.addArgument("acountNameB", "b");
        params.addArgument("amount", "10");
        return params;
    }

    // 初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行

    public void setupTest(JavaSamplerContext arg0) {
        SampleResult results = new SampleResult();
    }


    // 测试执行的循环体，根据线程数和循环次数的不同可执行多次

    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult results = new SampleResult();
        String groupID = arg0.getParameter("groupID");
        String scName = arg0.getParameter("scName");
        String invokMethodName = arg0.getParameter("invokMethodName");
        String acountNameA = arg0.getParameter("acountNameA");
        String acountNameB = arg0.getParameter("acountNameB");
        String amount = arg0.getParameter("amount");
        results.sampleStart();// jmeter 开始统计响应时间标记
        try {
            SDK sdk = new SDK();
            JulongChainResponseMessage responseMessage = sdk.invoke(groupID,scName,invokMethodName,acountNameA,acountNameB,amount);
            System.out.println(responseMessage.getStatus());
            results.setSuccessful(true);
            // 被测对象调用
        } catch (Throwable e) {
            results.setSuccessful(false);
            e.printStackTrace();
        } finally {
            results.sampleEnd();// jmeter 结束统计响应时间标记
        }
        return results;
    }

    // 结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行

    public void teardownTest(JavaSamplerContext arg0) {
    }

    public static void main(String[] args) {
        JmeterScript jmeterScript = new JmeterScript();
        Arguments params = jmeterScript.getDefaultParameters();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        jmeterScript.setupTest(arg0);
        jmeterScript.runTest(arg0);
        jmeterScript.teardownTest(arg0);
    }
}
