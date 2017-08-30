package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pojo.LogInfo;

public class JsonService {
	public static List<LogInfo> getInfosByJson(String jsonStr){
		List<LogInfo> infos = new ArrayList<LogInfo>();
		try {
			JSONObject obj = new JSONObject(jsonStr);
			JSONArray array = obj.getJSONObject("hits").getJSONArray("hits");
			for (int i = 0; i < array.length(); i++) {
				LogInfo info = new LogInfo(array.getJSONObject(i));
				infos.add(info);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return infos;
	}
	
	public static int getSizeByJson(String jsonStr){
		int n = 0;
		try {
			JSONObject obj = new JSONObject(jsonStr);
			n = obj.getJSONObject("hits").getInt("total");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return n;
	}
	
	/**
	 * 
	 * @param path	存储省分配置文件路径
	 * @return 返回json格式的省分配置信息包括域号、省分编码、省分名称
	 */
	
	public static JSONObject getProvinceArray(String path){
		JSONObject obj = new JSONObject();
		try {
			Scanner sc = new Scanner(new File(path));
			
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				String[] items = line.split("\\s");
				JSONObject item = new JSONObject();
				item.put("code", items[1]);
				item.put("name", items[2]);
				if(!obj.has(items[0])){
					obj.put(items[0], new JSONArray());
				}
				JSONArray array = obj.getJSONArray(items[0]);
				array.put(item);
			}
			sc.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	public static List<String> getDomainProvinceCodes(String domain){
		List<String> list = new ArrayList<String>();
		JSONObject obj = getProvinceArray("src/config/province.config");
		try {
			JSONArray array = obj.getJSONArray(domain);
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				list.add(item.getString("code"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
