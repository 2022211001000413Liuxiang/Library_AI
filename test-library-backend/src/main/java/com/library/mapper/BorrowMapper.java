package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import org.apache.ibatis.annotations.Param;

public interface BorrowMapper extends BaseMapper<Borrow> {

    IPage<Borrow> selectBorrowPage(Page<Borrow> page, @Param("bookName") String bookName, @Param("userName") String userName, @Param("status") Integer status);

    IPage<Borrow> selectBorrowPageByUserId(Page<Borrow> page, @Param("userId") Long userId, @Param("status") Integer status);
}
