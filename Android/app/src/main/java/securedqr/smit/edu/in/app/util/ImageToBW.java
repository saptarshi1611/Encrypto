package securedqr.smit.edu.in.app.util;

import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

/**
 * creates greyscale image
 * @since 2.0
 */
public class ImageToBW {

	/**
	 * Writes greyscale image to BW.png
	 * @param src Input image file
	 * @return path to Bw.png
	 * @throws IOException
	 */
	public static String toBW(String src) throws IOException {
		String bw=QRCode.filePath+"/BW.jpg";
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
	    ColorMatrixColorFilter cmf = new ColorMatrixColorFilter(cm);
		Bitmap bmp=BitmapFactory.decodeFile(src);					
		int sw=bmp.getWidth(),sh=(int)bmp.getHeight();
		Bitmap bmp1=Bitmap.createScaledBitmap(bmp,(int)(sw*.7),(int)(sh*.7),true);
		Bitmap bmp2=bmp1.copy(Bitmap.Config.ARGB_8888,true);			
		Paint paint = new Paint();
		paint.setColorFilter(cmf);
		Canvas canvas = new Canvas(bmp2);
	    canvas.drawBitmap(bmp2, 0, 0, paint);
		FileOutputStream fp=new FileOutputStream(bw);				
		bmp2.compress(Bitmap.CompressFormat.JPEG,7,fp);
		fp.close();
		return bw;
	}
}