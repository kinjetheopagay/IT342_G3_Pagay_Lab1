package com.staffguard.staffguard.cashrecord;

import com.staffguard.staffguard.user.User;
import com.staffguard.staffguard.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashRecordService {

    private final CashRecordRepository cashRecordRepository;
    private final UserRepository userRepository;

    public CashRecordService(CashRecordRepository cashRecordRepository,
                              UserRepository userRepository) {
        this.cashRecordRepository = cashRecordRepository;
        this.userRepository = userRepository;
    }

    public CashRecordResponse addCashRecord(CashRecordRequest request, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        User supervisor = null;
        if (request.getSupervisorId() != null) {
            supervisor = userRepository.findById(request.getSupervisorId()).orElse(null);
        }

        CashRecord record = new CashRecord();
        record.setEmployee(employee);
        record.setSupervisor(supervisor);
        record.setDate(LocalDate.parse(request.getDate()));
        record.setTimePosted(LocalTime.now());
        record.setPos(request.getPos());
        record.setTotalSales(request.getTotalSales());
        record.setAmount(request.getAmount());
        record.setStatus(request.getStatus().toUpperCase());

        return toResponse(cashRecordRepository.save(record));
    }

    public List<CashRecordResponse> getMyCashRecords(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cashRecordRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CashRecordResponse> getAllCashRecords() {
        return cashRecordRepository.findAllByOrderByDateDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CashRecordResponse toResponse(CashRecord r) {
        return new CashRecordResponse(
                r.getId(),
                r.getEmployee().getName(),
                r.getSupervisor() != null ? r.getSupervisor().getName() : null,
                r.getDate().toString(),
                r.getTimePosted() != null ? r.getTimePosted().toString().substring(0, 5) : null,
                r.getPos(),
                r.getTotalSales(),
                r.getAmount(),
                r.getStatus()
        );
    }
}