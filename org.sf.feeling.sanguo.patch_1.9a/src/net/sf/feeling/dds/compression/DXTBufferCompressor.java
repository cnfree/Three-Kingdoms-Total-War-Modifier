/**
 * 
 */

package net.sf.feeling.dds.compression;

import gr.zdimensions.jsquish.Squish;
import gr.zdimensions.jsquish.Squish.CompressionType;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import net.sf.feeling.dds.util.ImageUtils;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;


/**
 * @author danielsenff
 * 
 */
public class DXTBufferCompressor
{

	// byte[] compressedData;
	protected byte[] byteData;
	protected int[] intData;
	protected Point dimension;
	protected CompressionType compressionType;

	/**
	 * @param data
	 *            Byte-Array should store ARGB
	 * @param width
	 * @param height
	 * @param compressionType
	 */
	public DXTBufferCompressor( final byte[] data, final int width,
			final int height, final Squish.CompressionType compressionType )
	{
		this( data, new Point( width, height ), compressionType );
	}

	/**
	 * @param byteBuffer
	 *            ByteBuffer should store ARGB
	 * @param width
	 * @param height
	 * @param compressionType
	 */
	public DXTBufferCompressor( final ByteBuffer byteBuffer, final int width,
			final int height, final Squish.CompressionType compressionType )
	{
		this( toByteArray( byteBuffer ),
				new Point( width, height ),
				compressionType );
	}

	/**
	 * @param image
	 * @param compressionType
	 */
	public DXTBufferCompressor( final ImageData image,
			final Squish.CompressionType compressionType )
	{

		this( ImageUtils.convertBIintoARGBArray( (ImageData) image ),
				new Point( image.width, image.height ),
				compressionType );
	}

	/**
	 * @param data
	 *            Byte-Array should store ARGB
	 * @param dimension
	 * @param compressionType
	 */
	public DXTBufferCompressor( final byte[] data, final Point dimension,
			final Squish.CompressionType compressionType )
	{
		this.byteData = data;
		this.dimension = dimension;
		this.compressionType = compressionType;
	}

	public DXTBufferCompressor( final int[] data, final Point dimension,
			final Squish.CompressionType compressionType )
	{
		this.intData = data;
		this.dimension = dimension;
		this.compressionType = compressionType;
	}

	/**
	 * @return ByteBuffer
	 */
	public ByteBuffer getByteBuffer( )
	{
		byte[] compressedData;
		try
		{

			// the data-Array given to the squishCompressToArray is expected to
			// be
			// width * height * 4 -> with RGBA, which means, if we got RGB, we
			// need to add A!
			if ( byteData.length < dimension.y * dimension.x * 4 )
			{
				System.out.println( "blow up array from RGB to ARGB" );
				byteData = convertRGBArraytiRGBAArray( byteData, dimension );
			}

			compressedData = squishCompressToArray( byteData,
					dimension.x,
					dimension.y,
					compressionType );
			return ByteBuffer.wrap( compressedData );
		}
		catch ( DataFormatException e )
		{
			e.printStackTrace( );
		}
		return null;

	}

	private byte[] convertRGBArraytiRGBAArray( byte[] data,
			final Point dimension )
	{

		int rgbLength = data.length;
		int rgbaLength = dimension.x * dimension.y * 4;

		byte[] rgbaBuffer = new byte[rgbaLength];

		// populate new array
		// we always copy 3 byte chunks, skip one byte, which we set to 255 and
		// take the next 3 byte
		int loopN = 0;
		for ( int i = 0; i < rgbLength; i = i + 3 )
		{

			int srcPos = i;
			int destPos = i + loopN;

			System.arraycopy( data, srcPos, rgbaBuffer, destPos, 3 );
			loopN++;
		}

		return rgbaBuffer;
	}

	/**
	 * Get the Byte-array held by this object.
	 * 
	 * @return
	 */
	public byte[] getArray( )
	{
		try
		{
			return squishCompressToArray( byteData,
					dimension.x,
					dimension.y,
					compressionType );
		}
		catch ( final DataFormatException e )
		{
			e.printStackTrace( );
		}
		return byteData;
	}

	/**
	 * Compresses the RGBA-byte-array into a DXT-compressed byte-array.
	 * 
	 * @param rgba
	 *            Byte-Array needs to be in RGBA-order
	 * @param height
	 * @param width
	 * @param compressionType
	 * @return
	 * @throws DataFormatException
	 */
	private static byte[] squishCompressToArray( final byte[] rgba,
			final int width, final int height,
			final Squish.CompressionType compressionType )
			throws DataFormatException
	{

		// expected array length
		int length = width * height * 4;
		if ( rgba.length != length )
			throw new DataFormatException( "unexpected length:"
					+ rgba.length
					+ " instead of "
					+ length );

		int storageRequirements = Squish.getStorageRequirements( width,
				height,
				compressionType );

		return Squish.compressImage( rgba,
				width,
				height,
				new byte[storageRequirements],
				compressionType,
				Squish.CompressionMethod.CLUSTER_FIT );
	}

	private static byte[] toByteArray( final ByteBuffer bytebuffer )
	{
		byte[] rgba = new byte[bytebuffer.capacity( )];
		bytebuffer.get( rgba );
		return rgba;
	}

	public int getStorageRequirements( )
	{
		return getStorageRequirements( dimension, compressionType );

	}

	/**
	 * Return the length of the required {@link ByteBuffer} for the image
	 * 
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 */
	public static int getStorageRequirements( final int width,
			final int height, final Squish.CompressionType type )
	{
		return Squish.getStorageRequirements( width, height, type );
	}

	/**
	 * Return the length of the required {@link ByteBuffer} for the image
	 * 
	 * @param imagePoint
	 * @param type
	 * @return
	 */
	public static int getStorageRequirements( final Point imagePoint,
			final Squish.CompressionType type )
	{
		return Squish.getStorageRequirements( (int) imagePoint.x,
				(int) imagePoint.y,
				type );
	}

}
