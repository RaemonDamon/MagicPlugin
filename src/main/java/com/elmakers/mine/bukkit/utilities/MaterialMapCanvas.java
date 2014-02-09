package com.elmakers.mine.bukkit.utilities;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class MaterialMapCanvas implements MapCanvas {

	public static int CANVAS_WIDTH = 128;
	public static int CANVAS_HEIGHT = 128;
	
	private static MapCursorCollection emptyCursors = new MapCursorCollection();
	private byte[] pixels = new byte[128 * 128];
	private Map<Byte, DyeColor> dyeColors = new HashMap<Byte, DyeColor>();
	
	public MapView getMapView() {
		return null;
	}

	public MapCursorCollection getCursors() {
		return emptyCursors;
	}

	public void setCursors(MapCursorCollection cursors) {
		// .. Nothing.
	}

	@SuppressWarnings("deprecation")
	public void setPixel(int x, int y, byte color) {
		if (x < 0 || y < 0 || x > CANVAS_WIDTH || y > CANVAS_HEIGHT) return;

		pixels[x + y * CANVAS_WIDTH] = color;
		
		// Map colors in advance.
		if (color != MapPalette.TRANSPARENT && !dyeColors.containsKey(color)) {
			java.awt.Color mapColor = MapPalette.getColor(color);
			Color targetColor = Color.fromRGB(mapColor.getRed(), mapColor.getGreen(), mapColor.getBlue());
			
			// Find best dyeColor
			DyeColor bestDyeColor = null;
			Double bestDistance = null;
			for (DyeColor testDyeColor : DyeColor.values()) {
				Color testColor = testDyeColor.getColor();
				double testDistance = getDistance(testColor, targetColor);
				if (bestDistance == null || testDistance < bestDistance) {
					bestDistance = testDistance;
					bestDyeColor = testDyeColor;
					if (testDistance == 0) break;
				}
			}
			
			dyeColors.put(color, bestDyeColor);
		}
	}
	
	private static double getDistance(Color c1, Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }

	public byte getPixel(int x, int y) {
		if (x < 0 || y < 0 || x > CANVAS_WIDTH || y > CANVAS_HEIGHT) return 0;
		
		return pixels[x + y * CANVAS_WIDTH];
	}
	
	@SuppressWarnings("deprecation")
	public DyeColor getDyeColor(int x, int y) {
		byte color = getPixel(x, y);
		if (color == MapPalette.TRANSPARENT) return null;
		if (!dyeColors.containsKey(color)) return null;
		
		return dyeColors.get(color);
	}

	public byte getBasePixel(int x, int y) {
		return 0;
	}

	// Shamelessly stolen from CraftMapCanvas.... wish they'd give us
	// an extendible version or just let us create them at least :)
	@SuppressWarnings("deprecation")
	public void drawImage(int x, int y, Image image) {
		byte[] bytes = MapPalette.imageToBytes(image);
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                setPixel(x + x2, y + y2, bytes[y2 * image.getWidth(null) + x2]);
            }
        }
	}

	@SuppressWarnings("deprecation")
	public void drawText(int x, int y, MapFont font, String text) {
		int xStart = x;
        byte color = MapPalette.DARK_GRAY;
        if (!font.isValid(text)) {
            throw new IllegalArgumentException("text contains invalid characters");
        }

        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += font.getHeight() + 1;
                continue;
            } else if (ch == '\u00A7') {
                int j = text.indexOf(';', i);
                if (j >= 0) {
                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    }
                    catch (NumberFormatException ex) {}
                }
            }

            CharacterSprite sprite = font.getChar(text.charAt(i));
            for (int r = 0; r < font.getHeight(); ++r) {
                for (int c = 0; c < sprite.getWidth(); ++c) {
                    if (sprite.get(r, c)) {
                        setPixel(x + c, y + r, color);
                    }
                }
            }
            x += sprite.getWidth() + 1;
        }
	}

}