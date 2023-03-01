package com.franc.app.membership.mapper;

import com.franc.app.membership.vo.AccountVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {

    List<AccountVo> findAll() throws Exception;

}
