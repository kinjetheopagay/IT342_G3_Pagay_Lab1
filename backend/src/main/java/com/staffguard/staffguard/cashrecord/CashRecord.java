package com.staffguard.staffguard.cashrecord;

import com.staffguard.staffguard.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cash_records")
public class CashRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime timePosted;
    private String pos;
    private Double totalSales;
    private Double amount;

    @Column(nullable = false)
    private String status;
}