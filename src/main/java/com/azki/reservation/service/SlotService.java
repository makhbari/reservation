package com.azki.reservation.service;

import com.azki.reservation.data.AvailableSlotInfo;
import com.azki.reservation.entity.AvailableSlot;
import com.azki.reservation.repository.AvailableSlotRepository;
import com.azki.reservation.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.azki.reservation.util.DateUtil.dateTimeToSeconds;

@Service
@Slf4j
public class SlotService {
    private static final String CACHE_KEY = "availableSlots";
    private final AvailableSlotRepository availableSlotRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public SlotService(AvailableSlotRepository availableSlotRepository, ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        this.availableSlotRepository = availableSlotRepository;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initAvailableSlotsCache() {
        List<AvailableSlot> availableSlots = availableSlotRepository.findAllByReservedIsFalseAndStartTimeBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7));
        log.info("available slots count is : " + availableSlots.size());

        availableSlots.forEach(availableSlot -> {
            try {
                storeAvailableSlotInfoInCache(getSlotInfo(availableSlot));
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private AvailableSlotInfo getSlotInfo(AvailableSlot availableSlot) {
        return new AvailableSlotInfo(DateUtil.formatLocalDateTime(availableSlot.getStartTime()),
                DateUtil.formatLocalDateTime(availableSlot.getEndTime()), availableSlot.isReserved(), availableSlot.getVersion());
    }

    public void storeAvailableSlotInfoInCache(AvailableSlotInfo slotInfo) throws IOException, ParseException {
        long eventTimeInSeconds = dateTimeToSeconds(slotInfo.getStartTime());

        String slotInfoJson = objectMapper.writeValueAsString(slotInfo);

        redisTemplate.opsForZSet().add(CACHE_KEY, slotInfoJson, eventTimeInSeconds);
    }

    public AvailableSlotInfo findClosestSlot(String targetDateTime) throws IOException, ParseException {
        long targetTimeInSeconds = dateTimeToSeconds(targetDateTime);

        // جستجو برای slotهایی که در بازه زمانی 24 ساعت قبل و بعد از تاریخ ورودی قرار دارند
        Set<ZSetOperations.TypedTuple<String>> closestSlot = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(CACHE_KEY, targetTimeInSeconds - (24 * 3600), targetTimeInSeconds + (24 * 3600));

        org.springframework.data.redis.core.ZSetOperations.TypedTuple<String> closestSlotTuple = closestSlot.stream()
                .min((entry1, entry2) -> {
                    // محاسبه فاصله زمانی از تاریخ ورودی
                    long diff1 = Math.abs(entry1.getScore().longValue() - targetTimeInSeconds);
                    long diff2 = Math.abs(entry2.getScore().longValue() - targetTimeInSeconds);
                    return Long.compare(diff1, diff2);
                })
                .orElse(null);

        if (closestSlotTuple != null) {
            return objectMapper.readValue(closestSlotTuple.getValue(), AvailableSlotInfo.class);
        }

        return null;
    }

    @Transactional
    public int updateAvailableSlotInDB(LocalDateTime startTime, int version) {
        return availableSlotRepository.updateAvailableSlot(startTime, version);
    }

    @Transactional
    public int updateAvailableSlotInDB(LocalDateTime startTime) {
        return availableSlotRepository.updateAvailableSlot(startTime);
    }

    public void updateSlotInfoInCache(AvailableSlotInfo slot) throws IOException, ParseException {
        long eventTimeInSeconds = dateTimeToSeconds(slot.getStartTime());

        String jsonEvent = objectMapper.writeValueAsString(slot);
        redisTemplate.opsForZSet().remove(CACHE_KEY, jsonEvent);
        redisTemplate.opsForZSet().add(CACHE_KEY, jsonEvent, eventTimeInSeconds);
    }
}
