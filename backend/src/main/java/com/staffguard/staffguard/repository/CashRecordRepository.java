package com.staffguard.staffguard.repository;

import com.staffguard.staffguard.model.CashRecord;
import com.staffguard.staffguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CashRecordRepository extends JpaRepository<CashRecord, Long> {
    List<CashRecord> findByEmployeeOrderByDateDesc(User employee);
    List<CashRecord> findAllByOrderByDateDesc();
}