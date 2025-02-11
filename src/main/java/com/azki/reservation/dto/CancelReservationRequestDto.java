package com.azki.reservation.dto;

import com.azki.reservation.constant.CancellationReason;
import lombok.Data;

import java.io.Serializable;

@Data
public class CancelReservationRequestDto implements Serializable {
    private CancellationReason reason;
    private String description;
}
