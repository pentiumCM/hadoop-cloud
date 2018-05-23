package org.jit.sose.util;

import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.fs.*;  
import org.apache.hadoop.fs.FileSystem;  
import org.apache.hadoop.io.IOUtils;  
  
import java.io.*;  
import java.net.URI;  
import java.net.URISyntaxException;  
  
public class HdfsOperate {  
 
  
    /** 
     * 给定文件名和文件内容，创建hdfs文件 
     * 
     * @param dst 
     * @param contents 
     * @throws IOException 
     */  
    public void createFile(String dst, byte[] contents)  
            throws IOException {  
        Configuration conf = new Configuration();  
        Path dstPath = new Path(dst);  
        FileSystem fs = dstPath.getFileSystem(conf);  
  
        FSDataOutputStream outputStream = fs.create(dstPath);  
        outputStream.write(contents);  
        outputStream.close();  
        System.out.println("create file " + dst + " success!");  
        //fs.close();  
    }  
  
    /** 
     * 删除hdfs文件 
     * 
     * @param filePath 
     * @throws IOException 
     */  
    public void delete(String filePath) throws IOException {  
        Configuration conf = new Configuration();  
        Path path = new Path(filePath);  
        FileSystem fs = path.getFileSystem(conf);  
  
        boolean isok = fs.deleteOnExit(path);  
        if (isok) {  
            System.out.println("delete file " + filePath + " success!");  
        } else {  
            System.out.println("delete file " + filePath + " failure");  
        }  
        //fs.close();  
    }  
  
    /** 
     * 创建hdfs目录 
     * 
     * @param path 
     * @throws IOException 
     */  
    public void mkdir(String path) throws IOException {  
        Configuration conf = new Configuration();  
        Path srcPath = new Path(path);  
        FileSystem fs = srcPath.getFileSystem(conf);  
  
        boolean isok = fs.mkdirs(srcPath);  
        if (isok) {  
            System.out.println("create dir ok!");  
        } else {  
            System.out.println("create dir failure");  
        }  
        //fs.close();  
    }  
  
    /** 
     * 读取hdfs文件内容，并在控制台打印出来 
     * 
     * @param filePath 
     * @throws IOException 
     */  
    public void readFile(String filePath) throws IOException {  
        Configuration conf = new Configuration();  
        Path srcPath = new Path(filePath);  
        FileSystem fs = null;  
        URI uri;  
        try {  
            uri = new URI(filePath);  
            fs = FileSystem.get(uri, conf);  
        } catch (URISyntaxException e) {  
            e.printStackTrace();  
        }  
        InputStream in = null;  
        try {  
            in = fs.open(srcPath);  
            IOUtils.copyBytes(in, System.out, 4096, false);  
        } finally {  
            IOUtils.closeStream(in);  
        }  
    }  
  
    /** 
     * 下载hdfs文件到本地目录 
     * 
     * @param dstPath 
     * @param srcPath 
     * @throws Exception 
     */  
    public void downloadFile(String dstPath, String srcPath) throws Exception {  
        Path path = new Path(srcPath);  
        Configuration conf = new Configuration();  
        FileSystem hdfs = path.getFileSystem(conf);  
  
        File rootfile = new File(dstPath);  
        if (!rootfile.exists()) {  
            rootfile.mkdirs();  
        }  
  
        if (hdfs.isFile(path)) {  
            //只下载非txt文件  
            String fileName = path.getName();  
            if (!fileName.toLowerCase().endsWith("txt")) {  
                FSDataInputStream in = null;  
                FileOutputStream out = null;  
                try {  
                    in = hdfs.open(path);  
                    File srcfile = new File(rootfile, path.getName());  
                    if (!srcfile.exists()) srcfile.createNewFile();  
                    out = new FileOutputStream(srcfile);  
                    IOUtils.copyBytes(in, out, 4096, false);  
                } finally {  
                    IOUtils.closeStream(in);  
                    IOUtils.closeStream(out);  
                }  
                //下载完后，在hdfs上将原文件删除  
                this.delete(path.toString());  
            }  
        } else if (hdfs.isDirectory(path)) {  
            File dstDir = new File(dstPath);  
            if (!dstDir.exists()) {  
                dstDir.mkdirs();  
            }  
            //在本地目录上加一层子目录  
            String filePath = path.toString();//目录  
            String subPath[] = filePath.split("/");  
            String newdstPath = dstPath + subPath[subPath.length - 1] + "/";  
            System.out.println("newdstPath=======" + newdstPath);  
            if (hdfs.exists(path) && hdfs.isDirectory(path)) {  
                FileStatus[] srcFileStatus = hdfs.listStatus(path);  
                if (srcFileStatus != null) {  
                    for (FileStatus status : hdfs.listStatus(path)) {  
                        //下载子目录下文件  
                        downloadFile(newdstPath, status.getPath().toString());  
                    }  
                }  
            }  
        }  
    }  
  
