package com.example.library_management_api.service;

import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.*;
import com.example.library_management_api.response.*;
import com.example.library_management_api.util.PasswordValidator;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    BlackListRepository blackListRepository;


    @Autowired
    ReaderRepository readerRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;




    @Autowired
    RolePermissionRepository rolePermissionRepository;
    @Autowired
    private ProductReservationRepository productReservationRepository;
    @Autowired
    private FavouriteProductRepository favouriteProductRepository;

    @Autowired
    BorrowedProductRepository borrowedProductRepository;


    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }



    // Lấy danh sách user
    public MyApiResponse<Map<String, Object>> listAllUser(Pageable pageable) {

        Page<User> page = userRepository.findAllUser(pageable);
        List<User> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(
                    HttpStatus.NO_CONTENT,
                    "Không có người dùng nào."
            );
        }

        // ===== MAP userId -> List<RoleResponse> =====
        Map<Long, List<RoleResponse>> userRoleMap = new HashMap<>();

        List<Object[]> rows = roleRepository.findAllUserRoles();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            Long roleId = ((Number) row[1]).longValue();
            String roleName = (String) row[2];

            userRoleMap
                    .computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(new RoleResponse(roleName));
        }

        List<UserResponse> userResponses = listUser.stream()
                .map(user -> UserResponse.fromEntity(
                        user.getToken(),
                        userRoleMap.getOrDefault(user.getId(), List.of()),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success(
                "Lấy danh sách người dùng thành công.",
                result
        );
    }




    public MyApiResponse<Map<String, Object>> listAllUserByStatus(Pageable pageable) {
        Page<User> page = userRepository.findAllUserByStatus(pageable);
        List<User> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có người dùng nào.");
        }

        Map<Long, List<RoleResponse>> userRoleMap = new HashMap<>();

        List<Object[]> rows = roleRepository.findAllUserRoles();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            Long roleId = ((Number) row[1]).longValue();
            String roleName = (String) row[2];

            userRoleMap
                    .computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(new RoleResponse(roleName));
        }

        List<UserResponse> userResponses = listUser.stream()
                .map(user -> UserResponse.fromEntity(
                        user.getToken(),
                        userRoleMap.getOrDefault(user.getId(), List.of()),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách người dùng thành công.", result);
    }

    public MyApiResponse<Map<String, Object>> listAllReader(Pageable pageable) {
        Page<Reader> page = readerRepository.findAllReader(pageable);
        List<Reader> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có người dùng nào.");
        }

        Map<Long, String> roleMap = roleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Role::getId,
                        Role::getRoleName
                ));

        List<ReaderResponse> userResponses = listUser.stream()
                .map(user -> ReaderResponse.fromEntity(
                        user.getToken(),
                        roleMap.getOrDefault(user.getRoleId(), "UNKNOWN"),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách người dùng thành công.", result);
    }


    public MyApiResponse<Map<String, Object>> listAllUserByRole(Pageable pageable, String roleName) {
        Page<User> page = userRepository.findAllByRoleName(roleName,pageable);
        List<User> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có người dùng nào.");
        }

        Map<Long, List<RoleResponse>> userRoleMap = new HashMap<>();

        List<Object[]> rows = roleRepository.findAllUserRoles();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            Long roleId = ((Number) row[1]).longValue();
            roleName = (String) row[2];

            userRoleMap
                    .computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(new RoleResponse(roleName));
        }

        List<UserResponse> userResponses = listUser.stream()
                .map(user -> UserResponse.fromEntity(
                        user.getToken(),
                        userRoleMap.getOrDefault(user.getId(), List.of()),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách người dùng thành công.", result);
    }

    public MyApiResponse<Map<String, Object>> listSearchAllUser(String keyword,Pageable pageable) {
        Page<User> page = userRepository.searchAllUser(keyword,pageable);
        List<User> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không tìm thấy người dùng.");
        }

        Map<Long, List<RoleResponse>> userRoleMap = new HashMap<>();

        List<Object[]> rows = roleRepository.findAllUserRoles();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            Long roleId = ((Number) row[1]).longValue();
            String roleName = (String) row[2];

            userRoleMap
                    .computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(new RoleResponse(roleName));
        }

        List<UserResponse> userResponses = listUser.stream()
                .map(user -> UserResponse.fromEntity(
                        user.getToken(),
                        userRoleMap.getOrDefault(user.getId(), List.of()),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách người dùng thành công.", result);
    }


    public MyApiResponse<Map<String, Object>> listSearchAllReader(String keyword,Pageable pageable) {
        Page<Reader> page = readerRepository.searchAllReader(keyword,pageable);
        List<Reader> listUser = page.getContent();

        if (listUser.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không tìm thấy độc giả.");
        }

        Map<Long, String> roleMap = roleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Role::getId,
                        Role::getRoleName
                ));

        List<ReaderResponse> userResponses = listUser.stream()
                .map(user -> ReaderResponse.fromEntity(
                        user.getToken(),
                        roleMap.getOrDefault(user.getRoleId(), "UNKNOWN"),
                        user.getBirthday(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getName(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getPublicId()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", userResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách độc giả thành công.", result);
    }


    public MyApiResponse<UserResponse> findUser(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        List<RoleResponse> roles = roleRepository.findRolesByUserId(user.getId())
                .stream()
                .map(role -> new RoleResponse(
                        role.getRoleName()
                ))
                .toList();

        UserResponse userResponse = UserResponse.fromEntity(
                user.getToken(),
                roles,
                user.getBirthday(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getUserCode(),
                user.getStatus(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPublicId()
        );
        return MyApiResponse.success("Tìm thấy người dùng", userResponse);
    }

    public MyApiResponse<ReaderResponse> findReader(String token) {
        Reader user = readerRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        Role role = roleRepository.findById(user.getRoleId())
                .orElse(null);

        String roleName = role != null
                ? role.getRoleName()
                : "UNKNOWN";

        ReaderResponse userResponse = ReaderResponse.fromEntity(
                user.getToken(),
                roleName,
                user.getBirthday(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getUserCode(),
                user.getStatus(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPublicId()
        );
        return MyApiResponse.success("Tìm thấy người dùng", userResponse);
    }

    public MyApiResponse<UserResponse> findUserByUserCode(String userCode) {
        User user = userRepository.findByUserCode(userCode);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        List<RoleResponse> roles = roleRepository.findRolesByUserId(user.getId())
                .stream()
                .map(role -> new RoleResponse(
                        role.getRoleName()
                ))
                .toList();

        UserResponse userResponse = UserResponse.fromEntity(
                user.getToken(),
                roles,
                user.getBirthday(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getUserCode(),
                user.getStatus(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPublicId()
        );
        return MyApiResponse.success("Tìm thấy người dùng", userResponse);
    }

    public MyApiResponse<UserResponse> findUserByPublicId(String publicId) {
        User user = userRepository.findByPublicId(publicId);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        List<RoleResponse> roles = roleRepository.findRolesByUserId(user.getId())
                .stream()
                .map(role -> new RoleResponse(
                        role.getRoleName()
                ))
                .toList();

        UserResponse userResponse = UserResponse.fromEntity(
                user.getToken(),
                roles,
                user.getBirthday(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getUserCode(),
                user.getStatus(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPublicId()
        );
        return MyApiResponse.success("Tìm thấy người dùng", userResponse);
    }

    public MyApiResponse<ReaderResponse> findReaderByPublicId(String publicId) {
        Reader reader = readerRepository.findByPublicId(publicId);
        if (reader == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        Role role = roleRepository.findById(reader.getRoleId())
                .orElse(null);

        String roleName = role != null
                ? role.getRoleName()
                : "UNKNOWN";

        ReaderResponse userResponse = ReaderResponse.fromEntity(
                reader.getToken(),
                roleName,
                reader.getBirthday(),
                reader.getEmail(),
                reader.getPhone(),
                reader.getName(),
                reader.getUserCode(),
                reader.getStatus(),
                reader.getAddress(),
                reader.getCreatedAt(),
                reader.getUpdatedAt(),
                reader.getPublicId()
        );
        return MyApiResponse.success("Tìm thấy người dùng", userResponse);
    }

    public MyApiResponse<UserLoginResponse> loginCms(LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        if (isBlank(email) || isBlank(password)) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email và password là bắt buộc");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Email không tồn tại");
        }

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return MyApiResponse.error(HttpStatus.UNAUTHORIZED, "Sai mật khẩu");
        }

        if (user.getStatus() == 0) {
            return MyApiResponse.error(HttpStatus.FORBIDDEN, "Tài khoản bị khóa");
        }

        // ===== LẤY ROLE TỪ user_role =====
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());

        if (roles.isEmpty()) {
            return MyApiResponse.error(HttpStatus.FORBIDDEN, "Tài khoản chưa được gán role");
        }

        List<Role> activeRoles = roles.stream()
                .filter(r -> r.getStatus() == 1)
                .toList();

        if (activeRoles.isEmpty()) {
            return MyApiResponse.error(HttpStatus.FORBIDDEN, "Tất cả role đều bị khóa");
        }

        // ===== TOKEN =====
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userRepository.save(user);

        // ===== ROUTE PERMISSION (MERGE THEO ROLE) =====
        List<Long> roleIds = activeRoles.stream()
                .map(Role::getId)
                .toList();

        List<RouteLinkResponse> routeLinks =
                permissionRepository.findRouteLinksByRoleIds(roleIds);

        return MyApiResponse.success(
                "Đăng nhập CMS thành công",
                new UserLoginResponse(
                        token,
                        activeRoles.stream()
                                .map(r -> new RoleResponse(r.getRoleName()))
                                .toList(),
                        user.getStatus(),
                        routeLinks
                )
        );
    }




    public MyApiResponse<ReaderLoginResponse> loginReader(LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        if (isBlank(email) || isBlank(password)) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Email và password là bắt buộc"
            );
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Reader reader = readerRepository.findByEmail(email);
        if (reader == null) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Email không tồn tại"
            );
        }

        if (!encoder.matches(password, reader.getPassword())) {
            return MyApiResponse.error(
                    HttpStatus.UNAUTHORIZED,
                    "Sai mật khẩu"
            );
        }

        if (reader.getStatus() == 0) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị khóa"
            );
        }

        boolean isBlackList =
                blackListRepository.existsByReaderId(reader.getId());

        if (isBlackList) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản dã bị khoá do nằm trong danh sách blacklist,vui lòng đến phòng tiếp tân để giải quyết!"
            );
        }

        // ✅ LOGIN OK
        String token = UUID.randomUUID().toString();
        reader.setToken(token);
        readerRepository.save(reader);

        return MyApiResponse.success(
                "Đăng nhập Reader thành công",
                new ReaderLoginResponse(
                        token,
                        "READER",
                        reader.getStatus(),
                        false,
                        Collections.emptyList()
                )
        );
    }








    public MyApiResponse<String> save(User user) {
        if (user == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Người dùng không hợp lệ");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        userRepository.save(user);
        return MyApiResponse.success("Lưu người dùng thành công", "OK");
    }

    public MyApiResponse<UserUpdateResponse> updateUser(UserUpdateRequest request) {

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getToken())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên không được để trống");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        User existingUser = userRepository.findByToken(request.getToken());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        // Email không được trùng
        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        int age = Period.between(request.getBirthday(), LocalDate.now()).getYears();
        if (age < 9) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Người dùng phải đủ 9 tuổi");
        }

        // ===== UPDATE =====
        existingUser.setEmail(request.getEmail().trim());
        existingUser.setName(request.getName().trim());
        existingUser.setUserCode(request.getUserCode().trim());
        existingUser.setBirthday(request.getBirthday());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhone(request.getPhone());

        userRepository.save(existingUser);

        UserUpdateResponse response = UserUpdateResponse.builder()
                .email(existingUser.getEmail())
                .name(existingUser.getName())
                .userCode(existingUser.getUserCode())
                .birthday(existingUser.getBirthday())
                .address(existingUser.getAddress())
                .phone(existingUser.getPhone())
                .build();

        return MyApiResponse.success("Cập nhật người dùng thành công", response);
    }


    public MyApiResponse<UserUpdateResponse> updateReader(UserUpdateRequest request) {

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getToken())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên không được để trống");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        Reader existingUser = readerRepository.findByToken(request.getToken());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        // Email không được trùng (CHỈ TRONG READER)
        if (!existingUser.getEmail().equals(request.getEmail())
                && readerRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        int age = Period.between(request.getBirthday(), LocalDate.now()).getYears();
        if (age < 9) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Người dùng phải đủ 9 tuổi");
        }

        // ===== UPDATE =====
        existingUser.setEmail(request.getEmail().trim());
        existingUser.setName(request.getName().trim());
        existingUser.setUserCode(request.getUserCode().trim());
        existingUser.setBirthday(request.getBirthday());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhone(request.getPhone());

        readerRepository.save(existingUser);

        UserUpdateResponse response = UserUpdateResponse.builder()
                .email(existingUser.getEmail())
                .name(existingUser.getName())
                .userCode(existingUser.getUserCode())
                .birthday(existingUser.getBirthday())
                .address(existingUser.getAddress())
                .phone(existingUser.getPhone())
                .build();

        return MyApiResponse.success("Cập nhật người dùng thành công", response);
    }


    public MyApiResponse<UserUpdateResponse> updateUserByUserCode(UserUpdateRequest request) {

        User existingUser = userRepository.findByUserCode(request.getUserCode());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        if (request.getEmail() != null &&
                !existingUser.getEmail().equals(request.getEmail()) &&
                userRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.FORBIDDEN, "Email đã tồn tại");
        }

        existingUser.setEmail(request.getEmail() != null ? request.getEmail() : existingUser.getEmail());
        existingUser.setName(request.getName() != null ? request.getName() : existingUser.getName());
        existingUser.setBirthday(request.getBirthday() != null ? request.getBirthday() : existingUser.getBirthday());
        existingUser.setAddress(request.getAddress() != null ? request.getAddress() : existingUser.getAddress());
        existingUser.setUserCode(request.getUserCode() != null ? request.getUserCode() : existingUser.getUserCode());
        existingUser.setPhone(request.getPhone() != null ? request.getPhone() : existingUser.getPhone());


        userRepository.save(existingUser);
        UserUpdateResponse response = new UserUpdateResponse();
        response.setBirthday(existingUser.getBirthday());
        response.setName(existingUser.getName());
        response.setAddress(existingUser.getAddress());
        response.setEmail(existingUser.getEmail());
        response.setPhone(existingUser.getPhone());
        response.setUserCode(existingUser.getUserCode());
        return MyApiResponse.success("Cập nhật người dùng thành công", response);
    }

    public MyApiResponse<UserUpdateResponse> updateUserByPublicId(UserUpdateRequest request) {

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getPublicId())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "PublicId không được để trống");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên không được để trống");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        User existingUser = userRepository.findByPublicId(request.getPublicId());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        // Cho phép đổi email nhưng không trùng
        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        int age = Period.between(request.getBirthday(), LocalDate.now()).getYears();
        if (age < 9) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Người dùng phải đủ 9 tuổi!"
            );
        }

        // update
        existingUser.setEmail(request.getEmail().trim());
        existingUser.setName(request.getName().trim());
        existingUser.setUserCode(request.getUserCode().trim());
        existingUser.setBirthday(request.getBirthday());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhone(request.getPhone());

        userRepository.save(existingUser);

        UserUpdateResponse response = UserUpdateResponse.builder()
                .email(existingUser.getEmail())
                .name(existingUser.getName())
                .userCode(existingUser.getUserCode())
                .birthday(existingUser.getBirthday())
                .address(existingUser.getAddress())
                .phone(existingUser.getPhone())
                .build();

        return MyApiResponse.success("Cập nhật người dùng thành công", response);
    }



    public MyApiResponse<UserUpdateResponse> updateReaderByPublicId(UserUpdateRequest request) {

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên không được để trống");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        Reader existingUser = readerRepository.findByPublicId(request.getPublicId());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        if (!existingUser.getEmail().equals(request.getEmail())
                && readerRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Ngày sinh không được để trống"
            );
        }

        LocalDate birthday = request.getBirthday();
        int currentYear = LocalDate.now().getYear();
        int birthYear = birthday.getYear();
        int age = currentYear - birthYear;

        if (age < 9) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Người dùng phải đủ 9 tuổi!"
            );
        }

        existingUser.setEmail(request.getEmail().trim());
        existingUser.setName(request.getName().trim());
        existingUser.setUserCode(request.getUserCode().trim());
        existingUser.setBirthday(request.getBirthday());
        existingUser.setAddress(request.getAddress());
        existingUser.setPhone(request.getPhone());

        readerRepository.save(existingUser);

        UserUpdateResponse response = UserUpdateResponse.builder()
                .email(existingUser.getEmail())
                .name(existingUser.getName())
                .userCode(existingUser.getUserCode())
                .birthday(existingUser.getBirthday())
                .address(existingUser.getAddress())
                .phone(existingUser.getPhone())
                .build();

        return MyApiResponse.success("Cập nhật độc giả thành công", response);
    }


    public MyApiResponse<CreateUserResponse> createUser(CreateUserRequest request) {

        // ===== VALIDATE BASIC =====
        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên người dùng không được để trống");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Role không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        int age = LocalDate.now().getYear() - request.getBirthday().getYear();
        if (age < 9) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Người dùng phải đủ 9 tuổi mới được tạo tài khoản"
            );
        }


        List<Role> roles = roleRepository.findAllById(request.getRoleIds());

        if (roles.size() != request.getRoleIds().size()) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Một hoặc nhiều role không tồn tại"
            );
        }

        boolean isReader = roles.stream()
                .anyMatch(r -> "READER".equalsIgnoreCase(r.getRoleName()));

        if (isReader && roles.size() > 1) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Không thể phân quyền READER cùng với các role quản trị"
            );
        }

        if (isReader) {
            if (readerRepository.findByEmail(request.getEmail()) != null) {
                return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
            }
        } else {
            if (userRepository.findByEmail(request.getEmail()) != null) {
                return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
            }
        }



        // ===== COMMON DATA =====
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(randomPassword);
        String token = UUID.randomUUID().toString();
        String publicId = UUID.randomUUID().toString();

        if (isReader) {
            Role readerRole = roles.get(0);

            Reader reader = Reader.builder()
                    .name(request.getName().trim())
                    .email(request.getEmail().trim())
                    .userCode(request.getUserCode().trim())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .birthday(request.getBirthday())
                    .roleId(readerRole.getId())
                    .password(hashedPassword)
                    .token(token)
                    .status(1L)
                    .publicId(publicId)
                    .build();

            readerRepository.save(reader);
        } else {
            User user = User.builder()
                    .name(request.getName().trim())
                    .email(request.getEmail().trim())
                    .userCode(request.getUserCode().trim())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .birthday(request.getBirthday())
                    .password(hashedPassword)
                    .token(token)
                    .status(1L)
                    .publicId(publicId)
                    .build();

            userRepository.save(user);

            for (Role role : roles) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                userRoleRepository.save(ur);
            }
        }


        // ===== SEND MAIL =====
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(request.getEmail());
        message.setSubject("Thông báo");
        message.setText(
                "Chúc mừng bạn đã đăng ký tài khoản thành công.\n" +
                        "Mật khẩu của bạn là: " + randomPassword
        );
        emailSender.send(message);

        CreateUserResponse response = new CreateUserResponse(
                token,
                roles.stream()
                        .map(Role::getRoleName)
                        .toList()
        );


        return MyApiResponse.success("Tạo người dùng thành công", response);
    }


    public MyApiResponse<UserCreateResponse> createReader(UserRequest request) {

        // ===== VALIDATE BASIC =====
        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên người dùng không được để trống");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (request.getRoleId() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Role không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        int age = LocalDate.now().getYear() - request.getBirthday().getYear();
        if (age < 9) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Người dùng phải đủ 9 tuổi mới được tạo tài khoản"
            );
        }

        // ===== LẤY ROLE =====
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        // 🔥 CHỈ CHO PHÉP READER
        if (!"READER".equalsIgnoreCase(role.getRoleName())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Chỉ được phép tạo tài khoản Reader"
            );
        }

        // ===== CHECK EMAIL TRÙNG =====
        if (readerRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        // ===== COMMON DATA =====
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(randomPassword);
        String token = UUID.randomUUID().toString();
        String publicId = UUID.randomUUID().toString();

        // ===== SAVE READER =====
        Reader reader = Reader.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .userCode(request.getUserCode().trim())
                .phone(request.getPhone())
                .address(request.getAddress())
                .birthday(request.getBirthday())
                .roleId(role.getId())
                .password(hashedPassword)
                .token(token)
                .status(1L)
                .publicId(publicId)
                .build();

        readerRepository.save(reader);

        // ===== SEND MAIL =====
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(request.getEmail());
        message.setSubject("Thông báo");
        message.setText(
                "Chúc mừng bạn đã đăng ký tài khoản thành công.\n" +
                        "Mật khẩu của bạn là: " + randomPassword
        );
        emailSender.send(message);

        // ===== RESPONSE =====
        UserCreateResponse response = new UserCreateResponse(
                token,
                role.getRoleName()
        );

        return MyApiResponse.success("Tạo Reader thành công", response);
    }


    public MyApiResponse<UserCreateResponse> createReaderByUser(UserRequest request) {

        // ===== VALIDATE BASIC =====
        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }

        if (isBlank(request.getName())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Tên người dùng không được để trống");
        }

        if (isBlank(request.getEmail())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (isBlank(request.getPhone())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống");
        }

        if (!request.getPhone().matches("^(03|05|07|08|09)[0-9]{8}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Số điện thoại không đúng định dạng");
        }

        if (!request.getUserCode().matches("^[0-9]{12}$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD phải gồm đúng 12 chữ số");
        }

        if (isBlank(request.getUserCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "CCCD không được để trống");
        }

        if (request.getRoleId() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Role không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (request.getBirthday() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Ngày sinh không được để trống");
        }

        int age = LocalDate.now().getYear() - request.getBirthday().getYear();
        if (age < 9) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Người dùng phải đủ 9 tuổi mới được tạo tài khoản"
            );
        }

        // ===== LẤY ROLE =====
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        // 🔥 CHỈ CHO PHÉP READER
        if (!"READER".equalsIgnoreCase(role.getRoleName())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Chỉ được phép tạo tài khoản Reader"
            );
        }

        // ===== CHECK EMAIL TRÙNG =====
        if (readerRepository.findByEmail(request.getEmail()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        // ===== COMMON DATA =====
        String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(randomPassword);
        String token = UUID.randomUUID().toString();
        String publicId = UUID.randomUUID().toString();

        // ===== SAVE READER =====
        Reader reader = Reader.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .userCode(request.getUserCode().trim())
                .phone(request.getPhone())
                .address(request.getAddress())
                .birthday(request.getBirthday())
                .roleId(role.getId())
                .password(hashedPassword)
                .token(token)
                .status(1L)
                .publicId(publicId)
                .build();

        readerRepository.save(reader);

        // ===== SEND MAIL =====
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(request.getEmail());
        message.setSubject("Thông báo");
        message.setText(
                "Chúc mừng bạn đã đăng ký tài khoản thành công.\n" +
                        "Mật khẩu của bạn là: " + randomPassword
        );
        emailSender.send(message);

        // ===== RESPONSE =====
        UserCreateResponse response = new UserCreateResponse(
                token,
                role.getRoleName()
        );

        return MyApiResponse.success("Tạo Reader thành công", response);
    }






    public MyApiResponse<String> deleteUser(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        userRepository.delete(user);
        return MyApiResponse.success("Xóa người dùng thành công", "OK");
    }


    public MyApiResponse<String> updatePassword(UpdatePasswordRequest request) {

        if (request == null || request.getToken() == null || request.getToken().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token người dùng không hợp lệ");
        }

        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Mật khẩu cũ và mật khẩu mới là bắt buộc");
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    PasswordValidator.errorMessage()
            );
        }

        User existingUser = userRepository.findByToken(request.getToken());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            return MyApiResponse.error(HttpStatus.UNAUTHORIZED, "Mật khẩu cũ không đúng");
        }

        existingUser.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(existingUser);

        return MyApiResponse.success("Đổi mật khẩu thành công", null);
    }



    public MyApiResponse<String> updateReaderPassword(UpdatePasswordRequest request) {

        if (request == null || request.getToken() == null || request.getToken().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token người dùng không hợp lệ");
        }

        if (request.getOldPassword() == null || request.getOldPassword().isEmpty()
                || request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Mật khẩu cũ và mật khẩu mới là bắt buộc");
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    PasswordValidator.errorMessage()
            );
        }

        Reader existingUser = readerRepository.findByToken(request.getToken());
        if (existingUser == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy độc giả");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            return MyApiResponse.error(HttpStatus.UNAUTHORIZED, "Mật khẩu cũ không đúng");
        }

        existingUser.setPassword(encoder.encode(request.getNewPassword()));
        readerRepository.save(existingUser);

        return MyApiResponse.success("Đổi mật khẩu thành công", null);
    }


    public MyApiResponse<User> changeStatus(String publicId) {
        User user = userRepository.findByPublicId(publicId);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        Long currentStatus = user.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1) ? 0L : 1L;

        user.setStatus(newStatus);
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);

        String message = (newStatus == 1)
                ? "Đã bật trạng thái hiển thị cho người dùng."
                : "Đã ẩn người dùng thành công.";

        return MyApiResponse.success(message, user);
    }

    public MyApiResponse<Reader> changeStatusReader(String publicId) {
        Reader user = readerRepository.findByPublicId(publicId);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        Long currentStatus = user.getStatus();
        Long newStatus = (currentStatus != null && currentStatus == 1) ? 0L : 1L;

        user.setStatus(newStatus);
        user.setUpdatedAt(LocalDate.now());
        readerRepository.save(user);

        String message = (newStatus == 1)
                ? "Đã bật trạng thái hiển thị cho người dùng."
                : "Đã ẩn người dùng thành công.";

        return MyApiResponse.success(message, user);
    }

    public MyApiResponse<String> logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        User user = userRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        user.setToken(null);
        userRepository.save(user);

        return MyApiResponse.success("Logout thành công", "OK");
    }


    public MyApiResponse<String> readerLogout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        Reader user = readerRepository.findByToken(token);
        if (user == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng");
        }

        user.setToken(null);
        readerRepository.save(user);

        return MyApiResponse.success("Logout thành công", "OK");
    }


    public MyApiResponse<String> sendResetPasswordLinkUser(String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Email chưa được đăng ký cho tài khoản quản trị"
            );
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        user.setResetPasswordToken(token);
        user.setResetPasswordExpired(expiredAt);
        userRepository.save(user);

        String resetLink =
                "http://localhost:4200/reset-password/cms?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(email);
        message.setSubject("Reset mật khẩu tài khoản quản trị");
        message.setText(
                "Nhấn vào link sau để đặt lại mật khẩu (hết hạn sau 15 phút):\n"
                        + resetLink
        );

        emailSender.send(message);

        return MyApiResponse.success(
                "Link reset mật khẩu Quản trị đã được gửi qua email",
                null
        );
    }

    public MyApiResponse<String> resetPasswordByTokenUser(ResetPasswordRequest request) {

        User user = userRepository.findByResetToken(request.getToken());
        if (user == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        if (user.getResetPasswordExpired().isBefore(LocalDateTime.now())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token đã hết hạn");
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    PasswordValidator.errorMessage()
            );
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpired(null);
        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);

        return MyApiResponse.success("Reset mật khẩu Quản trị thành công", null);
    }



    public MyApiResponse<String> sendResetPasswordLinkReader(String email) {

        Reader reader = readerRepository.findByEmail(email);
        if (reader == null) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Email chưa được đăng ký cho tài khoản độc giả"
            );
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        reader.setResetPasswordToken(token);
        reader.setResetPasswordExpired(expiredAt);
        readerRepository.save(reader);

        String resetLink =
                "http://localhost:4200/reset-password/reader?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vinhdaumoi2805@gmail.com");
        message.setTo(email);
        message.setSubject("Reset mật khẩu tài khoản độc giả");
        message.setText(
                "Nhấn vào link sau để đặt lại mật khẩu (hết hạn sau 15 phút):\n"
                        + resetLink
        );

        emailSender.send(message);

        return MyApiResponse.success(
                "Link reset mật khẩu Độc giả đã được gửi qua email",
                null
        );
    }


    public MyApiResponse<String> resetPasswordByTokenReader(ResetPasswordRequest request) {

        Reader reader = readerRepository.findByResetToken(request.getToken());
        if (reader == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token không hợp lệ");
        }

        if (reader.getResetPasswordExpired().isBefore(LocalDateTime.now())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Token đã hết hạn");
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    PasswordValidator.errorMessage()
            );
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        reader.setPassword(encoder.encode(request.getNewPassword()));
        reader.setResetPasswordToken(null);
        reader.setResetPasswordExpired(null);
        reader.setUpdatedAt(LocalDate.now());

        readerRepository.save(reader);

        return MyApiResponse.success("Reset mật khẩu Độc giả thành công", null);
    }


    @Transactional
    public MyApiResponse<String> changeRole(ChangeRoleRequest request) {

        User user = userRepository.findByPublicId(request.getPublicId());
        if (user != null) {
            return handleUser(user, request.getTargetRoles());
        }

        Reader reader = readerRepository.findByPublicId(request.getPublicId());
        if (reader != null) {
            return handleReader(reader, request.getTargetRoles());
        }

        return MyApiResponse.error(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy tài khoản"
        );
    }
    private MyApiResponse<String> handleUser(User user, List<String> targetRoles) {

        boolean containsReader = targetRoles.stream()
                .anyMatch(r -> "READER".equalsIgnoreCase(r));

        if (containsReader && targetRoles.size() > 1) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Không thể gán vai trò ĐỘC GIẢ chung với vai trò QUẢN TRỊ"
            );
        }


        // USER → READER
        if (targetRoles.size() == 1 &&
                "READER".equalsIgnoreCase(targetRoles.get(0))) {

            if (readerRepository.countByEmail(user.getEmail()) > 0) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Email đã tồn tại ở tài khoản độc giả"
                );
            }

            userRoleRepository.deleteByUserId(user.getId());

            Reader reader = new Reader();
            copyUserToReader(user, reader);

            Role readerRole = roleRepository.findByRoleName("READER");
            reader.setRoleId(readerRole.getId());

            readerRepository.save(reader);
            userRepository.delete(user);

            return MyApiResponse.success(
                    "Chuyển QUẢN TRỊ → ĐỘC GIẢ thành công",
                    null
            );
        }

        // USER → USER (partial replace)
        List<Role> currentRoles = roleRepository.findRolesByUserId(user.getId());

        Set<String> current = currentRoles.stream()
                .map(r -> r.getRoleName().toUpperCase())
                .collect(Collectors.toSet());

        Set<String> target = targetRoles.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // remove
        Set<String> remove = new HashSet<>(current);
        remove.removeAll(target);

        // add
        Set<String> add = new HashSet<>(target);
        add.removeAll(current);

        if (!remove.isEmpty()) {
            List<Role> removeRoles =
                    roleRepository.findByRoleNameIn(new ArrayList<>(remove));

            for (Role role : removeRoles) {
                userRoleRepository.deleteByUserIdAndRoleId(
                        user.getId(),
                        role.getId()
                );
            }
        }

        if (!add.isEmpty()) {
            List<Role> addRoles =
                    roleRepository.findByRoleNameIn(new ArrayList<>(add));

            for (Role role : addRoles) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                userRoleRepository.save(ur);
            }
        }

        return MyApiResponse.success(
                "Cập nhật vai trò QUẢN TRỊ thành công",
                null
        );
    }


    private MyApiResponse<String> handleReader(Reader reader, List<String> targetRoles) {
        boolean containsReader = targetRoles.stream()
                .anyMatch(r -> "READER".equalsIgnoreCase(r));

        if (containsReader) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Vai trò ĐỘC GIẢ không thể tồn tại chung với vai trò QUẢN TRỊ"
            );
        }

        if (targetRoles.size() == 1 && "READER".equalsIgnoreCase(targetRoles.get(0))) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Tài khoản đã là ĐỘC GIẢ"
            );
        }

        // giữ rule nghiệp vụ cũ
        if (borrowedProductRepository.countByReaderId(reader.getId()) > 0
                || favouriteProductRepository.countByReaderId(reader.getId()) > 0
                || productReservationRepository.countByReaderId(reader.getId()) > 0) {

            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Tài khoản đã có dữ liệu nghiệp vụ, không thể đổi vai trò"
            );
        }

        if (userRepository.countByEmail(reader.getEmail()) > 0) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Email đã tồn tại ở tài khoản quản trị"
            );
        }

        User user = new User();
        copyReaderToUser(reader, user);
        userRepository.save(user);

        List<Role> roles = roleRepository.findByRoleNameIn(targetRoles);
        for (Role role : roles) {
            UserRole ur = new UserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(role.getId());
            userRoleRepository.save(ur);
        }

        readerRepository.delete(reader);

        return MyApiResponse.success(
                "Chuyển ĐỘC GIẢ → QUẢN TRỊ thành công",
                null
        );
    }





    private void copyUserToReader(User u, Reader r) {
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setPassword(u.getPassword());
        r.setToken(u.getToken());
        r.setUserCode(u.getUserCode());
        r.setPhone(u.getPhone());
        r.setStatus(u.getStatus());
        r.setBirthday(u.getBirthday());
        r.setAddress(u.getAddress());
        r.setPublicId(u.getPublicId());
    }

    private void copyReaderToUser(Reader r, User u) {
        u.setName(r.getName());
        u.setEmail(r.getEmail());
        u.setPassword(r.getPassword());
        u.setToken(r.getToken());
        u.setUserCode(r.getUserCode());
        u.setPhone(r.getPhone());
        u.setStatus(r.getStatus());
        u.setBirthday(r.getBirthday());
        u.setAddress(r.getAddress());
        u.setPublicId(r.getPublicId());
    }


    public boolean isAdmin(Long userId) {
        return userRepository.existsAdminRole(userId);
    }





}
