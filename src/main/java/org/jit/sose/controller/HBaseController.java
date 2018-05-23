package org.jit.sose.controller;

import java.io.IOException;

import org.jit.sose.config.ParameterConfig;
import org.jit.sose.hadoop.HBaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * HBase的业务层
 * @author chenmin
 *
 */
@Controller
public class HBaseController {
	
	/**
	 * 创建HBase的表
	 * @param tableName	表名
	 * @param family	列族名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.CREATEHBASETABLE_DO,method = RequestMethod.POST)
	public JSONObject createHbaseTable(String tableName, String[] family){
		JSONObject createJson = new JSONObject();
		try {
			createJson = HBaseUtil.createTable(tableName, family);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			createJson.put(ParameterConfig.CREATEHBASERESULT, "创建HBase表失败");
		}
		return createJson;
	}
	
	/**
	 * 修改（添加）HBase中某行的数据信息
	 * @param tableName：表名
	 * @param rowKey：需要操作的行
	 * @param family：需要操作的列族
	 * @param column：需要操作的列
	 * @param value：   将要修改成的值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.UPDATEHBASEBYROW_DO,method = RequestMethod.POST)
	public JSONObject updateHBaseRow(String tableName,String rowKey,String family,String column,String value){
		JSONObject uodateJson = new JSONObject();
		uodateJson = HBaseUtil.updateHbaseRow(tableName, rowKey, family, column, value);
		return uodateJson;
	}
	
	
	/**
	 * 查询HBase中表的数据信息
	 * @param tableName：需要查询的表名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.QUERYHBASETABLE_DO,method = RequestMethod.POST)
	public JSONObject queryHbaseTable(String tableName){
		JSONObject queryJson = new JSONObject();
		queryJson = HBaseUtil.getResultScannerByFilter(tableName, null);
		return queryJson;
	}
	

}
