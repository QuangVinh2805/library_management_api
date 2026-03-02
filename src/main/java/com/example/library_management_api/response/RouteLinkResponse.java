package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteLinkResponse {
    private String route;
    private String endPoint;
    private String method;
}
