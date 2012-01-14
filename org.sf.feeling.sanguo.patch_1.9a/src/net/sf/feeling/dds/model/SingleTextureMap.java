/**
 * 
 */
package net.sf.feeling.dds.model;

import gr.zdimensions.jsquish.Squish.CompressionType;

import java.nio.ByteBuffer;

import net.sf.feeling.dds.util.ImageUtils;

import org.eclipse.swt.graphics.ImageData;


/**
 * TextureMap without MipMaps
 * @author danielsenff
 *
 */
public class SingleTextureMap extends AbstractTextureMap {

	ImageData bi;
	
	/**
	 * @param bi
	 */
	public SingleTextureMap(final ImageData bi) {
		this.bi = bi;
	}
	
	/**
	 * @return 
	 */
	public ImageData getData() {
		return this.bi;
	}
	
	/* (non-Javadoc)
	 * @see DDSUtil.AbstractMipMaps#getDXTCompressedBuffer(gr.zdimensions.jsquish.Squish.CompressionType)
	 */
	public ByteBuffer[] getDXTCompressedBuffer(CompressionType compressionType) {
		ByteBuffer[] buffer = new ByteBuffer[1];
		buffer[0] = super.compress(bi, compressionType);
		return buffer;
	}

	

	/* (non-Javadoc)
	 * @see DDSUtil.AbstractMipMaps#getHeight()
	 */
	public int getHeight() {
		return this.bi.height;
	}

	/* (non-Javadoc)
	 * @see DDSUtil.AbstractMipMaps#getWidth()
	 */
	public int getWidth() {
		return this.bi.width;
	}

	/* (non-Javadoc)
	 * @see DDSUtil.AbstractTextureMap#getUncompressedBuffer()
	 */
	public ByteBuffer[] getUncompressedBuffer() {
		ByteBuffer[] mipmapBuffer = new ByteBuffer[1];
		mipmapBuffer[0] = ByteBuffer.wrap(ImageUtils.convertBIintoARGBArray(this.bi));
		return mipmapBuffer;
	}

}
