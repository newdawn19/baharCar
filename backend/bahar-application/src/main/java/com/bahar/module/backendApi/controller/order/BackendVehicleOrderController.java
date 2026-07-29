package com.bahar.module.backendApi.controller.order;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.common.ParamDto;
import com.bahar.common.dto.order.VehicleOrderDto;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.enums.VehicleOrderStatusEnum;
import com.bahar.common.param.VehicleOrderPage;
import com.bahar.common.service.StoreService;
import com.bahar.common.service.VehicleOrderService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtStore;
import com.bahar.repository.model.MtVehicleOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags="后台管理-车辆服务单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/vehicleOrder")
public class BackendVehicleOrderController extends BaseController {

    /**
     * 服务订单接口
     * */
    private VehicleOrderService vehicleOrderService;

    /**
     * 店铺接口
     * */
    private StoreService storeService;

    @ApiOperation(value="车辆服务单列表", notes="查询车辆服务单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(@ModelAttribute VehicleOrderPage vehicleOrderPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            vehicleOrderPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            vehicleOrderPage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<VehicleOrderDto> paginationResponse = vehicleOrderService.getVehicleOrderListByPagination(vehicleOrderPage);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        // 服务单状态列表
        List<ParamDto> statusList = VehicleOrderStatusEnum.getVehicleOrderStatusList();

        Map<String, Object> result = new HashMap<>();
        result.put("storeList", storeList);
        result.put("statusList", statusList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    @ApiOperation(value="查询车辆服务单", notes="查询车辆服务单")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(id);
        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        return getSuccessResult(mtVehicleOrder);
    }

    @ApiOperation(value="更新服务单信息", notes="更新车辆服务单信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(@RequestBody Map<String, String> param) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String orderId = param.get("orderId");
        String remark = param.get("remark");
        String status  = param.get("status");

        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(Integer.parseInt(orderId));
        if (mtVehicleOrder == null) {
            return getFailureResult(201, "服务单不存在");
        }

        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        mtVehicleOrder.setRemark(remark);
        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }
    @ApiOperation(value = "更新车辆服务单状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody Map<String, String> param) throws BusinessCheckException {
        Integer vehicleId = param.get("vehicleId") == null ? 0 : Integer.parseInt(param.get("vehicleId"));
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(vehicleId);
        if (mtVehicleOrder == null) {
            return getFailureResult(201, "服务单不存在");
        }

        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "不同商户，没有操作权限");
        }
        mtVehicleOrder.setOperator(accountInfo.getAccountName());
        mtVehicleOrder.setStatus(status);
        vehicleOrderService.updateVehicleOrder(mtVehicleOrder);

        return getSuccessResult(true);
    }

    @ApiOperation(value = "删除车辆服务单", notes = "删除车辆服务单")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtVehicleOrder mtVehicleOrder = vehicleOrderService.getVehicleOrderById(id);
        if (!accountInfo.getMerchantId().equals(mtVehicleOrder.getMerchantId())) {
            return getFailureResult(201, "不同商户，没有操作权限");
        }
        vehicleOrderService.deleteVehicleOrder(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
