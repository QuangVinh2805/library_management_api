package com.example.library_management_api.controller;

import com.example.library_management_api.models.Reader;
import com.example.library_management_api.models.User;
import com.example.library_management_api.repository.UserRepository;
import com.example.library_management_api.request.*;
import com.example.library_management_api.response.*;
import com.example.library_management_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    public static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách tất cả user
    @GetMapping("/all")
    public MyApiResponse<Map<String, Object>> listAllUser(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userService.listAllUser(pageable);
    }

    @GetMapping("/getAllByStatus")
    public MyApiResponse<Map<String, Object>> listAllUserByStatus(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userService.listAllUserByStatus(pageable);
    }

    @GetMapping("/allReader")
    public MyApiResponse<Map<String, Object>> listAllReader(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userService.listAllReader(pageable);
    }


    @GetMapping("/allUserByRoleName")
    public MyApiResponse<Map<String, Object>> listAllUserByRole(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam String roleName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userService.listAllUserByRole(pageable,roleName);
    }

    @GetMapping("/getSearchAllUser")
    public MyApiResponse<Map<String, Object>> listSearchAllUser(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return userService.listSearchAllUser(keyword,pageable);
    }


    @GetMapping("/getSearchAllReader")
    public MyApiResponse<Map<String, Object>> listSearchAllReader(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return userService.listSearchAllReader(keyword,pageable);
    }


    //Tìm user theo TOKEN (thay cho ID)
    @GetMapping("/userByToken")
    public MyApiResponse<UserResponse> findUser(@RequestParam("token") String token) {
        return userService.findUser(token);
    }


    @GetMapping("/readerByToken")
    public MyApiResponse<ReaderResponse> findReader(@RequestParam("token") String token) {
        return userService.findReader(token);
    }

    @GetMapping("/getUserByUserCode")
    public MyApiResponse<UserResponse> findUserByUserCode(@RequestParam String userCode) {
        return userService.findUserByUserCode(userCode);
    }

    @GetMapping("/getUserByPublicId")
    public MyApiResponse<UserResponse> findUserByPublicId(@RequestParam String publicId) {
        return userService.findUserByPublicId(publicId);
    }

    @GetMapping("/getReaderByPublicId")
    public MyApiResponse<ReaderResponse> findReaderByPublicId(@RequestParam String publicId) {
        return userService.findReaderByPublicId(publicId);
    }

    // Đăng nhập cms
    @PostMapping("/loginCms")
    public ResponseEntity<MyApiResponse<UserLoginResponse>> loginCms(
            @RequestBody LoginRequest request
    ) {
        MyApiResponse<UserLoginResponse> response =
                userService.loginCms(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Đăng nhập reader
    @PostMapping("/loginReader")
    public ResponseEntity<MyApiResponse<ReaderLoginResponse>> loginReader(
            @RequestBody LoginRequest request
    ) {
        MyApiResponse<ReaderLoginResponse> response =
                userService.loginReader(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Xóa user theo TOKEN
    @DeleteMapping("/delete/{token}")
    public MyApiResponse<String> deleteUser(@PathVariable("token") String token) {
        return userService.deleteUser(token);
    }

    //Cập nhật user theo TOKEN
    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<UserUpdateResponse>> updateUser(@RequestBody UserUpdateRequest request) {
        MyApiResponse<UserUpdateResponse> response = userService.updateUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PutMapping("/updateReader")
    public ResponseEntity<MyApiResponse<UserUpdateResponse>> updateReader(@RequestBody UserUpdateRequest request) {
        MyApiResponse<UserUpdateResponse> response = userService.updateReader(request);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PutMapping("/updateByUserCode")
    public MyApiResponse<UserUpdateResponse> updateUserByUserCode(@RequestBody UserUpdateRequest request) {
        return userService.updateUserByUserCode(request);
    }

    @PutMapping("/updateByPublicId")
    public ResponseEntity<MyApiResponse<UserUpdateResponse>> updateUserByPublicId(
            @RequestBody UserUpdateRequest request
    ) {
        MyApiResponse<UserUpdateResponse> response = userService.updateUserByPublicId(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/updateReaderByPublicId")
    public ResponseEntity<MyApiResponse<UserUpdateResponse>> updateReaderByPublicId(
            @RequestBody UserUpdateRequest request
    ) {
        MyApiResponse<UserUpdateResponse> response = userService.updateReaderByPublicId(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    //Tạo user mới
    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<CreateUserResponse>> createUser(
            @RequestBody CreateUserRequest request
    ) {
        MyApiResponse<CreateUserResponse> response = userService.createUser(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    //Tạo user mới
    @PostMapping("/createReader")
    public ResponseEntity<MyApiResponse<UserCreateResponse>> createReader(
            @RequestBody UserRequest request
    ) {
        MyApiResponse<UserCreateResponse> response = userService.createReader(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }


    //Tạo user mới
    @PostMapping("/createReaderByUser")
    public ResponseEntity<MyApiResponse<UserCreateResponse>> createReaderByUser(
            @RequestBody UserRequest request
    ) {
        MyApiResponse<UserCreateResponse> response = userService.createReaderByUser(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }


    //Cập nhật password
    @PutMapping("/UpdatePassword")
    public ResponseEntity<MyApiResponse<String>> updatePassword(@RequestBody UpdatePasswordRequest request) {
        MyApiResponse<String> response = userService.updatePassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/UpdateReaderPassword")
    public ResponseEntity<MyApiResponse<String>> updateReaderPassword(@RequestBody UpdatePasswordRequest request) {
        MyApiResponse<String> response = userService.updateReaderPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //Thay đổi trang thái tài khoản người dùng
    @PutMapping("/changeStatus")
    public MyApiResponse<User> changeStatus(@RequestParam String publicId) {
        return userService.changeStatus(publicId);
    }

    @PutMapping("/changeStatusReader")
    public MyApiResponse<Reader> changeStatusReader(@RequestParam String publicId) {
        return userService.changeStatusReader(publicId);
    }


    //Đăng xuất
    @PostMapping("/logout")
    public MyApiResponse<String> logout(@RequestParam String token) {
        return userService.logout(token);
    }

    @PostMapping("/readerLogout")
    public MyApiResponse<String> readerLogout(@RequestParam String token) {
        return userService.readerLogout(token);
    }


    @PostMapping("/user/reset-password/send-link")
    public ResponseEntity<MyApiResponse<String>> sendResetLinkUser(
            @RequestParam String email
    ) {
        MyApiResponse<String> response = userService.sendResetPasswordLinkUser(email);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PostMapping("/user/reset-password/confirm")
    public ResponseEntity<MyApiResponse<String>> resetPasswordUser(
            @RequestBody ResetPasswordRequest request
    ) {
        MyApiResponse<String> response = userService.resetPasswordByTokenUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);

    }


    @PostMapping("/reader/reset-password/send-link")
    public ResponseEntity<MyApiResponse<String>> sendResetLinkReader(
            @RequestParam String email
    ) {
        MyApiResponse<String> response = userService.sendResetPasswordLinkReader(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reader/reset-password/confirm")
    public ResponseEntity<MyApiResponse<String>> resetPasswordReader(
            @RequestBody ResetPasswordRequest request
    ) {
        MyApiResponse<String> response = userService.resetPasswordByTokenReader(request);
        return ResponseEntity.status(response.getStatus()).body(response);

    }



    @PostMapping("/changeRole")
    public ResponseEntity<MyApiResponse<String>> changeRole(
            @RequestBody ChangeRoleRequest request
    ) {
        MyApiResponse<String> response = userService.changeRole(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

}
