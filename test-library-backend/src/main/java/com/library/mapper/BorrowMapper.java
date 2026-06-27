package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BorrowMapper extends BaseMapper<Borrow> {

    IPage<Borrow> selectBorrowPage(Page<Borrow> page, @Param("bookName") String bookName, @Param("userName") String userName, @Param("status") Integer status);

    Long selectBorrowPageCount(@Param("bookName") String bookName, @Param("userName") String userName, @Param("status") Integer status);

    IPage<Borrow> selectBorrowPageByUserId(Page<Borrow> page, @Param("userId") Long userId, @Param("status") Integer status);

    Borrow selectBorrowById(@Param("id") Long id);

    List<Map<String, Object>> selectMonthlyTrend(@Param("months") int months);

    List<Map<String, Object>> selectTopBooks(@Param("limit") int limit);
}
