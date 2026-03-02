package com.example.library_management_api.request;

import lombok.Data;

import java.util.Date;

@Data
public class ProductRequest {
    private String productName;
    private Long quantity;
    private String publisherName;
    private String publicationDate;
    private String description;
    private String authorName;
    private String categoryName;
    private Long price;
    private String location;
}
