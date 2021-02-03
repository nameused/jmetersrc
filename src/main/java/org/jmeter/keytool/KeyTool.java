//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jmeter.keytool;

import com.sansec.asn1.ASN1Encodable;
import com.sansec.asn1.ASN1Sequence;
import com.sansec.asn1.ASN1Set;
import com.sansec.asn1.x509.AuthorityKeyIdentifier;
import com.sansec.asn1.x509.SubjectKeyIdentifier;
import com.sansec.asn1.x509.SubjectPublicKeyInfo;
import com.sansec.asn1.x509.X509Extensions;
import com.sansec.asn1.x509.X509Name;
import com.sansec.devicev4.util.JarUtil;
import com.sansec.jce.PKCS10CertificationRequest;
import com.sansec.jce.provider.SwxaProvider;
import com.sansec.openssl.PEMWriter;
import com.sansec.util.encoders.Base64;
import com.sansec.x509.X509V3CertificateGenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

public class KeyTool {
    public static String KSTYPE = "SWKS";
    public static final String PROVIDER = "SwxaJCE";
    static boolean IS_RSA_ALGO = true;
    static String SERVER_CERT_ALIAS = "server_cert";
    static String ROOT_CERT_ALIAS = "root_cert";
    static String ROOT_CERT_ALIAS_2 = "root_cert_2";
    static String PASSWORD = "123456";
    static String DNAME = "C=CN,ST=Bei jing,L=Bei jing,O=swxa,OU=Test,CN=localhost";
    static String VALIDITY = "365";
    static String KEY_INDEX = "10";
    static int KEY_SIZE;
    static String BASE_DIR;
    static String JKS_FILE_NAME;
    static String ROOT_CERT_FILE_NAME;
    static String ROOT_CERT_FILE_NAME_2;
    static String SERVER_CERT_FILE_NAME;
    static String CERT_REQ_FILE_NAME;
    static KeyStore ks;

    public KeyTool() {
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new SwxaProvider());
        ks = KeyStore.getInstance(KSTYPE, "SwxaJCE");

