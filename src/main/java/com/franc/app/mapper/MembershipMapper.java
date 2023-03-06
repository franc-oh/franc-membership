package com.franc.app.mapper;

import com.franc.app.vo.MembershipVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MembershipMapper {

    List<MembershipVO> findAllOrMyMspList(Map<String, Object> paramMap) throws Exception;

}
