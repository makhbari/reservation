package com.azki.reservation.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotInfo implements Serializable {
    private String startTime;
    private String endTime;
    private boolean reserved;
    private int version;
}
