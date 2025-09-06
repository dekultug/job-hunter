package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IdValidException.class)
    public ResponseEntity<RestResponse<Object>> handIdException(IdValidException exception) {
        RestResponse<Object> rest = new RestResponse<Object>();
        rest.setStatus(HttpStatus.BAD_REQUEST.value());
        rest.setError(exception.getMessage());
        rest.setMessage("IdValidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rest);
    }

}
