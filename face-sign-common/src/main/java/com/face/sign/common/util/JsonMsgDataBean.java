package com.face.sign.common.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * @author Ocean
 * @version 1.0.0
 * @title: 数据Bean
 * @description: 返回统一的json数据格式
 * @date 2025/01/01 00:00
 */
@Getter
@Setter
@Component
public class JsonMsgDataBean {

    //表示请求成功或者失败的响应码
    private Integer code;

    //请求成功或者失败的信息描述
    private String msg;

    //响应的具体数据
    private Object data;

    /**
     * 构造函数
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public JsonMsgDataBean(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造函数
     * @return
     */
    public JsonMsgDataBean() {
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应成功
     * @return JsonData
     */
    public static JsonMsgDataBean buildSuccess(){
        return new JsonMsgDataBean(200,"响应成功",null);
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应成功
     * @param data
     * @return JsonData
     */
    public static JsonMsgDataBean buildSuccess(Object data){
        return new JsonMsgDataBean(200,"响应成功",data);
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应成功
     * @param msg
     * @return JsonData
     */
    public static JsonMsgDataBean buildSuccess(String msg){
        return new JsonMsgDataBean(200,"响应成功",msg);
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应失败
     * @return JsonData
     */
    public static JsonMsgDataBean buildFail(){
        return new JsonMsgDataBean(-100,"响应失败",null);
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应失败
     * @param msg
     * @return JsonData
     */
    public static JsonMsgDataBean buildFail(String msg){
        return new JsonMsgDataBean(-100,msg,null);
    }

    /**
     * 需要向外部暴露响应成功以及失败的信息--响应失败
     * @param code
     * @param msg
     * @return JsonData
     */
    public static JsonMsgDataBean buildFail(Integer code, String msg){
        return new JsonMsgDataBean(code, msg,null);
    }
}
