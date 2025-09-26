package com.coffee_and_code.sql_learning.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility to generate a simple SQL icon if the image file is missing
 */
public class IconGenerator {

    public static Image createDefaultIcon() {
        WritableImage icon = new WritableImage(64, 64);
        PixelWriter writer = icon.getPixelWriter();

        // Create a simple blue background with "SQL" text-like pattern
        Color blue = Color.web("#2563eb");
        Color white = Color.WHITE;

        // Fill background
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                writer.setColor(x, y, blue);
            }
        }

        // Simple "SQL" pattern (very basic)
        // This is just a placeholder - you should use a proper icon
        for (int x = 16; x < 48; x++) {
            for (int y = 28; y < 36; y++) {
                writer.setColor(x, y, white);
            }
        }

        return icon;
    }
}