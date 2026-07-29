package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.order.VehicleOrderDto;
import com.bahar.common.param.VehicleOrderPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtVehicleOrder;

import java.util.List;
import java.util.Map;

public interface VehicleOrderService extends IService<MtVehicleOrder> {

    MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder);

    MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException;

    PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(VehicleOrderPage vehicleOrderPage);

    MtVehicleOrder getVehicleOrderById(Integer id);

    List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap);

    void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException;
}
