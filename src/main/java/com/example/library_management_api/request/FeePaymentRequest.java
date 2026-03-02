package com.example.library_management_api.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeePaymentRequest {
    private Long borrowedProductId;
    private String librarianToken;
}
