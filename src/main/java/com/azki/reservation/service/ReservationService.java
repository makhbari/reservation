package com.azki.reservation.service;

import com.azki.reservation.constant.ErrorsStatus;
import com.azki.reservation.constant.ReservationStatus;
import com.azki.reservation.data.AvailableSlotInfo;
import com.azki.reservation.dto.CancelReservationRequestDto;
import com.azki.reservation.dto.CreateReservationRequestDto;
import com.azki.reservation.entity.CancelReservation;
import com.azki.reservation.entity.Reservation;
import com.azki.reservation.entity.User;
import com.azki.reservation.exception.InvalidUserException;
import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.repository.CancelReservationRepository;
import com.azki.reservation.repository.ReservationRepository;
import com.azki.reservation.util.DateUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ReservationService {
    private final JmsTemplate jmsTemplate;
    private final UserService userService;
    private final SlotService slotService;
    private final UserReservationService userReservationService;
    private final ReservationRepository reservationRepository;
    private final CancelReservationRepository cancelReservationRepository;

    public ReservationService(JmsTemplate jmsTemplate, ReservationRepository reservationRepository,
                              UserService userService, SlotService slotService, UserReservationService userReservationService, CancelReservationRepository cancelReservationRepository) {
        this.jmsTemplate = jmsTemplate;
        this.userReservationService = userReservationService;
        this.userService = userService;
        this.slotService = slotService;
        this.reservationRepository = reservationRepository;
        this.cancelReservationRepository = cancelReservationRepository;
    }

    public void createReservation(CreateReservationRequestDto request) {
        jmsTemplate.convertAndSend("ReservationEventQueue", request);
    }

    @JmsListener(destination = "ReservationEventQueue")
    public void handleQueueMessage(CreateReservationRequestDto request) throws InvalidUserException, IOException, ParseException, NotFoundException {
        log.info("start reservation process on request info: {}", request);

        User user = userService.findUser(request.getUserId());

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start time of reservation must be after today!");
        }

        AvailableSlotInfo availableSlotInfo = slotService.findClosestSlot(DateUtil.formatLocalDateTime(request.getStartTime()));
        if (availableSlotInfo == null) {
            throw new NotFoundException(ErrorsStatus.AVAILABLE_SLOT_NOT_FOUND.getCode(), ErrorsStatus.AVAILABLE_SLOT_NOT_FOUND.getMessage());
        } else {
            userReservationService.saveUserReservation(user, request, availableSlotInfo);
        }
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public boolean cancelReservation(Long reservationId, CancelReservationRequestDto requestDto) throws NotFoundException, IOException, ParseException {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty()) {
            throw new NotFoundException(ErrorsStatus.RESERVATION_NOT_FOUND.getCode(), ErrorsStatus.RESERVATION_NOT_FOUND.getMessage());
        }

        Reservation reservation = reservationOptional.get();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        CancelReservation cancelReservation = new CancelReservation();
//        cancelReservation.setId(1L);
        cancelReservation.setUser(reservation.getUser());
        cancelReservation.setReservation(reservation);
        cancelReservation.setCancellationReason(requestDto.getReason());
        cancelReservation.setDescription(requestDto.getDescription());
        cancelReservation.setCancellationTime(LocalDateTime.now());
        cancelReservationRepository.save(cancelReservation);


        slotService.updateAvailableSlotInDB(reservation.getStartTime());

        AvailableSlotInfo slotInfo = new AvailableSlotInfo();
        slotInfo.setReserved(false);
        slotInfo.setStartTime(DateUtil.formatLocalDateTime(reservation.getStartTime()));
        slotInfo.setEndTime(DateUtil.formatLocalDateTime(reservation.getEndTime()));
        slotInfo.setVersion(0);
        slotService.storeAvailableSlotInfoInCache(slotInfo);

        return true;
    }
}

