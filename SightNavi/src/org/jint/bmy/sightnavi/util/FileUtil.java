package org.jint.bmy.sightnavi.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jintian
 *
 */
public class FileUtil {
	
	public static boolean writeFile(String fromPath, String toPath, int bufSize) throws IOException {
		boolean status = false;
		FileInputStream fis = null;
		FileOutputStream fos = null;

		File parentFile = new File(toPath).getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		try {
			fis = new FileInputStream(fromPath);
			fos = new FileOutputStream(toPath);

			byte[] buf = new byte[bufSize];
			int bufLength = 0;
			while ((bufLength = fis.read(buf)) != -1) {
				fos.write(buf, 0, bufLength);
				fos.flush();
			}

			status = true;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}

		return status;
	}
	
	public static boolean writeFile(InputStream fromStream, String toPath,
			int bufSize, boolean closeStream) throws IOException {
		boolean status = false;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		File parentFile = new File(toPath).getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		try {
			bis = new BufferedInputStream(fromStream);
			bos = new BufferedOutputStream(new FileOutputStream(toPath));

			byte[] buf = new byte[bufSize];
			int bufLength = 0;
			while ((bufLength = bis.read(buf)) != -1) {
				bos.write(buf, 0, bufLength);
				bos.flush();
			}

			status = true;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				
				if (bis != null && closeStream) {
					bis.close();
				} else {
					bis.skip(0);
				}
			} catch (IOException e) {
				throw e;
			}
		}

		return status;
	}

	public static String changeFileName(String fileName, String addition) {
		String tempFileName = fileName;
		
		try{
			String name = tempFileName.substring(0, tempFileName.lastIndexOf("."));
			String postfix = tempFileName.substring(tempFileName.lastIndexOf("."),
					tempFileName.length());
			tempFileName = name + "_" + addition + postfix;
		}catch(StringIndexOutOfBoundsException ex){
			tempFileName = fileName + "_" + addition;
		}
		

		return tempFileName;
	}
	
	public static boolean appendDataToFile(byte[] data, String toPath) throws IOException {
		boolean status = false;
		
		FileOutputStream fos = null;

		File parentFile = new File(toPath).getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		try {
			fos = new FileOutputStream(toPath, true);
			fos.write(data);
			status = true;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}

		return status;
	}
	
	public static boolean moveFile(String from, String to){
		File fromFile = new File(from);
		File toFile = new File(to);
		
		if(!fromFile.exists()){
			return false;
		}
		
		toFile.getParentFile().mkdirs();
		
		try {
			FileInputStream inStream = new FileInputStream(fromFile);
			FileOutputStream outStream = new FileOutputStream(toFile);
			
			byte[] buf = new byte[8192];
			int len = 0;
			while((len = inStream.read(buf)) != -1 ){
				outStream.write(buf, 0, len);
			}
			
			outStream.close();
			inStream.close();
			
			fromFile.deleteOnExit();
			
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	public static String getFileNameFromPath(String path){
		String name = path.substring(path.lastIndexOf("/")+1);
		return name;
	}
	
	public static boolean isFileExist(String path){
		File file = new File(path);
		boolean result = file.exists();
		return result;
	}

}
