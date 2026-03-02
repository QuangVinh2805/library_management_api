package com.example.library_management_api.response;

import lombok.Data;

@Data
public class RouteResponse {
    private Long id;
    private String route;
    private String description;
    private Long status;
}
