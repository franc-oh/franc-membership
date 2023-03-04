package com.franc.app.mapper;

import com.franc.app.vo.MyMembershipVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MyMembershipMapper {
    Integer getBarcodeSeq() throws Exception;

    void save(MyMembershipVO vo) throws Exception;

    void rejoin(MyMembershipVO vo) throws Exception;

    MyMembershipVO findById(Map<String, Object> paramMap) throws Exception;
}
