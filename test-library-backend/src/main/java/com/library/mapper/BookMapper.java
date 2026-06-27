package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface BookMapper extends BaseMapper<Book> {

    @Select("<script>" +
            "SELECT * FROM book " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='author != null and author != \"\"'> AND author LIKE CONCAT('%', #{author}, '%')</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Book> searchBooks(@Param("name") String name, @Param("author") String author, @Param("category") String category);

    @Select("SELECT COALESCE(category, '未分类') as name, COUNT(*) as value FROM book GROUP BY category ORDER BY value DESC")
    List<Map<String, Object>> selectCategoryDistribution();
}