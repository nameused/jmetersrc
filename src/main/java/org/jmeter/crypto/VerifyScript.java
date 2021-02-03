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
public class VerifyScript implements JavaSamplerClient {
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
            String sign = javaSamplerContext.getParameter("sign");
            String publicKey = javaSamplerContext.getParameter("publicKey");
            // jmeter 开始统计响应时间标记
            results.sampleStart();
            String result = csp.verify(publicKey,data.getBytes(),sign);
            if("true".equalsIgnoreCase(result)){
                results.setSuccessful(true);
                System.out.println("验证结果：" + result);
            }else {
                results.setSuccessful(false);
                System.out.println("验证结果：" + result);
            }

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
        params.addArgument("sign", "5SuOTnISyLZiKV2x5jYY0E2t4TabJvY1RctotErTP1etXNzTO/erNqxBTKtd024MN+CsHvSkdzS8XcWIs060PA==");
        params.addArgument("publicKey", "BJ6F7XJIBdl92iIDvjbmz5+p+4tgGwULyaD8bIsurrrCDGcgXlnRSajNqX//yQJFjQol9xveHmRYMcZUS19X23c=");

        return params;
    }

    public static void main(String[] args) {
        VerifyScript verifyScript = new VerifyScript();
        Arguments params = verifyScript.getDefaultParameters();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        verifyScript.setupTest(arg0);
        verifyScript.runTest(arg0);
        verifyScript.teardownTest(arg0);
    }
}
