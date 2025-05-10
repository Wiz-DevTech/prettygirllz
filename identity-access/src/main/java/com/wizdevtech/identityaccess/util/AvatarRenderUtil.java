package com.wizdevtech.identityaccess.util;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for avatar rendering operations
 */
public class AvatarRenderUtil {
    private static final Logger logger = LoggerFactory.getLogger(AvatarRenderUtil.class);

    /**
     * Generates a unique filename for an avatar asset
     *
     * @param prefix The prefix for the filename
     * @param extension The file extension (without the dot)
     * @return A unique filename
     */
    public static String generateUniqueFilename(String prefix, String extension) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s.%s", prefix, uuid, extension);
    }

    /**
     * Converts a hex color string to RGB values
     *
     * @param hexColor The hex color string (e.g., "#FF5500")
     * @return An array of RGB values [r, g, b]
     */
    public static int[] hexToRgb(String hexColor) {
        if (hexColor == null || !hexColor.startsWith("#") || hexColor.length() != 7) {
            return new int[]{0, 0, 0}; // Default to black
        }

        try {
            Color color = Color.decode(hexColor);
            return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
        } catch (NumberFormatException e) {
            logger.error("Invalid hex color format: {}", hexColor);
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Ensures a directory exists, creating it if necessary
     *
     * @param dirPath The directory path to check/create
     * @return True if the directory exists or was created successfully
     */
    public static boolean ensureDirectoryExists(String dirPath) {
        Path path = Paths.get(dirPath);
        if (Files.exists(path)) {
            return Files.isDirectory(path);
        }

        try {
            Files.createDirectories(path);
            return true;
        } catch (Exception e) {
            logger.error("Failed to create directory: {}", dirPath, e);
            return false;
        }
    }

    /**
     * Calculates file size in human-readable format
     *
     * @param file The file to check
     * @return A human-readable size string (e.g., "2.5 MB")
     */
    public static String getReadableFileSize(File file) {
        if (file == null || !file.exists()) {
            return "0 B";
        }

        long bytes = file.length();
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};

        int unitIndex = 0;
        double size = bytes;
        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * Validates if a file is an image based on its extension
     *
     * @param filename The filename to check
     * @return True if the file has an image extension
     */
    public static boolean isImageFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".png") ||
                lowerFilename.endsWith(".jpg") ||
                lowerFilename.endsWith(".jpeg") ||
                lowerFilename.endsWith(".gif") ||
                lowerFilename.endsWith(".bmp");
    }
}