        while (true) {
            while (true) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("    1  生成JKS文件                       ");
                System.out.println("    2  生成证书请求文件           ");
                System.out.println("    3  导入根证书文件                ");
                System.out.println("    4  导入服务器证书                ");
                System.out.println("    5  显示JKS文件证书             ");
                System.out.println("    0  退出                                         ");
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
                int n = getInt("Select : ");
                switch (n) {
                    case 0:
                        System.exit(0);
                    case 1:
                        genKeyStore();
                        break;
                    case 2:
                        genCertReq();
                        break;
                    case 3:
                        importRootCert();
                        break;
                    case 4:
                        importServerCert();
                        break;
                    case 5:
                        listCert();
                        break;
                default:
                    break;
                }
            }
        }
    }

    private static int getInt(String prompt) {
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (Exception var2) {
            }
        }
    }

    public static void genCertReq() throws Exception {
        System.out.print("生成证书请求文件[ " + CERT_REQ_FILE_NAME + " ] ... ");
        String alias = SERVER_CERT_ALIAS;
        KeyStore ks = genKeyStore(JKS_FILE_NAME, PASSWORD);
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, PASSWORD.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        if (cert == null) {
            throw new Exception("alias [" + alias + "] cert no exists");
        } else {
            PublicKey publicKey = cert.getPublicKey();
            String subjectDN = cert.getSubjectDN().toString();
            String signatureAlgorithm = "SHA1WithRSA";
            if (publicKey.getAlgorithm().equals("SM2")) {
                signatureAlgorithm = "SM3WithSM2";
            }

            X509Name subject = new X509Name(subjectDN);
            PKCS10CertificationRequest p10 = new PKCS10CertificationRequest(signatureAlgorithm, subject, publicKey, (ASN1Set) null, privateKey, "SwxaJCE");
            Writer out = new FileWriter(CERT_REQ_FILE_NAME);
            PEMWriter write = new PEMWriter(out, "SwxaJCE");
            write.writeObject(p10);
            write.close();
            System.out.println("ok");
        }
    }

    public static void genKeyStore() throws Exception {
        System.out.print("产生JKS文件[ " + JKS_FILE_NAME + " ] ... ");
        FileOutputStream stream = new FileOutputStream(JKS_FILE_NAME);
        ks.load((InputStream) null, PASSWORD.toCharArray());
        KeyPair kp = genKeyPair();
        Certificate cert = genSelfCert(kp);
        Certificate[] chain = new Certificate[]{cert};
        ks.setKeyEntry(SERVER_CERT_ALIAS, kp.getPrivate(), PASSWORD.toCharArray(), chain);
        ks.store(stream, PASSWORD.toCharArray());
        System.out.println("ok");
        stream.close();
    }

    private static KeyStore genKeyStore(String filename, String password) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        ks.load(fis, password.toCharArray());
        fis.close();
        return ks;
    }

    public static void importServerCert() throws Exception {
        System.out.print("导入服务器证书文件[ " + SERVER_CERT_FILE_NAME + " ] ... ");
        FileInputStream in = new FileInputStream(JKS_FILE_NAME);
        ks.load(in, PASSWORD.toCharArray());
        Certificate serverCert = getCertFromFile(SERVER_CERT_FILE_NAME);
        Certificate rootCert = getCertFromFile(ROOT_CERT_FILE_NAME);
        Certificate[] chain = new Certificate[]{serverCert, rootCert};
        ks.deleteEntry(SERVER_CERT_ALIAS);
        KeyPair kp = genKeyPair();
        ks.setKeyEntry(SERVER_CERT_ALIAS, kp.getPrivate(), PASSWORD.toCharArray(), chain);
        FileOutputStream out = new FileOutputStream(JKS_FILE_NAME);
        ks.store(out, PASSWORD.toCharArray());
        System.out.println("ok");
        in.close();
        out.close();
    }

    private static Certificate getCertFromFile(String filename) throws Exception {
        FileInputStream inStream = new FileInputStream(filename);
        CertificateFactory cf = CertificateFactory.getInstance("X509", "SwxaJCE");
        Certificate cert = cf.generateCertificate(inStream);
        inStream.close();
        if (cert == null) {
            File file = new File(filename);
            Long fileLength = file.length();
            byte[] array = new byte[fileLength.intValue()];
            inStream = new FileInputStream(file);
            inStream.read(array);
            array = Base64.decode(array);
            cert = cf.generateCertificate(new ByteArrayInputStream(array));
        }

        inStream.close();
        return cert;
    }

    public static void importRootCert() throws Exception {
        System.out.print("导入根证书文件[ " + ROOT_CERT_FILE_NAME + " ] ... ");
        FileInputStream in = new FileInputStream(JKS_FILE_NAME);
        ks.load(in, PASSWORD.toCharArray());
        Certificate cert = getCertFromFile(ROOT_CERT_FILE_NAME);
        ks.setCertificateEntry(ROOT_CERT_ALIAS, cert);
        FileOutputStream out = new FileOutputStream(JKS_FILE_NAME);
        ks.store(out, PASSWORD.toCharArray());
        System.out.println("ok");
        in.close();
        out.close();
    }

    public static void listCert() throws Exception {
        FileInputStream in = new FileInputStream(JKS_FILE_NAME);
        ks.load(in, PASSWORD.toCharArray());
        String alias = null;
        Enumeration e = ks.aliases();

        while (e.hasMoreElements()) {
            alias = (String) e.nextElement();
            Certificate certificates = ks.getCertificate(alias);
            System.out.println("alias:" + alias + "\n" + certificates);
        }

        in.close();
    }

    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator kpg = null;
        if (IS_RSA_ALGO) {
            kpg = KeyPairGenerator.getInstance("RSA", "SwxaJCE");
        } else {
            kpg = KeyPairGenerator.getInstance("SM2", "SwxaJCE");
        }

        kpg.initialize(KEY_SIZE);
        KeyPair keypair = kpg.generateKeyPair();
        return keypair;
    }

    public static Certificate genSelfCert(KeyPair keypair) throws Exception {
        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();
        String signatureAlgorithm = "SHA1WithRSA";
        if (publicKey.getAlgorithm().equals("SM2")) {
            signatureAlgorithm = "SM3WithSM2";
        }

        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        BigInteger serialNumber = BigInteger.valueOf(1L);
        certGen.setSerialNumber(serialNumber);
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + (long) Integer.valueOf(VALIDITY) * 24L * 60L * 60L * 1000L);
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(endDate);
        certGen.setIssuerDN(new X509Name(DNAME));
        certGen.setSubjectDN(new X509Name(DNAME));
        ASN1Encodable encode = ASN1Sequence.fromByteArray(publicKey.getEncoded());
        SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(encode);
        SubjectKeyIdentifier subjectKeyId = new SubjectKeyIdentifier(info);
        AuthorityKeyIdentifier authKeyId = new AuthorityKeyIdentifier(info);
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, subjectKeyId);
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, authKeyId);
        certGen.setSignatureAlgorithm(signatureAlgorithm);
        certGen.setPublicKey(publicKey);
        Certificate cert = certGen.generate(privateKey);
        return cert;
    }

    static {
        KEY_SIZE = Integer.parseInt(KEY_INDEX) * 65536;
        BASE_DIR = "sansec/";
        JKS_FILE_NAME = BASE_DIR + "swjce.jks";
        ROOT_CERT_FILE_NAME = BASE_DIR + "root.cer";
        ROOT_CERT_FILE_NAME_2 = BASE_DIR + "root2.cer";
        SERVER_CERT_FILE_NAME = BASE_DIR + "server.cer";
        CERT_REQ_FILE_NAME = BASE_DIR + "server.csr";

        try {
            JarUtil util = new JarUtil(KeyTool.class);
            String path = "config";
            FileInputStream fis = new FileInputStream(path + "/config.ini");
            Properties prop = new Properties();
            prop.load(fis);
            String str = null;
            str = prop.getProperty("SERVER_CERT_ALIAS");
            if (str != null) {
                SERVER_CERT_ALIAS = str;
            }

            str = prop.getProperty("ROOT_CERT_ALIAS");
            if (str != null) {
                ROOT_CERT_ALIAS = str;
            }

            str = prop.getProperty("PASSWORD");
            if (str != null) {
                PASSWORD = str;
            }

            str = prop.getProperty("DNAME");
            if (str != null) {
                DNAME = new String(str.getBytes("ISO-8859-1"), "utf-8");
            }

            str = prop.getProperty("VALIDITY");
            if (str != null) {
                VALIDITY = str;
            }

            str = prop.getProperty("KEY_INDEX");
            if (str != null) {
                KEY_INDEX = str;
            }

            KEY_SIZE = Integer.parseInt(KEY_INDEX) * 65536;
            str = prop.getProperty("BASE_DIR");
            if (str != null) {
                BASE_DIR = str;
            }

            str = prop.getProperty("KEYSTORE_TYPE");
            if (str != null) {
                KSTYPE = str;
            }

            str = prop.getProperty("JKS_FILE_NAME");
            if (str != null) {
                JKS_FILE_NAME = BASE_DIR + str;
            }

            str = prop.getProperty("ROOT_CERT_FILE_NAME");
            if (str != null) {
                ROOT_CERT_FILE_NAME = BASE_DIR + str;
            }

            str = prop.getProperty("ROOT_CERT_FILE_NAME_2");
            if (str != null) {
                ROOT_CERT_FILE_NAME_2 = BASE_DIR + str;
            }

            str = prop.getProperty("SERVER_CERT_FILE_NAME");
            if (str != null) {
                SERVER_CERT_FILE_NAME = BASE_DIR + str;
            }

            str = prop.getProperty("CERT_REQ_FILE_NAME");
            if (str != null) {
                CERT_REQ_FILE_NAME = BASE_DIR + str;
            }

            str = prop.getProperty("IS_RSA_ALGO");
            if (str == null) {
                System.out.println("默认使用RSA算法");
            } else {
                IS_RSA_ALGO = Boolean.valueOf(str);
            }

            fis.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        ks = null;
    }
}
