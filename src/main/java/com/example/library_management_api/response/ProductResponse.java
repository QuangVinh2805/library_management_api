package com.example.library_management_api.response;


import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProductResponse {
    private String productName;
    private Long quantity;
    private String publisherName;
    private String publicationDate;
    private String description;
    private String authorName;
    private String categoryName;
    private String image;
    private Long status;
    private String hashId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long price;
    private Long isNew;
    private String location;
}
