package com.niel.code.Tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;

import com.niel.code.Util.FileControl;
import com.niel.code.widget.MD5Structure;

public class FileManager {
	private static final String TAG = "FILEMANAGER";
	private static boolean D = true;
	private static final String PATH = "/storage/sdcard1/apk/games/";
	private static final String SDCARD_ROOT = "/sdcard";
	private static Context mContext;
	
	public FileManager(Context context) {
		mContext = context;
	}
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static ArrayList<HashMap<String, String>> getMountStorage() {
		ArrayList<HashMap<String, String>> mMountPaths = new ArrayList<HashMap<String,String>>();
		File[] files = new File(PATH).listFiles();
		HashMap<String, String> filesMap;
        int j = 0;
        for(int i=0; i<files.length; i++) {
        	filesMap = new HashMap<String, String>();
        	filesMap.put("name", files[i].getName());
            filesMap.put("path", files[i].getPath());
            mMountPaths.add(filesMap);
            if(D) Log.d(TAG, "INFO : Message "+mMountPaths.get(j).toString());
            j++;
        }
        return mMountPaths;
	}
	
	public static ArrayList<HashMap<String, String>> getFolderList(String path) {
		if( (path == null) || (path.length() == 0) ) return new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String, String>> mMountPaths = new ArrayList<HashMap<String,String>>();
		File[] files = new File(path).listFiles();
		if(files.length == 0) return new ArrayList<HashMap<String,String>>();
		HashMap<String, String> filesMap;
        for(File f : files) {
        	if(f.isDirectory()) {
        		filesMap = new HashMap<String, String>();
            	filesMap.put("name", f.getName());
                filesMap.put("path", f.getPath());
                mMountPaths.add(filesMap);
        	}
        }
        return mMountPaths;
	}
	
	public static ArrayList<HashMap<String, String>> getMP4List(String path) {
		if( (path == null) || (path.length() == 0) ) return new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> mFolderPaths = new ArrayList<HashMap<String, String>>();
		File file  = new File(path);
		if(file.length() == 0) return new ArrayList<HashMap<String, String>>();
		for(File f : file.listFiles()) {
			if(f.getName().lastIndexOf(".mp4" ) != -1) {
				String mp4 = f.getPath();
				HashMap<String, String> item = new HashMap<String, String>();
				String[] split = path.split("/");
				String fileName = split[split.length - 1].split("\\.")[0];
				item.put("name", f.getName());
				item.put("path", mp4);
				mFolderPaths.add(item);
			}
		}
		return mFolderPaths;
	}
	
	public static ArrayList<String> getPhotoList(String path) {
		if( (path == null) || (path.length() == 0) ) return new ArrayList<String>();
		ArrayList<String> mFolderPaths = new ArrayList<String>();
		File   file  = new File(path);
		File[] files = file.listFiles();
		for(File f : files) {
			if( (f.getName().lastIndexOf(".png" ) != -1) || (f.getName().lastIndexOf(".jpg" ) != -1) ) {
				String photo = f.getPath();
				mFolderPaths.add(photo);
			}
		}
		return mFolderPaths;
	}
	
	private static ArrayList<HashMap<String, String>> increaseBackPath(String search_path, ArrayList<HashMap<String, String>> folder) {
		ArrayList<HashMap<String, String>> tempFolder = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> root_Path = new HashMap<String, String>();
		root_Path.put("path", parseBackPath(search_path));
		root_Path.put("name", "..");
		tempFolder.add(root_Path);
		for(HashMap<String, String> list : folder) {
			tempFolder.add(list);
		}
		return tempFolder;
	}
	
	private static String parseBackPath(String search_path) {
		if(search_path == null) return SDCARD_ROOT;
		StringBuilder combination_path = new StringBuilder();
		if(search_path.equals(SDCARD_ROOT)) {
			combination_path.append(SDCARD_ROOT);
		} else {
			String[] split = search_path.split("/");
			for(int i = 0; i < split.length; i++) {
				if(i >= split.length -1) {
					break;
				} else {
					if( (split[i] == null) || (split[i].equals("")) ) {
						continue;
					}
					combination_path.append("/"+split[i]);
				}
			}
		}
		return combination_path.toString();
	}
	
	public static ArrayList<MD5Structure> getList(String search_path) {
		ArrayList<MD5Structure> mp4 = new ArrayList<MD5Structure>();
		ArrayList<HashMap<String, String>> folder = null;
		ArrayList<HashMap<String, String>> file = null;
		if(search_path == null) {
			folder = FileManager.getFolderList(SDCARD_ROOT);
			file = FileManager.getMP4List(SDCARD_ROOT);
		} else {
			folder = FileManager.getFolderList(search_path);
			file = FileManager.getMP4List(search_path);
		}
		
		if (file.size() != 0) {
			for (HashMap<String, String> map : file) {
				folder.add(map);
			}
		}
		Collections.sort(folder, new Comparator<HashMap<String, String>>() {

			@Override
			public int compare(HashMap<String, String> entry1,
					HashMap<String, String> entry2) {
				return entry1.get("name").compareTo(entry2.get("name"));
			}
		});
		ArrayList<HashMap<String, String>> temp_Folder = increaseBackPath(search_path, folder);
		folder.clear(); folder = (ArrayList<HashMap<String, String>>) temp_Folder.clone();
		for(HashMap<String, String> item : folder) {
			String path = null;
			String fileName = null;
			for (Entry<String, String> entry : item.entrySet()) {
				if (entry.getKey().contains("path")) path = entry.getValue();
				if (entry.getKey().contains("name")) fileName = entry.getValue();
			}
			String md5 = null;
			try {
				md5 = FileControl.readFile(path.split("\\.")[0] + ".xml") == null ? null
						: FileControl.readFile(path.split("\\.")[0] + ".xml");
			} catch (IOException e) {
				md5 = null;
			}
			MD5Structure md5struectue = new MD5Structure(path, fileName, md5, md5 == null ? "0" : "100");
			if(new File(path).isDirectory()) {
				md5struectue.setType(MD5Structure.TYPE_FOLDER);
			} else {
				md5struectue.setType(MD5Structure.TYPE_FILE);
			}
			mp4.add(md5struectue);
		}
		return mp4;
	}
}