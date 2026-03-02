package com.example.library_management_api.response;

import lombok.Data;

import java.util.Date;

@Data
public class FavouriteProductResponse {
    private Long id;
    private String hashId;
    private String token;
    private String productName;
    private String name;
    private String image;
    private Long status;
}
