package com.example.library_management_api.service;


import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.BlackListRequest;
import com.example.library_management_api.request.UpdateBlackListRequest;
import com.example.library_management_api.response.BlackListResponse;
import com.example.library_management_api.response.MyApiResponse;
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
public class BlackListService {
    @Autowired
    BlackListRepository blackListRepository;

    @Autowired
    BorrowedProductRepository borrowedProductRepository;

    @Autowired
    BorrowedFeeRepository borrowedFeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReaderRepository readerRepository;

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    //Kiểm tra và thêm người dùng vào Blacklist nếu vi phạm các quy định.
    public void checkAndApplyBlackList(Reader user) {
        if (user == null) return;
        // Đã trong blacklist thì bỏ qua
        if (blackListRepository.findByReaderId(user.getId()) != null) return;

        Long lostTimes = borrowedProductRepository.countLost(user.getId());
        Long overdueTimes = borrowedProductRepository.countOverdueReturns(user.getId());

        //Mất sách >= 5 lần
        if (lostTimes >= 5) {
            addBlackList(user, "Làm mất sách quá 5 lần");
            return;
        }

        //Trả quá hạn >= 10 lần
        if (overdueTimes >= 10) {
            addBlackList(user, "Trả sách quá hạn hơn 10 lần");
            return;
        }

        //Không trả tiền đền sách > 7 ngày (tính từ ngày ghi nhận mất - ReturnDate)
        List<BorrowedProduct> list = borrowedProductRepository.findByReaderId(user.getId());
        LocalDate now = LocalDate.now();
        for (BorrowedProduct bp : list) {
            BorrowedFee fee = borrowedFeeRepository.findByBorrowedProductId(bp.getId());
            // Chỉ kiểm tra những phiếu có phí đền bù và phí chưa được trả
            if (fee != null &&
                    fee.getCompensationFee() != null &&
                    fee.getCompensationFee() > 0 &&
                    fee.getHasLostItemFeePaid() == 0) {

                // Cần phải có ReturnDate (ngày ghi nhận mất) để tính mốc 7 ngày
                if (bp.getReturnDate() != null) {
                    long daysLate = ChronoUnit.DAYS.between(bp.getDueDate(), now);

                    if (daysLate > 7) {
                        addBlackList(user, "Không trả phí làm mất trong hơn 7 ngày");
                        return;
                    }
                }
            }
        }
    }

    private void addBlackList(Reader user, String reason) {
        BlackList bl = new BlackList();
        bl.setReaderId(user.getId());
        bl.setReason(reason);
        blackListRepository.save(bl);
    }


    public MyApiResponse<Map<String, Object>> getAllBlackList(Pageable pageable) {
        Page<BlackList> page = blackListRepository.findAllBlackList(pageable);
        List<BlackList> blackList = page.getContent();

        if(blackList.isEmpty()){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc có ai nằm trong black list cả");
        }

        List<BlackListResponse> blackListResponses = blackList.stream().map(
                bl -> {
                    Reader user = readerRepository.findByReaderId(bl.getReaderId());
                    BlackListResponse blackListResponse = new BlackListResponse();
                    blackListResponse.setUserName(user.getName());
                    blackListResponse.setEmail(user.getEmail());
                    blackListResponse.setUserCode(user.getUserCode());
                    blackListResponse.setReason(bl.getReason());
                    return blackListResponse;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", blackListResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách black list thành công", result);
    }

    public MyApiResponse<Map<String, Object>> getSearchAllBlackList(String keyword,Pageable pageable) {
        Page<BlackList> page = blackListRepository.searchBlacklistedReader(keyword,pageable);
        List<BlackList> blackList = page.getContent();

        if(blackList.isEmpty()){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc có ai nằm trong black list cả");
        }

        List<BlackListResponse> blackListResponses = blackList.stream().map(
                        bl -> {
                            Reader user = readerRepository.findByReaderId(bl.getReaderId());
                            BlackListResponse blackListResponse = new BlackListResponse();
                            blackListResponse.setUserName(user.getName());
                            blackListResponse.setEmail(user.getEmail());
                            blackListResponse.setUserCode(user.getUserCode());
                            blackListResponse.setReason(bl.getReason());
                            return blackListResponse;
                        })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", blackListResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách black list thành công", result);
    }

    public MyApiResponse<BlackListResponse> createBlackList(BlackListRequest request){

        if (isInvalidEmail(request.getEmail())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Email không hợp lệ"
            );
        }

        Reader user = readerRepository.findByEmail(request.getEmail());
        if(user == null){
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy người dùng nào"
            );
        }

        BlackList bl = new BlackList();
        bl.setReaderId(user.getId());
        bl.setReason(request.getReason());
        blackListRepository.save(bl);

        BlackListResponse response = new BlackListResponse();
        response.setUserName(user.getName());
        response.setEmail(request.getEmail());
        response.setUserCode(user.getUserCode());
        response.setReason(request.getReason());

        return MyApiResponse.success(
                "Đã thêm người dùng vào blacklist thành công",
                response
        );
    }


    public MyApiResponse<BlackListResponse> updateBlackList(BlackListRequest request){

        if (isInvalidEmail(request.getEmail())) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Email không hợp lệ"
            );
        }

        Reader user = readerRepository.findByEmail(request.getEmail());
        if(user == null){
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy người dùng này"
            );
        }

        BlackList bl = blackListRepository.findByReaderId(user.getId());
        if(bl == null){
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Người dùng này không nằm trong blacklist"
            );
        }

        bl.setReason(request.getReason());
        blackListRepository.save(bl);

        BlackListResponse response = new BlackListResponse();
        response.setUserName(user.getName());
        response.setEmail(request.getEmail());
        response.setUserCode(user.getUserCode());
        response.setReason(request.getReason());

        return MyApiResponse.success("Cập nhật thành công", response);
    }



    public MyApiResponse<String> deleteBlackList(String email){

        if (isInvalidEmail(email)) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Email không hợp lệ"
            );
        }

        Reader user = readerRepository.findByEmail(email);
        if(user == null){
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy người dùng này"
            );
        }

        BlackList blackList = blackListRepository.findByReaderId(user.getId());
        if(blackList == null){
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Người dùng này không nằm trong blacklist"
            );
        }

        blackListRepository.delete(blackList);
        return MyApiResponse.success("Xoá thành công", null);
    }


    private boolean isInvalidEmail(String email) {
        return email == null || email.trim().isEmpty()
                || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }


}
