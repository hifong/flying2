package com.flying.common.http;

import java.io.InputStream;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.flying.common.util.Pair;
import com.flying.common.util.Utils;
import com.flying.framework.application.Application;
import com.flying.framework.data.Data;

public class HttpUtils {
	private final static Logger log = Logger.getLogger(HttpUtils.class);
	private final static String CHARSET = "utf-8";
	
	public static HttpClient newDefaultClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		return newClient(Application.getInstance().getConfigs("http"));
	}
	
	public static HttpClient newClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		return newClient(null);
	}
	
	public static HttpClient newClient(final Data config) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		final int requestTimeout 	= config == null?10000:config.getInt("requestTimeout", 	10000);
		final int soTimeout 		= config == null?10000:config.getInt("soTimeout", 		10000);
		final int timeout 			= config == null?10000:config.getInt("timeout", 		10000);
		
		//SSL
		SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
		sslContextbuilder.useTLS();
		SSLContext sslContext = sslContextbuilder.loadTrustMaterial(null, new TrustStrategy() {

			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}

		}).build();

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))
						.build();
		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connManager.setDefaultSocketConfig(socketConfig);

		MessageConstraints messageConstraints = MessageConstraints.custom()
				.setMaxHeaderCount(200)
				.setMaxLineLength(2000)
				.build();
		//
	    ConnectionConfig connectionConfig = ConnectionConfig.custom()
	    				.setMalformedInputAction(CodingErrorAction.IGNORE)
	    				.setUnmappableInputAction(CodingErrorAction.IGNORE)
	    				.setCharset(Consts.UTF_8)
	    				.setMessageConstraints(messageConstraints)
	    				.build();
	    
	    connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(80);
        
		RequestConfig defaultRequestConfig = RequestConfig.custom()
		                .setSocketTimeout(soTimeout)
		                .setConnectTimeout(timeout)
		                .setConnectionRequestTimeout(requestTimeout)
						.setCookieSpec(CookieSpecs.BEST_MATCH)
						.setExpectContinueEnabled(true)
						.setStaleConnectionCheckEnabled(true)
						.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
						.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
						.build();
		
		//
		CloseableHttpClient httpclient = HttpClients
				.custom()
				.disableRedirectHandling()
				.setConnectionManager(connManager)
				.setDefaultRequestConfig(defaultRequestConfig)
				.build();
		
		return httpclient;
	}

	public static<T> T execute(HttpEntityEnclosingRequestBase httpMethod, HttpEntity httpEntity, ResponseHandler<T> handler) throws Exception {
		return execute(httpMethod, null, httpEntity, handler);
	}
	
	public static<T> T execute(HttpEntityEnclosingRequestBase httpMethod, List<Pair<String, String>> headers, HttpEntity httpEntity, ResponseHandler<T> handler) throws Exception {
		log.info("HttpUtils.post url:" + httpMethod.getURI());
		
		HttpClient client = newClient();
        httpMethod.setEntity(httpEntity);
        if(headers != null)
        	headers.forEach(x -> httpMethod.addHeader(x.getKey(), x.getValue()));
        CloseableHttpResponse resp = (CloseableHttpResponse)client.execute(httpMethod);
        try {
        	if(handler == null) handler = new DefaultResponseHandler<T>();
        	return handler.handle(resp);
        } finally {
        	httpMethod.abort();
        	httpMethod.releaseConnection();
        	resp.getEntity().getContent().close();
        	resp.close();
        }
	}
	
	//
	public static <T> T post(String url, List<Pair<String, String>> headers, List<Pair<String, String>> params, ResponseHandler<T> handler) throws Exception {
        List <NameValuePair> nvps = params == null?null:params.stream()
        		.map(p -> new BasicNameValuePair(p.getKey(), p.getValue()))
        		.collect(Collectors.toList());
        return execute(new HttpPost(url), headers, new UrlEncodedFormEntity(nvps, CHARSET), handler);
	}
	
	public static <T> T post(String url, List<Pair<String, String>> headers, String body, ResponseHandler<T> handler) throws Exception {
        StringEntity entity = new StringEntity(body);
        return execute(new HttpPost(url), headers, entity, handler);
	}
	
	public static <T> T post(String url, List<Pair<String, String>> params, ResponseHandler<T> handler) throws Exception {
        return post(url, null, params, handler);
	}

	public static <T> T post(String url, ResponseHandler<T> handler) throws Exception {
		return post(url, (List<Pair<String, String>>)null, handler);
	}
	
	public static <T> T post(String url, byte[] buff, ResponseHandler<T> handler) throws Exception {
        return execute(new HttpPost(url), new ByteArrayEntity(buff), handler);
	}
	
	//
	public static <T> T put(String url, List<Pair<String, String>> params, ResponseHandler<T> handler) throws Exception {
        List <NameValuePair> nvps = params == null?null:params.stream()
        		.map(p -> new BasicNameValuePair(p.getKey(), p.getValue()))
        		.collect(Collectors.toList());
        return execute(new HttpPut(url), new UrlEncodedFormEntity(nvps, CHARSET), handler);
	}

	public static <T> T put(String url, ResponseHandler<T> handler) throws Exception {
		return put(url, (List<Pair<String, String>>)null, handler);
	}
	
	public static <T> T put(String url, byte[] buff, ResponseHandler<T> handler) throws Exception {
        return execute(new HttpPut(url), new ByteArrayEntity(buff), handler);
	}
	
	//
	public static <T> T get(String url, ResponseHandler<T> handler) throws Exception {
		List<Pair<String, String>> headers = Utils.newArrayList();
		headers.add(new Pair<String, String>("Connection", "close"));
		return get(url, headers, handler);
	}
	
	public static <T> T get(String url, List<Pair<String, String>> headers, ResponseHandler<T> handler) throws Exception {
		log.info("HttpUtils.get url:" + url);
		
		HttpClient client = newClient();
        HttpGet httpMethod = new HttpGet(url); 
        
        if(headers != null)
        	headers.forEach(x -> httpMethod.addHeader(x.getKey(), x.getValue()));

        try (CloseableHttpResponse resp = (CloseableHttpResponse)client.execute(httpMethod)){
        	if(handler == null)
        		handler = new DefaultResponseHandler<T>();
        	return handler.handle(resp);
        } finally {
        	httpMethod.abort();
        	httpMethod.releaseConnection();
        }
	}

	public static class DefaultResponseHandler<T> implements ResponseHandler<T> {

		@SuppressWarnings("unchecked")
		@Override
		public T convert(InputStream is) throws Exception {
			String res = IOUtils.toString(is);
			return (T)res;
		}
		
	}
}
