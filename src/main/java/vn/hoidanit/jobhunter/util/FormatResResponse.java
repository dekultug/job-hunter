package vn.hoidanit.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

@ControllerAdvice
public class FormatResResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> rest = new RestResponse<Object>();
        rest.setStatus(status);

        if (body instanceof String) {
            return body;
        }

        if (status >= 400) {
            // fail
            return body;
        } else {
            // success
            rest.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            rest.setMessage(message != null ? message.value() : "Call Api Success");

        }
        return rest;
    }

}