package com.example.library_management_api.service;

import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.BorrowItemRequest;
import com.example.library_management_api.request.BorrowRequest;
import com.example.library_management_api.response.BorrowResponse;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.ReturnResponse;
import com.example.library_management_api.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class BorrowedProductService {
    @Autowired
    BorrowedProductRepository borrowedProductRepository;

    @Autowired
    BorrowedFeeRepository borrowedFeeRepository;

    @Autowired
    BlackListRepository blackListRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    ReaderRepository readerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BlackListService blackListService;


    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    public MyApiResponse<List<BorrowResponse>> createBorrow(BorrowRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Email không đúng định dạng");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Danh sách sách mượn không được rỗng");
        }

        Reader reader = readerRepository.findByEmail(request.getEmail());
        if (reader == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Email không tồn tại trong hệ thống");
        }

        if (blackListRepository.findByReaderId(reader.getId()) != null) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Độc giả đang nằm trong Blacklist, không được phép mượn sách"
            );
        }

        if (isReaderBlockedByOverdue(reader.getId())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Độc giả có sách quá hạn chưa trả quá 7 ngày – không thể tạo phiếu mượn"
            );
        }

        if (isReaderBorrowingLimit(reader.getId())) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Độc giả đang mượn tối đa 3 sách – không thể mượn thêm"
            );
        }

        if (borrowedProductRepository.countPendingReservation(reader.getId()) >= 3) {
            return MyApiResponse.error(
                    HttpStatus.FORBIDDEN,
                    "Độc giả đã đăng ký 3 đơn mượn sách – không thể tạo phiếu mượn"
            );
        }

        List<BorrowResponse> responses = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (BorrowItemRequest item : request.getItems()) {

            if (item.getDueDate() == null) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Ngày trả không được để trống cho từng sách"
                );
            }

            if (item.getDueDate().isBefore(today)) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Ngày trả phải >= ngày hiện tại"
                );
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Số lượng mượn phải lớn hơn 0"
                );
            }

            Product product = productRepository.findByHashId(item.getHashId());
            if (product == null) {
                return MyApiResponse.error(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sách với mã: " + item.getHashId()
                );
            }

            if (item.getQuantity() > product.getQuantity()) {
                return MyApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Sách " + product.getProductName() +
                                " chỉ còn " + product.getQuantity() + " quyển"
                );
            }

            // Trừ kho
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            BorrowedProduct bp = new BorrowedProduct();
            bp.setBorrowDate(today);
            bp.setDueDate(item.getDueDate());
            bp.setReturnDate(null);
            bp.setQuantity(item.getQuantity().longValue());
            bp.setProductId(product.getId());
            bp.setReaderId(reader.getId());
            bp.setIsLost(0L);
            bp.setStatus(3L);
            borrowedProductRepository.save(bp);

            BorrowedFee fee = new BorrowedFee();
            fee.setBorrowedProductId(bp.getId());
            fee.setLateFee(0L);
            fee.setCompensationFee(0L);
            fee.setTotalFee(0L);
            fee.setHasLostItemFeePaid(0L);
            fee.setHasOverdueFeePaid(0L);
            fee.setCreatedAt(today);
            borrowedFeeRepository.save(fee);

            BorrowResponse response = new BorrowResponse();
            response.setBorrowedProductId(bp.getId());
            response.setProductName(product.getProductName());
            response.setUserName(reader.getName());
            response.setBorrowDate(bp.getBorrowDate());
            response.setDueDate(bp.getDueDate());
            response.setQuantity(bp.getQuantity());
            response.setStatus(bp.getStatus());
            response.setIsLost(bp.getIsLost());

            responses.add(response);
        }

        blackListService.checkAndApplyBlackList(reader);

        return MyApiResponse.success("Tạo phiếu mượn thành công", responses);
    }




    public MyApiResponse<Map<String, Object>> getAllBorrow(Pageable pageable) {
        Page<BorrowedProduct> page = borrowedProductRepository.findAllBorrowedProducts(pageable);

        List<BorrowedProduct> listBorrowedProduct = page.getContent();

        if (listBorrowedProduct.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có danh sách mượn trả nào.");
        }

        List<BorrowResponse> borrowResponses = listBorrowedProduct.stream()
                .map(bp -> {

                    Product product = productRepository.findByProductId(bp.getProductId());
                    Reader user = readerRepository.findByReaderId(bp.getReaderId());

                    LocalDate dueDay = bp.getDueDate();
                    boolean overdue = false;

                    if (dueDay != null && bp.getReturnDate() == null) {
                        overdue = LocalDate.now().isAfter(dueDay);
                    }

                    BorrowResponse res = new BorrowResponse();
                    res.setBorrowedProductId(bp.getId());
                    res.setProductName(product != null ? product.getProductName() : null);
                    res.setUserName(user != null ? user.getName() : null);
                    res.setEmail(user != null ? user.getEmail(): null);
                    res.setImage(product != null ? product.getImage() : null);
                    res.setBorrowDate(bp.getBorrowDate());
                    res.setDueDate(bp.getDueDate());
                    res.setOverdue(overdue);
                    res.setQuantity(bp.getQuantity());
                    res.setIsLost(bp.getIsLost());
                    res.setStatus(bp.getStatus());
                    res.setReturnDate(bp.getReturnDate());
                    res.setCreatedAt(bp.getCreatedAt());
                    res.setUpdatedAt(bp.getUpdatedAt());

                    return res;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", borrowResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách mượn trả thành công.", result);
    }


    public MyApiResponse<Map<String, Object>> getSearchAllBorrow(String keyword,Pageable pageable) {
        Page<BorrowedProduct> page = borrowedProductRepository.searchBorrowedProduct(keyword,pageable);

        List<BorrowedProduct> listBorrowedProduct = page.getContent();

        if (listBorrowedProduct.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có danh sách mượn trả nào.");
        }

        List<BorrowResponse> borrowResponses = listBorrowedProduct.stream()
                .map(bp -> {

                    Product product = productRepository.findByProductId(bp.getProductId());
                    Reader user = readerRepository.findByReaderId(bp.getReaderId());

                    BorrowResponse res = new BorrowResponse();
                    res.setBorrowedProductId(bp.getId());
                    res.setProductName(product != null ? product.getProductName() : null);
                    res.setUserName(user != null ? user.getName() : null);
                    res.setEmail(user != null ? user.getEmail(): null);
                    res.setImage(product != null ? product.getImage() : null);
                    res.setBorrowDate(bp.getBorrowDate());
                    res.setDueDate(bp.getDueDate());
                    res.setQuantity(bp.getQuantity());
                    res.setIsLost(bp.getIsLost());
                    res.setStatus(bp.getStatus());
                    res.setReturnDate(bp.getReturnDate());
                    res.setCreatedAt(bp.getCreatedAt());
                    res.setUpdatedAt(bp.getUpdatedAt());

                    return res;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", borrowResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy danh sách mượn trả thành công.", result);
    }





    public MyApiResponse<ReturnResponse> getBorrowedProductById(Long borrowedProductId) {

        BorrowedProduct bp = borrowedProductRepository.findBorrowedProductById(borrowedProductId);

        if (bp == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu");
        }

        if (bp.getStatus() != 3L && bp.getStatus() != 4L) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể xem chi tiết khi đơn đang hoặc đã được độc giả mượn hoặc trả"
            );
        }

        Product product = productRepository.findByProductId(bp.getProductId());
        Reader user = readerRepository.findByReaderId(bp.getReaderId());
        BorrowedFee fee = borrowedFeeRepository.findByBorrowedProductId(borrowedProductId);

        LocalDate now = LocalDate.now();
        long daysLate = 0;

        if (bp.getDueDate() != null && now.isAfter(bp.getDueDate())) {
            daysLate = ChronoUnit.DAYS.between(bp.getDueDate(), now);
        }

        ReturnResponse returnResponse = new ReturnResponse();
        returnResponse.setProductName(product.getProductName());
        returnResponse.setUserName(user.getName());
        returnResponse.setUserCode(user.getUserCode());
        returnResponse.setBorrowDate(bp.getBorrowDate());
        returnResponse.setReturnDate(bp.getReturnDate());
        returnResponse.setDueDate(bp.getDueDate());
        returnResponse.setStatus(bp.getStatus());
        returnResponse.setIsLost(bp.getIsLost());
        returnResponse.setQuantity(bp.getQuantity());
        returnResponse.setLateFee(fee.getLateFee());
        returnResponse.setCompensationFee(fee.getCompensationFee());
        returnResponse.setHasOverdueFeePaid(fee.getHasOverdueFeePaid());
        returnResponse.setHasLostItemFeePaid(fee.getHasLostItemFeePaid());
        returnResponse.setLateDay(daysLate);

        return MyApiResponse.success("Lấy dữ liệu thành công", returnResponse);
    }


    public MyApiResponse<Map<String, Object>> searchByDateRange(
            String type, LocalDate start, LocalDate end, Pageable pageable
    ) {

        Page<BorrowedProduct> page =
                borrowedProductRepository.searchByDateRange(type, start, end, pageable);

        List<BorrowedProduct> listBorrowedProduct = page.getContent();

        if (listBorrowedProduct.isEmpty()) {
            return MyApiResponse.error(HttpStatus.NO_CONTENT, "Không có danh sách mượn trả nào.");
        }

        List<BorrowResponse> borrowResponses = listBorrowedProduct.stream()
                .map(bp -> {

                    Product product = productRepository.findByProductId(bp.getProductId());
                    Reader user = readerRepository.findByReaderId(bp.getReaderId());

                    BorrowResponse res = new BorrowResponse();
                    res.setBorrowedProductId(bp.getId());
                    res.setProductName(product != null ? product.getProductName() : null);
                    res.setUserName(user != null ? user.getName() : null);
                    res.setEmail(user != null ? user.getEmail(): null);
                    res.setImage(product != null ? product.getImage() : null);
                    res.setBorrowDate(bp.getBorrowDate());
                    res.setDueDate(bp.getDueDate());
                    res.setQuantity(bp.getQuantity());
                    res.setIsLost(bp.getIsLost());
                    res.setStatus(bp.getStatus());
                    res.setReturnDate(bp.getReturnDate());
                    res.setCreatedAt(bp.getCreatedAt());
                    res.setUpdatedAt(bp.getUpdatedAt());

                    return res;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", borrowResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Lấy data thành công",result);
    }

    private boolean isReaderBlockedByOverdue(Long readerId) {
        return borrowedProductRepository.countOverdueMoreThan7Days(readerId) > 0;
    }


    private boolean isReaderBorrowingLimit(Long readerId) {
        return borrowedProductRepository.countBorrowingBooks(readerId) >= 3;
    }


}
