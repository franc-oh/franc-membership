package com.franc.app.mapper;

import com.franc.app.vo.AccountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {

    List<AccountVO> findAll() throws Exception;

    AccountVO findById(@Param("accountId") Long accountId) throws Exception;

}
