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
}