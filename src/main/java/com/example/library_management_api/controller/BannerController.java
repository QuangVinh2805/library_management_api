package com.example.library_management_api.controller;


import com.example.library_management_api.models.Author;
import com.example.library_management_api.models.Banner;
import com.example.library_management_api.request.BannerRequest;
import com.example.library_management_api.request.ProductRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {
    @Autowired
    BannerService bannerService;

    @GetMapping("/getAll")
    public MyApiResponse<List<Banner>> getAllBanner(){
        return bannerService.getAllBanners();
    }


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Banner>> createBanner(@ModelAttribute BannerRequest req,
                                        @RequestPart(value = "image", required = false) MultipartFile image){
        MyApiResponse<Banner> response = bannerService.createBanner(req,image);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<Banner>> updateBanner(@RequestParam Long bannerId,
                                              @ModelAttribute BannerRequest bannerRequest,
                                              @RequestPart(value = "image", required = false) MultipartFile image){
        MyApiResponse<Banner> response = bannerService.updateBanner(bannerId,bannerRequest,image);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);    }

    @PutMapping("/changeStatus")
    public MyApiResponse<Banner> changeStatus(@RequestParam Long bannerId){
        return bannerService.changeStatus(bannerId);
    }

}
