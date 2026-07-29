package com.bahar.common.enums;

import com.bahar.common.dto.common.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车辆类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public enum VehicleTypeEnum {
    SEDAN("C", "轿车"),
    SUV("S", "SUV"),
    MPV("M", "MPV"),
    SPORTS("P", "跑车"),
    PICKUP("T", "皮卡"),
    RV("R", "房车"),
    TRUCK("H", "货车"),
    BUS("B", "客车"),
    OTHER("O", "其他");

    private String key;

    private String value;

    VehicleTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 通过key获取value
     */
    public static String getValue(String k) {
        for (VehicleTypeEnum c : VehicleTypeEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * 通过value获取key
     */
    public static String getKey(String v) {
        for (VehicleTypeEnum c : VehicleTypeEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }

    /**
     * 获取车辆类型列表（用于下拉选择）
     */
    public static List<ParamDto> getVehicleTypeList() {
        return Arrays.stream(VehicleTypeEnum.values())
                .map(type -> new ParamDto(type.getKey(), type.getValue(), type.getValue()))
                .collect(Collectors.toList());
    }
}
