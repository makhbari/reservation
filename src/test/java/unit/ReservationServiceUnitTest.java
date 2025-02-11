package unit;

import com.azki.reservation.constant.ErrorsStatus;
import com.azki.reservation.data.AvailableSlotInfo;
import com.azki.reservation.dto.CreateReservationRequestDto;
import com.azki.reservation.entity.User;
import com.azki.reservation.exception.InvalidUserException;
import com.azki.reservation.exception.NotFoundException;
import com.azki.reservation.service.ReservationService;
import com.azki.reservation.service.SlotService;
import com.azki.reservation.service.UserReservationService;
import com.azki.reservation.service.UserService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private SlotService slotService;
    @Mock
    private UserReservationService userReservationService;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void handleQueueMessage_UserNotFound_InvalidUserExceptionIsThrown() throws InvalidUserException {
        Mockito.when(userService.findUser(anyLong())).thenThrow(new InvalidUserException(ErrorsStatus.USER_ID_NOT_FOUND.getCode(), ErrorsStatus.USER_ID_NOT_FOUND.getMessage()));
        assertThrows(InvalidUserException.class, () -> reservationService.handleQueueMessage(getCreateReservationRequestDto(LocalDateTime.now())));
    }

    @Test
    public void handleQueueMessage_ReservationTimeBeforeToday_ValidationExceptionIsThrown() throws InvalidUserException {
        Mockito.when(userService.findUser(anyLong())).thenReturn(new User());
        assertThrows(ValidationException.class, () -> reservationService.handleQueueMessage(getCreateReservationRequestDto(LocalDateTime.now().minusDays(1))));
    }

    @Test
    public void handleQueueMessage_NoAvailableSlot_NotFoundExceptionIsThrown() throws InvalidUserException, IOException, ParseException {
        Mockito.when(userService.findUser(anyLong())).thenReturn(new User());
        Mockito.when(slotService.findClosestSlot(anyString())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> reservationService.handleQueueMessage(getCreateReservationRequestDto(LocalDateTime.now().plusHours(1))));
    }

    @Test
    public void handleQueueMessage_SlotIsAvailable_NormalResponse() throws InvalidUserException, IOException, ParseException, NotFoundException {
        User user = new User();
        Mockito.when(userService.findUser(anyLong())).thenReturn(user);
        AvailableSlotInfo availableSlot = new AvailableSlotInfo();
        Mockito.when(slotService.findClosestSlot(anyString())).thenReturn(availableSlot);
        CreateReservationRequestDto requestDto = getCreateReservationRequestDto(LocalDateTime.now().plusHours(1));
        reservationService.handleQueueMessage(requestDto);
        verify(userReservationService, times(1)).saveUserReservation(user, requestDto, availableSlot);
    }

    private CreateReservationRequestDto getCreateReservationRequestDto(LocalDateTime starTime) {
        CreateReservationRequestDto requestDto = new CreateReservationRequestDto();
        requestDto.setUserId(1);
        requestDto.setStartTime(starTime);
        requestDto.setEndTime(starTime.plusHours(1));
        return requestDto;
    }
}
