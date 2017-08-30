package test;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import service.ElasticsearchService;
import service.InfluxDBUtil;
import service.JsonService;
import service.MLSUtil;

public class Test {
	
	public static void testEsService(){
		
			ElasticsearchService service = new ElasticsearchService();
			String ret = service.getInfosByModuleTimeMessage("cbss-billing-2017-08", "gboss.cbs.billing.rate.2", "2017-08-18T00:00:00.000Z", "2017-08-19T00:00:00.000Z", "APPLICATION STARTUP -- enter main loop", 0, 0);
			System.out.println(ret);
	}
	
	public static void testInfluxDB(){
		
		double ret = InfluxDBUtil.querySectionTimeDiff_Province("2017-08-22T06:10:00.000Z", "2017-08-22T06:11:00.000Z", "11");
		System.out.println(ret);
	}
	
	public static void testGetProvinceIndex(){
		ElasticsearchService service = new ElasticsearchService();
		//System.out.println(service.getProvinceIndex("17"));
	}
	
	public static void testReadProvince(){
		JSONObject provinces = JsonService.getProvinceArray("src/config/province.config");
		try {
			System.out.println(provinces.getJSONArray("1").getJSONObject(1));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testRestartNum(){
		ElasticsearchService service = new ElasticsearchService();
		int ret = service.getRateRestartNum_Domain("6", "2017-08-01T00:00:00.000Z", "2017-08-21T23:59:59.999Z");
		System.out.println(ret);
	}
	
	//0, 1472.7, 0.0
	//0, 1355.6, 0.0
	//0, 9818.999999999998, 325.0
	//0, 5101.4, 529.0
	//0, 4881.8, 441.0
	//0, 5015.1, 7.0
	//0, 5773.099999999999, 0.0
	
	/**
	 * 0, 15352.39999999999, 18.0
	 * 0, 14117.799999999996, 0.0
	 * 0, 17028.4, 16.0
	 * 0, 16294.699999999999, 3.0
	 * 0, 15375.2, 7748.0
	 * 0, 6415.2999999999965, 7120.0
	 * 0, 5493.699999999998, 5526.0
	 * 0, 5539.699999999999, 5947.0
	 * 0, 2646.6999999999985, 5592.0
	 * 0, 4224.6, 947.0
	 * 0, 9323.0, 0.0
	 */
	
	public static void testWriteMLS(){
		List<String> list = MLSUtil.createSourceData("2017-08-28T00:50:00.000Z", "13");
		MLSUtil.appendDataToFile("./TEST.TXT", list, "");
	}
	
	public static void testReadViewPoint(){
		List<Map<String, String>> list = MLSUtil.readViewPoint("src/config/exception.data");
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);
			System.out.println("time: "+map.get("time")+", code: "+map.get("code")+", tag: "+map.get("tag"));
			List<String> temp = MLSUtil.createSourceData(map.get("time"), map.get("code"));
			MLSUtil.appendDataToFile("./TEST.DAT", temp, map.get("tag"));
		}
	}
	public static void main(String[] args){
		testWriteMLS();
	}
}
