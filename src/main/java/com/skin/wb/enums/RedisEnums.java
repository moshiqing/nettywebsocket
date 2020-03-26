package com.skin.wb.enums;

/**
 * @description:Redis配置枚举
 * @author: moshiqing
 * @time: 2020/3/11 16:33
 */

public enum RedisEnums {

    /**
     * netty地址合集
     */
    NETTYSERVER("netty:server:address"),

    /**
     * netty上存在的所有用户信息
     */
    NETTYUSERSERVER("netty:server:user:address"),

    /**
     * 用户server地址
     */
    USERSCHANNEL("netty:user:");

    private String value;

    public String getValue() {
        return value;
    }

    RedisEnums(String value){
        this.value=value;
    }
}
