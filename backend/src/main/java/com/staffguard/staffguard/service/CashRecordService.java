package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.CashRecordRequestDTO;
import com.staffguard.staffguard.dto.CashRecordResponseDTO;
import com.staffguard.staffguard.exception.UserNotFoundException;
import com.staffguard.staffguard.model.CashRecord;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.CashRecordRepository;
import com.staffguard.staffguard.repository.UserRepository;
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

    public CashRecordResponseDTO addCashRecord(CashRecordRequestDTO dto, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new UserNotFoundException("Employee not found"));

        User supervisor = null;
        if (dto.getSupervisorId() != null) {
            supervisor = userRepository.findById(dto.getSupervisorId())
                    .orElse(null);
        }

        CashRecord record = new CashRecord();
        record.setEmployee(employee);
        record.setSupervisor(supervisor);
        record.setDate(LocalDate.parse(dto.getDate()));
        record.setTimePosted(LocalTime.now()); // ✅ auto set current time
        record.setPos(dto.getPos());
        record.setTotalSales(dto.getTotalSales());
        record.setAmount(dto.getAmount());
        record.setStatus(dto.getStatus().toUpperCase());

        return toDTO(cashRecordRepository.save(record));
    }

    public List<CashRecordResponseDTO> getMyCashRecords(String email) {
        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return cashRecordRepository.findByEmployeeOrderByDateDesc(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CashRecordResponseDTO> getAllCashRecords() {
        return cashRecordRepository.findAllByOrderByDateDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CashRecordResponseDTO toDTO(CashRecord r) {
        return new CashRecordResponseDTO(
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