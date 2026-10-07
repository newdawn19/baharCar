package com.bahar.module.clientApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.order.VehicleOrderDto;
import com.bahar.common.param.VehicleOrderPage;
import com.bahar.common.service.VehicleOrderService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtVehicleOrder;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务单类controller
 *
 * CopyRight https://www.bahar.cn
 */
@Api(tags="会员端-服务单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/vehicleOrder")
public class ClientVehicleOrderController extends BaseController {

    /**
     * 服务单服务接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 获取我的订单列表
     */
    @ApiOperation(value = "获取我的服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody VehicleOrderPage param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        param.setUserId(userInfo.getId());
        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(param);
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取服务单详情
     */
    @ApiOperation(value = "获取服务单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "服务单ID不能为空");
        }
        MtVehicleOrder orderInfo = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        return getSuccessResult(orderInfo);
    }
}
