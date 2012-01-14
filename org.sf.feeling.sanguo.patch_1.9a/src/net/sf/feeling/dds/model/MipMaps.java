/**
 * 
 */

package net.sf.feeling.dds.model;

import gr.zdimensions.jsquish.Squish;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;

import net.sf.feeling.dds.ddsutil.ImageRescaler;
import net.sf.feeling.dds.ddsutil.MipMapsUtil;
import net.sf.feeling.dds.ddsutil.NonCubicDimensionException;
import net.sf.feeling.dds.ddsutil.Rescaler;
import net.sf.feeling.dds.jogl.DDSImage;
import net.sf.feeling.dds.util.ImageUtils;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * MipMap Texture contains several layers of MipMaps, each is 1/4 the size of
 * the one above.
 * 
 * @author Daniel Senff
 * 
 */
public class MipMaps extends AbstractTextureMap implements Iterable<ImageData>
{

	/**
	 * Topmost MipMap Index
	 */
	public static final int TOP_MOST_MIP_MAP = 0;

	Vector<ImageData> mipmaps;
	protected Rescaler rescaler;
	private int numMipMaps;

	/**
	 * @param topmost
	 * 
	 */
	public MipMaps( )
	{
		this.rescaler = new ImageRescaler( );
		this.mipmaps = new Vector<ImageData>( );
	}

	/**
	 * Populate this MipMap-Object based on the given topmost Map.
	 * 
	 * @param topmost
	 */
	public void generateMipMaps( ImageData topmost )
	{
		this.mipmaps.add( topmost );

		if ( !DDSImageFile.isPowerOfTwo( topmost.width )
				&& !DDSImageFile.isPowerOfTwo( topmost.height ) )
			throw new NonCubicDimensionException( );

		this.mipmaps = generateMipMapArray( this.mipmaps );
	}

	private Vector<ImageData> generateMipMapArray(
			Vector<ImageData> mipMapsVector )
	{
		ImageData topmost = mipMapsVector.get( 0 );
		// dimensions of first map
		int mipmapWidth = topmost.width;
		int mipmapHeight = topmost.height;
		numMipMaps = MipMapsUtil.calculateMaxNumberOfMipMaps( mipmapWidth,
				mipmapHeight );

		ImageData previousMap = topmost;
		for ( int i = 1; i < numMipMaps; i++ )
		{
			// calculation for next map
			mipmapWidth = MipMaps.calculateMipMapSize( mipmapWidth );
			mipmapHeight = MipMaps.calculateMipMapSize( mipmapHeight );
			ImageData mipMapBi = rescaler.rescaleBI( previousMap,
					mipmapWidth,
					mipmapHeight );
			mipMapsVector.add( mipMapBi );
			// by using this map in the next MipMap generation step, we increase
			// performance, since we don't always scale from the biggest image.
			previousMap = mipMapBi;
		}
		return mipMapsVector;
	}

	/**
	 * Returns the highest MipMap in the original resolution.
	 * 
	 * @return
	 */
	public ImageData getTopMostMipMap( )
	{
		return getMipMap( 0 );
	}

	/**
	 * @return
	 */
	public int getNumMipMaps( )
	{
		return this.numMipMaps;
	}

	public int getHeight( )
	{
		return getMipMap( TOP_MOST_MIP_MAP ).height;
	}

	public int getWidth( )
	{
		return getMipMap( TOP_MOST_MIP_MAP ).width;
	}

	/**
	 * Returns a Map of the given level.
	 * 
	 * @param index
	 * @return
	 */
	public ImageData getMipMap( final int index )
	{
		return this.mipmaps.get( index );
	}

	/**
	 * All contained MipMaps compressed with DXT in {@link ByteBuffer} Squishes
	 * each mipmap and store in a {@link DDSImage} compatible {@link ByteBuffer}
	 * -Array.
	 * 
	 * @param compressionType
	 * @return
	 */
	public ByteBuffer[] getDXTCompressedBuffer(
			final Squish.CompressionType compressionType )
	{
		ByteBuffer[] mipmapBuffer = new ByteBuffer[this.numMipMaps];

		for ( int j = 0; j < this.numMipMaps; j++ )
		{
			System.out.println( "compress mipmap " + j );
			mipmapBuffer[j] = compress( getMipMap( j ), compressionType );
		}
		return mipmapBuffer;
	}

