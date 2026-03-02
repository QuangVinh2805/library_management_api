package com.example.library_management_api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private String token;
    private List<RoleResponse> roles;
    private Long status;
    private List<RouteLinkResponse> routeLinks;



}
