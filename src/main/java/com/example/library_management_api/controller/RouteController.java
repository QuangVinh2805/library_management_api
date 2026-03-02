package com.example.library_management_api.controller;

import com.example.library_management_api.models.Role;
import com.example.library_management_api.request.RouteRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.RouteResponse;
import com.example.library_management_api.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/getAll")
    public MyApiResponse<Map<String, Object>> getAllRoute(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return routeService.getAllRoutes(pageable);
    }


    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<RouteResponse>> createRoute(
            @RequestBody RouteRequest routeRequest) {
        MyApiResponse<RouteResponse> response =
                routeService.createRoute(routeRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<RouteResponse>> updateRoute(
            @RequestParam Long id,
            @RequestBody RouteRequest routeRequest) {

        MyApiResponse<RouteResponse> response =
                routeService.updateRoute(id, routeRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/changeStatus")
    public MyApiResponse<RouteResponse> changeStatus(
            @RequestParam Long id ) {
        return routeService.changeStatus(id);
    }

    @GetMapping("/getSearchAllRoute")
    public MyApiResponse<Map<String, Object>> getSearchAllRoute(@RequestParam String keyword,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return routeService.getSearchAllRoute(keyword, pageable);
    }
}
