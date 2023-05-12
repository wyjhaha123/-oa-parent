package com.wyj.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; //状态码
    private String message; //返回信息;
    private T data; //返回对象

    //私有化构造函数 不能被外部实例
    private Result(){}

    //封装返回数据
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<T>();
        if (body != null){
            result.setData(body);

        }
        //状态码
        result.setCode(resultCodeEnum.getCode());
        //信息
        result.setMessage(resultCodeEnum.getMessage());

        return result;
    }

    //成功
    public static Result ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }
    public static<T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }
    //失败
    public static Result fail(){
        return build(null,ResultCodeEnum.FAIL);
    }
    public static<T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
    public Result<T> message(String message){
        this.setMessage(message);
        return this;
    }
}
