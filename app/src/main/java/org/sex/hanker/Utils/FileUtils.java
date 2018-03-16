package org.sex.hanker.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;


public class FileUtils {
	/**
	 * sd卡的根目录                                                                    
	 */                                 
	private static String mSdRootPath = Environment.getExternalStorageDirectory().toString()+BundleTag.PicSavePath;
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */


	public FileUtils(Context context){

		mDataRootPath = context.getCacheDir().toString()+BundleTag.PicSavePath;
	}


	/**
	 * 获取储存Image的目录
	 * @return
	 */
	private static String getStorageDirectory(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath : mDataRootPath ;
//		return  (Environment.getExternalStorageDirectory().toString()
//				+ "/" + "zzImageg");
	}
	
	/**

	 cacheDir = StorageUtils.getOwnCacheDirectory(
	 getApplicationContext(), "/BiYing/Cache"); * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * @param fileName 
	 * @param bitmap   
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException{
		if(bitmap == null){
			return;
		}
//		  File dirFile = new File(getStorageDirectory());
//	        if(!dirFile.exists()){
//	            dirFile.mkdir();
//	        }
//	        File myCaptureFile = new File(getStorageDirectory() + File.separator + fileName);
//	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//	        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//	        bos.flush();
//	        bos.close();
		String path = getStorageDirectory();
		File   folderFile = new File(path);
		  try {  
			  if(!folderFile.exists()){
					folderFile.mkdir();
				}
	        } catch (Exception e) {  
	  
	        } 
		  
			File  f = new File(path + "/" + fileName);
			if (!f.exists()) {

				try {
					f.createNewFile();
					// 将数据库的文件创建在SD卡中
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 没有该文件则创建
			}
		FileOutputStream fos = new FileOutputStream(f);
		bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
		fos.flush();
		fos.close();
	}
	
	/**
	 * 从手机或者sd卡获取Bitmap
	 * @param fileName
	 * @return
	 */
	public Bitmap getBitmap(String fileName){
		  BitmapFactory.Options options = new BitmapFactory.Options();
          int be = (int)(options.outHeight / (float)200);  
          LogTools.e("be"+options.outHeight, ""+be);
          Bitmap d = null;
          if (be > 0)  {
              be = 1;  
          options.inSampleSize = be;
        d= BitmapFactory.decodeFile(getStorageDirectory() + "/" + fileName,options);
          }
          else{
        	d=  BitmapFactory.decodeFile(getStorageDirectory() + "/" + fileName/*,options*/);  
          }
          return d;
//		BitmapFactory.Options newOpts = new BitmapFactory.Options(); 
//		//开始读入图片，此时把options.inJustDecodeBounds 设回true了 
//		newOpts.inJustDecodeBounds = true; 
//		Bitmap bitmap = BitmapFactory.decodeFile(getStorageDirectory() + "/" + fileName,newOpts);//此时返回bm为空 
//
//		newOpts.inJustDecodeBounds = false; 
//		int w = newOpts.outWidth; 
//		int h = newOpts.outHeight; 
//		//现在主流手机比较多是800*500分辨率，所以高和宽我们设置为 
//		float hh = 800f;//这里设置高度为800f 
//		float ww = 500f;//这里设置宽度为500f
//		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可 
//		int be = 1;//be=1表示不缩放 
//		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放 
//		be = (int) (newOpts.outWidth / ww); 
//		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放 
//		be = (int) (newOpts.outHeight / hh); 
//		} 
//		if (be <= 0) 
//		be = 1; 
//		newOpts.inSampleSize = be;//设置缩放比例 
//          return bitmap;
	}
	
	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName){
		return new File(getStorageDirectory() + "/" + fileName).exists();
	}
	public static long getFileSize(String fileName) {
		return new File(getStorageDirectory()  + "/" + fileName).length();
	}

	/**
	 * 获取文件的大小
	 * @param fileName
	 * @return
	 */

	public static long getFileSize() {
		long size=0;
		File dirFile = new File(getStorageDirectory());
		if(dirFile==null)return 0;
		dirFile.length();
		String[] children = dirFile.list();
		LogTools.e("内存大小aaaa",""+children);
		for (int i = 0; i < children.length; i++) {
			size=size+new File(dirFile, children[i]).length();
			LogTools.e("内存大小"+size,""+new File(dirFile, children[i]).length());
		}
		return size;
	}

	/**
	 * 转换文件大小
	 *
	 * @param fileS
	 * @return
	 */
	public static String FormetFileSize() {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";

		long fileS=getFileSize();
		if (fileS == 0) {
			fileSizeString=wrongSize;
			return fileSizeString;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}




	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public static void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if(! dirFile.exists()){
			return;
		}
		if (dirFile.isDirectory()) {
			dirFile.length();
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
	}
}
