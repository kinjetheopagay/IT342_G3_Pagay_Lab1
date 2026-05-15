package com.staffguard.staffguard.schedule;

import com.staffguard.staffguard.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    @ManyToMany
    @JoinTable(
        name = "schedule_employees",
        joinColumns = @JoinColumn(name = "schedule_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<User> employees;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime shiftStart;

    @Column(nullable = false)
    private LocalTime shiftEnd;
}