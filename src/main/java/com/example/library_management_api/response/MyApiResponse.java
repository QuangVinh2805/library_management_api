package com.example.library_management_api.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
@Builder
public class MyApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public MyApiResponse() {
    }

    public MyApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> MyApiResponse<T> success(String message, T data) {
        return new MyApiResponse<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> MyApiResponse<T> error(HttpStatus status, String message) {
        return new MyApiResponse<>(status.value(), message, null);
    }

    // Getters & setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
