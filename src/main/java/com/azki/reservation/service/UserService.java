package com.azki.reservation.service;

import com.azki.reservation.constant.ErrorsStatus;
import com.azki.reservation.entity.User;
import com.azki.reservation.exception.InvalidUserException;
import com.azki.reservation.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(long userId) throws InvalidUserException {
        return userRepository.findById(userId).orElseThrow(() -> new InvalidUserException(ErrorsStatus.USER_ID_NOT_FOUND.getCode(), ErrorsStatus.USER_ID_NOT_FOUND.getMessage()));
    }
}
