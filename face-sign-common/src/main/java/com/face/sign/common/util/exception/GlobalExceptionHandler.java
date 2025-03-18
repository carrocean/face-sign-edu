package com.face.sign.common.util.exception;

import com.face.sign.common.util.JsonMsgDataBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BizException.class)
    public JsonMsgDataBean handleRegistrationException(BizException ex, WebRequest request) {
        return JsonMsgDataBean.buildFail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * 处理其他运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public JsonMsgDataBean handleRuntimeException(RuntimeException ex, WebRequest request) {
        return JsonMsgDataBean.buildFail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public JsonMsgDataBean handleGeneralException(Exception ex, WebRequest request) {
        return JsonMsgDataBean.buildFail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "发生了一个未知错误，请联系管理员");
    }
}