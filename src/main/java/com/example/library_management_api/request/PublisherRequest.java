package com.example.library_management_api.request;


import lombok.Data;

@Data
public class PublisherRequest {
    private String publisherName;
    private String email;
    private String phone;
}
