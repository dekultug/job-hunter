package vn.hoidanit.jobhunter.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.RestResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handIdException(MethodArgumentNotValidException exception) {

        BindingResult result = exception.getBindingResult();

        RestResponse<Object> rest = new RestResponse<Object>();
        rest.setStatus(HttpStatus.BAD_REQUEST.value());
        rest.setError(exception.getMessage());
        rest.setMessage("IdValidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rest);
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class

    })
    public ResponseEntity<RestResponse<Object>> handIdException(Exception exception) {
        RestResponse<Object> rest = new RestResponse<Object>();
        rest.setStatus(HttpStatus.BAD_REQUEST.value());
        rest.setError(exception.getMessage());
        rest.setMessage("Exception...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rest);
    }

}
