package service;

import java.awt.ItemSelectable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MLSUtil {
	public static List<String> createSourceData(String time, String province) {
		List<String> list = new ArrayList<String>();
		try {
			ElasticsearchService service = new ElasticsearchService();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date date = format.parse(time);
			long mid = date.getTime();
			long left = mid-1800000;
			ArrayList<String> times = new ArrayList<String>();
			for (int i = 0; i <= 6; i++) {
				times.add(format.format(new Date(left+600000*i)));
			}
			//list.add(String.valueOf(service.getRateRestartNum_Province(province, times.get(0), times.get(6))));
			/*for (int i = 0; i < 6; i++) {
				list.add(String.valueOf(InfluxDBUtil.querySectionEff_Province(times.get(i), times.get(i+1), province)));
			}
			for (int i = 0; i < 6; i++) {
				list.add(String.valueOf(InfluxDBUtil.querySectionBacklog_Province(times.get(i), times.get(i+1), province)));
			}*/
			for (int i = 0; i < 6; i++) {
				list.add(String.valueOf(InfluxDBUtil.querySectionTimeDiff_Province(times.get(i), times.get(i+1), province)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public static void appendDataToFile(String filePath, List<String> list, String tag) {
		try {
			File file = new File(filePath);
			FileWriter fw = new FileWriter(file,true);
			String temp = String.join(",", list);
			temp += (","+tag);
			fw.write(temp+"\n");
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Map<String, String>> readViewPoint(String filePath){
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		try {
			Scanner sc = new Scanner(new FileInputStream(new File(filePath)));
			while(sc.hasNext()){
				String line = sc.nextLine();
				String[] items = line.split("#")[0].trim().split("\\s+");
				Map<String, String> map = new HashMap<String, String>();
				map.put("time", items[0]);
				map.put("code", items[1]);
				map.put("tag", items[2]);
				list.add(map);
			}
			sc.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}
}
