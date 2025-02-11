package com.azki.reservation.repository;

import com.azki.reservation.entity.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {

    @Query(value = "SELECT * FROM available_slots " +
            "WHERE CAST(start_time AS DATE) = :startDate", nativeQuery = true)
    List<AvailableSlot> findAllByStartDate(@Param("startDate") LocalDate startDate);

    List<AvailableSlot> findAllByReservedIsFalseAndStartTimeBetween(LocalDateTime fromStartTime, LocalDateTime endStartTime);


    @Modifying
    @Query(value = "UPDATE available_slots " +
            "SET is_reserved = 1, version = version + 1 " +
            "WHERE start_time = :startTime and version  = :version", nativeQuery = true)
    int updateAvailableSlot(@Param("startTime") LocalDateTime startTime, @Param("version") int version);

    @Modifying
    @Query(value = "UPDATE available_slots " +
            "SET is_reserved = 0 " +
            "WHERE start_time = :startTime", nativeQuery = true)
    int updateAvailableSlot(LocalDateTime startTime);
}
