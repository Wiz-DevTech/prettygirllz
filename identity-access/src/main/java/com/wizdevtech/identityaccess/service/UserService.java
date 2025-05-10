package com.wizdevtech.identityaccess.service;

import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long userId);

    byte[] getUserAvatar(Long userId) throws ResourceNotFoundException;

    MediaType getAvatarMediaType(Long userId);

    void updateAvatar(Long userId, MultipartFile file) throws IOException, IllegalArgumentException;

    void updateAvatar(Long userId, byte[] avatar, String mimeType) throws ResourceNotFoundException;

    Page<User> findUsers(String search, Pageable pageable);

    // If you have other methods in your implementation that should be exposed, add them here
    // For example:
    // User createUser(User user);
    // User updateUser(Long userId, User userDetails);
    // void deleteUser(Long userId);
}