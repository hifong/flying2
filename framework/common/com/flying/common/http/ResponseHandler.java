package com.flying.common.http;

import java.io.InputStream;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface ResponseHandler<T> {
	default T handle(CloseableHttpResponse resp) throws Exception {
		if(resp.getStatusLine().getStatusCode() == 200) {
			return convert(resp.getEntity().getContent());
		} else {
			System.err.println("Fail, code "+ resp.getStatusLine().getStatusCode());
			return null;
		}
	}
	T convert(InputStream is) throws Exception ;
}
