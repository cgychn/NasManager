package com.nas.server.mapper;

import com.nas.server.entity.db.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into user(id, username, password, name, sex) values(NULL, #{username}, #{password}, #{name}, #{sex})")
    void saveUser(User user);

    @Select("select * from user where username = #{username}")
    User findUserByUserName(String username);

    @Select("select * from user where id = #{id}")
    User findUserById(int id);

    @Select("select count(1) from user where username = #{username} and password = #{passwordMD5}")
    int verifyUser(@Param("username") String username, @Param("passwordMD5") String passwordMD5);
}
