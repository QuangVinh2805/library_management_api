package com.example.library_management_api.request;

import lombok.Data;

@Data
public class FavouriteProductRequest {
    private String hashId;
    private String token;
}
