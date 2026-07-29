package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.member.BalanceDto;
import com.bahar.common.param.BalancePage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtBalance;
import java.util.List;

/**
 * 余额业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface BalanceService extends IService<MtBalance> {

    /**
     * 分页查询余额列表
     *
     * @param balancePage
     * @return
     */
    PaginationResponse<BalanceDto> queryBalanceListByPagination(BalancePage balancePage);

    /**
     * 添加余额记录
     *
     * @param mtBalance
     * @param updateBalance
     * @throws BusinessCheckException
     * @return
     */
    Boolean addBalance(MtBalance mtBalance, Boolean updateBalance) throws BusinessCheckException;

    /**
     * 发放余额
     *
     * @param accountInfo
     * @param object
     * @param userIds
     * @param amount
     * @param remark
     * @throws BusinessCheckException
     * @return
     */
    void distribute(AccountInfo accountInfo, String object, String userIds, String amount, String remark) throws BusinessCheckException;

    /**
     * 获取订单余额记录
     *
     * @param orderSn
     * @return
     * */
    List<MtBalance> getBalanceListByOrderSn(String orderSn);
}
