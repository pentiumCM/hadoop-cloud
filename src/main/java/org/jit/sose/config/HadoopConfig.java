package org.jit.sose.config;

public class HadoopConfig {

	// 文件系统Key
	public static final String FS = "fs.defaultFS";
	
	// HBase的连接key
	public static final String HBASEKEY = "hbase.zookeeper.quorum";
	
	// hadoop集群master节点的IP
	public static final String MASTER_IP = "172.20.10.11";
	
	// hadoop集群master节点的hdfs的端口
	public static final String HDFS_PORT = "9000";
	
	// Hbase的根路径
	public static final String HBASEROOTDIR = "/hbase";
	
	// 文件上传到hdfs中的路径
	public static final String HDFS_DEST = "hdfs://"+ MASTER_IP + ":"+ HDFS_PORT;
	
	// hadoop集群的master节点的hdfs连接信息
	public static final String MASTER = "hdfs://"+ MASTER_IP + ":"+ HDFS_PORT;
	
	// hbase拼接的根路径URL
	public static final String HBASEROOTURL = "hdfs://"+ MASTER_IP + ":"+ HDFS_PORT + HBASEROOTDIR;
	
	
	
}
