package service;

import java.util.Map;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class InfluxDBService {
	
	private String username;
	private String password;
	private String openurl;
	private String database;
	
	private InfluxDB influxDB;
	
	public InfluxDBService(String username, String password, String openurl, String database){
		this.username = username;
		this.password = password;
		this.openurl = openurl;
		this.database = database;
	}
	
	public InfluxDB buildInfluxDB(){
		if (influxDB == null) {
			influxDB = InfluxDBFactory.connect(openurl,username,password);
			influxDB.createDatabase(database);
		}
		return influxDB;
	}
	
	/**
	 * 设置数据保存策略 
     * defalut 策略�? /database 数据库名/ 30d 数据保存时限30�?/ 1  副本个数�?1/ 结尾DEFAULT 表示 设为默认的策�? 
	 */
	public void setRetentionPolicy(){
		String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT", "defalut", database, "30d", 1);
		this.query(command);
	}
	
	/**
	 * 查询influxdb
	 * @param command 查询语句
	 * @return QuerySet
	 */
	public QueryResult query(String command){
		return influxDB.query(new Query(command, database));
	}
	
	//public 
	
	/**
	 * 插入influxdb
	 * @param measurement 表名
	 * @param tags	标签
	 * @param fields	字段
	 */
	public void insert(String measurement,Map<String, String> tags,Map<String, Object> fields){
		Builder builder = Point.measurement(measurement);
		builder.tag(tags);
		builder.fields(fields);
		
		influxDB.write(database,"",builder.build());
	}
	
	/**
	 * 删除操作
	 * @param command 删除语句
	 * @return 错误信息
	 */
	public String deleteMeasurementData(String command){
		QueryResult result = influxDB.query(new Query(command, database));
		return result.getError();
	}
	
	/**
	 * 创建数据�?
	 * @param dbName 数据库名
	 */
	public void createDB(String dbName){
		influxDB.createDatabase(dbName);
	}
	
	/**
	 * 删除数据库操�?
	 * @param dbName 数据库名
	 */
	public void deleteDB(String dbName){
		influxDB.deleteDatabase(dbName);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOpenurl() {
		return openurl;
	}

	public void setOpenurl(String openurl) {
		this.openurl = openurl;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
