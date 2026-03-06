package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    @Select("<script>" +
            "SELECT * FROM user " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<User> searchUsers(@Param("name") String name, @Param("username") String username);
}