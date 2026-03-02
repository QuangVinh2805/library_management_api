    package com.example.library_management_api.service;

    import com.example.library_management_api.models.*;
    import com.example.library_management_api.repository.RouteRepository;
    import com.example.library_management_api.request.RouteRequest;
    import com.example.library_management_api.response.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    public class RouteService {
        @Autowired
        RouteRepository routeRepository;

        public MyApiResponse<Map<String, Object>> getAllRoutes(Pageable pageable) {
            Page<Route> page = routeRepository.findAllRoute(pageable);
            List<Route> route = page.getContent();

            if(route.isEmpty()){
                return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc tìm thấy route");
            }

            List<RouteResponse> routeResponses = route.stream().map(
                            r -> {
                                RouteResponse routeResponse = new RouteResponse();
                                routeResponse.setId(r.getId());
                                routeResponse.setRoute(r.getRoute());
                                routeResponse.setDescription(r.getDescription());
                                routeResponse.setStatus(r.getStatus());
                                return routeResponse;
                            })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("data", routeResponses);
            result.put("totalItems", page.getTotalElements());
            result.put("totalPages", page.getTotalPages());
            result.put("currentPage", page.getNumber());
            return MyApiResponse.success("Lấy danh sách route thành công", result);
        }

        public MyApiResponse<Map<String, Object>> getSearchAllRoute(String keyword,Pageable pageable) {
            Page<Route> page = routeRepository.searchAllRoutesByName(keyword,pageable);
            List<Route> route = page.getContent();

            if(route.isEmpty()){
                return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc tìm thấy route");
            }

            List<RouteResponse> routeResponses = route.stream().map(
                            r -> {
                                RouteResponse routeResponse = new RouteResponse();
                                routeResponse.setId(r.getId());
                                routeResponse.setRoute(r.getRoute());
                                routeResponse.setDescription(r.getDescription());
                                routeResponse.setStatus(r.getStatus());
                                return routeResponse;
                            })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("data", routeResponses);
            result.put("totalItems", page.getTotalElements());
            result.put("totalPages", page.getTotalPages());
            result.put("currentPage", page.getNumber());
            return MyApiResponse.success("Lấy danh sách route thành công", result);
        }


        public MyApiResponse<RouteResponse> createRoute(RouteRequest routeRequest) {

            if (routeRequest == null || routeRequest.getRoute() == null
                    || routeRequest.getRoute().trim().isEmpty()) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Route không được để trống"
                );
            }

            String routeValue = routeRequest.getRoute().trim();

            Route route = routeRepository.findByRoute(routeValue);
            if (route != null) {
                return MyApiResponse.error(
                        HttpStatus.CONFLICT,
                        "Route đã tồn tại"
                );
            }

            route = new Route();
            route.setRoute(routeValue);
            route.setDescription(routeRequest.getDescription());
            route.setStatus(1L);
            route.setCreatedAt(new Date());

            routeRepository.save(route);

            RouteResponse routeResponse = new RouteResponse();
            routeResponse.setRoute(route.getRoute());
            routeResponse.setDescription(route.getDescription());
            routeResponse.setStatus(route.getStatus());

            return MyApiResponse.success("Tạo route thành công", routeResponse);
        }



        public MyApiResponse<RouteResponse> updateRoute(Long id, RouteRequest routeRequest) {

            if (id == null) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Id không được null"
                );
            }

            if (routeRequest == null || routeRequest.getRoute() == null
                    || routeRequest.getRoute().trim().isEmpty()) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Route không được để trống"
                );
            }

            Route route = routeRepository.findById(id).orElse(null);
            if (route == null) {
                return MyApiResponse.error(
                        HttpStatus.NOT_FOUND,
                        "Route không tồn tại"
                );
            }

            String routeValue = routeRequest.getRoute().trim();

            // Check trùng route (trừ chính nó)
            Route existRoute = routeRepository.findByRoute(routeValue);
            if (existRoute != null && !existRoute.getId().equals(route.getId())) {
                return MyApiResponse.error(
                        HttpStatus.CONFLICT,
                        "Route đã tồn tại"
                );
            }

            route.setRoute(routeValue);
            route.setDescription(routeRequest.getDescription());
            route.setUpdatedAt(new Date());

            routeRepository.save(route);

            RouteResponse routeResponse = new RouteResponse();
            routeResponse.setRoute(route.getRoute());
            routeResponse.setDescription(route.getDescription());
            routeResponse.setStatus(route.getStatus());

            return MyApiResponse.success("Cập nhật route thành công", routeResponse);
        }



        public MyApiResponse<RouteResponse> changeStatus(Long id) {

            Route routes = routeRepository.findRouteById(id);
            if (routes == null) {
                return MyApiResponse.error(HttpStatus.NOT_FOUND, "Route không tồn tại");
            }

            // Toggle status
            routes.setStatus(routes.getStatus() == 1L ? 0L : 1L);
            routes.setUpdatedAt(new Date());

            routeRepository.save(routes);

            RouteResponse routeResponse = new RouteResponse();
            routeResponse.setRoute(routes.getRoute());
            routeResponse.setDescription(routes.getDescription());
            routeResponse.setStatus(routes.getStatus());

            return MyApiResponse.success("Thay đổi trạng thái thành công", routeResponse);
        }


    }
