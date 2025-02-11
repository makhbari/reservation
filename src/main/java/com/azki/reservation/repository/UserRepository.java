package com.azki.reservation.repository;

import com.azki.reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface UserRepository extends JpaRepository<User, Long> {
}
