package com.azki.reservation.service;

import com.azki.reservation.constant.ReservationStatus;
import com.azki.reservation.data.AvailableSlotInfo;
import com.azki.reservation.dto.CreateReservationRequestDto;
import com.azki.reservation.entity.Reservation;
import com.azki.reservation.entity.User;
import com.azki.reservation.repository.ReservationRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
@Slf4j
public class UserReservationService {
    private final SlotService slotService;
    private final ReservationRepository reservationRepository;

    public UserReservationService(SlotService slotService, ReservationRepository reservationRepository) {
        this.slotService = slotService;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveUserReservation(User user, CreateReservationRequestDto requestDto, AvailableSlotInfo slotInfo) throws IOException, ParseException {
        try {
            int updatedRowsCount = slotService.updateAvailableSlotInDB(requestDto.getStartTime(), slotInfo.getVersion());
            if (updatedRowsCount == 0) {
                log.info("reservation time: {} has been reserved for another user.", requestDto.getStartTime());
                throw new OptimisticLockException("Reservation time has been reserved for another user.");
            }
        } catch (Exception ex) {
            log.error("exception occurred in updateAvailableSlotInDB with error:{}", ex.getMessage(), ex);
            throw ex;
        }
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setStartTime(requestDto.getStartTime());
        reservation.setEndTime(requestDto.getEndTime());
        reservationRepository.save(reservation);

        slotInfo.setReserved(true);
        slotInfo.setVersion(slotInfo.getVersion() + 1);
        slotService.updateSlotInfoInCache(slotInfo);
    }
}
