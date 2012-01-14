/**
 * 
 */
package net.sf.feeling.dds.model;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;

import java.nio.ByteBuffer;

import net.sf.feeling.dds.compression.DXTBufferCompressor;
import net.sf.feeling.dds.ddsutil.DDSUtil;

import org.eclipse.swt.graphics.ImageData;



/**
 * Abstract TextureMap
 * @author danielsenff
 *
 */
public abstract class AbstractTextureMap implements TextureMap {

	
	public ByteBuffer[] getDXTCompressedBuffer(final int pixelformat) {
		CompressionType compressionType = DDSUtil.getSquishCompressionFormat(pixelformat);
		return this.getDXTCompressedBuffer(compressionType );
	}
	
	/**
	 * @param bi
	 * @param compressionType
	 * @return
	 */
	public ByteBuffer compress(final ImageData bi, 
			final Squish.CompressionType compressionType) {
		DXTBufferCompressor compi = new DXTBufferCompressor(bi, compressionType);
		return compi.getByteBuffer();
	}
	
	
}
