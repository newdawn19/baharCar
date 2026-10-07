package com.bahar.module.backendApi.controller.commission;

import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.commission.CommissionRelationDto;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.CommissionRelationPage;
import com.bahar.common.service.CommissionRelationService;
import com.bahar.common.service.StoreService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtCommissionRelation;
import com.bahar.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销提成邀请记录管理类controller
 *
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-分销提成邀请记录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionRelation")
public class BackendCommissionRelationController extends BaseController {

    /**
     * 分销提成邀请记录业务接口
     */
    private CommissionRelationService commissionRelationService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 分销提成邀请记录列表查询
     */
    @ApiOperation(value = "分销提成邀请记录查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject list(@ModelAttribute CommissionRelationPage commissionRelationPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionRelationPage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionRelationPage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<CommissionRelationDto> paginationResponse = commissionRelationService.queryRelationByPagination(commissionRelationPage);

        Map<String, Object> param = new HashMap<>();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            param.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新邀请记录状态
     */
    @ApiOperation(value = "更新邀请记录状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer id = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();

        MtCommissionRelation mtCommissionRelation = commissionRelationService.getById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(mtCommissionRelation.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        mtCommissionRelation.setStatus(status);
        commissionRelationService.updateById(mtCommissionRelation);

        return getSuccessResult(true);
    }

    /**
     * 删除分销提成邀请记录
     */
    @ApiOperation(value = "删除分销提成邀请记录")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:relation:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtCommissionRelation mtCommissionRelation = commissionRelationService.getById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(mtCommissionRelation.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        mtCommissionRelation.setStatus(StatusEnum.DISABLE.getKey());
        commissionRelationService.updateById(mtCommissionRelation);

        return getSuccessResult(true);
    }
}
