package org.jit.sose.mapper;

import org.apache.ibatis.annotations.Param;
import org.jit.sose.dto.FileDto;

import java.util.List;

public interface FileDtoMapper {

    //查询某一用户的所有状态的文件
    List<FileDto> queryPersonAll(@Param("fileNewName") String fileNewName,@Param("fileOwner") String fileOwner);

    //查询某一用户的某一种状态的文件
    List<FileDto> selectByfilestatus(@Param("username") String username, @Param("filestatus") String filestatus);

    //按文件名(原名)查找文件
    List<FileDto> selectByFileOriginalName(@Param("fileOriginalName") String fileOriginalName,@Param("fileOwner") String fileOwner,@Param("filestatus") String filestatus);

    //按文件名(新名称)查找文件
    FileDto selectByFileNewName(@Param("fileNewName") String fileNewName,@Param("fileOwner") String fileOwner,@Param("filestatus") String filestatus);

    // 按用户名查找所有的文件
    List<FileDto> selectByPerson(@Param("fileOwner") String fileOwner, @Param("filestatus") String filestatus);

    // 按文件名(新的名称)删除文件
    int deleteByFileName(@Param("fileNewName") String fileNewName,@Param("fileOwner") String fileOwner,@Param("filestatus") String filestatus);

    // 插入文件信息
    int insert(FileDto fileDto);



}