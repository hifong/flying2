package com.flying.common.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.flying.common.util.DateUtils;
import com.flying.common.util.Utils;

/**
 * 描述 ：https工具类
 *
 * @author 陈奕丞 on 2017/9/14.
 */
@Service
public class HttpsUtils {

    /**
     * 作为客户端通讯使用协议
     */
    private static final String[] EPCC_PROTOCOLS = new String[]{"TLSv1.2"};
    
    public static CloseableHttpClient createSSLClientDefault() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        //  SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(sslContext);
        //  ALLOW_ALL_HOSTNAME_VERIFIER:这个主机名验证器基本上是关闭主机名验证的,实现的是一个空操作，并且不会抛出javax.net.ssl.SSLException异常。
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, EPCC_PROTOCOLS, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
    
    public static CloseableHttpClient createSSLClientWithCert() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();;
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, EPCC_PROTOCOLS, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
    
    public static String sendPostMessage(String url, Map<String, String> param, Map<String, String> headParams, Charset charset, Integer
            connectTimeout) throws Exception {
        //创建信任证书
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = createSSLClientDefault()) {
            //发起HTTP的POST请求
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            if (param != null && !param.isEmpty()) {
                param.forEach((k, v) -> {
                    paramList.add(new BasicNameValuePair(k, v));
                });
            }
            if (headParams != null && !headParams.isEmpty()) {
                headParams.forEach((k, v) -> {
                    httpPost.setHeader(k, v);
                });
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, charset));
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).build());
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }
    
    public static String sendGetMessage(String url, Map<String, String> param, Map<String, String> headParams, Charset charset, Integer
            connectTimeout) throws Exception {
        //创建信任证书

        CloseableHttpResponse response = null;
        StringBuilder urlSb = new StringBuilder(url);
        try (CloseableHttpClient httpClient = createSSLClientDefault()) {
            if (param != null && !param.isEmpty()) {
                urlSb.append("?");
                param.forEach((k, v) -> {
                    urlSb.append(k + "=" + v);
                });
            }
            HttpGet httpGet = new HttpGet(urlSb.toString());
            if (headParams != null && !headParams.isEmpty()) {
                headParams.forEach((k, v) -> {
                    httpGet.setHeader(k, v);
                });
            }
            httpGet.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).build());
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static String sendPostXmlMessage(String url, String xml) throws Exception {
    	return sendPostXmlMessage(url, xml, Charset.forName("UTF-8"), 3000);
    }
    
    public static String sendPostXmlMessage(String url, String xml, Charset charset, Integer
            connectTimeout) throws Exception {
        //创建信任证书
    	long start = System.currentTimeMillis();
    	CloseableHttpClient httpClient = createSSLClientDefault();
    	long createHttpClient = System.currentTimeMillis();
    	
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(xml, charset);
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout).build());
        	httpPost.setHeader("Content-Type", "text/xml;charset=" + charset);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

        	long last = System.currentTimeMillis();
        	System.out.println("Time:\t" + (createHttpClient - start)+"\t"+(last - start));
        	
            return EntityUtils.toString(entity);
        } finally {
        	httpClient.close();
            if (response != null) {
                response.close();
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }
    
    public static String sendPostXmlMessageWithCert(String url, String xml, Charset charset, Integer
            connectTimeout, String cert) throws Exception {
        //创建信任证书
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = createSSLClientWithCert()) {
            StringEntity stringEntity = new StringEntity(xml, charset);
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout).build());
            httpPost.setHeader("Content-Type", "text/xml;charset=" + charset);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } finally {
            if (response != null) {
                response.close();
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
    	List<Row> rows = Utils.newArrayList();
    	Map<String, Row> map = Utils.newHashMap();
    	try(RandomAccessFile raf = new RandomAccessFile("C:\\Users\\王海峰\\Desktop\\log\\89.log","r")) {
        	String line = raf.readLine();
	    	int lineNum = 1;
	    	while(line != null) {
	    		if(line.startsWith("2019-05-13")) {
		    		String time = line.substring(0, 24);
		    		String thread = line.substring(line.indexOf("(")+ 1, line.indexOf(")"));
		    		if(line.indexOf("95516") > 0) {
		    			map.put(thread, new Row(time, thread, line, lineNum));
		    		} else if(line.indexOf("Response") > 0 && map.containsKey(thread)) {
		    			Row row = map.remove(thread);
		    			row.endLine = line;
		    			row.endLineNum = lineNum;
		    			row.endTime = time;
		    			rows.add(row);
		    		}
	    		}
	    		line = raf.readLine();
	    		lineNum ++;
	    	}
    	}
    	FileOutputStream fos = new FileOutputStream("C:\\Temp\\pc.txt");
    	for(Row r: rows) {
    		fos.write((r.toString()+"\n").getBytes());
    	}
    	fos.close();
//    	System.out.println(sendPostXmlMessage("https://partner.95516.com/portal/index",""));

//    	for(int i=0; i< 100; i ++) {
//        	System.out.println(sendPostXmlMessage("http://localhost:8080/gallery/CategoryService/find.do?category_id=1",""));
//    	}
    }
    
    static class Row {
    	String time;
    	String thread;
    	String startLine;
    	int startLineNum;
    	
    	String endTime;
    	String endLine;
    	int endLineNum;
    	
    	Row(String time, String thread, String startLine, int startLineNum) {
    		this.time = time;
    		this.thread = thread;
    		this.startLine = startLine;
    		this.startLineNum = startLineNum;
    	}
    	
    	public String toString() {
    		Date sd = DateUtils.parseDate(time, "yyyy-MM-dd hh:mm:ss,SSS");
    		Date ed = DateUtils.parseDate(endTime, "yyyy-MM-dd hh:mm:ss,SSS");
    		return time+"\t"+startLineNum+"\t"+endLineNum+"\t"+(ed.getTime() - sd.getTime());
    	}
    }
}
