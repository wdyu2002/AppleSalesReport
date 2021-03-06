package com.devculture.util;

import java.awt.Color;
import javax.swing.ImageIcon;

public class Utilities {
	
	public static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = Utilities.class.getResource(path);
		if(imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	public static int getSingleCharByteValue(char low) {
		if(low >= '0' && low <= '9') {
			return low - '0';
		} else if(low >= 'a' && low <= 'f') {
			return (low - 'a') + 10;
		}
	
		System.err.println(low + " is not a valid hex-character");
		return 0;
	}
	
	public static int getDoubleCharByteValue(char high, char low) {
		return getSingleCharByteValue(high) * 16 + getSingleCharByteValue(low);
	}
	
	public static Color getColorFromString(String color) {
		// make all lower-case
		color = color.toLowerCase();
		
		if(color.startsWith("0x") && color.length() <= 10) {
			char dst[] = new char[8];
			int index = dst.length - (color.length() - 2);
			
			if(index < dst.length) {
				// fill color chars into dst
				color.getChars(2, color.length(), dst, index);
				// fill empty spaces with char values
				for(int i=0; i<index; i++) {
					dst[i] = i<2 ? 'f' : '0';
				}
				// generate color
				int a = getDoubleCharByteValue(dst[0], dst[1]);
				int r = getDoubleCharByteValue(dst[2], dst[3]);
				int g = getDoubleCharByteValue(dst[4], dst[5]);
				int b = getDoubleCharByteValue(dst[6], dst[7]);
				return new Color(r, g, b, a);
			}
		}
		
		System.err.println(color + " is not a valid color string");
		return Color.black;
	}
	
}
