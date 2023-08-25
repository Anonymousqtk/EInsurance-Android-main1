package com.pvi.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class FileHelper {

	private static final int COMPRESS_QUALITY = 95;
	private static final int MIN_WIDTH = 1632;
	private static final int IMAGE_THUMB = 100;
	private static final float maxHeight = 900.0f;
	private static final float maxWidth = 1200.0f;
	private static final float FONT_SIZE = 14;
	private static final String PVIAUTOCARE = "PVIAUTOCARE";
	private static final String FILE_NAME = "EInsurance";
	private final static File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PVIAUTOCARE);
	public static char[] charA = { 'à', 'á', 'ạ', 'ả', 'ã',// 0-&gt;16
			'â', 'ầ', 'ấ', 'ậ', 'ẩ', 'ẫ', 'ă', 'ằ', 'ắ', 'ặ', 'ẳ', 'ẵ' };// a,// ă,// â
	public static char[] charE = { 'ê', 'ề', 'ế', 'ệ', 'ể', 'ễ',// 17-&gt;27
			'è', 'é', 'ẹ', 'ẻ', 'ẽ' };// e
	public static char[] charI = { 'ì', 'í', 'ị', 'ỉ', 'ĩ' };// i 28-&gt;32
	public static char[] charO = { 'ò', 'ó', 'ọ', 'ỏ', 'õ',// o 33-&gt;49
			'ô', 'ồ', 'ố', 'ộ', 'ổ', 'ỗ',// ô
			'ơ', 'ờ', 'ớ', 'ợ', 'ở', 'ỡ' };// ơ
	public static char[] charU = { 'ù', 'ú', 'ụ', 'ủ', 'ũ',// u 50-&gt;60
			'ư', 'ừ', 'ứ', 'ự', 'ử', 'ữ' };// ư
	public static char[] charY = { 'ỳ', 'ý', 'ỵ', 'ỷ', 'ỹ' };// y 61-&gt;65
	public static char[] charD = { 'đ', ' ','Đ','D','d' }; // 66-70
	public static String charact = String.valueOf(charA, 0, charA.length)
			+ String.valueOf(charE, 0, charE.length)
			+ String.valueOf(charI, 0, charI.length)
			+ String.valueOf(charO, 0, charO.length)
			+ String.valueOf(charU, 0, charU.length)
			+ String.valueOf(charY, 0, charY.length)
			+ String.valueOf(charD, 0, charD.length);
	// *********** check file if exist ******************************//
	public static boolean isExistFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static boolean removeFileAtPath(String path) {
		File file = new File(path);
		return file.delete();
	}

	public static void deleteTempContent() {
		if (storageDir.isDirectory()) {
			String[] children = storageDir.list();
			for (int i = 0; i < children.length; i++) {
				new File(storageDir, children[i]).delete();
			}
		}
	}
	public static String ConvertString(String pStr) {
		String convertString = pStr.toLowerCase();
		Character[] returnString = new Character[convertString.length()];
		for (int i = 0; i < convertString.length(); i++) {
			char temp = convertString.charAt(i);
			if ((int) temp < 97 || temp > 122) {
				char tam1 = GetAlterChar(temp);
				if ((int) temp != 32)
					convertString = convertString.replace(temp, tam1);
			}
		}
		return convertString;
	}
	private static char GetAlterChar(char pC) {

		if ((int) pC == 32) {
			return ' ';
		}

		char tam = pC;// Character.toLowerCase(pC);

		int i = 0;
		while (i < charact.length() && charact.charAt(i) != tam) {
			i++;
		}
		if (i < 0 || i > 67)
			return pC;

		if (i == 66) {
			return 'đ';
		}
		if(i == 68){
			return 'Đ';
		}
		if(i == 69){
			return 'D';
		}
		if(i == 70){
			return 'd';
		}
		if (i >= 0 && i <= 16) {
			return 'a';
		}
		if (i >= 17 && i <= 27) {
			return 'e';
		}
		if (i >= 28 && i <= 32) {
			return 'i';
		}
		if (i >= 33 && i <= 49) {
			return 'o';
		}
		if (i >= 50 && i <= 60) {
			return 'u';
		}
		if (i >= 61 && i <= 65) {
			return 'y';
		}

		return pC;
	}
	public static Bitmap loadImageFromPath(String path) {
		Bitmap bitmap = null;
		try {
			File file = new File(path);
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return bitmap;
	}

	public static String compressImage(String filePath, Context base, boolean allowOverwrite, String fileName, int major) {
//		float deltaW = 0.0f, deltaH = 0.0f;
//		int deltaQ = 0;
//		if (major == GlobalMethod.SHIP_MAJOR) {
//			deltaW = 300.0f;
//			deltaH = 400.0f;
//			deltaQ = 10;
//		}

		Bitmap scaledBitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;
		Log.d("size start: ", actualHeight + "------" + actualWidth);

		while (actualHeight == -1 || actualWidth == 1 || actualHeight == 1 || actualWidth == -1) {
			bmp = BitmapFactory.decodeFile(filePath, options);
			actualHeight = options.outHeight;
			actualWidth = options.outWidth;
			Log.d("size processing: ", actualHeight + "------" + actualWidth);
		}

		float imgRatio = actualWidth / actualHeight;
		float maxRatio = (maxWidth) / (maxHeight);

		if (actualHeight > (maxHeight) || actualWidth > (maxWidth)) {
			if (imgRatio < maxRatio) {
				imgRatio = (maxHeight) / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) (maxHeight);
			} else if (imgRatio > maxRatio) {
				imgRatio = (maxWidth) / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) (maxWidth);
			} else {
				actualHeight = (int) (maxHeight);
				actualWidth = (int) (maxWidth);

			}
		}

		options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			bmp = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			Log.d("size compress: ", actualHeight + "------" + actualWidth);
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream out = null;
		String filename = getFilename(base, allowOverwrite, fileName);

		try {
			out = new FileOutputStream(filename);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return filename;

	}

	private static String getFilename(Context base, boolean allowOverwrite, String fileName) {
		ContextWrapper cw = new ContextWrapper(base);
		// path to /data/data/ClaimOnline/app_data/PVICLAIM
		File directory = cw.getDir(PVIAUTOCARE, Context.MODE_PRIVATE);
		// Create PVICLAIM directory
		Random r = new Random();
		File imagePath = new File(directory, fileName + "image" + r.nextInt(1000000) + ".jpg");

		if (imagePath.exists() && allowOverwrite) {
			imagePath.delete();
		}
		return imagePath.getAbsolutePath();
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;

		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	public static void addWatermark(String filename, String name, String time, String plate) {

		Bitmap bitmap = loadImageFromPath(filename);

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap result = Bitmap.createBitmap(w, h, bitmap.getConfig());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bitmap, 0, 0, null);

		Typeface tf = Typeface.create("Helvetica", Typeface.NORMAL);

		// draw text border
		Paint strokePaint = new Paint();
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setStrokeWidth(2);
		strokePaint.setColor(Color.DKGRAY);
		strokePaint.setAlpha(255);
		strokePaint.setTextSize(FONT_SIZE);
		strokePaint.setTypeface(tf);
		strokePaint.setAntiAlias(true);
		strokePaint.setUnderlineText(false);
		canvas.drawText(name, 0, FONT_SIZE, strokePaint);
		canvas.drawText(plate, 0, 2 * FONT_SIZE, strokePaint);
		canvas.drawText(time, 0, 3 * FONT_SIZE, strokePaint);

		// draw fill color text
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setAlpha(255);
		paint.setTextSize(FONT_SIZE);
		paint.setTypeface(tf);
		paint.setAntiAlias(true);
		paint.setUnderlineText(false);
		canvas.drawText(name, 0, FONT_SIZE, paint);
		canvas.drawText(plate, 0, 2 * FONT_SIZE, paint);
		canvas.drawText(time, 0, 3 * FONT_SIZE, paint);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			result.compress(Bitmap.CompressFormat.JPEG, 100, out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static Bitmap loadImageForThumbnail(String path) {
		try {
			return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), IMAGE_THUMB, IMAGE_THUMB);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getFileName(String filePath) {
		String[] seperated = filePath.split("/");
		String imageNamed = seperated[seperated.length - 1];
		return imageNamed;
	}

	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";

		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(PVIAUTOCARE, "failed to create directory!");
				return null;
			}
		}

		File image = null;
		try{
			image = File.createTempFile(imageFileName,".jpg", storageDir);
		} catch (Exception e) {
			Log.d(PVIAUTOCARE, e.getMessage());
		}

		return image;
	}

	public static File createImageFileWithToast(Activity ctx) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";

		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(PVIAUTOCARE, "failed to create directory!");
				Toast.makeText(ctx, "Không thể tạo thư mục tại đường dẫn " + storageDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
				return null;
			}
		}

		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		return image;
	}

	public static String getDownloadPath() throws IOException {
		// Create the storage directory if it does not exist
		if (!storageDir.exists()) {
			if (!storageDir.mkdirs()) {
				Log.d(PVIAUTOCARE, "failed to create directory!");
				return null;
			}
		}

		File apk = File.createTempFile(FILE_NAME, /* prefix */".apk", /* suffix */storageDir /* directory */);

		GlobalData.getInstance().setCurrentDownloadPath(apk.getAbsolutePath());
		return GlobalData.getInstance().getCurrentDownloadPath();
	}

	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTimeLocate() {
		return new SimpleDateFormat("ddMMyyyy HHmmss").format(new Date());
	}
	
	// If targetLocation does not exist, it will be created.
	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {

			// make sure the directory we plan to store the recording in exists
			File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create dir " + directory.getAbsolutePath());
			}

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	// ******************** Un-used methods ******************************//
	/**
	 * 
	 * @param source
	 * @param newHeight
	 * @param newWidth
	 * @return
	 */
	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight,
			int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap
		// will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our
		// new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
				source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}

	/**
	 * 
	 * @param bitmapData
	 * @param fileName
	 * @param base
	 * @param allowOverwrite
	 * @return
	 */
	public static String writeFileToPath(Bitmap bitmapData, String fileName, Context base, boolean allowOverwrite) {

		ContextWrapper cw = new ContextWrapper(base);
		// path to /data/data/ClaimOnline/app_data/PVICLAIM
		File directory = cw.getDir(PVIAUTOCARE, Context.MODE_PRIVATE);
		// Create PVICLAIM directory
		Random r = new Random();
		File imagePath = new File(directory, fileName + "image" + r.nextInt(1000000) + ".jpg");

		if (imagePath.exists() && allowOverwrite) {
			imagePath.delete();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imagePath);
			bitmapData.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY,
					fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("saveToInternalStorg() ", e.getMessage());
			e.printStackTrace();
		}

		// add more
		bitmapData.recycle();
		bitmapData = null;
		System.gc();

		return imagePath.getAbsolutePath();
	}

	/**
	 * 
	 * @param currentPhotoPath
	 * @return
	 */
	public static Bitmap loadLazyImage(String currentPhotoPath) {

		// **** standard size: 1632 x 1224
		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		int outW = MIN_WIDTH, outH = 0;
		if (photoW > MIN_WIDTH || photoH > MIN_WIDTH) {
			boolean isWidthBigger = true;

			if (photoW < photoH) {
				isWidthBigger = false;
			}

			if (isWidthBigger) {
				outW = MIN_WIDTH;
				outH = Math.round((MIN_WIDTH * photoH) / photoW);
			} else {
				outH = MIN_WIDTH;
				outW = Math.round((MIN_WIDTH * photoW) / photoH);
			}

			/* Figure out which way needs to be reduced less */
			if ((outW > 0) || (outH > 0)) {
				scaleFactor = Math.min(photoW / outW, photoH / outH);
			}
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		// bmOptions.inPreferredConfig = Config.RGB_565;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		int currentDensity = bitmap.getDensity();
		Bitmap result = null;

		if (outW > 0 && outH > 0) {
			result = Bitmap.createScaledBitmap(bitmap, outW, outH, false);
			result.setDensity(currentDensity);
			// bitmap.recycle();
			// bitmap = null;
			// System.gc();
		} else {
			return bitmap;
		}

		return result;
	}

	public static boolean deleteLastFile(Context ctx) {
		// Find the last picture
		String[] projection = new String[] {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Images.ImageColumns.DATE_TAKEN,
				MediaStore.Images.ImageColumns.MIME_TYPE
		};
		Cursor cursor = ctx.getContentResolver()
				.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
						null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

		if (cursor.moveToFirst()) {
			String imageLocation = cursor.getString(1);
			File imageFile = new File(imageLocation);
			if (imageFile.exists()) {
				Log.d("----", imageLocation);
				return imageFile.delete();
			}
		}
		return false;
	}
}
