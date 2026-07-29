package com.bahar.module.backendApi.controller.order;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.common.ParamDto;
import com.bahar.common.dto.order.RefundDto;
import com.bahar.common.dto.order.UserOrderDto;
import com.bahar.common.enums.RefundStatusEnum;
import com.bahar.common.enums.RefundTypeEnum;
import com.bahar.common.param.RefundInfoParam;
import com.bahar.common.param.RefundPage;
import com.bahar.common.service.OrderService;
import com.bahar.common.service.RefundService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后管理controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-售后订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/refund")
public class BackendRefundController extends BaseController {

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 售后列表查询
     */
    @ApiOperation(value = "售后列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject list(@ModelAttribute RefundPage refundPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            refundPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            refundPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<RefundDto> paginationResponse = refundService.getRefundListByPagination(refundPage);

        // 售后状态列表
        List<ParamDto> statusList = RefundStatusEnum.getRefundStatusList();

        // 售后类型列表
        List<ParamDto> refundTypeList = RefundTypeEnum.getRefundTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("statusList", statusList);
        result.put("refundTypeList", refundTypeList);

        return getSuccessResult(result);
    }

    /**
     * 查询退款详情
     * */
    @ApiOperation(value = "查询退款详情")
    @RequestMapping(value = "/info/{refundId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject info(@PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        UserOrderDto orderInfo = null;
        RefundDto refundInfo = refundService.getRefundById(refundId);
        if (refundInfo != null) {
            orderInfo = orderService.getOrderById(refundInfo.getOrderId());
        }

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(refundInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("refundInfo", refundInfo);
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存售后订单
     */
    @ApiOperation(value = "保存售后订单")
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject save(@RequestBody RefundInfoParam param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        RefundDto refundDto = refundService.getRefundById(param.getRefundId());
        if (!refundDto.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        if (param.getStatus().equals(RefundStatusEnum.REJECT.getKey())) {
            RefundDto dto = new RefundDto();
            dto.setId(param.getRefundId());
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(RefundStatusEnum.REJECT.getKey());
            dto.setRemark(param.getRemark());
            dto.setRejectReason(param.getRejectReason());
            refundService.updateRefund(dto, accountInfo);
        } else {
            RefundDto dto = new RefundDto();
            dto.setId(param.getRefundId());
            dto.setOperator(accountInfo.getAccountName());
            dto.setStatus(param.getStatus());
            dto.setRemark(param.getRemark());
            if (param.getStatus().equals(RefundStatusEnum.COMPLETE.getKey())) {
                refundService.agreeRefund(dto, accountInfo);
            } else {
                refundService.updateRefund(dto, accountInfo);
            }
        }
        return getSuccessResult(true);
    }

    /**
     * 发起退款
     */
    @ApiOperation(value = "发起退款")
    @RequestMapping(value = "doRefund", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject doRefund(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String refundAmount = param.get("refundAmount") == null ? "" : param.get("refundAmount").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (orderId <= 0 || StringUtil.isEmpty(refundAmount)) {
            return getFailureResult(201, "参数有误，发起退款失败");
        }
        Boolean result = refundService.doRefund(orderId, refundAmount, remark, accountInfo);
        if (result) {
            return getSuccessResult(true);
        } else {
            return getFailureResult(201, "退款失败");
        }
    }
}
