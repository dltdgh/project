package pojo;

import org.json.JSONObject;

/**
 * {"_index":"cbss-billing-2016-10-01","_type":"WARNING","_id":"AVd72kn20RcVnl-MePvX","_score":1.4142135,"_source":{"message":"2016-10-01 00:08:05.491 MAJOR [gboss.cbs.billing.smsremind.25007] 50993 0 ChkSmsContentPreAddCache error! SmsContent=��ܰ��ʾ����ֹ<&MONTH&>��<&DAY&>��<&HOUR&>ʱ<&MINUTE&>�֣�<@30@> �ظ���������ѯ����������http://wap.10010.com ��ѯ���顣 CurFileName=GPP87_0870000GJSJ0019502800201610010004004AB7.m.b_25007.out.K.20161001000016 UserID=8716083019197111 FeepolicyId=5665000","@version":"1","@timestamp":"2016-09-30T16:08:05.491Z","path":"/cbss/billing/log/smsremind.20161001.log","biz":"billing","datatype":"fileinput","hostname":"app202","hostip":"10.161.10.2","kafka":{"msg_size":603,"topic":"log_billing","consumer_group":"logstash_es","partition":1,"offset":505803331,"key":"S3luNEZ0"},"datatime":"2016-10-01 00:08:05.491","module":"[gboss.cbs.billing.smsremind.25007]","type":"WARNING","dtime":"2016-10-01","errortype":"error","tags":["_grokparsefailure"]}}
 * @author duliantao
 *
 */

public class LogInfo {
	private String _index;
	private String _type;
	private String _id;
	private String message;
	private String path;
	private String biz;
	private String datatype;
	private String hostname;
	private String hostip;
	private String datatime;
	private String module;
	private String type;
	private String dtime;
	private String kafka;
	
	public LogInfo(JSONObject obj){
		try {
			set_index(obj.getString("_index"));
			set_type(obj.getString("_type"));
			set_id(obj.getString("_id"));
			JSONObject _source = obj.getJSONObject("_source");
			setMessage(_source.getString("message"));
			setPath(_source.getString("path"));
			setBiz(_source.getString("biz"));
			setDatatype(_source.getString("datatype"));
			setHostname(_source.getString("hostname"));
			setHostip(_source.getString("hostip"));
			setModule(_source.getString("module"));
			setDatatime(_source.getString("datatime"));
			setType(_source.getString("type"));
			setDtime(_source.getString("dtime"));
			setKafka(_source.getString("kafka"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public String get_index() {
		return _index;
	}
	public void set_index(String _index) {
		this._index = _index;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getBiz() {
		return biz;
	}
	public void setBiz(String biz) {
		this.biz = biz;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getHostip() {
		return hostip;
	}
	public void setHostip(String hostip) {
		this.hostip = hostip;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getDatatime() {
		return datatime;
	}

	public void setDatatime(String datatime) {
		this.datatime = datatime;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDtime() {
		return dtime;
	}
	public void setDtime(String dtime) {
		this.dtime = dtime;
	}
	public String getKafka() {
		return kafka;
	}
	public void setKafka(String kafka) {
		this.kafka = kafka;
	}
	@Override
	public String toString() {
		return "LogInfo [_index=" + _index + ", _type=" + _type + ", _id="
				+ _id + ", message=" + message + ", path=" + path + ", biz="
				+ biz + ", datatype=" + datatype + ", hostname=" + hostname
				+ ", hostip=" + hostip + ", datatime=" + datatime + ", module="
				+ module + ", type=" + type + ", dtime=" + dtime
				+ ", kafka=" + kafka + "]";
	}
	
}
