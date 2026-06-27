package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;

    private String author;
    private String category;
    private String publisher;
    private LocalDate publishDate;
    private Integer stock;
    private Integer status;
    private String description;
    private String isbn;
    private String coverUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}