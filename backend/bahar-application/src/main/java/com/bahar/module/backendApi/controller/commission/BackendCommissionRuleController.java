package com.bahar.module.backendApi.controller.commission;

import com.bahar.common.dto.commission.CommissionRuleDto;
import com.bahar.common.dto.common.ParamDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.CommissionTypeEnum;
import com.bahar.common.param.CommissionRulePage;
import com.bahar.common.param.CommissionRuleParam;
import com.bahar.common.param.StatusParam;
import com.bahar.common.service.CommissionRuleService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtCommissionRule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销提成规则管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-分销提成规则相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionRule")
public class BackendCommissionRuleController extends BaseController {

    /**
     * 分销提成规则服务接口
     */
    private CommissionRuleService commissionRuleService;

    /**
     * 规则列表查询
     */
    @ApiOperation(value = "规则列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject list(@ModelAttribute CommissionRulePage commissionRulePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionRulePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionRulePage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<MtCommissionRule> paginationResponse = commissionRuleService.queryDataByPagination(commissionRulePage);

        // 分佣提成类型列表
        List<ParamDto> typeList = CommissionTypeEnum.getCommissionTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 更新分销提成规则状态
     */
    @ApiOperation(value = "更新分销提成规则状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        CommissionRuleDto commissionRuleDto = commissionRuleService.queryCommissionRuleById(params.getId());
        if (commissionRuleDto == null) {
            return getFailureResult(201);
        }

        CommissionRuleParam commissionRule = new CommissionRuleParam();
        commissionRule.setOperator(accountInfo.getAccountName());
        commissionRule.setId(params.getId());
        commissionRule.setStatus(params.getStatus());
        commissionRuleService.updateCommissionRule(commissionRule, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 保存分销提成规则
     */
    @ApiOperation(value = "保存分销提成规则")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject saveHandler(@RequestBody CommissionRuleParam commissionRule) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            commissionRule.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            commissionRule.setStoreId(accountInfo.getStoreId());
        }
        commissionRule.setOperator(accountInfo.getAccountName());
        if (commissionRule.getId() != null && commissionRule.getId() > 0) {
            commissionRuleService.updateCommissionRule(commissionRule, accountInfo);
        } else {
            commissionRuleService.addCommissionRule(commissionRule);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取分销提成规则详情
     */
    @ApiOperation(value = "获取分销提成规则详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        CommissionRuleDto commissionRule = commissionRuleService.queryCommissionRuleById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("commissionRule", commissionRule);
        return getSuccessResult(result);
    }
}
