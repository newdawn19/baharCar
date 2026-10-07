package com.bahar.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单列表请求参数
 *
 * CopyRight https://www.bahar.cn
 */
@Data
public class VehicleOrderPage extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private String id;

    @ApiModelProperty(value="车牌号", name="vehiclePlateNo")
    private String vehiclePlateNo;

    @ApiModelProperty(value="订单号", name="orderSn")
    private String orderSn;

    @ApiModelProperty(value="会员ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="会员号", name="userNo")
    private String userNo;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="店铺ID，逗号分隔", name="storeIds")
    private String storeIds;

    @ApiModelProperty(value="开始时间", name="startTime")
    private String startTime;

    @ApiModelProperty(value="结束时间", name="endTime")
    private String endTime;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
