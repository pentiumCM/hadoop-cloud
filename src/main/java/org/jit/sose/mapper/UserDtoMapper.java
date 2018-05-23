package org.jit.sose.mapper;

import org.apache.ibatis.annotations.Param;
import org.jit.sose.dto.UserDto;

public interface UserDtoMapper {

    int insert(UserDto record);

    int insertSelective(UserDto record);

    UserDto  selectByUserName(String name);

    int updateByPrimaryKey(UserDto record);

    public boolean register(UserDto user);

    public int updateHeadImg(@Param("name")String username, @Param("imagepath") String head);

    int updatePwd(@Param("name") String name, @Param("pwd") String pwd);

}