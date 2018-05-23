package org.jit.sose.service;


import org.jit.sose.dto.UserDto;

public interface UserService {

    public UserDto selectUserInfo(String name);

    public boolean searchrep(String name);

    public boolean login(String name,String pwd);

    public boolean register(UserDto user);

    public boolean updateHead(String username,String headImg);

    public boolean updatePwd(String username,String pwd);
}
