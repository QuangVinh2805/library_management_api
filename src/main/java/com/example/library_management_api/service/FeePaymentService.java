package com.example.library_management_api.service;

import com.example.library_management_api.models.BorrowedFee;
import com.example.library_management_api.models.BorrowedProduct;
import com.example.library_management_api.models.Reader;
import com.example.library_management_api.models.User;
import com.example.library_management_api.repository.BorrowedFeeRepository;
import com.example.library_management_api.repository.BorrowedProductRepository;
import com.example.library_management_api.repository.ReaderRepository;
import com.example.library_management_api.repository.UserRepository;
import com.example.library_management_api.request.FeePaymentRequest;
import com.example.library_management_api.response.MyApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class FeePaymentService {
    @Autowired
    BorrowedProductRepository borrowedProductRepository;
    @Autowired
    BorrowedFeeRepository borrowedFeeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlackListService blacklistService;
    @Autowired
    ReaderRepository readerRepository;

    public MyApiResponse<String> recordPayment(FeePaymentRequest request) {

        BorrowedProduct bp = borrowedProductRepository
                .findBorrowedProductById(request.getBorrowedProductId());

        if (bp == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu mượn");
        }

        // ❌ đã hoàn tất rồi
        if (bp.getStatus() == 4L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Phiếu mượn đã hoàn tất.");
        }

        BorrowedFee fee = borrowedFeeRepository.findByBorrowedProductId(bp.getId());

        if (fee == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND,
                    "Không tìm thấy thông tin phí mượn");
        }

        // ❌ chưa trả sách mà đòi thanh toán
        if (bp.getReturnDate() == null && bp.getIsLost() == 0L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Phiếu mượn chưa được trả, không thể thanh toán");
        }

        // ❌ không có phí
        if (fee.getLateFee() == 0L && fee.getCompensationFee() == 0L) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Không phát sinh phí để thanh toán");
        }

        // ===== GHI NHẬN THANH TOÁN =====

        if (fee.getLateFee() > 0 && fee.getHasOverdueFeePaid() == 0L) {
            fee.setHasOverdueFeePaid(1L);
        }

        if (fee.getCompensationFee() > 0 && fee.getHasLostItemFeePaid() == 0L) {
            fee.setHasLostItemFeePaid(1L);
        }

        boolean allFeesPaid =
                (fee.getLateFee() == 0L || fee.getHasOverdueFeePaid() == 1L) &&
                        (fee.getCompensationFee() == 0L || fee.getHasLostItemFeePaid() == 1L);

        // ✅ ĐÃ THANH TOÁN HẾT → HOÀN TẤT
        if (allFeesPaid) {
            bp.setStatus(4L);
        }

        fee.setUpdatedAt(LocalDate.now());

        borrowedFeeRepository.save(fee);
        borrowedProductRepository.save(bp);

        Reader user = readerRepository.findByReaderId(bp.getReaderId());
        blacklistService.checkAndApplyBlackList(user);

        String message = "Ghi nhận thanh toán thành công.";
        if (bp.getStatus() == 4L) {
            message += " Phiếu mượn đã hoàn tất.";
        }

        return MyApiResponse.success(message, null);
    }

}
