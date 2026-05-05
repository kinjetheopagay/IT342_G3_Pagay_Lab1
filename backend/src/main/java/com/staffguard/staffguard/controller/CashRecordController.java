package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.CashRecordRequestDTO;
import com.staffguard.staffguard.dto.CashRecordResponseDTO;
import com.staffguard.staffguard.service.CashRecordService;
import com.staffguard.staffguard.util.JwtUtil;
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

    // Admin adds cash record
    @PostMapping("/employee/{employeeId}")
    public CashRecordResponseDTO addCashRecord(
            @PathVariable Long employeeId,
            @RequestBody CashRecordRequestDTO dto) {
        return cashRecordService.addCashRecord(dto, employeeId);
    }

    // Employee views their cash records
    @GetMapping("/my")
    public List<CashRecordResponseDTO> getMyCashRecords(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return cashRecordService.getMyCashRecords(email);
    }

    // Admin views all cash records
    @GetMapping("/all")
    public List<CashRecordResponseDTO> getAllCashRecords() {
        return cashRecordService.getAllCashRecords();
    }
}