package com.devculture.tools.AppleSalesReporter.Data;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.devculture.util.FileManager;
import com.devculture.util.PathFinder;

public class Favorite implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	public Favorite(String name) {
		this.name = name;
	}
	
	public void setName(String str) {
		name = str;
	}

	public String getName() {
		return name;
	}

	/** file checker **/

	public boolean isFileExist() {
		File directory = new File(PathFinder.getInstance().getSavedDirectoryCanonicalPath());
		File[] children = FileManager.getChildren(directory);
		
		for(File child : children) {
			if(child.isFile() && child.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void deleteFile() {
		File file = new File(PathFinder.getInstance().getSavedDirectoryCanonicalPath() + "/" + name);
		if(file.exists()) {
			file.delete();
		}
	}

	/** serializable **/

	public void readObject(ObjectInputStream stream) throws Exception {
		stream.defaultReadObject();
	}

	public void writeObject(ObjectOutputStream stream) throws Exception {
		stream.defaultWriteObject();
	}

}