package com.skin.wb.entity;

import lombok.Data;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/12 10:56
 */
@Data
public class DataContent {

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户信息
     */
    private String message;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 接受到的用户
     */
    private String toId;

}
