package com.example.library_management_api.service;


import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.ReturnProductRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.ReturnResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReturnProductService {
    @Autowired
    BorrowedProductRepository borrowedProductRepository;

    @Autowired
    BorrowedFeeRepository borrowedFeeRepository;

    @Autowired
    BlackListRepository blackListRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BlackListService blacklistService;

    @Autowired
    ReaderRepository readerRepository;

    @Autowired
    ProductReservationRepository productReservationRepository;

    private static final long LATE_FEE_PER_DAY = 5000L;
    private static final long MAX_LATE_DAYS_BEFORE_LOST = 15L; // Quá hạn 15 ngày = làm mất


    @Transactional
    public MyApiResponse<ReturnResponse> returnBook(ReturnProductRequest request) {

        BorrowedProduct bp = borrowedProductRepository.findBorrowedProductById(request.getBorrowedProductId());

        if (bp == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu mượn");
        }


        if (bp.getStatus() == 0L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,"Phiếu mượn này đã bị huỷ");
        }

        // ❌ đã hoàn tất
        if (bp.getStatus() == 4L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Phiếu mượn đã hoàn tất");
        }

        // ❌ chưa nhận sách mà đòi trả
        if (bp.getStatus() != 3L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Phiếu mượn chưa được mượn");
        }


        // ❌ đã báo mất trước đó
        if (bp.getIsLost() == 1L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Phiếu mượn đã báo mất");
        }

        Product product = productRepository.findByProductId(bp.getProductId());
        BorrowedFee fee = borrowedFeeRepository.findByBorrowedProductId(bp.getId());

        LocalDate now = LocalDate.now();

        long daysLate = 0;

        if (bp.getDueDate() != null && now.isAfter(bp.getDueDate())) {
            daysLate = ChronoUnit.DAYS.between(bp.getDueDate(), now);
        }

        boolean isLostByRequest = Boolean.TRUE.equals(request.getIsLost());
        boolean isLostByOverdue = daysLate > MAX_LATE_DAYS_BEFORE_LOST;

        // ================= MẤT SÁCH =================
        if (isLostByRequest || isLostByOverdue) {

            bp.setIsLost(1L);
            bp.setReturnDate(now);

            fee.setCompensationFee(product.getPrice().longValue() * bp.getQuantity());
            fee.setLateFee(0L);

            fee.setHasLostItemFeePaid(0L);
            fee.setHasOverdueFeePaid(0L);

//            bp.setStatus(0L); // chờ thanh toán phí
        }

        // ================= TRẢ SÁCH =================
        else {

            bp.setReturnDate(now);
            bp.setIsLost(0L);

            fee.setCompensationFee(0L);
            fee.setHasLostItemFeePaid(0L);

            if (daysLate > 0) {

                fee.setLateFee(daysLate * LATE_FEE_PER_DAY);
                fee.setHasOverdueFeePaid(0L);

//                bp.setStatus(0L); // chờ thanh toán phí trễ

            } else {

                fee.setLateFee(0L);
                fee.setHasOverdueFeePaid(1L);

                bp.setStatus(4L); // hoàn tất luôn
            }

            // ✅ cộng lại kho khi trả sách
            product.setQuantity(product.getQuantity() + bp.getQuantity());
            product.setUpdatedAt(now);
            productRepository.save(product);
        }

        fee.setUpdatedAt(now);

        borrowedProductRepository.save(bp);
        borrowedFeeRepository.save(fee);

        Reader user = readerRepository.findByReaderId(bp.getReaderId());

        ReturnResponse res = new ReturnResponse();
        res.setProductName(product.getProductName());
        res.setUserName(user.getName());
        res.setBorrowDate(bp.getBorrowDate());
        res.setReturnDate(bp.getReturnDate());
        res.setDueDate(bp.getDueDate());
        res.setStatus(bp.getStatus());
        res.setIsLost(bp.getIsLost());
        res.setQuantity(bp.getQuantity());
        res.setLateFee(fee.getLateFee());
        res.setCompensationFee(fee.getCompensationFee());
        res.setHasOverdueFeePaid(fee.getHasOverdueFeePaid());
        res.setHasLostItemFeePaid(fee.getHasLostItemFeePaid());
        res.setLateDay(daysLate);

        blacklistService.checkAndApplyBlackList(user);

        return MyApiResponse.success("Trả sách thành công", res);
    }
}
