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
package org.jmeter.crypto;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.bcia.crypto.bean.CryptoClientConfigFactory;
import org.bcia.crypto.client.CspImpl;

/**
 * @author zhangmingyang
 * @Date: 2020/9/1
 * @company Dingxuan
 */
public class SignScript implements JavaSamplerClient {
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();
    }

    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();
        try {
            CryptoClientConfigFactory.setConfigPath("config\\crypto.properties");
            // 被测对象调用
            CspImpl csp = new CspImpl();
            String data = javaSamplerContext.getParameter("data");
            String privateKey = javaSamplerContext.getParameter("privateKey");
            // jmeter 开始统计响应时间标记
            results.sampleStart();
            String result = csp.sign(privateKey,data.getBytes());
            results.setSuccessful(true);
            System.out.println("计算的签名值为：" + result);

        } catch (Throwable e) {
            results.setSuccessful(false);
            e.printStackTrace();
        } finally {
            // jmeter 结束统计响应时间标记
            results.sampleEnd();
        }
        return results;
    }


    public void teardownTest(JavaSamplerContext javaSamplerContext) {
    }


    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("data", "hash test data");
        params.addArgument("privateKey", "scgEM/4K7VSd5bHkPIl9bm53AieOHy2bSvSsnfqv7kzbnBXqhwjvdOMnk3OrUXnMUB3UQQJy3dbci1yfXZnyA46mQJ9IWw2CeRBjkCuSw+kZlbeMg8dLFw2Icllve1Rwjc+AHhjKzOFJHfS9uQILeadK1BppNNFfUzFrncZiVEXPGaoxUvPkP9kB7xHhml93");
        return params;
    }

    public static void main(String[] args) {
        SignScript signScript = new SignScript();
        Arguments params = signScript.getDefaultParameters();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        signScript.setupTest(arg0);
        signScript.runTest(arg0);
        signScript.teardownTest(arg0);
    }
}
