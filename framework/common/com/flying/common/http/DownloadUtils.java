package com.flying.common.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DownloadUtils {
	public static void downloadFile(String url, String destFile) throws Exception {
		HttpUtils.get(url, new ResponseHandler<String>() {
			@Override
			public String convert(InputStream is) throws Exception {
				FileOutputStream fos = new FileOutputStream(new File(destFile));
				try {
					IOUtils.copy(is, fos);
					return destFile;
				} finally {
					fos.flush();
					fos.close();
					is.close();
				}
			}
			
		});
	}
	public static void main(String[] args) throws Exception {
		//https://mtl.ttsqgs.com/images/img/11585/2.jpg
		String url = "https://www.jpxgyw.com/uploadfile/201807/12/F7191831927.jpg";
		downloadFile(url, "C:\\Tmp\\2.jpg");
	}
}
