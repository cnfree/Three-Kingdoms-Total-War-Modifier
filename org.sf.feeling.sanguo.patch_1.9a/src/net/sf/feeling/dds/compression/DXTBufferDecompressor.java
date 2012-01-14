/**
 * 
 */

package net.sf.feeling.dds.compression;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;

import java.nio.ByteBuffer;

import net.sf.feeling.dds.util.ImageUtils;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author danielsenff
 * 
 */
public class DXTBufferDecompressor
{

	// protected ByteBuffer compressedBuffer;
	protected ByteBuffer uncompressedBuffer;
	// protected int pixelformat;
	protected CompressionType compressionType;
	protected Point dimension;

	/**
	 * @param compressedBuffer
	 * @param width
	 * @param height
	 * @param pixelformat
	 */
	public DXTBufferDecompressor( final ByteBuffer compressedBuffer,
			final int width, final int height, CompressionType type )
	{
		this( compressedBuffer, new Point( width, height ), type );
	}

	/**
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param compressionType
	 */
	public DXTBufferDecompressor( byte[] compressedData, int width, int height,
			CompressionType compressionType )
	{
		this( ByteBuffer.wrap( compressedData ),
				new Point( width, height ),
				compressionType );
	}

	/**
	 * @param compressedBuffer
	 * @param dimension
	 * @param pixelformat
	 */
	public DXTBufferDecompressor( final ByteBuffer compressedBuffer,
			final Point dimension, CompressionType type )
	{
		this.uncompressedBuffer = squishDecompressBuffer( compressedBuffer,
				dimension.x,
				dimension.y,
				type );
		this.dimension = dimension;
		this.compressionType = type;

	}

	/**
	 * @return
	 */
	public ImageData getImage( )
	{

		// ByteBuffer byteBuf = squishDecompressBuffer(compressedBuffer,
		// dimension.width, dimension.height, compressionType );
		try
		{
			ImageData image = ImageUtils.convertToRGBA( uncompressedBuffer.array( ), dimension );
			return image;
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	/**
	 * Compresses a Byte-Array into a DXT-compressed {@link ByteBuffer} If the
	 * type is null, it returns the uncompressed ByteBuffer
	 * 
	 * Decompresses a DXT-compressed Byte-Array and returns a byte-Array If the
	 * {@link CompressionType} is null, it return the source data
	 * 
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param type
	 * @return byte[]
	 * @throws OutOfMemoryError
	 */
	public static byte[] squishDecompressToArray( final byte[] compressedData,
			final int width, final int height, final Squish.CompressionType type )
			throws OutOfMemoryError
	{

		// Use JSquish to decompress images. Then bind as normal.
		if ( type != null )
		{
			byte[] decompressedData = Squish.decompressImage( null,
					width,
					height,
					compressedData,
					type );
			return decompressedData;
		}

		return compressedData;

	}

	/**
	 * Decompresses a DXT-compressed Byte-Array and returns a ByteBuffer If the
	 * {@link CompressionType} is null, it return the source data
	 * 
	 * @param compressedData
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 * @throws OutOfMemoryError
	 */
	public static ByteBuffer squishDecompress( final byte[] compressedData,
			final int width, final int height, final Squish.CompressionType type )
			throws OutOfMemoryError
	{

		return ByteBuffer.wrap( squishDecompressToArray( compressedData,
				width,
				height,
				type ) );
	}

	/**
	 * Decompresses a DXT-compressed {@link ByteBuffer}
	 * 
	 * @param byteBuffer
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 * @throws OutOfMemoryError
	 */
	private static ByteBuffer squishDecompressBuffer(
			final ByteBuffer byteBuffer, final int width, final int height,
			final Squish.CompressionType type ) throws OutOfMemoryError
	{

		byte[] data = new byte[byteBuffer.capacity( )];
		byteBuffer.get( data );

		return squishDecompress( data, width, height, type );
	}

}
