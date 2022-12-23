package com.example.st.arcgiscss.util;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileOperation {
	
	public static File sd_path= Environment.getExternalStorageDirectory();
	
	public static File createFileDir(File file){
		if(!file.exists()&&!file.isDirectory()){
			file.mkdirs();
			System.out.println("Folder created successfully");
		}else{
			System.out.println("Folder already exists");
		}
		return file;
	}
	

    public static boolean fileSaveApp(String fileName, String fileContent,
                                      Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    context.MODE_APPEND); 
            fos.write(fileContent.getBytes()); 
            fos.close(); 
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace(); 
        } catch (IOException e) {
            e.printStackTrace(); 
        } 
        return false; 
    } 


	public static boolean fileSave(String fileName, String fileContent,
                                   File path) {
		File file = new File(path, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		int count = 0;
		try {
			fos = new FileOutputStream(file);
			count = fileContent.getBytes().length;
			fos.write(fileContent.getBytes(), 0, count);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	

	public static String readFile(String fileName, Context context) {
        String str = "";
         
        try { 
            FileInputStream fis = context.openFileInput(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024]; 
            int len = 0; 
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len); 
            }
            str = new String(bos.toByteArray(), "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace(); 
        } catch (IOException e) {
            e.printStackTrace(); 
        } 
 
        return str; 
    } 
}
