package com.bahar.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bahar.common.dto.order.VehicleDto;
import com.bahar.common.dto.order.VehicleOrderDto;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.VehicleOrderPage;
import com.bahar.common.service.*;
import com.bahar.common.util.CommonUtil;
import com.bahar.framework.annoation.OperationServiceLog;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.mapper.MtVehicleOrderMapper;
import com.bahar.repository.model.MtStore;
import com.bahar.repository.model.MtUser;
import com.bahar.repository.model.MtUserCoupon;
import com.bahar.repository.model.MtVehicleOrder;
import com.bahar.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class VehicleOrderServiceImpl extends ServiceImpl<MtVehicleOrderMapper, MtVehicleOrder> implements VehicleOrderService {

    @Resource
    private MtVehicleOrderMapper mtVehicleOrderMapper;

    private UserCouponService userCouponService;

    private CouponService couponService;

    private MemberService memberService;

    private StoreService storeService;

    @Override
    @OperationServiceLog(description = "查询车辆服务单列表")
    public PaginationResponse<VehicleOrderDto> getVehicleOrderListByPagination(VehicleOrderPage vehicleOrderPage) {
        String userNo = vehicleOrderPage.getUserNo();
        String mobile = vehicleOrderPage.getMobile();
        Integer userId = vehicleOrderPage.getUserId();
        if (StringUtils.isNotEmpty(userNo)){
            MtUser userInfo = memberService.queryMemberByUserNo(0, userNo);
            if (userInfo != null) {
                userId = userInfo.getId();
            } else {
                userId = 0;
            }
        } else if (StringUtils.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(0, mobile);
            if (userInfo != null) {
                userId = userInfo.getId();
            } else {
                userId = 0;
            }
        }
        LambdaQueryWrapper<MtVehicleOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtVehicleOrder::getStatus, StatusEnum.DISABLE.getKey());
        String orderSn = vehicleOrderPage.getOrderSn();
        if (StringUtils.isNotEmpty(orderSn)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getOrderSn, orderSn);
        }
        String storeIds = vehicleOrderPage.getStoreIds();
        if (StringUtils.isNotEmpty(storeIds)) {
            List<String> idList = Arrays.asList(storeIds.split(","));
            lambdaQueryWrapper.in(MtVehicleOrder::getStoreId, idList);
        }
        String status = vehicleOrderPage.getStatus();
        if (StringUtils.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStatus, status);
        }
        if (userId != null) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getUserId, userId);
        }
        Integer merchantId = vehicleOrderPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getMerchantId, merchantId);
        }
        Integer storeId = vehicleOrderPage.getStoreId();
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStoreId, storeId);
        }
        String vehiclePlateNo = vehicleOrderPage.getVehiclePlateNo();
        if (StringUtils.isNotEmpty(vehiclePlateNo)){
            lambdaQueryWrapper.eq(MtVehicleOrder::getVehiclePlateNo, vehiclePlateNo);
        }
        String startTime = vehicleOrderPage.getStartTime();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtVehicleOrder::getCreateTime, startTime);
        }
        String endTime = vehicleOrderPage.getEndTime();
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtVehicleOrder::getCreateTime, endTime);
        }
        Page<MtVehicleOrder> pageHelper = PageHelper.startPage(vehicleOrderPage.getPage(), vehicleOrderPage.getPageSize());
        lambdaQueryWrapper.orderByDesc(MtVehicleOrder::getId);
        List<MtVehicleOrder> dataList = mtVehicleOrderMapper.selectList(lambdaQueryWrapper);
        List<VehicleOrderDto> vehicleOrderList = new ArrayList<>();
        if (dataList != null && dataList.size() > 0) {
            for (MtVehicleOrder mtVehicleOrder : dataList) {
                 VehicleOrderDto vehicleOrderDto = new VehicleOrderDto();
                 BeanUtils.copyProperties(mtVehicleOrder, vehicleOrderDto);
                 MtUser mtUser = memberService.queryMemberById(mtVehicleOrder.getUserId());
                 if (mtUser != null) {
                     vehicleOrderDto.setName(mtUser.getName());
                     vehicleOrderDto.setMobile(mtUser.getMobile());
                     vehicleOrderDto.setUserNo(mtUser.getUserNo());
                 }
                 MtStore mtStore = storeService.queryStoreById(mtVehicleOrder.getStoreId());
                 if (mtStore != null) {
                     vehicleOrderDto.setStoreInfo(mtStore);
                 }
                 vehicleOrderList.add(vehicleOrderDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(vehicleOrderPage.getPage(), vehicleOrderPage.getPageSize());
        PageImpl<VehicleDto> pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<VehicleOrderDto> paginationResponse = new PaginationResponse(pageImpl, VehicleOrderDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(vehicleOrderList);

        return paginationResponse;
    }

    @Override
    public MtVehicleOrder getVehicleOrderById(Integer id) {
        return mtVehicleOrderMapper.selectById(id);
    }

    @Override
    @OperationServiceLog(description = "更新车辆服务单")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicleOrder updateVehicleOrder(MtVehicleOrder mtVehicleOrder) {
        mtVehicleOrder.setUpdateTime(new Date());

        Boolean result = updateById(mtVehicleOrder);
        log.info("更新车辆服务单：{}", result);

        return mtVehicleOrder;
    }

    @Override
    @OperationServiceLog(description = "提交车辆服务单")
    @Transactional(rollbackFor = Exception.class)
    public MtVehicleOrder submitVehicleOrder(MtVehicleOrder mtVehicleOrder) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", mtVehicleOrder.getUserId());
        params.put("vehiclePlateNo", mtVehicleOrder.getVehiclePlateNo());

        MtUserCoupon mtUserCoupon = userCouponService.getUserCouponDetail(mtVehicleOrder.getCouponId());
        if (mtUserCoupon == null || !mtUserCoupon.getUserId().equals(mtVehicleOrder.getUserId())) {
            throw new BusinessCheckException("该次卡不存在");
        }

        params.put("couponId", mtVehicleOrder.getCouponId());
        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtVehicleOrder> dataList = queryVehicleOrderList(params);
        if (dataList != null && dataList.size() > 0) {
            throw new BusinessCheckException("服务单已存在");
        }

        couponService.useCoupon(mtVehicleOrder.getCouponId(), mtVehicleOrder.getUserId(), mtVehicleOrder.getStoreId(), 0, new BigDecimal("0"), "车辆服务单核销");

        mtVehicleOrder.setCreateTime(new Date());
        mtVehicleOrder.setUpdateTime(new Date());
        mtVehicleOrder.setOrderSn(CommonUtil.createOrderSN(mtVehicleOrder.getUserId().toString()));
        mtVehicleOrder.setStatus(StatusEnum.ENABLED.getKey());
        this.save(mtVehicleOrder);
        return mtVehicleOrder;
    }

    @Override
    public List<MtVehicleOrder> queryVehicleOrderList(Map<String, Object> paramMap) {
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String vehiclePlateNo = paramMap.get("vehiclePlateNo") == null ? "" : paramMap.get("vehiclePlateNo").toString();
        String status = paramMap.get("status") == null ? "" : paramMap.get("status").toString();
        LambdaQueryWrapper<MtVehicleOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtVehicleOrder::getStatus, StatusEnum.DISABLE.getKey());
        if (StringUtil.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getUserId, userId);
        }
        if (StringUtil.isNotEmpty(vehiclePlateNo)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getVehiclePlateNo, vehiclePlateNo);
        }
        if (StringUtil.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtVehicleOrder::getStatus, status);
        }
        return mtVehicleOrderMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void deleteVehicleOrder(Integer id, String operator) throws BusinessCheckException {
        MtVehicleOrder mtVehicleOrder = mtVehicleOrderMapper.selectById(id);
        if(mtVehicleOrder == null) {
            throw new BusinessCheckException("车辆服务单不存在");
        }
        mtVehicleOrder.setStatus(StatusEnum.DISABLE.getKey());
        mtVehicleOrder.setUpdateTime(new Date());
        updateById(mtVehicleOrder);
    }
}
