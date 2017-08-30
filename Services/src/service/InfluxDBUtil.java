package service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBUtil {
	private static Properties influxdbConf = new Properties();
	private static InfluxDBService service = null;
	
	static {
		try {
			influxdbConf.load(new FileInputStream(new File("src/config/influxdb.properties")));
			String username = influxdbConf.getProperty("influxdb_username");
			String password = influxdbConf.getProperty("influxdb_password");
			String openurl = "http://"+influxdbConf.getProperty("influxdb_address")+":"+influxdbConf.getProperty("influxdb_port");
			String database = influxdbConf.getProperty("influxdb_dbname");
			service = new InfluxDBService(username, password, openurl, database);
			service.buildInfluxDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据时间区间和域号求积压量�?�和
	 * @param start 起始时间（标准格式）
	 * @param end 结束时间（标准格式）
	 * @param domain 域号
	 **/
	public static double querySectionBacklog_Domain(String start, String end, String domain){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000020' AND time >= '"+start+"' AND time < '"+end+"' AND \"CHANNEL\" =~ /"+domain+".*/ fill(0)";
		return getResultSum(sql, 1);
	}
	
	/**
	 * 根据时间区间和省分编码求积压量�?�和
	 * @param start 起始时间（标准格式）
	 * @param end 结束时间（标准格式）
	 * @param province 省分编码 
	 **/
	public static double querySectionBacklog_Province(String start, String end, String province){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000020' AND time >= '"+start+"' AND time < '"+end+"' AND \"PROVINCE\" = '"+province+"' fill(0)";
		return getResultSum(sql, 1);
	}
	
	/**
	 * 根据时间区间和域号求批价效率总和
	 * @param start 起始时间（标准格式）
	 * @param end 结束时间（标准格式）
	 * @param domain 域号
	 **/
	public static double querySectionEff_Domain(String start, String end, String domain){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000022' AND time >= '"+start+"' AND time < '"+end+"' AND \"CHANNEL\" =~ /"+domain+".*/ fill(0)";
		return getResultSum(sql, 1);
	}
	
	/**
	 * 根据时间区间和省分编码求批价效率总和
	 * @param start 起始时间（标准格式）
	 * @param end 结束时间（标准格式）
	 * @param province 省分编码 
	 **/
	public static double querySectionEff_Province(String start, String end, String province){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000022' AND time >= '"+start+"' AND time < '"+end+"' AND \"PROVINCE\" = '"+province+"' fill(0)";
		return getResultSum(sql, 1);
	}
	
	public static double querySectionTimeDiff_Domain(String start, String end, String domain){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		List<String> provinces = JsonService.getDomainProvinceCodes(domain);
		String temp = "";
		for (int i = 0; i < provinces.size(); i++) {
			if (i != 0) {
				temp += " OR ";
			}
			temp += "\"PROVINCE\" = '"+provinces.get(i)+"'";
		}
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000266' AND \"TIMEDIFTYPE\"='0' AND time >= '"+start+"' AND time < '"+end+"' AND ("+temp+")  fill(0)";
		return getResultSum(sql, 1);
	}
	
	public static double querySectionTimeDiff_Province(String start, String end, String province){
		start = formatTime2UTC(start);
		end = formatTime2UTC(end);
		String sql = "SELECT sum(\"targetvalue\") FROM \"autobill_rp1\".\"TARGETDATA\" WHERE \"targetid\" = '1200000266' AND \"TIMEDIFTYPE\"='0' AND time >= '"+start+"' AND time < '"+end+"' AND \"PROVINCE\" = '"+province+"'  fill(0)";
		return getResultSum(sql, 1);
	}
	
	/**
	 * 根据sql查询后的结果的某�?列求和（仅支持数值类型的列）
	 * @param sql influx查询语句
	 * @param column 返回queryResult的列�?
	 * @return doublele类型的求和结�?
	 */
	private static double getResultSum(String sql, int column){
		double ret = 0.0;
		System.out.println(sql);
		QueryResult queryResult = service.query(sql);
		System.out.println(queryResult);
		if (queryResult.getError() != null) {
			return ret;
		}
		List<Result> results = queryResult.getResults();
		for (int i = 0; i < results.size(); i++) {
			Result result = results.get(i);
			List<Series> seriesList = result.getSeries();
			if (seriesList == null || result.getError() != null) {
				return ret;
			}
			for (int j = 0; j < seriesList.size(); j++) {
				Series series = seriesList.get(j);
				List<List<Object>> valuesList = series.getValues();
				for (int k = 0; k < valuesList.size(); k++) {
					List<Object> values = valuesList.get(k);
					ret += Double.valueOf(values.get(column).toString());
				}
			}
		}
		return ret;
	}
	/**
	 * 将本地时间转换为UTC时间
	 * @param rawTime 本地时间字符�? 格式�?2017-08-22T08:23:22.567Z
	 * @return 转换后的标准格式 UTC时间字符�? 格式�?2017-08-22T08:23:22.567Z
	 */
	private static String formatTime2UTC(String rawTime){
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
}

/**
 * 遍历QueryResult
 */
/*QueryResult queryResult = service.query(sql);
List<Result> results = queryResult.getResults();
for (int i = 0; i < results.size(); i++) {
	Result result = results.get(i);
	List<Series> seriesList = result.getSeries();
	for (int j = 0; j < seriesList.size(); j++) {
		Series series = seriesList.get(j);
		String name = series.getName();
		Map<String, String> tags = series.getTags();
		List<String> columns = series.getColumns();
		List<List<Object>> valuesList = series.getValues();
		
	}
}*/
