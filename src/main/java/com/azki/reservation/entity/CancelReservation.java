package com.azki.reservation.entity;

import com.azki.reservation.constant.CancellationReason;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CancelReservation")
public class CancelReservation implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservationId", referencedColumnName = "id")
    private Reservation reservation;

    @Column(name = "cancellation_time", nullable = false)
    private LocalDateTime cancellationTime;

    @Column(name = "cancellation_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private CancellationReason cancellationReason;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
