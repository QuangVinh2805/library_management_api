package com.example.library_management_api.controller;


import com.example.library_management_api.request.FeePaymentRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.FeePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class FeePaymentController {
    @Autowired
    FeePaymentService feePaymentService;


    @PostMapping("/fee/payment")
    public MyApiResponse<String> recordPayment(@RequestBody FeePaymentRequest request) {
        return feePaymentService.recordPayment(request);

    }
}
