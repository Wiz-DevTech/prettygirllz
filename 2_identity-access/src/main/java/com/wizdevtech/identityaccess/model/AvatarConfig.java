package com.wizdevtech.identityaccess.model;

import com.wizdevtech.identityaccess.model.enums.AnimationDriver;
import com.wizdevtech.identityaccess.model.enums.AvatarType;
import com.wizdevtech.identityaccess.model.enums.RenderingEngine;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avatar_configs")
public class AvatarConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Add this field to your AvatarConfig class
    @Column(name = "status")
    private String status = "ACTIVE"; // Default value

    // Add these getter and setter methods
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "avatar_type")
    @Enumerated(EnumType.STRING)
    private AvatarType avatarType; // CARTOON, VIDEO, SIMULATED, REALISTIC, LIFELIKE

    @Column(name = "avatar_preview_url")
    private String avatarPreviewUrl;

    @Column(name = "version")
    private Integer version = 1;

    // Facial features
    @Column(name = "face_shape")
    private String faceShape;

    @Column(name = "skin_tone")
    private String skinTone;

    @Column(name = "hair_style")
    private String hairStyle;

    @Column(name = "hair_color")
    private String hairColor;

    @Column(name = "eye_color")
    private String eyeColor;

    // Body features
    @Column(name = "body_type")
    private String bodyType;

    @Column(name = "height")
    private Double height;

    // Clothing
    @Column(name = "clothing_style")
    private String clothingStyle;

    // Animation preferences
    @Column(name = "animation_driver")
    @Enumerated(EnumType.STRING)
    private AnimationDriver animationDriver; // FACE, BODY, VOICE, GESTURE

    // Rendering settings
    @Column(name = "rendering_engine")
    @Enumerated(EnumType.STRING)
    private RenderingEngine renderingEngine; // CUBIC, UNITY, UNREAL

    // Additional settings stored as JSON
    @Column(name = "additional_settings", columnDefinition = "json")
    private String additionalSettings;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public AvatarConfig() {
        this.version = 1;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AvatarType getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(AvatarType avatarType) {
        this.avatarType = avatarType;
    }

    public String getAvatarPreviewUrl() {
        return avatarPreviewUrl;
    }

    public void setAvatarPreviewUrl(String avatarPreviewUrl) {
        this.avatarPreviewUrl = avatarPreviewUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFaceShape() {
        return faceShape;
    }

    public void setFaceShape(String faceShape) {
        this.faceShape = faceShape;
    }

    public String getSkinTone() {
        return skinTone;
    }

    public void setSkinTone(String skinTone) {
        this.skinTone = skinTone;
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(String hairStyle) {
        this.hairStyle = hairStyle;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getClothingStyle() {
        return clothingStyle;
    }

    public void setClothingStyle(String clothingStyle) {
        this.clothingStyle = clothingStyle;
    }

    public AnimationDriver getAnimationDriver() {
        return animationDriver;
    }

    public void setAnimationDriver(AnimationDriver animationDriver) {
        this.animationDriver = animationDriver;
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }

    public String getAdditionalSettings() {
        return additionalSettings;
    }

    public void setAdditionalSettings(String additionalSettings) {
        this.additionalSettings = additionalSettings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}