
package org.sf.feeling.sanguo.patch.util;

import java.util.Comparator;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinComparator implements Comparator
{

	public int compare( Object o1, Object o2 )
	{
		return compare( o1.toString( ), o2.toString( ) );
	}

	public static int compare( String o1, String o2 )
	{
		for ( int i = 0; i < o1.toString( ).length( )
				&& i < o2.toString( ).length( ); i++ )
		{
			char c1 = o1.charAt( i );
			char c2 = o2.charAt( i );

			String pinyin1 = pinyin( c1 );
			String pinyin2 = pinyin( c2 );

			if ( pinyin1 != null && pinyin2 != null )
			{

				if ( !pinyin1.equals( pinyin2 ) )
				{
					return pinyin1.compareTo( pinyin2 );
				}
			}
			else
			{
				if ( c1 != c2 )
				{
					return c1 - c2;
				}
			}

		}
		return o1.toString( ).length( ) - o2.toString( ).length( );
	}

	/**
	 * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
	 * 
	 * @param c
	 * @return
	 */
	private static String pinyin( char c )
	{
		String[] a = PinyinHelper.toHanyuPinyinStringArray( c );
		if ( a == null )
			return null;
		return a[0];
	}
}