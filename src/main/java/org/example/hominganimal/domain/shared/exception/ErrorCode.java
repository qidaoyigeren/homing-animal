package org.example.hominganimal.domain.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 通用
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),
    // 设备相关 1xxx
    DEVICE_NOT_FOUND(1001, "设备不存在"),
    DEVICE_ALREADY_BOUND(1002, "设备已绑定"),
    DEVICE_OFFLINE(1003, "设备不在线"),
    DEVICE_BINDLIMIT(1004, "设备绑定数量已达上限"),

    // 事件相关 2xxx
    EVENT_NOT_FOUND(2001, "事件不存在"),
    EVENT_ALREADY_HANDLED(2002, "事件已处理"),

    // 萤石相关 3xxx
    EZVIZ_API_ERROR(3001, "萤石API调用失败"),
    EZVIZ_TOKEN_ERROR(3002, "萤石Token获取失败"),
    EZVIZ_CAPTURE_ERROR(3003, "设备抓拍失败"),
    //认证相关 4xxx
    USER_ALREADY_EXIST(4001,"用户已存在"),
    USER_NOT_FOUND(4002,"用户不存在" ),
    USER_PASSWORD_ERROR(4003,"密码错误" );
    private final int code;
    private final String message;
}
