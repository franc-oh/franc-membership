package com.franc.app.service;

import com.franc.app.code.Code;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.AccountMapper;
import com.franc.app.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

    /**
     * 사용자 정보 가져오기 (상태 체크)
     * @param accountId
     * @return
     * @throws Exception
     */
    public AccountVO getInfoAndCheckStatus(Long accountId) throws Exception {
        // #1. 사용자 정보 가져오기
        AccountVO accountVo = accountMapper.findById(accountId);
        if(accountVo == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_ACCOUNT);
        }

        // #2. 사용자 상태 체크 => 정상 상태만 허용
        if(accountVo.getStatus() != Code.STATUS_USE) {
            throw new BizException(ExceptionResult.NOT_ACTIVE_ACCOUNT);
        }

        return accountVo;
    }
}
