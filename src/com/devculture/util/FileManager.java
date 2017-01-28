package com.devculture.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileManager {
	
	public static File getCurrentDirectory() {
		return new File(".");
	}
	
	public static boolean isDirectory(File directory) {
		return directory.isDirectory();
	}
	
	public static boolean hasChildren(File directory) {
		return directory.list() != null;
	}
	
	public static File[] getChildren(File directory) {
		return directory.listFiles();
	}
	
	public static File[] getChildrenFiltered(File directory) {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// file names must start with S_D_ and end with file extension .txt
				return name.startsWith("S_D_") && name.endsWith(".txt");
			}
		};
		return directory.listFiles(filter);
	}
	
}
