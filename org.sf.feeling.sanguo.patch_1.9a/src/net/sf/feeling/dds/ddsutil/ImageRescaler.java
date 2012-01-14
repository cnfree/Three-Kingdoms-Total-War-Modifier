/**
 * 
 */
package net.sf.feeling.dds.ddsutil;

import org.eclipse.swt.graphics.ImageData;

/**
 * @author danielsenff
 *
 */
public class ImageRescaler extends Rescaler {
		
		/**
		 * @param image
		 * @param width
		 * @param height
		 * @return
		 */
		@Override
		public ImageData rescaleBI(final ImageData image,
				final int width, final int height) {
			return image.scaledTo( width, height );
		}
	
}
