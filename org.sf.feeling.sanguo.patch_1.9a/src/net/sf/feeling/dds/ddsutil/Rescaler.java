/**
 * 
 */
package net.sf.feeling.dds.ddsutil;

import org.eclipse.swt.graphics.ImageData;

/**
 * @author danielsenff
 *
 */
public abstract class Rescaler {

	/**
	 * @param image2
	 * @param width
	 * @param height
	 * @return
	 */
	public abstract ImageData rescaleBI(ImageData image, int width, int height);
	
	
}
