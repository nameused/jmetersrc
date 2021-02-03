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
package org.jmeter.sansec;
import com.sansec.jce.provider.SwxaProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

/**
 * @author zhangmingyang
 * @Date: 2021/2/2
 * @company Dingxuan
 */
public class Crypto {
    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException {
        Security.addProvider(new SwxaProvider("11111111",null,null));
        SwxaProvider.ProviderStatus status = SwxaProvider.getProviderStatus();
        System.out.println(status.getStatus());
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES","SwxaJCE");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        System.out.println(secretKey.getAlgorithm());
        System.out.println(Hex.toHexString(secretKey.getEncoded()));
    }
}
