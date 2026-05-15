package com.staffguard.staffguard.cashrecord;

import com.staffguard.staffguard.shared.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cash-records")
@CrossOrigin
public class CashRecordController {

    private final CashRecordService cashRecordService;
    private final JwtUtil jwtUtil;

    public CashRecordController(CashRecordService cashRecordService, JwtUtil jwtUtil) {
        this.cashRecordService = cashRecordService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/employee/{employeeId}")
    public CashRecordResponse addCashRecord(
            @PathVariable Long employeeId,
            @RequestBody CashRecordRequest request) {
        return cashRecordService.addCashRecord(request, employeeId);
    }

    @GetMapping("/my")
    public List<CashRecordResponse> getMyCashRecords(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return cashRecordService.getMyCashRecords(email);
    }

    @GetMapping("/all")
    public List<CashRecordResponse> getAllCashRecords() {
        return cashRecordService.getAllCashRecords();
    }
}