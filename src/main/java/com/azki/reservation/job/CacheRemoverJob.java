package com.azki.reservation.job;

import com.azki.reservation.service.SlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class CacheRemoverJob {

    private final SlotService slotService;

    public CacheRemoverJob(SlotService slotService) {
        this.slotService = slotService;
    }

    @Scheduled(cron = "${info.available.slot.cache.remove.job.cron:-}", zone = "Asia/Tehran")
    public void removeAvailableSlotCache() throws ParseException {
        log.info("removeAvailableSlotCache starting ...");
        slotService.removeAvailableSlotCache(LocalDateTime.now());
        log.info("removeAvailableSlotCache end ...");
    }
}
