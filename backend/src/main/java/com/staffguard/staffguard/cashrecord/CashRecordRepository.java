package com.staffguard.staffguard.cashrecord;

import com.staffguard.staffguard.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CashRecordRepository extends JpaRepository<CashRecord, Long> {
    List<CashRecord> findByEmployeeOrderByDateDesc(User employee);
    List<CashRecord> findAllByOrderByDateDesc();
}