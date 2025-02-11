package com.azki.reservation.controller;

import com.azki.reservation.dto.CancelReservationRequestDto;
import com.azki.reservation.dto.CreateReservationRequestDto;
import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;


@RestController
@RequestMapping(BaseApi.API_PATH_PREFIX_V1 + "/reservations")
public class ReservationController extends BaseApi {

    private final ReservationService reservationService;

    public ReservationController(ReservationService service) {
        this.reservationService = service;
    }

    @PostMapping("/")
    public ResponseEntity<String> createReservation(@RequestBody CreateReservationRequestDto requestDto) {
        reservationService.createReservation(requestDto);
        return ResponseEntity.ok("Reservation created successfully.");
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, @RequestBody CancelReservationRequestDto requestDto) throws NotFoundException, IOException, ParseException {
        boolean isCancelled = reservationService.cancelReservation(reservationId, requestDto);
        if (isCancelled) {
            return ResponseEntity.ok("Reservation cancelled successfully.");
        } else {
            return ResponseEntity.status(404).body("Reservation not found.");
        }
    }
}
