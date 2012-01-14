/**
 * 
 */
package net.sf.feeling.dds.ddsutil;

import java.awt.Dimension;

import net.sf.feeling.dds.model.MipMaps;

import org.eclipse.swt.graphics.ImageData;


/**
 * @author danielsenff
 *
 */
public class MipMapsUtil {

	/**
	 * Topmost MipMap Index 
	 */
	public static final int TOP_MOST_MIP_MAP = 0;

	/**
	 * Generates all MipMaps of a Texture and stores them as {@link ImageData} in an Array.
	 * @param topmost
	 * @param mipmapWidth
	 * @param mipmapHeight
	 * @param hasMipMaps
	 * @return ImageDatas[]
	 */
	protected static ImageData[] generateBIMipMaps(ImageData topmost, 
			int mipmapWidth, int mipmapHeight, boolean hasMipMaps) {
		
		ImageData[] mipmapBI;
		
		if (hasMipMaps && MipMapsUtil.isPowerOfTwo(topmost.width) && 
				MipMapsUtil.isPowerOfTwo(topmost.height)) {
			
			//get maximum number of mipmaps to create
			int maxNumberOfMipMaps = MipMapsUtil.calculateMaxNumberOfMipMaps(mipmapWidth, mipmapHeight);
			// new array storing all mipmaps
			mipmapBI = new ImageData[maxNumberOfMipMaps];
	
			mipmapBI = MipMaps.generateMipMaps(topmost, mipmapWidth, mipmapHeight, mipmapBI);
			
		} else { // doesn't have mipmaps
			mipmapBI = new ImageData[1];
			mipmapBI[0] = topmost;
		}
		return mipmapBI;
	}

	/**
	 * Number of MipMaps that will be generated from this image sizes.
	 * @param width
	 * @param height
	 * @return
	 */
	public static int calculateMaxNumberOfMipMaps(final int width, final int height) {
		return ((int) Math.floor(Math.log(Math.max(width, height)) / Math.log(2.0)))+1; // plus original
	}




	/**
	 * Number of MipMaps that will be generated from this image dimension.
	 * @param dimension
	 * @return
	 */
	public static int calculateMaxNumberOfMipMaps(final Dimension dimension) {
		int width = dimension.width;
		int height = dimension.height;
		return ((int) Math.floor(Math.log(Math.max(width, height)) / Math.log(2.0)))+1; // plus original
	}




	/**
	 * Checks if a value is a power of two
	 * @param value
	 * @return
	 */
	public static boolean isPowerOfTwo(final int value) {
		double p = Math.floor(Math.log(value) / Math.log(2.0));
		double n = Math.pow(2.0, p);
	    return (n==value);
	}

}