	/**
	 * Returns a Vector with all MipMaps
	 * 
	 * @return
	 */
	public Vector<ImageData> getAllMipMaps( )
	{
		return this.mipmaps;
	}

	/**
	 * Returns an Array of {@link ImageData}s of MipMaps.
	 * 
	 * @return
	 */
	public ImageData[] getAllMipMapsArray( )
	{
		/*
		 * final ImageData[] array = new ImageData[numMipMaps]; for (int i = 0;
		 * i < numMipMaps; i++) { array[i] = getMipMap(i); }
		 */

		return (ImageData[]) this.mipmaps.toArray( new ImageData[0] );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DDSUtil.AbstractTextureMap#getUncompressedBuffer()
	 */
	public ByteBuffer[] getUncompressedBuffer( )
	{
		ByteBuffer[] mipmapBuffer = new ByteBuffer[numMipMaps];
		for ( int i = 0; i < numMipMaps; i++ )
		{
			mipmapBuffer[i] = ByteBuffer.wrap( ImageUtils.convertBIintoARGBArray( getMipMap( i ) ) );
		}
		return mipmapBuffer;
	}

	/**
	 * @param topmost
	 * @param mipmapWidth
	 * @param mipmapHeight
	 * @param mipmapBI
	 * @return
	 */
	public static ImageData[] generateMipMaps( final ImageData topmost,
			int mipmapWidth, int mipmapHeight, final ImageData[] mipmapBI )
	{
		int i = 0; // cause the first already is set
		while ( true )
		{

			ImageRescaler rescaler = new ImageRescaler( );
			mipmapBI[i] = rescaler.rescaleBI( mipmapBI[i],
					mipmapWidth,
					mipmapHeight );

			if ( mipmapWidth == 1 || mipmapHeight == 1 )
				break;

			i++;
			mipmapWidth = calculateMipMapSize( mipmapWidth );
			mipmapHeight = calculateMipMapSize( mipmapHeight );
		}
		return mipmapBI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ImageData> iterator( )
	{
		return new Iterator<ImageData>( ) {

			int count = 0;

			public boolean hasNext( )
			{
				boolean b = count++ < mipmaps.size( ) - 1;
				return b;
			}

			public ImageData next( )
			{
				return mipmaps.get( count );
			}

			public void remove( )
			{
				throw new UnsupportedOperationException( );
			}

		};
	}

	public Rescaler getRescaler( )
	{
		return this.rescaler;
	}

	public void setRescaler( Rescaler rescaler )
	{
		this.rescaler = rescaler;
	}

	/**
	 * returns the new size for the next iteration of a generated MipMap Usually
	 * half the current value, unless current value is 1
	 * 
	 * @param currentValue
	 * @return
	 */
	public static int calculateMipMapSize( final int currentValue )
	{
		return ( currentValue > 1 ) ? currentValue / 2 : 1;
	}

	/**
	 * @param targetIndex
	 * @param currentValue
	 * @return
	 */
	public static int getMipMapSizeAtIndex( final int targetIndex,
			final int currentValue )
	{
		int newValue = currentValue;
		for ( int i = 0; i < targetIndex; i++ )
		{
			newValue = MipMaps.calculateMipMapSize( newValue );
		}
		return newValue;
	}

	public int getMipMapWidth( int index )
	{
		return getMipMap( index ).width;
	}

	public int getMipMapHeight( int index )
	{
		return getMipMap( index ).height;
	}

	/**
	 * Returns the {@link Dimension} of the MipMap at the given index.
	 * 
	 * @param index
	 * @return
	 */
	public Point getMipMapDimension( final int index )
	{
		return new Point( getMipMapWidth( index ), getMipMapHeight( index ) );
	}

}
