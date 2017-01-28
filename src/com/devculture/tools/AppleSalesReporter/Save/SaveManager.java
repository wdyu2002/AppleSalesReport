package com.devculture.tools.AppleSalesReporter.Save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.devculture.tools.AppleSalesReporter.Data.Favorite;
import com.devculture.tools.AppleSalesReporter.Data.ReportData;
import com.devculture.util.PathFinder;

public class SaveManager {
	
	/** variables **/
	
	private final String FAVORITE_LISTING = "/savedlisting.ser";
	private static SaveManager self = null;
	private Vector<Favorite> favorites = null;
	
	/** singleton **/
	
	private SaveManager() {
		favorites = new Vector<Favorite>();
	}
	
	public static SaveManager getInstance() {
		if(self == null) {
			self = new SaveManager();
		}
		return self;
	}
	
	/** methods **/
	
	public Vector<Favorite> getFavorites() {
		return favorites;
	}
	
	public void initSaveDirectory() {
		File file = new File(PathFinder.getInstance().getSavedDirectoryCanonicalPath());
		if(!file.exists()) {
			file.mkdir();
		}
	}
	
	public boolean isFavoritesFileExist(String filename) throws Exception {
		// load favorites -> fav vector
		loadFavorites();
		
		// check if filename is in fav vector
		for(Favorite favorite : favorites) {
			if(favorite.getName().equalsIgnoreCase(filename)) {
				return favorite.isFileExist();
			}
		}
		
		return false;
	}
	
	public void loadFavorites() throws Exception {
		// save fav vector -> serialize -> file
		FileInputStream listStream = null;
		ObjectInputStream listFile = null;

		try {
			File file = new File(PathFinder.getInstance().getSavedDirectoryCanonicalPath() + FAVORITE_LISTING);
			if(file.exists()) {
				listStream = new FileInputStream(file);
				listFile = new ObjectInputStream(listStream);
				
				// clean favorites array
				favorites.removeAllElements();
				int size = listFile.readInt();
				
				// load data into favorites
				for(int i=0; i<size; i++) {
					Favorite favorite = (Favorite)listFile.readObject();
					// make sure file actually exists in directory
					if(favorite.isFileExist()) {
						favorites.add(favorite);
					} else {
						// we don't have to do anything here
						// because files that do not actually exist
						// will simply not make it onto the
						// vector, thereby not posing a thread
						// when we save the favorites vector
					}
				}			
			}
		} finally {
			// clean
			if(listFile != null) {
				listFile.close();
			}
			
			if(listStream != null) {
				listStream.close();
			}
		}
	}
	
	public void deleteFavorite(Favorite favorite) throws Exception {
		// check if fav exist
		if(favorite.isFileExist()) {
			// delete actual saved file
			favorite.deleteFile();
		}
		
		// delete favorites from fav vector
		favorites.remove(favorite);
		
		// re-save fav vector
		serializeFavoritesVector();
	}
	
	public void serializeFavoritesVector() throws Exception {
		// save fav vector -> serialize -> file
		FileOutputStream listStream = null;
		ObjectOutputStream listFile = null;
		
		try {
			// do not append to file data
			listStream = new FileOutputStream(PathFinder.getInstance().getSavedDirectoryCanonicalPath() + FAVORITE_LISTING, false);
			listFile = new ObjectOutputStream(listStream);
			listFile.writeInt(favorites.size());

			// save each favorites object
			for(Favorite favorite : favorites) {
				listFile.writeObject(favorite);
			}
		} finally {
			// clean
			if(listFile != null) {
				listFile.close();
			}
			
			if(listStream != null) {
				listStream.close();
			}
		}
	}
	
	public boolean saveQueryAs(String filename, ReportData dataRef) throws Exception {
		// we can assume this will not be a duplicate filename
		// if this is not the case, the old file will simply be overwritten
		FileOutputStream dataStream = null;
		ObjectOutputStream dataFile = null;
		boolean success = false;
		
		try {
			File file = new File(PathFinder.getInstance().getSavedDirectoryCanonicalPath() + "/" + filename);
			if(!file.exists()) {
				file.createNewFile();
			}
			
			dataStream = new FileOutputStream(file, false);
			dataFile = new ObjectOutputStream(dataStream);
			dataRef.saveInfixFilters(dataFile);
			
			// save to fav vector
			favorites.add(new Favorite(filename));
			
			// save fav vector -> file
			serializeFavoritesVector();
			
			success = true;
		} finally {
			// clean
			if(dataStream != null) {
				dataStream.close();
			}
			
			if(dataFile != null) {
				dataFile.close();
			}
		}
		
		return success;
	}
	
	public boolean loadQuery(String filename, ReportData dataRef) throws Exception {
		FileInputStream dataStream = null;
		ObjectInputStream dataFile = null;
		boolean success = false;
		
		try {
			// make sure saved file exists first
			if(isFavoritesFileExist(filename)) {
				dataStream = new FileInputStream(PathFinder.getInstance().getSavedDirectoryCanonicalPath() + "/" + filename);
				dataFile = new ObjectInputStream(dataStream);
				dataRef.loadInfixFilters(dataFile);
				success = true;
			}
		} finally {
			// clean
			if(dataStream != null) {
				dataStream.close();
			}
			
			if(dataFile != null) {
				dataFile.close();
			}
		}
		
		return success;
	}

}
