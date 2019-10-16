package com.eem.demo.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller返回对象
 * @author Administrator
 */
public class ReturnObj {
    /**
     * 状态码
     * 100为成功
     * 200为失败
     */
    private int code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 用户返回给前端的信息
     */
    private Map<String, Object> extend = new HashMap<>();


    public static ReturnObj success(){
        ReturnObj result = new ReturnObj();
        result.setCode(100);
        result.setMsg("处理成功！");
        return result;
    }

    public static ReturnObj fail(){
        ReturnObj result = new ReturnObj();
        result.setCode(200);
        result.setMsg("处理失败！");
        return result;
    }

    /**
     * 往对象添加信息的方法
     * @param key
     * @param value
     * @return
     */
    public ReturnObj add(String key,Object value){
        this.getExtend().put(key, value);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }
}
