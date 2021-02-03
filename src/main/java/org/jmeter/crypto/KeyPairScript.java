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
import org.bcia.crypto.bean.AsymmetricKeyPair;
import org.bcia.crypto.bean.CryptoClientConfigFactory;
import org.bcia.crypto.client.CspImpl;

/**
 * @author zhangmingyang
 * @Date: 2020/9/1
 * @company Dingxuan
 */
public class KeyPairScript implements JavaSamplerClient {
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();
    }

    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult results = new SampleResult();
        try {
            CryptoClientConfigFactory.setConfigPath("config\\crypto.properties");
            // 被测对象调用
            long time1=System.currentTimeMillis();
            CspImpl csp = new CspImpl();
            // jmeter 开始统计响应时间标记
            results.sampleStart();

            AsymmetricKeyPair result = csp.getAsymmetricKey();
            long time2=System.currentTimeMillis();
            System.out.println("密钥对生成耗时："+(time2-time1));
            results.setSuccessful(true);
            System.out.println("生成的私钥：" + result.getPrivateKey());
            System.out.println("生成的公钥：" + result.getPublicKey());

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
        return params;
    }

    public static void main(String[] args) {
        KeyPairScript keyPairScript = new KeyPairScript();
        Arguments params = keyPairScript.getDefaultParameters();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        keyPairScript.setupTest(arg0);
        keyPairScript.runTest(arg0);
        keyPairScript.teardownTest(arg0);
    }
}
