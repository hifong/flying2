package com.flying.sql;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flying.common.http.JsonHttpUtils;
import com.flying.common.util.Codes;
import com.flying.common.util.JSONUtils;
import com.flying.common.util.Pair;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.data.Data;

@Service("sql")
public class SQLService {
	
	public Data execute(@Param(value="token",required=true)String token, 
			@Param(value="sql",required=true)String sql) throws Exception {
		List<Pair<String, String>> headers = Utils.newArrayList();
		headers.add(new Pair<String, String>("Cookie", "token=" + token));

		List<Pair<String, String>> params = Utils.newArrayList();
		params.add(new Pair<String, String>("sql", sql));
		
		Object obj = null;
		try {
			obj = JsonHttpUtils.post("https://efps.epaylinks.cn/api/txs/pay/querySql", headers, params);
		} catch(Exception e) {
			return new Data(Codes.MSG, e);
		}
		if(obj == null) {
			return new Data(Codes.MSG, "NULL");
		}
		if(obj instanceof JSONObject) {
			return new Data(Codes.MSG, obj.toString());
		} 
		JSONArray ja = (JSONArray)obj;
		if(ja.size() == 0) {
			return new Data(Codes.MSG, obj.toString());
		}
		JSONObject first = ja.getJSONObject(0);
		List<String> fields = Utils.newArrayList();
		fields.addAll(first.keySet());
		
		List<Map<String, Object>> rows = Utils.newArrayList();
		for(int i=0; i< ja.size(); i++) {
			Map<String, Object> row = Utils.newHashMap();
			JSONObject r = ja.getJSONObject(i);
			fields.forEach(x -> row.put(x, r.get(x)));
			rows.add(row);
		}
		return new Data("fields", fields, "rows", rows);
	}

	public Data insideMQ(@Param(value="token",required=true)String token, 
			@Param(value="customer_code",required=true)String customer_code) throws Exception {
		final String sql = "select so.end_time,'FZ_GATEWAY_PAYMENT_RESULT_MSG' as msg_type,'InsidePay' as pay_instruction,so.state as pay_state, " + 
				"ipr.source_customer_list,ipr.target_customer_list,ipr.transaction_no,'ZF' as transaction_type " + 
				"from txs_split_order so,pay_inside_pay_record ipr " + 
				"where so.transaction_no=ipr.transaction_no " + 
				"and so.out_trade_no in( " + 
				"  select po.out_trade_no " + 
				"  from txs_split_order so,txs_pre_order po " + 
				"  where so.out_trade_no=po.out_trade_no " + 
				"  and po.pay_state=1 " + 
				"  and not exists (select 1 from sett_settment_voucher sv where so.transaction_no=sv.transcation_no) " + 
				"  and so.customer_code='"+customer_code+"' " + 
				")";
		Data result = this.execute(token, sql);
		List<Map<String, Object>> rows = result.get("rows");
		for(Map<String, Object> row: rows) {
			String transactionNo = (String)row.get("TRANSACTION_NO");
			//
			final String ssql = "select so.amount,so.amount - so.procedure_fee as arrivedAmount, so.business_inst_id, " + 
					"so.customer_code,so.procedure_fee,1529942400000 as sett_end_time,1529856000000 as sett_start_time,  " + 
					"(select id from sett_cycle_rule_inst cri where cri.customer_business_inst_id =so.business_inst_id) as sett_cycle_inst_id " + 
					"from txs_split_order so " + 
					"where so.transaction_no='"+transactionNo+"'";
			Data s = this.execute(token, ssql);
			List<Map<String, Object>> srows = s.get("rows");
			List<Map<String, Object>> sourceRows = Utils.newArrayList();
			for(Map<String, Object> tr: srows) {
				Map<String, Object> source = Utils.newHashMap();
				source.put("amount", tr.get("AMOUNT"));
				source.put("arrivedAmount", tr.get("ARRIVEDAMOUNT"));
				source.put("businessInstanceId", tr.get("BUSINESS_INST_ID"));
				source.put("customerCode", tr.get("CUSTOMER_CODE"));
				source.put("procedureFee", tr.get("PROCEDURE_FEE"));
				source.put("settCycleEndTime", tr.get("SETT_END_TIME"));
				source.put("settCycleRuleInstId", tr.get("SETT_CYCLE_INST_ID"));
				source.put("settCycleStartTime", tr.get("SETT_START_TIME"));
				sourceRows.add(source);
			}
			row.put("SOURCE_CUSTOMER_LIST", JSONUtils.toJSONString(sourceRows));
			//
			final String tsql="select so.amount,sr.amount as arrivedAmount, sr.business_inst_id, " + 
					"sr.customer_code,sr.procedurefee,1529942400000 as sett_end_time,1529856000000 as sett_start_time,  " + 
					"(select id from sett_cycle_rule_inst cri where cri.customer_business_inst_id =so.business_inst_id) as sett_cycle_inst_id " + 
					"from txs_split_order so,txs_split_record sr " + 
					"where so.transaction_no=sr.transaction_no " + 
					"and so.transaction_no='"+transactionNo+"'";

			Data t = this.execute(token, tsql);
			List<Map<String, Object>> trows = t.get("rows");
			List<Map<String, Object>> targetRows = Utils.newArrayList();
			for(Map<String, Object> tr: trows) {
				Map<String, Object> target = Utils.newHashMap();
				target.put("amount", tr.get("AMOUNT"));
				target.put("arrivedAmount", tr.get("ARRIVEDAMOUNT"));
				target.put("businessInstanceId", tr.get("BUSINESS_INST_ID"));
				target.put("customerCode", tr.get("CUSTOMER_CODE"));
				target.put("procedureFee", tr.get("PROCEDUREFEE"));
				target.put("settCycleEndTime", tr.get("SETT_END_TIME"));
				target.put("settCycleRuleInstId", tr.get("SETT_CYCLE_INST_ID"));
				target.put("settCycleStartTime", tr.get("SETT_START_TIME"));
				targetRows.add(target);
			}
			row.put("TARGET_CUSTOMER_LIST", JSONUtils.toJSONString(targetRows));
		}
		return result;
	}
}
