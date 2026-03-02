package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderLoginResponse {
    private String token;
    private String role;
    private Long status;
    private Boolean isBlackList;
    private List<RouteLinkResponse> routeLinks;



}