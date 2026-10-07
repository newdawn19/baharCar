package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.coupon.ReqSendLogDto;
import com.bahar.common.param.SendLogPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtSendLog;

/**
 * 发券记录业务接口
 *
 * CopyRight https://www.bahar.cn
 */
public interface SendLogService extends IService<MtSendLog> {

    /**
     * 分页查询列表
     *
     * @param sendLogPage
     * @return
     */
    PaginationResponse<MtSendLog> querySendLogListByPagination(SendLogPage sendLogPage) throws BusinessCheckException;

    /**
     * 添加记录
     *
     * @param  reqSendLogDto
     * @throws BusinessCheckException
     */
    MtSendLog addSendLog(ReqSendLogDto reqSendLogDto) throws BusinessCheckException;

    /**
     * 根据组ID获取发券记录
     *
     * @param  id ID
     * @throws BusinessCheckException
     */
    MtSendLog querySendLogById(Long id) throws BusinessCheckException;

    /**
     * 删除发券记录
     *
     * @param  id       ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteSendLog(Long id, String operator) throws BusinessCheckException;
}
