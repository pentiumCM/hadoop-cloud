package org.jit.sose.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.jit.sose.config.HadoopConfig;
import org.jit.sose.config.ParameterConfig;

import com.alibaba.fastjson.JSONObject;

/**
 * HBase数据库操作的工具类
 * @author chenmin
 *
 */
public class HBaseUtil {

	// 初始化管理员（对HBase表的操作需要管理员权限）
	static Admin admin = null;
	// 初始化HBase的连接
	static Connection con = null;

	/**
	 * 创建HBase的连接
	 * @param
	 * @return 返回创建连接的结果
	 */
	public static Connection getHbaseCon() {
		// 获取hbase配置文件
		Configuration conf = HBaseConfiguration.create();
		conf.set(HadoopConfig.HBASEKEY, HadoopConfig.MASTER_IP);
		try {
			// 创建Hbase的连接
			con = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("创建连接失败");
		}
		return con;
	}

	/**
	 * 创建HBase的表
	 * 
	 * @category create 'tableName','family1','family2','family3'
	 * @param tableName：表名
	 * @param family： 列族名
	 * @throws Exception
	 */
	public static JSONObject createTable(String tableName, String[] family) throws IOException {
		JSONObject createHbaseJson = new JSONObject();
		// 获取hbase的连接
		con = getHbaseCon();
		// 获得管理员
		Admin admin = con.getAdmin();
		// 判断表名是否合理
		TableName tn = TableName.valueOf(tableName);
		// 构建表的描述，并创建表
		HTableDescriptor desc = new HTableDescriptor(tn);
		for (int i = 0; i < family.length; i++) {
			desc.addFamily(new HColumnDescriptor(family[i]));
		}
		// 判断表是否存在
		if (admin.tableExists(tn)) {
			System.out.println("createTable => table Exists!");
			admin.disableTable(tn);
			admin.deleteTable(tn);
			admin.createTable(desc);
		} else {
			// 创建表成功
			admin.createTable(desc);
			System.out.println("createTable => create Success! => " + tn);
			createHbaseJson.put(ParameterConfig.CREATEHBASERESULT, ParameterConfig.CREATEHBASERESULT);
		}
		return createHbaseJson;
	}

	/**
	 * 插入或修改一条数据，针对列族中有一个列，value为String类型
	 * 
	 * @category put 'tableName','rowKey','familyName:columnName'
	 * @param tableName:表名
	 * @param rowKey
	 * @param family：列族
	 * @param column：列族里面的列(可动态创建列)
	 * @param value：   要修改的数据
	 * @return boolean
	 * @throws IOException
	 */
	public static JSONObject updateHbaseRow(String tableName, String rowKey, String family, String column, String value) {
		JSONObject updateJson = new JSONObject();
		try {
			// 获取HBase里面的某一张表
			Table table = getHbaseCon().getTable(TableName.valueOf(tableName));
			// 将数据插入到表中
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
			table.put(put);
			// 关闭表
			table.close();
			updateJson.put("修改表结果", "插入数据成功");
		} catch (IOException e) {
			e.printStackTrace();
			updateJson.put("修改表结果", "插入数据失败");
		}
		return updateJson;
	}
	
	
    /**
     * 删除某一行的数据，行健为String类型
     * @category deleteall 'tableName','rowKey'
     * @param tableName：要操作的表名
     * @param rowKey：要刪除的行
     * @throws IOException
     */
    public static JSONObject deleteDataByRow(String tableName, String rowKey) {
    	JSONObject deleteJson = new JSONObject();
    	try {
	        Table table = getHbaseCon().getTable(TableName.valueOf(tableName));
	        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
	        table.delete(deleteAll);
	        table.close();
	        deleteJson.put("删除行的结果为:", "删除行成功");
    	} catch (IOException e) {
			e.printStackTrace();
			deleteJson.put("删除行的结果为：", "删除行成功");
		}
    	return deleteJson;
    }
    
    
    /**
     * 按照一定规则扫描表（或者无规则）
     * @param tableName:要查询的表名
     * @param filter
     * @return
     */
    public static JSONObject getResultScannerByFilter(String tableName, Filter filter) {
    	JSONObject scanJson = new JSONObject();
    	ResultScanner resultScanner = null;
    	try {
    		Table table = getHbaseCon().getTable(TableName.valueOf(tableName));
    		Scan scan = new Scan();
			if (filter != null) {
				scan.setFilter(filter);
			}
    		resultScanner = table.getScanner(scan);
    		table.close();
    		scanJson.put("查询表的结果：", resultScanner.toString());
    	} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询表失败");
		}
    	return scanJson;
    }

}
