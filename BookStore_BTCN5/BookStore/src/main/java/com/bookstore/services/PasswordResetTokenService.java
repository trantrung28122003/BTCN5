package com.bookstore.services;

import com.bookstore.entity.PasswordResetToken;
import com.bookstore.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public void createPasswordResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.deleteByEmail(passwordResetToken.getEmail());
        passwordResetTokenRepository.save(passwordResetToken);
    }
    @Transactional(readOnly = true)
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
    @Transactional(readOnly = true)
    public PasswordResetToken findByEmail(String email) {
        return passwordResetTokenRepository.findByEmail(email);
    }
    @Transactional
    public void deleteByEmail(String email) {
        passwordResetTokenRepository.deleteByEmail(email);
    }
}
