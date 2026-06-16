package com.rksdev.personallearningos.user.repository;

import com.rksdev.personallearningos.user.model.PasswordResetTokenEntity;
import com.rksdev.personallearningos.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);
}