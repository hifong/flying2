package com.flying.common.util;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import com.flying.common.log.Logger;
import com.flying.framework.module.LocalModule;

/**
 * @author liuyuan 
 * @date   2013-3-18 上午11:02:55
 *
 */
public class PKIUtils {  
    private static final Logger log = Logger.getLogger(PKIUtils.class);  
    
    private static String cert = "silent.cer";  
    private static String store = "silent.keystore";
    
    private static String storePassword = "testsilent";
    private static String keyPassword = "testsilent";
    private static String key = "silent";
    
    private PrivateKey privateKey;
    private PublicKey  publicKey;
    
    private LocalModule module;
    
    private static Map<String,PKIUtils> map = new HashMap();
    
    public static PKIUtils getInstance(LocalModule module){
    	PKIUtils u = map.get(module.getId());
    	if(u==null){
    		synchronized (PKIUtils.class) {
    			u = map.get(module.getId());
    			if(u==null){
    				u = new PKIUtils(module);
    				map.put(module.getId(), u);
    			}
			}
    	}
    	return u;
    }
         
    private PKIUtils(LocalModule module){
    	this.module = module;
    	publicKey = getPublicKey();
    	privateKey = getPrivateKey();
    }
    
    /** 
     *  
     * 用证书的私钥签名 
     * @param in 证书库 
     * @param storePassword 证书库密码 
     * @param keyPassword 证书密码 
     * @param key 钥别名 
     * @param data 待签名数据 
     * @return 签名 
     */  
    public byte[] signature(byte[] data) {  
        try {  
            Signature signet = Signature.getInstance("MD5withRSA");  
            signet.initSign(privateKey);  
            signet.update(data);  
            byte[] signed = signet.sign(); // 对信息的数字签名  
            return signed;  
        } catch (Exception ex) {  
            log.error("签名失败",ex);  
        }  
        return null;  
    }  
    /** 
     * 用证书的公钥验证签名 
     * @param in 证书 
     * @param data 原始数据 
     * @param signatureData 对原始数据的签名 
     * @return  
     */  
    public boolean verifySignature(byte[] data, byte[] signatureData){  
        try {  
            Signature signet = Signature.getInstance("MD5withRSA");  
            signet.initVerify(publicKey);  
            signet.update(data);  
            boolean result=signet.verify(signatureData);  
            return result;  
        } catch (Exception ex) {  
            log.error("验证签名失败",ex);  
        }  
        return false;  
    }  
  
    /** 
     * 获取证书公钥 
     * @param in 证书 
     * @return 公钥 
     */  
    private PublicKey getPublicKey() {  
        try {  
        	InputStream in = module.getClassLoader().getResourceAsStream(cert);
            // 用证书的公钥加密  
            CertificateFactory factory = CertificateFactory.getInstance("X.509");  
            Certificate cert = factory.generateCertificate(in);  
            // 得到证书文件携带的公钥  
            PublicKey key = cert.getPublicKey();  
            return key;  
        } catch (CertificateException ex) {  
            log.error("获取证书公钥失败",ex);  
        }  
        return null;  
    }  
  
    /** 
     * 加密数据 
     * @param key 公钥或私钥 
     * @param data 待加密数据 
     * @return  
     */  
    private byte[] encrypt(Key key, byte[] data) {  
        try {  
            // 定义算法：RSA  
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            // 正式执行加密操作  
            byte encryptedData[] = cipher.doFinal(data);  
            return encryptedData;  
        } catch (Exception ex) {  
            log.error("加密数据失败",ex);  
        }  
        return null;  
    }  
    /** 
     * 用证书的公钥加密 
     * @param in 证书 
     * @param data 待加密数据 
     * @return 密文 
     */  
    public byte[] encryptWithPublicKey( byte[] data) {  
        try {  
            byte encryptedData[] = encrypt(publicKey,data);  
            return encryptedData;  
        } catch (Exception ex) {  
            log.error("用证书的公钥加密失败",ex);  
        }  
        return null;  
    }  
    /** 
     * 用证书的私钥加密 
     * @param in 证书库 
     * @param storePassword 证书库密码 
     * @param keyPassword 证书密码 
     * @param key 钥别名 
     * @param data 待加密数据 
     * @return 密文 
     */  
    public byte[] encryptWithPrivateKey(byte[] data) {  
        try {  
            byte encryptedData[] = encrypt(privateKey,data);  
            return encryptedData;  
        } catch (Exception ex) {  
            log.error("用证书的私钥加密失败",ex);  
        }  
        return null;  
    }  
  
    /** 
     * 获取证书私钥 
     * @param in 证书库 
     * @param storePassword 证书库密码 
     * @param keyPassword 证书密码 
     * @param key 钥别名 
     * @return 私钥 
     */  
    private PrivateKey getPrivateKey() {  
        try {  
        	InputStream in = module.getClassLoader().getResourceAsStream(store);
            // 加载证书库  
            KeyStore ks = KeyStore.getInstance("JKS");  
            ks.load(in, storePassword.toCharArray());  
            // 获取证书私钥  
            PrivateKey privateKey = (PrivateKey) ks.getKey(key, keyPassword.toCharArray());  
            return privateKey;  
        } catch (Exception ex) {  
            log.error("获取证书私钥失败",ex);  
        }  
        return null;  
    }  
  
    /** 
     * 解密数据 
     * @param key 公钥或私钥 
     * @param data 待解密数据 
     * @return  明文 
     */  
    private byte[] decrypt(Key key, byte[] data) {  
        try {  
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
            cipher.init(Cipher.DECRYPT_MODE, key);  
            // 解密后的数据  
            byte[] result = cipher.doFinal(data);  
            return result;  
        } catch (Exception ex) {  
            log.error("解密数据失败",ex);  
        }  
        return null;  
    }  
    /** 
     *  
     * 用证书的私钥解密  
     * @param in 证书库 
     * @param storePassword 证书库密码 
     * @param keyPassword 证书密码 
     * @param key 钥别名 
     * @param data 待解密数据 
     * @return 明文 
     */  
    public byte[] decryptWithPrivateKey(byte[] data) {  
        try {  
            // 解密后的数据  
            byte[] result = decrypt(privateKey,data);  
            return result;  
        } catch (Exception ex) {  
            log.error("用证书的私钥解密失败",ex);  
        }  
        return null;  
    }  
    /** 
     *  
     * 用证书的公钥解密  
     * @param in 证书 
     * @param data 待解密数据 
     * @return 明文 
     */  
    public byte[] decryptWithPublicKey(byte[] data) {  
        try {  
            // 解密后的数据  
            byte[] result = decrypt(publicKey,data);  
            return result;  
        } catch (Exception ex) {  
            log.error("用证书的公钥解密失败",ex);  
        }  
        return null;  
    }  
} 