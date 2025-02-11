package com.azki.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "available_slots")
public class AvailableSlot implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_reserved", nullable = false)
    private boolean reserved;

    @Version
    @Column(name = "version")
    private int version;
}
