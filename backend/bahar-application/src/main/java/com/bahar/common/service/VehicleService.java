package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.order.VehicleDto;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtVehicle;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface VehicleService extends IService<MtVehicle> {

    /**
     * 更新会员车辆
     *
     * @param mtVehicle 会员车辆
     * @param accountInfo 操作员
     * @return
     * */
    MtVehicle updateVehicle(MtVehicle mtVehicle, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 保存会员车辆
     *
     * @param mtVehicle 会员车辆
     * @throws BusinessCheckException
     * @return
     * */
    MtVehicle saveVehicle(MtVehicle mtVehicle) throws BusinessCheckException;

    /**
     * 分页查询会员车辆
     *
     * @param request 会员车辆
     * @throws BusinessCheckException
     * @return
     * */
    PaginationResponse<VehicleDto> getUserVehicleListByPagination(HttpServletRequest request);

    /**
     * 根据ID查询会员车辆
     *
     * @param id 会员车辆ID
     * @return
     * */
    VehicleDto getVehicleById(Integer id);

    /**
     * 根据ID查询会员车辆
     *
     * @param id 会员车辆ID
     * @return
     * */
    MtVehicle queryVehicleById(Integer id);

    /**
     * 删除会员车辆
     *
     * @param id 会员车辆ID
     * @param  accountInfo 操作员
     * @throws BusinessCheckException
     * @return
     * */
    void deleteVehicle(Integer id, AccountInfo  accountInfo) throws BusinessCheckException;

    /**
     * 根据用户ID查询会员车辆
     *
     * @param userId 会员ID
     * @param isDefault 是否默认车辆
     * @return
     * */
    List<VehicleDto> getVehicleByUserId(Integer userId, boolean isDefault);

    /**
     * 查询会员车辆
     *
     * @param paramMap 查询条件
     * @return
     * */
    List<MtVehicle> queryVehicleList(Map<String, Object> paramMap);

}
