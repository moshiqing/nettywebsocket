package com.skin.wb.enums;

/**
 * 聊天消息枚举处理
 */
public enum ChatTypes {

    /**
     * 注册
     */
    register(1),
    /**
     * 单聊
     */
    Single(2),
    /**
     * 群聊
     */
    gourp(3);

    int value;

    public int getValue() {
        return value;
    }

    ChatTypes(int value){
        this.value=value;
    }

}
