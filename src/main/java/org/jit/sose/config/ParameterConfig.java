package org.jit.sose.config;

public class ParameterConfig {

	//本地服务器的项目路径
	public static final String PROJURL = "http://192.168.1.66:8080/hadoop-cloud/";
	
	//本地服务器的虚拟目录路径
	public static final String VIRTUALDIRECTORYURL = "http://192.168.1.66:8080/localFile/";
	
	// 用户指定文件上传到hdfs中文件夹的参数
	public static final String USERDIR = "userDir";

	// 用户默认上传的文件夹位置的文件夹名称
	public static final String DEFAULTINPUTDIR = "/input/";

	// 用户上传文件的文件名参数
	public static final String FILE = "file";

	// 用户指定要下载的文件的文件名的参数
	public static final String DOWNLOADFILE = "downloadFile";

	// 指定用户下载hdfs文件到本地的路径
	public static final String LOCALDOWNDIR = "F:/upload/localHdfs/";

	// hdfs文件下载到本地的结果key
	public static final String HDFSTOLOCALRESULTKEY = "hdfsToLocalResult";
	
	// 创建Hbase表的结果
	public static final String CREATEHBASERESULT = "createHbaseResult";
	
	// 创建Hbase表成功的标识
	public static final String CREATEHBASEVALUE = "创建表成功";

	// 文件存在的状态
	public static final String FILEEXISTSTATUS = "A";
	
	//文件不存在的状态
	public static final String FILENOTEXISTSTATUS = "X";
	
	// hdfs文件下载到本地的结果key
	public static final String HDFSTOLOCALRESULTVALUE = "文件下载成功";

	// 客户端文件上传到hdfs的结果
	public static final String LOCALTOHDFSRESULTKEY = "localToHdfsResult";

	// 客户端文件上传到hdfs的结果
	public static final String LOCALTOHDFSRESULTVALUE = "文件上传成功";

	// 上传文件的方法名
	public static final String UPFILE_DO = "/upFile.do";

	// 文件从hdfs下载到本地在本地服务器上的路径
	public static final String LOCALSERVERFILEURL = "localServerFileUrl";
	
	// 查询文件夹的下的件列表方法名
	public static final String HDFSFILELIST_DO = "/hdfsFileList.do";

	// hdfs文件下载到本地文件系统的方法名
	public static final String HDFSDOWNTOLOCAL_DO = "/hdfsDownToLocal.do";
	
	// HBase数据库创表的方法名
	public static final String CREATEHBASETABLE_DO = "/createHbaseTable.do";
	
	// HBase数据库修改（插入）一行数据的方法名
	public static final String UPDATEHBASEBYROW_DO = "/updateHBaseByRow.do";
	
	// HBase数据库删除一行的方法名
	public static final String DELETEHBASEBYROW_DO = "/deleteHBaseByRow.do";
	
	// 查询HBase中某一张表的数据信息
	public static final String QUERYHBASETABLE_DO = "/queryHbaseTable.do";
}
