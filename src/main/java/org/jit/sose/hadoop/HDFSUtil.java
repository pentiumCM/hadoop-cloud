package org.jit.sose.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.jit.sose.config.HadoopConfig;
import org.jit.sose.config.ParameterConfig;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;

/**
 * HDFS文件操作的工具类
 * @author chenmin
 *
 */
public class HDFSUtil {

	/**
	 * 获取指定的hdfs路径下的文件列表
	 * 
	 * @param userDir： 指定的用户文件夹
	 * @return 返回文件列表信息
	 */
	public static JSONObject getFileList(String userDir) {
		JSONObject fileListJson = new JSONObject();
		try {
			Configuration conf = new Configuration();
			// 指定所要遍历的文件夹
			String fileDir = HadoopConfig.HDFS_DEST + "/" +userDir;
			Path srcDirPath = new Path(fileDir);
			System.out.println("文件夹的Path:" + srcDirPath);
			// 返回指定节点文件系统的URL
			URI uri = URI.create(HadoopConfig.MASTER);
			// 返回指定URL的节点的文件系统
			FileSystem fs = FileSystem.get(uri, conf);
			// 开始遍历指定的文件夹
			List<String> files = new ArrayList<String>();
			if (fs.exists(srcDirPath) && fs.isDirectory(srcDirPath)) {
				for (FileStatus status : fs.listStatus(srcDirPath)) {
					files.add(status.getPath().toString());
				}
			}
			// 关闭文件系统
			fs.close();
			fileListJson.put("filelist", files);
		} catch (Exception e) {
		}
		return fileListJson;
	}

	/**
	 * 文件上传到 HDFS 方法
	 * 
	 * @param inputDir: 文件上传到HDFS 上面的路径 ,类型为：String
	 * @param file:	 	需要上传的文件, 类型为：CommonsMultipartFile
	 * @return  返回文件上传的结果
	 */
	public static JSONObject FileUpToHdfs(String inputDir, CommonsMultipartFile file) {
		JSONObject hdfsJson = new JSONObject();
		// 获取hadoop中HDFS配置信息
		Configuration conf = new Configuration();
		// 不拷贝core-site.xml和hdfs-site.xml到当前工程下的情况下加一面这行代码。配置
		conf.set(HadoopConfig.FS, HadoopConfig.MASTER);
		// 返回指定节点文件系统的URL(在远程时使用)
		URI uri = URI.create(HadoopConfig.MASTER);
		// 返回指定URL的节点的文件系统
		FileSystem hdfsFS = null;
		// 新建输入输出字节流，进行IO操作
		OutputStream out = null;
		InputStream inputStream = null;
		try {
			// 返回指定URL的节点的文件系统
			hdfsFS = FileSystem.get(uri, conf);
			// 创建指定path对象的一个文件，返回一个用于写入数据的输出流
			out = hdfsFS.create(new Path(inputDir), new Progressable() {
				@Override
				public void progress() {
					System.out.println("上传完一个文件");
					hdfsJson.put(ParameterConfig.LOCALTOHDFSRESULTKEY, "文件上传成功");
				}
			});
			// 获取文件，将文件读到inputStream流里面
			inputStream = file.getInputStream();
			// 将本地文件拷贝到文件系统
			IOUtils.copyBytes(inputStream, out, 4096, true);
			System.out.println("退出文件上传方法");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hdfsJson.put(ParameterConfig.LOCALTOHDFSRESULTKEY, "文件上传失败");
		} finally {
			try {
				// 关闭流
				hdfsFS.close();
				out.close();
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("关闭流失败");
			}
		}
		return hdfsJson;
	}

	/**
	 * hdfs中的文件下载到本地
	 * 
	 * @param src：      文件在hdfs上面的路径, 类型为：String
	 * @param dest: 文件要下载到本地文件系统的位置, 类型为：String
	 * @return  返回文件下载的结果
	 */
	public static JSONObject fileHdfsDownLoad(String src, String dest) {
		JSONObject downJson = new JSONObject();
		// 新建hadoop中HDFS的文件系统
		FileSystem fs = null;
		try {
			// 获取hadoop中HDFS的配置信息
			Configuration conf = new Configuration();
			conf.set(HadoopConfig.FS, HadoopConfig.MASTER);
			// 指定需要下载的文件在hdfs上面的路径
			Path srcHdfsPath = new Path(src);
			System.out.println("文件在hdfs上面的路径：" + srcHdfsPath);
			// 获取HDFS的文件管理系统
			fs = srcHdfsPath.getFileSystem(conf);
			// 判断hdfs上面需要下载的文件是否存在
			if (!fs.exists(srcHdfsPath)) {
				downJson.put(ParameterConfig.HDFSTOLOCALRESULTKEY, "文件不存在");
				System.out.println("文件不存在");
				return downJson;
			}
			Path localPath = new Path(dest);
			// 复制HDFS文件系统中的文件到本地
			fs.copyToLocalFile(false, new Path(src), localPath);
			System.out.println("参数src：" + src);
		} catch (IOException ie) {
			ie.printStackTrace();
			downJson.put(ParameterConfig.HDFSTOLOCALRESULTKEY, "文件下载失败");
			return downJson;
		} finally {
			try {
				// 关闭文件系统
				fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 把文件下载的结果存到json中返回
		downJson.put(ParameterConfig.HDFSTOLOCALRESULTKEY, "文件下载成功");
		return downJson;
	}

	/**
	 * 获得新名称
	 * 
	 * @param name： 起始名称, 类型为：String
	 * @return  返回按时间叠加之后的新名称
	 */
	public static String getNewName(String name) {
		// 获取当前系统的时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String newName = sdf.format(new Date()) + "_" + name;
		return newName;
	}

}
