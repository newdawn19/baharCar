package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationRequest;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtUserAction;

/**
 * 会员行为业务接口
 *
 * CopyRight https://www.bahar.cn
 */
public interface UserActionService extends IService<MtUserAction> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserAction> queryUserActionListByPagination(PaginationRequest paginationRequest);

    /**
     * 新增会员行为
     *
     * @param  mtUserAction
     * @throws BusinessCheckException
     */
    boolean addUserAction(MtUserAction mtUserAction) throws BusinessCheckException;

    /**
     * 根据ID获取会员行为详情
     *
     * @param  id ID
     * @return
     */
    MtUserAction getUserActionDetail(Integer id);

    /**
     * 根据ID删除会员行为
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteUserAction(Integer id, String operator) throws BusinessCheckException;
}
