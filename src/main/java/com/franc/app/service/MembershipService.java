package com.franc.app.service;

import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.MembershipMapper;
import com.franc.app.vo.MembershipVO;
import com.franc.app.vo.MspAndMyMspInfoVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private static final Logger logger = LoggerFactory.getLogger(MembershipService.class);

    private final MembershipMapper membershipMapper;

    /**
     * 멤버십 전체조회 (+가입여부)
     * @param vo
     * @return
     * @throws Exception
     */
    public List<MembershipVO> findAllOrMyMspList(MembershipVO vo) throws Exception {
        // # 1. 페이징 셋팅
        vo.setPaging();

        // # 2. 조회
        return membershipMapper.findAllOrMyMspList(vo);
    }

    /**
     * 멤버십 상세조회 (+나의 멤버십정보)
     * @param vo
     * @return
     * @throws Exception
     */
    public MspAndMyMspInfoVO findByIdAndMyMspInfo(MspAndMyMspInfoVO vo) throws Exception {
        return membershipMapper.findByIdAndMyMspInfo(vo);
    }
}
