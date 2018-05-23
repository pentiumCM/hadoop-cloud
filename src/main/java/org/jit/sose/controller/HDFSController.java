package org.jit.sose.controller;


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jit.sose.config.HadoopConfig;
import org.jit.sose.config.ParameterConfig;
import org.jit.sose.dto.FileDto;
import org.jit.sose.hadoop.HDFSUtil;
import org.jit.sose.service.FileService;
import org.jit.sose.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;

/**
 * HDFS的业务层
 * @author chenmin
 *
 */
@Controller
public class HDFSController {
	
	@Resource
	FileService fileService;
	@Resource
	UserService userService;
	
	/**
	 * 获取hdfs中文件夹里面文件列表的信息
	 * @param request  userDir：指定的文件夹, 类型：String
	 * @return 指定路径下面的文件列表信息
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.HDFSFILELIST_DO,method = RequestMethod.POST)
	public JSONObject hdfsFileList(HttpServletRequest request){
		JSONObject hdfsJson = new JSONObject();
		// 获取用户指定要遍历的文件夹
		String userDir = request.getParameter(ParameterConfig.USERDIR);
		// 处理没传指定文件夹参数的异常，若没有采用默认的文件夹
		if (userDir == null) {
			userDir = ParameterConfig.DEFAULTINPUTDIR;
		}
		hdfsJson = HDFSUtil.getFileList(userDir);
		// 文件列表不为空的情况
		if (hdfsJson.getString("filelist") != null) {
			// 将文件具体信息存入集合当中返回
			hdfsJson.put("fileDetailList", fileService.fileLitByPeron(userDir,ParameterConfig.FILEEXISTSTATUS));
		}else {
			// 文件列表为空
			System.out.println("文件列表为空");
		}
		return hdfsJson;
	}
	
	
	/**
	 * 文件上传到hdfs功能
	 * @param request	userDir：用户指定的文件存放文件夹	类型：String, 该参数与用户名同名
	 * 					file 文件类 （用户上传的文件）  类型：file
	 * 
	 * @return		返回文件上传的结果
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.UPFILE_DO,method = RequestMethod.POST)
	public JSONObject upFile(HttpServletRequest request,@RequestParam(ParameterConfig.FILE) CommonsMultipartFile file) 
			throws Exception{
		System.out.println("进入文件上传方法");
		JSONObject hdfsJson = new JSONObject();
		//获取用户想要存放的文件夹
		String userDir = request.getParameter(ParameterConfig.USERDIR);
		System.out.println("获取的用户文件夹为："+userDir);
		if(!userService.searchrep(userDir)){
			System.out.println("用户不存在");
			hdfsJson.put(ParameterConfig.LOCALTOHDFSRESULTKEY, "用户不存在");
			return hdfsJson;
		}
		if (userDir == null) {
			userDir = ParameterConfig.DEFAULTINPUTDIR;
		}
		System.out.println("完善之后的用户文件夹为:"+userDir);
		//获取用户上传的文件名
		String fileOriginalName = file.getOriginalFilename();
		System.out.println("文件名为："+fileOriginalName);
		String fileNewName = HDFSUtil.getNewName(fileOriginalName);
		System.out.println("处理之后的文件名为:"+fileNewName);
        //获取用户指定上传文件的路径
        String inputDir = HadoopConfig.HDFS_DEST + "/" + userDir + "/" + fileNewName;
        System.out.println("文件在服务器上的路径为："+inputDir);
        
        //调用文件帮助类，实现文件的上传功能
        hdfsJson = HDFSUtil.FileUpToHdfs(inputDir, file);
        if (hdfsJson.getString(ParameterConfig.LOCALTOHDFSRESULTKEY)
        		.equals(ParameterConfig.LOCALTOHDFSRESULTVALUE)) {
        	// 在文件成功上传到HDFS后将文件的信息存到数据库中
        	FileDto fileDto = new FileDto();
        	// 文件的新名称
        	fileDto.setFilenewname(fileNewName);
        	// 文件的原始名称
        	fileDto.setFileoriginalname(fileOriginalName);
        	// 文件在本地的路径
        	fileDto.setFilelocalurl(ParameterConfig.VIRTUALDIRECTORYURL + fileNewName);
        	// status为文件的状态码，A代表可用
        	fileDto.setFilestatus("A");
        	// 文件在hdfs上面的路径
        	fileDto.setFilehdfsurl(HadoopConfig.HDFS_DEST + userDir + fileNewName);
        	// 文件所属人
        	fileDto.setFileowner(userDir);
        	// 文件上传的时间
        	//获取系统时间作为文件上传的时间
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(df.format(day));
            String fileTime = df.format(day).toString();
        	fileDto.setFiletime(fileTime);
        	fileService.upFile(fileDto);
        }
		return hdfsJson;
	}
	
	
	/**
	 * 将hdfs中的文件下载到本地文件系统
	 * @param request	DOWNLOADFILE：需要下载的在hdfs上的文件,类型:String
	 * 					USERDIR: 用户指定的文件夹  , 	类型：String,  该参数与用户名相同
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = ParameterConfig.HDFSDOWNTOLOCAL_DO, method = RequestMethod.POST)
	public JSONObject downToLocal(HttpServletRequest request){
		JSONObject downJson = new JSONObject();
		//获取到用户想要下载的文件名
		String downFileName = request.getParameter(ParameterConfig.DOWNLOADFILE);
		String hdfsDir = request.getParameter(ParameterConfig.USERDIR);
		FileDto fileDto = fileService.selectByFileNewName(downFileName,hdfsDir,"A");
		//判断用户指定的文件所在的文件夹，如果该参数为空，则用默认的文件夹替代
		if (hdfsDir == null) {	
			hdfsDir = ParameterConfig.DEFAULTINPUTDIR;
		}else {
			hdfsDir = "/" + hdfsDir + "/";
		}
		// 指定文件存放在hdfs上面的位置
		String hdfsFilePath = HadoopConfig.HDFS_DEST  + hdfsDir  + downFileName;
		String destFilePath = ParameterConfig.LOCALDOWNDIR + downFileName;
		downJson = HDFSUtil.fileHdfsDownLoad(hdfsFilePath,destFilePath);		//报连接timeout的bug
		if (downJson.getString(ParameterConfig.HDFSTOLOCALRESULTKEY).equals(ParameterConfig.HDFSTOLOCALRESULTVALUE)) {
			// 此刻文件是从hdfs上下载到本地服务器上，下一步开始拼接文件在本地服务器上的url
			String localServerFileUrl = ParameterConfig.VIRTUALDIRECTORYURL + downFileName;
			// 将从hdfs上下载到本地服务器上的文件的url存到json里面
			downJson.put(ParameterConfig.LOCALSERVERFILEURL, localServerFileUrl);
		}
		return downJson;
	}

}
