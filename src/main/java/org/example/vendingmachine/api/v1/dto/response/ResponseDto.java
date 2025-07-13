package org.example.vendingmachine.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private T data;
    private String errorMessage;
    private HttpStatus status;

    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(data, null, HttpStatus.OK);
    }

    public static <T> ResponseDto<T> created(T data) {
        return new ResponseDto<>(data, null, HttpStatus.CREATED);
    }

    public static <T> ResponseDto<T> error(String errorMessage, HttpStatus status) {
        return new ResponseDto<>(null, errorMessage, status);
    }
}
