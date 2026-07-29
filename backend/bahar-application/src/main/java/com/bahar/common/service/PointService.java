package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.member.PointDto;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationRequest;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtPoint;

/**
 * 积分业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface PointService extends IService<MtPoint> {

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<PointDto> queryPointListByPagination(PaginationRequest paginationRequest);

    /**
     * 添加积分
     *
     * @param  mtPoint
     * @return
     */
    void addPoint(MtPoint mtPoint);

    /**
     * 转赠积分
     *
     * @param userId
     * @param mobile
     * @param amount
     * @param remark
     * @throws BusinessCheckException
     * @return
     */
    boolean doGift(Integer userId, String mobile, Integer amount, String remark) throws BusinessCheckException;
}
