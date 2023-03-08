package com.franc.app.mapper;

import com.franc.app.vo.MyMembershipAccumHisVO;
import com.franc.app.vo.MyMembershipVO;
import com.franc.app.vo.MyMspDetailInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MyMembershipMapper {
    Integer getBarcodeSeq() throws Exception;

    void save(MyMembershipVO vo) throws Exception;

    void rejoin(MyMembershipVO vo) throws Exception;

    void withdrawal(MyMembershipVO vo) throws Exception;

    MyMembershipVO findById(Map<String, Object> paramMap) throws Exception;

    MyMspDetailInfoVO findDetailByBarCdAndFranchiseeId(@Param("barCd") String barCd, @Param("franchiseeId") String franchiseeId) throws Exception;

    void saveAccumHis(MyMembershipAccumHisVO vo) throws Exception;

    MyMembershipAccumHisVO findAccumHisById(@Param("cancelBarCd") String cancelBarCd) throws Exception;

    Integer getMyMembershipTotalAccumPoint(Map<String, Object> map) throws Exception;

}