    /** 
     * 下载hdfs文件内容，保存到内存对象中 
     * 
     * @param srcPath 
     * @throws Exception 
     */  
    public void downloadFileByte(String srcPath) throws Exception {  
        Path path = new Path(srcPath);  
        FileSystem hdfs = null;  
        Configuration conf = new Configuration();  
        hdfs = FileSystem.get(URI.create(srcPath), conf);  
        if (hdfs.exists(path)) {  
            if (hdfs.isFile(path)) {  
                //如果是文件  
                FSDataInputStream in = null;  
                FileOutputStream out = null;  
                try {  
                    in = hdfs.open(new Path(srcPath));  
                    byte[] t = new byte[in.available()];  
                    in.read(t);  
                    //hdfsfiles.add(new HdfsFile(path.getName(), srcPath, t));  
                } finally {  
                    IOUtils.closeStream(in);  
                    IOUtils.closeStream(out);  
                }  
            } else {  
                //如果是目录  
                FileStatus[] srcFileStatus = hdfs.listStatus(new Path(srcPath));  
                for (int i = 0; i < srcFileStatus.length; i++) {  
                    String srcFile = srcFileStatus[i].getPath().toString();  
                    downloadFileByte(srcFile);  
                }  
            }  
        }  
    }  
  
  
  
    /** 
     * 将本地目录或文件上传的hdfs 
     * 
     * @param localSrc 
     * @param dst 
     * @throws Exception 
     */  
    public void uploadFile(String localSrc, String dst) throws Exception {  
  
        Configuration conf = new Configuration();  
        File srcFile = new File(localSrc);  
        if (srcFile.isDirectory()) {  
            copyDirectory(localSrc, dst, conf);  
        } else {  
            copyFile(localSrc, dst, conf);  
        }  
    }  
  
    /** 
     * 拷贝本地文件hdfs目录下 
     * 
     * @param localSrc 
     * @param dst 
     * @param conf 
     * @return 
     * @throws Exception 
     */  
    private boolean copyFile(String localSrc, String dst, Configuration conf) throws Exception {  
        File file = new File(localSrc);  
        dst = dst + file.getName();  
        Path path = new Path(dst);  
        FileSystem fs = path.getFileSystem(conf);//FileSystem.get(conf);  
        fs.exists(path);  
        InputStream in = new BufferedInputStream(new FileInputStream(file));  
        OutputStream out = fs.create(new Path(dst));  
        IOUtils.copyBytes(in, out, 4096, true);  
        in.close();  
        return true;  
    }  
  
    /** 
     * 拷贝本地目录到hdfs 
     * @param src 
     * @param dst 
     * @param conf 
     * @return 
     * @throws Exception 
     */  
    private boolean copyDirectory(String src, String dst, Configuration conf) throws Exception {  
        Path path = new Path(dst);  
        FileSystem fs = path.getFileSystem(conf);  
        if (!fs.exists(path)) {  
            fs.mkdirs(path);  
        }  
        File file = new File(src);  
  
        File[] files = file.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            File f = files[i];  
            if (f.isDirectory()) {  
                String fname = f.getName();  
                if (dst.endsWith("/")) {  
                    copyDirectory(f.getPath(), dst + fname + "/", conf);  
                } else {  
                    copyDirectory(f.getPath(), dst + "/" + fname + "/", conf);  
                }  
            } else {  
                copyFile(f.getPath(), dst, conf);  
            }  
        }  
        return true;  
    }  
}  