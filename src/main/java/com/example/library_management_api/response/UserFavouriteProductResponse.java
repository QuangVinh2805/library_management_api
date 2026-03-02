package com.example.library_management_api.response;

import lombok.Data;

import java.util.Date;

@Data
public class UserFavouriteProductResponse {
    private boolean isFavourite;
    private Long id;
}
