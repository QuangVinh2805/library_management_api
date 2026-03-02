package com.example.library_management_api.controller;

import com.example.library_management_api.request.BorrowStatisticRequest;
import com.example.library_management_api.request.FavouriteProductRequest;
import com.example.library_management_api.request.ProductRequest;
import com.example.library_management_api.request.ProductReservationRequest;
import com.example.library_management_api.response.*;
import com.example.library_management_api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<ProductResponse>> createProduct(
            @ModelAttribute ProductRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        return productService.createProduct(req, image);
    }

    @GetMapping("/getAllProduct")
    public MyApiResponse<Map<String, Object>> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productService.getAllProduct(pageable);
    }

    @GetMapping("/getAllProductByStatus")
    public MyApiResponse<Map<String, Object>> getAllProductByStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productService.getAllProductByStatus(pageable);
    }


    @GetMapping("/getProductByHashId")
    public MyApiResponse<ProductResponse> getProductByHashId(@RequestParam String hashId) {
        return productService.getProduct(hashId);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<ProductResponse>> updateProduct(
            @RequestParam String hashId,
            @ModelAttribute ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        MyApiResponse<ProductResponse> response = productService.updateProduct(hashId, productRequest, image);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/changeStatusProduct")
    public ResponseEntity<MyApiResponse<ProductStatusResponse>> changeStatusProduct(@RequestParam String hashId) {
        MyApiResponse<ProductStatusResponse> response = productService.changeStatusProduct(hashId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/changeIsNewProduct")
    public MyApiResponse<ProductIsNewResponse> changeIsNewProduct(@RequestParam String hashId) {
        return productService.changeIsNewProduct(hashId);
    }

    @GetMapping("/getAllNewProduct")
    public MyApiResponse<List<ProductResponse>> getAllNewProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getNewProduct(pageable);
    }

    @GetMapping("/getProductCarouselByCategory")
    public MyApiResponse<List<ProductResponse>> getProductCarouselByCategory(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getProductCarouselByCategoryId(categoryId, pageable);
    }

    @GetMapping("/getProductByCategory")
    public MyApiResponse<Map<String, Object>> getProductByCategory(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getProductByCategoryId(categoryId, pageable);
    }


    @GetMapping("/getProductByAuthor")
    public MyApiResponse<Map<String, Object>> getProductByAuthor(
            @RequestParam Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getProductByAuthorId(authorId, pageable);
    }

    @GetMapping("/getProductByPublisher")
    public MyApiResponse<Map<String, Object>> getProductByPublisher(
            @RequestParam Long publisherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getProductByPublisherId(publisherId, pageable);
    }

    @GetMapping("/countByCategory")
    public MyApiResponse<String> getProductCount(@RequestParam Long categoryId) {
        return productService.getProductCountByCategory(categoryId);
    }

    @GetMapping("/countByAuthor")
    public MyApiResponse<String> getProductCountByAuthor(@RequestParam Long authorId) {
        return productService.getProductCountByAuthor(authorId);
    }

    @GetMapping("/countByPublisher")
    public MyApiResponse<String> getProductCountByPublisherId(@RequestParam Long publisherId) {
        return productService.getProductCountByPublisher(publisherId);
    }


    @GetMapping("/searchProductByStatus")
    public MyApiResponse<List<ProductResponse>> searchProductByStatus(@RequestParam String keyword,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getSearchProductByStatus(keyword, pageable);
    }

    @GetMapping("/searchAllProductByStatus")
    public MyApiResponse<Map<String, Object>> searchAllProductByStatus(@RequestParam String keyword,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getSearchAllProductByStatus(keyword, pageable);
    }

    @GetMapping("/searchAllProduct")
    public MyApiResponse<Map<String, Object>> searchAllProduct(@RequestParam String keyword,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getSearchAllProduct(keyword, pageable);
    }


    @PostMapping("/create/favouriteProduct")
    public MyApiResponse<FavouriteProductResponse> createFavourite(@RequestBody FavouriteProductRequest request) {
        return productService.createFavourite(request);
    }

    @GetMapping("/countFavourite")
    public MyApiResponse<Long> countFavourite(@RequestParam String hashId) {
        return productService.countFavourite(hashId);
    }

    @PutMapping("/changeStatusFavouriteProduct")
    public MyApiResponse<FavouriteProductResponse> updateStatus(@RequestParam Long favouriteProductId) {
        return productService.updateStatusFavouriteProduct(favouriteProductId);
    }


    @GetMapping("/getFavouriteProductByToken")
    public MyApiResponse<Map<String, Object>> getFavouriteProductByToken(@RequestParam String token,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getFavouriteProductByUser(token, pageable);
    }


    @GetMapping("/check-favourite")
    public MyApiResponse<UserFavouriteProductResponse> checkFavourite(
            @RequestParam String hashId,
            @RequestParam(required = false) String token
    ) {
        return productService.checkUserFavourite(token, hashId);
    }


    @PostMapping("/createProductReservation")
    public MyApiResponse<ProductReservationResponse> createProductReservation(@RequestBody ProductReservationRequest request) {
        return productService.createProductReservation(request);
    }


    @GetMapping("/getAllProductReservationByUser")
    public MyApiResponse<Map<String, Object>> getAllProductReservationByUser(@RequestParam String token,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productService.getAllProductReservationByUser(token, pageable);
    }

    @GetMapping("/getTopFavouriteProduct")
    public MyApiResponse<List<ProductResponse>> getTopFavouriteProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getTopFavouriteProducts(pageable);
    }

    @GetMapping("/getTopBorrowProduct")
    public MyApiResponse<List<ProductResponse>> getTopBorrowProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getTopBorrowedProducts(pageable);
    }

    @GetMapping("/getAllProductReservation")
    public MyApiResponse<Map<String, Object>> getAllProductReservation(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productService.getAllProductReservation(pageable);
    }


    @GetMapping("/getSearchAllProductReservation")
    public MyApiResponse<Map<String, Object>> getSearchAllProductReservation(@RequestParam String keyword,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return productService.getSearchAllProductReservation(keyword, pageable);
    }


    @PutMapping("/changeStatusProductReservation")
    public MyApiResponse<ProductReservationResponse> changeStatusProductReservation(@RequestParam Long productReservationId, @RequestParam Long newStatus) {
        return productService.updateProductReservation(productReservationId, newStatus);

    }

    @PostMapping("/statistics/borrow")
    public MyApiResponse<List<BorrowStatisticResponse>> getBorrowStatistic(@RequestParam int year) {
        return productService.getBorrowStatisticByYear(year);
    }

    @GetMapping("/countByMonth")
    public MyApiResponse<List<Map<String, Object>>> getProductCountByMonth() {
        return productService.getProductCountByMonth();
    }

    @GetMapping("/search")
    public MyApiResponse<Map<String, Object>> searchByDateRange(
            @RequestParam String type,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return productService.searchByDateRange(type, start, end, pageable);
    }
}
