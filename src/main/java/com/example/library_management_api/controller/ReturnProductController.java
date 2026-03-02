package com.example.library_management_api.controller;

import com.example.library_management_api.request.ReturnProductRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.ReturnResponse;
import com.example.library_management_api.service.ReturnProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/return")
public class ReturnProductController {
    @Autowired
    ReturnProductService returnProductService;


    @PostMapping("/product")
    public MyApiResponse<ReturnResponse> returnBook(@RequestBody ReturnProductRequest request) {
        return returnProductService.returnBook(request);
    }
}
