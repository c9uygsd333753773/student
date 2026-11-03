package com.sms.util;

/**
 * 统一 JSON 响应结构：code(0 成功 / 1 失败)、msg、data。
 */
public class Result {

    private int code;
    private String msg;
    private Object data;

    public Result() {}

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result ok() { return new Result(0, "操作成功", null); }
    public static Result ok(Object data) { return new Result(0, "操作成功", data); }
    public static Result ok(String msg, Object data) { return new Result(0, msg, data); }
    public static Result fail(String msg) { return new Result(1, msg, null); }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
