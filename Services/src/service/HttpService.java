package service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用httpurlconnection对网页进行访问
 * @author Administrator
 *
 */

public class HttpService {
	
	private static int TIME_OUT = 3000;
	private static String DEFAULT_ENCODING = "utf-8";
	
	/**
	 * 使用get方法获取输入流对象
	 * @param urlString 
	 * @param params  
	 * @return  获取失败返回null
	 */
	
	public static InputStream excuteGet(String urlString, Map<String, String> params) {
		InputStream in = null;
		URL url = createURL(urlString, params);
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod("GET");
			conn = addHeaders(conn, null);
			if (conn.getResponseCode() == 200) {
				in = conn.getInputStream();
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
	
	
	/**
	 * 执行post方法获取输入流对象 
	 * 获取失败返回null
	 */
	
	public static InputStream excutePost(String urlString, Map<String, String> params) {
		InputStream in = null;
		URL url = createURL(urlString, null);
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod("POST");
			conn = addHeaders(conn, null);
			conn.setDoOutput(true);
			OutputStream out = conn.getOutputStream();
			writeParamsToOutputStream(out, params);
			if (conn.getResponseCode() == 200) {
				in = conn.getInputStream();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	 * 执行post方法获取输入流对象请求格式为json 
	 * 获取失败返回null
	 */
	
	public static InputStream excutePostJson(String urlString, String jsonParams) {
		InputStream in = null;
		URL url = createURL(urlString, null);
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			conn = addHeaders(conn, null);
			conn.setDoOutput(true);
			OutputStream out = conn.getOutputStream();
			PrintWriter pw = new PrintWriter(out);
			pw.print(jsonParams);
			pw.close();
			closeOutputStream(out);
			if (conn.getResponseCode() == 200) {
				in = conn.getInputStream();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	 * 为conn设置请求头字段
	 */ 
	 
	private static HttpURLConnection addHeaders(HttpURLConnection conn, Map<String, String> headers) {
		// TODO Auto-generated method stub
		if (headers == null) {
			conn.setRequestProperty("accept", "Accept application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("accept-charset", "utf-8;q=0.7,*;q=0.7");
		//	conn.setRequestProperty("accept-encoding", "gzip, deflate");
			conn.setRequestProperty("accept-language", "zh-cn,zh;q=0.5");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
		}
		else {
			for (Map.Entry<String, String> entry : headers.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}

	/**
	 * 使用get方法获取网页文本字符串
	 * @param urlString
	 * @param params
	 * @return
	 */
	
	public static String getContentFromURLByGet(String urlString, Map<String, String> params){
		String charset = DEFAULT_ENCODING;/*getPageEncoding(urlString);*/  //获取页面编码
		StringBuilder sb = new StringBuilder();
		InputStream in = excuteGet(urlString, params);
		if(in != null){
			Scanner sc = new Scanner(in, charset);
			while (sc.hasNext()) {
				sb.append(sc.nextLine()+"\n");
			}
			try {
				sc.close();
				in.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}	
		return sb.toString();
	}
	
	/**
	 * 使用post方法获取网页文本字符串
	 * @param urlString
	 * @param params
	 * @return
	 */
	
	public static String getContentFromURLByPost(String urlString, Map<String, String> params){
		String charset = DEFAULT_ENCODING;/*getPageEncoding(urlString);*/  //获取页面编码
		StringBuilder sb = new StringBuilder();
		InputStream in = excutePost(urlString, params);
		if (in != null) {
			Scanner sc = new Scanner(in, charset);
			while (sc.hasNext()) {
				sb.append(sc.nextLine()+"\n");
			}
			try {
				sc.close();
				in.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}	
		return sb.toString();
	}
	
	/**
	 * 使用post方法获取网页文本字符串
	 * @param urlString
	 * @param params json
	 * @return
	 */
	
	public static String getContentFromURLByPost(String urlString, String jsonParams){
		String charset = DEFAULT_ENCODING;/*getPageEncoding(urlString);*/  //获取页面编码
		StringBuilder sb = new StringBuilder();
		InputStream in = excutePostJson(urlString, jsonParams);
		if (in != null) {
			Scanner sc = new Scanner(in, charset);
			while (sc.hasNext()) {
				sb.append(sc.nextLine()+"\n");
			}
			try {
				sc.close();
				in.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}	
		return sb.toString();
	}
	
	/**
	 * 创建一个带参数的url对象
	 * @param urlString 基本url路径
	 * @param params 存放参数的map对象
	 * @return url对象 失败时返回null
	 */
	
	public static URL createURL(String urlString, Map<String, String> params){
		String urlStr = urlString;
		if (params != null) {
			urlStr = urlStr+"?";
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (!urlStr.endsWith("?")) {
					urlStr = urlStr + "&";
				}
				urlStr = urlStr + entry.getKey() + "=" + entry.getValue();
			}
		}
		
		urlStr = transCode(urlStr, DEFAULT_ENCODING, "iso-8859-1");
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * 获取要访问页面的编码
	 * @param urlString  页面网址
	 * @return 编码字符串
	 */
	
	public static String getPageEncoding(String urlString){
		String charset = DEFAULT_ENCODING;
		String reg = "content=\".*?charset=(.*?)\"";
		Pattern pattern = Pattern.compile(reg);
		InputStream in = excuteGet(urlString, null);
		if (in != null) {
			Scanner sc = new Scanner(in);
			String temp;
			boolean flag = false;
			while (sc.hasNext()) {
				temp = sc.nextLine();
				Matcher matcher = pattern.matcher(temp);
				while (matcher.find()) {
					charset = matcher.group(1);
					flag = true;
					break;
				}
				if (flag) {
					break;
				}
			}
			sc.close();
			closeInputStream(in);
		}	
		System.out.println(charset);
		return charset;
	}
	
	/**
	 * 向输出流中写入参数
	 * @param out
	 * @param params
	 */
	
	public static void writeParamsToOutputStream(OutputStream out, Map<String, String> params){
		String temp = "";
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (!temp.equals("")) {
				temp = temp + "&";
			}
			temp = temp + entry.getKey() + "=" + entry.getValue();
		}
		PrintWriter pw = new PrintWriter(out);
		pw.print(temp);
		pw.close();
		closeOutputStream(out);
	}
	
	/**
	 * 获取页面访问cookie
	 * @param conn
	 * @return
	 */
	
	public static String getCookie(HttpURLConnection conn){
		return conn.getHeaderField("Set-Cookie");
	}
	
	/**
	 * 设置页面访问cookie
	 * @param conn
	 * @param cookie
	 * @return
	 */
	public static HttpURLConnection setCookie(HttpURLConnection conn, String cookie) {
		conn.setRequestProperty("Cookie", cookie);
		return conn;
	}
	
	public static void closeInputStream(InputStream in) {
		try {
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
	public static void closeOutputStream(OutputStream out) {
		try {
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static String transCode(String src, String srcEncoding, String targetEncoding) {
		try {
			src = new String(src.getBytes(srcEncoding), targetEncoding);
			src = src.replaceAll(" ", "%20");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return src;
	}
	
	public static void main(String[] args) {
		System.out.println(getContentFromURLByGet("http://10.161.12.84:9200/_search", null));
	}
}
