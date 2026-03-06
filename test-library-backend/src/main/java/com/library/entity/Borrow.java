package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("borrow")
public class Borrow {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;
    private Long userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联查询字段
    private String bookName;
    private String userName;
}