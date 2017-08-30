package service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pojo.LogInfo;

@WebService
public class ElasticsearchService {
	
	String SQL_URL = "http://10.161.12.86:8200/_sql";
	
	private String getResultBySQL(String sql) {
		System.out.println("query "+sql+" execute...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("sql", sql);
		String content = HttpService.getContentFromURLByGet(SQL_URL, params);
		//System.out.println("content:"+content);
		return content;
	}
	
	/*private String getInfoByIndex(String index, String type, String id){
		String sql = "select * from "+index+" where _type='"+type+"' and _id='"+id+"'";
		String content = getResultBySQL(sql);
		return content;
	}
	
	private void getInfosByIndex(String index, String type, int start, int n) {
		String sql = null;
		if(type == null){
			sql = "select * from "+index+" limit "+start+","+n;
		}
		else{
			sql = "select * from "+index+" where _type='"+type+"' limit "+start+","+n;
		}
		String content = getResultBySQL(sql);
		List<LogInfo> infos = JsonService.getInfosByJson(content);
		for (LogInfo info : infos) {
			System.out.println(info.toString());
		}
	}*/
	
	/**
	 * 
	 * @param index	存储在es里的索引前缀
	 * @param module	module字段节�??
	 * @param startTime	起始时间
	 * @param endTime	终结时间
	 * @param msg	message字段节�??
	 * @param start	起始位置
	 * @param n	返回数量
	 * @return	json格式的查询结�?
	 */
	
	public String getInfosByModuleTimeMessage(String index, String module, String startTime, String endTime, String msg, int start, int n){
		startTime = formatTime2UTC(startTime);
		endTime = formatTime2UTC(endTime);
		String sql = "select * from "+index+"* where module like '*"+module+"*' and @timestamp > '"+startTime+"' and @timestamp < '"+endTime+"' and message='*"+msg+"*' limit "+start+","+n;
		String content = getResultBySQL(sql);
		//System.out.println("content:"+content);
		String result = formatResult(content);
		return result;
	}
	
	@WebMethod
	public String getInfosByModuleAndTime(String module, String startTime, String endTime, int start, int n) throws JSONException{
		startTime = formatTime2UTC(startTime);
		endTime = formatTime2UTC(endTime);
		String sql = "select * from * where module like '*"+module+"*' and @timestamp > '"+startTime+"' and @timestamp < '"+endTime+"' limit "+start+","+n;
		String content = getResultBySQL(sql);
		//System.out.println("content:"+content);
		String result = formatResult(content);
		return result;
	}
	@WebMethod
	public String getInfosByModuleAndTime2(String part, String module, String day, String startTime, String endTime, int start, int n) throws JSONException{
		String table = part+"-"+day;
		startTime = formatTime2UTC(day+"T"+startTime+"Z");
		endTime = formatTime2UTC(day+"T"+endTime+"Z");
		String sql = "select * from "+table+" where module like '*"+module+"*' and @timestamp > '"+startTime+"' and @timestamp < '"+endTime+"' limit "+start+","+n;
		String content = getResultBySQL(sql);
		//System.out.println("content:"+content);
		String result = formatResult(content);
		return result;
	}
	
	@WebMethod
	public int getCountByTypeAndMsg(String index, String type, String msg) throws JSONException{
		String sql = "select count(*) from "+index+" where type='"+type+"' and message='*"+msg+"*'";
		String content = getResultBySQL(sql);
		System.out.println(content);
		int count = JsonService.getSizeByJson(content);
		return count;
	}
	
	@WebMethod
	public String getRecentLogs(String part, String module){
		
		String ymd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String sql = "select * from "+part+"-"+ymd+" where module like '*"+module+"*' order by @timestamp desc";
		String content = getResultBySQL(sql);
		//System.out.println(content);
		String result = formatResult(content);
		return result;
	}
	
	@WebMethod
	public String getRecentLogsWithNum(String part, String module, int n) throws JSONException{
		
		String ymd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String sql = "select * from "+part+"-"+ymd+" where module like '*"+module+"*' order by @timestamp desc limit 0,"+n;
		String content = getResultBySQL(sql);
		//System.out.println(content);
		String result = formatResult(content);
		return result;
	}
	
	private String formatResult(String content){
		JSONObject obj = new JSONObject();
		try {
			List<LogInfo> infos = JsonService.getInfosByJson(content);
			int total = JsonService.getSizeByJson(content);
			JSONArray array = new JSONArray();
			for (LogInfo info : infos) {
				JSONObject tObj = new JSONObject();
				tObj.put("msg", info.getMessage());
				array.put(tObj);
			}	
			obj.put("state", "success");
			obj.put("size", infos.size());
			obj.put("total", total);
			obj.put("hits", array);
		} catch (Exception e) {
			// TODO: handle exception
		}	
		return obj.toString();
	}
	
	private String formatTime2UTC(String rawTime){
		String ret = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date date = format.parse(rawTime);
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			format.setTimeZone(timeZone);
			ret = format.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private String getProvinceIndex(String code){
		String ret = null;
		try {
			JSONObject obj = JsonService.getProvinceArray("src/config/province.config");
			Iterator<?> iterator = obj.keys();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				JSONArray array = obj.getJSONArray(key);
				int len = array.length();
				for(int i = 0; i < len; i++){
					JSONObject item = array.getJSONObject(i);
					if (code.equals(item.getString("code"))) {
						ret = key+(i+1);
						break;
					}
				}
				if (ret != null) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private int getRateRestartNum(String channelPrefix, String start, String end){
		int n = 0;
		String day = "";
		String startDay = start.split("T")[0];
		String endDay = end.split("T")[0];
		if(startDay.equals(endDay)){
			day = startDay;
		}
		String ret = getInfosByModuleTimeMessage("cbss-billing-"+day, "gboss.cbs.billing.rate."+channelPrefix, start, end, "APPLICATION STARTUP -- enter main loop", 0, 0);
		try {
			JSONObject obj = new JSONObject(ret);
			//n = Integer.parseInt(obj.getString("total"));
			//System.out.println(obj);
			n = obj.getInt("total");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return n;
	}
	
	public int getRateRestartNum_Province(String code, String start, String end){
		String channelPrefix = getProvinceIndex(code);
		return getRateRestartNum(channelPrefix, start, end);
	}
	
	public int getRateRestartNum_Domain(String domain, String start, String end){
		return getRateRestartNum(domain, start, end);
	}
	
	/*public static void main(String[] args) {
		Endpoint.publish("http://127.0.0.1:8765/elasticsearch/sql", new ElasticsearchService());
		System.out.println("service started!");
	}*/
}
