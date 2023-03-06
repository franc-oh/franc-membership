package com.franc.app.service;

import com.franc.app.mapper.MembershipMapper;
import com.franc.app.util.PageUtil;
import com.franc.app.vo.MembershipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipMapper membershipMapper;

    /**
     * 멤버십 전체조회 (나의 멤버십 전체조회)
     * @param paramMap {accountId, joinYn, pageNo, pageLimit}
     * @return
     * @throws Exception
     */
    public List<MembershipVO> findAllOrMyMspList(Map<String, Object> paramMap) throws Exception {
        // # 1. 페이징 셋팅
        Integer pageNo = (String) paramMap.get("pageNo") == null ? null : Integer.parseInt((String) paramMap.get("pageNo"));
        Integer pageLimit = (String) paramMap.get("pageLimit") == null ? null : Integer.parseInt((String) paramMap.get("pageLimit"));
        Map<String, Integer> pageMap = PageUtil.getPageMap(pageNo, pageLimit);

        // # 2. 조회
        paramMap.put("offset", pageMap.get("offset"));
        paramMap.put("limit", pageMap.get("limit"));

        return membershipMapper.findAllOrMyMspList(paramMap);
    }
}
