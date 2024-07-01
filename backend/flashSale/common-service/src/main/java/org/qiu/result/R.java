package org.qiu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/19 15:56
 * @Version 1.0
 * @Since 1.0
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class R {
    private int code;
    private String msg;
    private Object data;

    public static R OK(){
        return R.builder()
                .code(CodeEnum.OK.getCode())
                .msg(CodeEnum.OK.getMsg())
                .build();
    }

    public static R OK(String msg, Object data){
        return R.builder()
                .code(CodeEnum.OK.getCode())
                .msg(msg)
                .data(data)
                .build();
    }

    public static R OK(Object data){
        return R.builder()
                .code(CodeEnum.OK.getCode())
                .msg(CodeEnum.OK.getMsg())
                .data(data)
                .build();
    }

    public static R FAIL(){
        return R.builder()
                .code(CodeEnum.FALL.getCode())
                .msg(CodeEnum.FALL.getMsg())
                .build();
    }

    public static R FAIL(String msg){
        return R.builder()
                .code(CodeEnum.FALL.getCode())
                .msg(msg)
                .build();
    }

    public static R FAIL(CodeEnum codeEnum){
        return R.builder()
                .code(codeEnum.getCode())
                .msg(codeEnum.getMsg())
                .build();
    }

    public static R FAIL(Object data){
        return R.builder()
                .code(CodeEnum.OK.getCode())
                .msg(CodeEnum.OK.getMsg())
                .data(data)
                .build();
    }
}
