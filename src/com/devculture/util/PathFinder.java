package com.devculture.util;

import java.io.File;

import com.devculture.swing.PopupMessage;

public class PathFinder {
	public static final String SUBDIR_DATA = "/reports";

	// save directory for favs, etc
	private static final String USER_DIRECTORY_APPLE_REPORTS = "/Documents/AppleSalesReports";
	// by default saved dir will be in /Users/<username>/Documents/AppleSalesReports/saved
	private static final String USER_DIRECTORY_SAVED = "saved";
	
	private File localDir = null;
	private File savedDir = null;
	
	private static PathFinder self = null;
	
	private PathFinder() {
		localDir = FileManager.getCurrentDirectory();
		
		// locate saved dir, create one if it doesn't already exist
		File tmp = new File(System.getProperty("user.home") + USER_DIRECTORY_APPLE_REPORTS);
		if(!tmp.exists()) {
			if(!tmp.mkdir()) {
				PopupMessage.showErrorMessage("Failed to create user directory for Apple Reports");
				return;
			}
		}

		savedDir = new File(tmp, USER_DIRECTORY_SAVED);
		if(!savedDir.exists()) {
			if(!savedDir.mkdir()) {
				PopupMessage.showErrorMessage("Failed to create saved directory for Apple Reports");
				
				// prevent saving to random unexpected locations
				savedDir = null;
				return;
			}
		}
	}
	
	public static PathFinder getInstance() {
		if(self == null) {
			self = new PathFinder();
		}
		return self;
	}
	
	public String getSubdirCanonicalPath(String subdir) {
		String path = null;
		try {
			path = localDir.getCanonicalPath() + subdir;
		} catch(Exception ex) {
			PopupMessage.showErrorMessage("Failed to grab subdirectory path from " + subdir);
		}
		return path;
	}
	
	public String getSavedDirectoryCanonicalPath() {
		String path = null;
		try {
			path = savedDir.getCanonicalPath();
		} catch(Exception ex) {
			PopupMessage.showErrorMessage("Failed to grab subdirectory path from " + USER_DIRECTORY_SAVED);
		}
		return path;
	}
	
}
