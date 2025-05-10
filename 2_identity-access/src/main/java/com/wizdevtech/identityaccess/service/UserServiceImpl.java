// src/main/java/com/wizdevtech/identityaccess/service/UserServiceImpl.java
package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.exception.ResourceNotFoundException;
import com.wizdevtech.identityaccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public byte[] getUserAvatar(Long userId) throws ResourceNotFoundException {
        return userRepository.findById(userId)
                .map(User::getAvatar)
                .orElseThrow(() -> new ResourceNotFoundException("Avatar not found for user: " + userId));
    }

    @Override
    public MediaType getAvatarMediaType(Long userId) {
        return userRepository.findById(userId)
                .map(user -> MediaType.parseMediaType(user.getAvatarMimeType()))
                .orElse(MediaType.IMAGE_JPEG);
    }

    @Override
    @Transactional
    public void updateAvatar(Long userId, MultipartFile file) throws IOException, IllegalArgumentException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }
        updateAvatar(userId, file.getBytes(), file.getContentType());
    }

    @Override
    @Transactional
    public void updateAvatar(Long userId, byte[] avatar, String mimeType) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setAvatar(avatar);
        user.setAvatarMimeType(mimeType);
        userRepository.save(user);
    }

    @Override
    public Page<User> findUsers(String search, Pageable pageable) {
        if (search == null || search.isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByEmailContainingIgnoreCase(search, pageable);
    }
}