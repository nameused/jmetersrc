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
public class EncryptScript implements JavaSamplerClient {
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();
    }

    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();

        try {
            // 被测对象调用
            CryptoClientConfigFactory.setConfigPath("config\\crypto.properties");
            CspImpl csp = new CspImpl();
            String data = javaSamplerContext.getParameter("data");
            String key = javaSamplerContext.getParameter("key");
            // jmeter 开始统计响应时间标记
            results.sampleStart();
            String result = csp.sysEncrypt(key, data.getBytes());
           // String result = csp.sysEncrypt(key, data.getBytes(),"SM4/ECB/PKCS5Padding");
            System.out.println("计算的加密值为：" + result);
            results.setSuccessful(true);

        } catch (Throwable e) {
            results.setSuccessful(false);
            e.printStackTrace();
        } finally {
            results.sampleEnd();// jmeter 结束统计响应时间标记
        }
        return results;
    }


    public void teardownTest(JavaSamplerContext javaSamplerContext) {

    }


    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("data", "hash test data");
        params.addArgument("key", "KWeDhVQxxa/JObKzEqH21OUUNszW1Mnzt69dabn7whA=");
        return params;
    }

    public static void main(String[] args) {
        EncryptScript encryptScript = new EncryptScript();
        Arguments params = encryptScript.getDefaultParameters();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        encryptScript.setupTest(arg0);
        encryptScript.runTest(arg0);
        encryptScript.teardownTest(arg0);
    }
}
