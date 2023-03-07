package com.franc.app.mapper;

import com.franc.app.vo.MembershipVO;
import com.franc.app.vo.MspAndMyMspInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MembershipMapper {

    List<MembershipVO> findAllOrMyMspList(MembershipVO vo) throws Exception;

    MspAndMyMspInfoVO findByIdAndMyMspInfo(MspAndMyMspInfoVO vo) throws Exception;

}
