
package net.sf.feeling.dds.util;

/*
 * Copyright 2009, Morten Nobel-Joergensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.image.BufferedImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Heinz Doerr
 */
public class ImageUtils
{

	public static void ints2bytes( int[] in, byte[] out, int index1,
			int index2, int index3 )
	{
		for ( int i = 0; i < in.length; i++ )
		{
			int index = i * 3;
			int value = in[i];
			out[index + index1] = (byte) value;
			value = value >> 8;
			out[index + index2] = (byte) value;
			value = value >> 8;
			out[index + index3] = (byte) value;
		}
	}

	public static void ints2bytes( int[] in, byte[] out, int index1,
			int index2, int index3, int index4 )
	{
		for ( int i = 0; i < in.length; i++ )
		{
			int index = i * 4;
			int value = in[i];
			out[index + index1] = (byte) value;
			value = value >> 8;
			out[index + index2] = (byte) value;
			value = value >> 8;
			out[index + index3] = (byte) value;
			value = value >> 8;
			out[index + index4] = (byte) value;
		}
	}

	public static int[] bytes2int( byte[] in, int index1, int index2, int index3 )
	{
		int[] out = new int[in.length / 3];
		for ( int i = 0; i < out.length; i++ )
		{
			int index = i * 3;
			int b1 = ( in[index + index1] & 0xff ) << 16;
			int b2 = ( in[index + index2] & 0xff ) << 8;
			int b3 = in[index + index3] & 0xff;
			out[i] = b1 | b2 | b3;
		}
		return out;
	}

	public static int[] bytes2int( byte[] in, int index1, int index2,
			int index3, int index4 )
	{
		int[] out = new int[in.length / 4];
		for ( int i = 0; i < out.length; i++ )
		{
			int index = i * 4;
			int b1 = ( in[index + index1] & 0xff ) << 24;
			int b2 = ( in[index + index2] & 0xff ) << 16;
			int b3 = ( in[index + index3] & 0xff ) << 8;
			int b4 = in[index + index4] & 0xff;
			out[i] = b1 | b2 | b3 | b4;
		}
		return out;
	}

	public static ImageData convertToRGBA( ImageData src )
	{
		ImageData dest = new ImageData( src.width,
				src.height,
				32,
				new PaletteData( 0xFF000000, 0xFF0000, 0xFF00 ) );

		for ( int x = 0; x < src.width; x++ )
		{
			for ( int y = 0; y < src.height; y++ )
			{
				int pixel = src.getPixel( x, y );
				if ( pixel == src.transparentPixel )
				{
					dest.setPixel( x, y, ( ( ( (byte) 0 ) & 0xFF ) << 24 )
							+ pixel );
				}
				else
				{
					dest.setPixel( x, y, ( ( ( (byte) 255 ) & 0xFF ) << 24 )
							+ pixel );
				}

			}
		}
		return dest;
	}

	// A,B,G,R
	public static ImageData convertToRGBA( byte[] pixels, Point dimension )
	{
		ImageData dest = new ImageData( dimension.x,
				dimension.y,
				32,
				new PaletteData( 0xFF000000, 0xFF0000, 0xFF00 ) );
		dest.data = pixels;
		return dest;
	}

	/**
	 * Transfers the pixel-Information from a {@link BufferedImage} into a
	 * byte-array. If the {@link BufferedImage} is of different type, the pixels
	 * are reordered and stored in RGBA-order.
	 * 
	 * @param bi
	 * @return array in order RGBA
	 */
	public static byte[] convertBIintoARGBArray( final ImageData bi )
	{
		byte[] convertDataBufferToArray = convertDataBufferToArray( bi );
		return convertDataBufferToArray;
	}

	private static byte[] convertDataBufferToArray( final ImageData bi )
	{
		int length = bi.height * bi.width * 4;
		byte[] argb = new byte[length];
		for ( int i = 0; i < length; i = i + 4 )
		{
			int pixelValue = bi.getPixel( i / 4 % bi.width, i / 4 / bi.width );
			if ( bi.depth > 24 )
			{
				argb[i] = (byte) ( ( pixelValue >> 24 ) & 0xFF );
				argb[i + 1] = (byte) ( ( pixelValue >> 16 ) & 0xFF );
				argb[i + 2] = (byte) ( ( pixelValue >> 8 ) & 0xFF );
				argb[i + 3] = (byte) ( pixelValue & 0xFF );
			}
			else
			{ // 24bit image
				argb[i] = (byte) ( 255 );
				argb[i + 1] = (byte) ( ( pixelValue >> 16 ) & 0xFF );
				argb[i + 2] = (byte) ( ( pixelValue >> 8 ) & 0xFF );
				argb[i + 3] = (byte) ( pixelValue & 0xFF );
			}
		}
		return argb;
	}

	public static int getPixel( int depth, byte[] data )
	{
		switch ( depth )
		{
			case 32 :
				return ( ( data[0] & 0xFF ) << 24 )
						+ ( ( data[1] & 0xFF ) << 16 )
						+ ( ( data[2] & 0xFF ) << 8 )
						+ ( data[3] & 0xFF );
			case 24 :
				return ( ( data[0] & 0xFF ) << 16 )
						+ ( ( data[1] & 0xFF ) << 8 )
						+ ( data[2] & 0xFF );
			case 16 :
				return ( ( data[1] & 0xFF ) << 8 ) + ( data[0] & 0xFF );
			case 8 :
				return data[0] & 0xFF;
		}
		SWT.error( SWT.ERROR_UNSUPPORTED_DEPTH );
		return 0;
	}

}