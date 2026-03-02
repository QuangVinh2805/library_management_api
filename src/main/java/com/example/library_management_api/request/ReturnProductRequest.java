package com.example.library_management_api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnProductRequest {
    private Long borrowedProductId;
    private Boolean isLost;
}
