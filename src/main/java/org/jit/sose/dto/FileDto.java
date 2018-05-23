package org.jit.sose.dto;

public class FileDto {

    private String filenewname;         //文件上传到服务器后的新名字

    private String fileowner;           //文件的归属人

    private String filelocalurl;        //文件在本地服务器上的路径
    
    private String filehdfsurl;			//文件在hdfs集群上面的URL

    private String filetime;            //文件上传的时间

    private String fileoriginalname;    //文件原来的名字

    private String filestatus;          //文件的状态
    
    public String getFilenewname() {
        return filenewname;
    }

    public void setFilenewname(String filenewname) {
        this.filenewname = filenewname == null ? null : filenewname.trim();
    }

    public String getFileowner() {
        return fileowner;
    }

    public void setFileowner(String fileowner) {
        this.fileowner = fileowner == null ? null : fileowner.trim();
    }

    public String getFilelocalurl() {
        return filelocalurl;
    }

    public void setFilelocalurl(String filelocalurl) {
        this.filelocalurl = filelocalurl == null ? null : filelocalurl.trim();
    }

    public String getFiletime() {
        return filetime;
    }

    public void setFiletime(String filetime) {
        this.filetime = filetime == null ? null : filetime.trim();
    }

    public String getFileoriginalname() {
        return fileoriginalname;
    }

    public void setFileoriginalname(String fileoriginalname) {
        this.fileoriginalname = fileoriginalname == null ? null : fileoriginalname.trim();
    }

    public String getFilestatus() {
        return filestatus;
    }

    public void setFilestatus(String filestatus) {
        this.filestatus = filestatus == null ? null : filestatus.trim();
    }

    public String getFilehdfsurl() {
        return filehdfsurl;
    }

    public void setFilehdfsurl(String filehdfsurl) {
        this.filehdfsurl = filehdfsurl == null ? null : filehdfsurl.trim();
    }
}