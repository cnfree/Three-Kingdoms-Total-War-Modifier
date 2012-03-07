
package org.sf.feeling.sanguo.patch.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.sf.feeling.swt.win32.extension.util.SortMap;

public class CustomComparator implements Comparator
{

	protected SortMap properties;

	public CustomComparator( SortMap properties )
	{
		this.properties = properties;
	}

	public CustomComparator( )
	{
	}

	public int compare( Object arg0, Object arg1 )
	{
		String code0 = null;
		String code1 = null;
		if ( properties != null )
		{
			code0 = (String) properties.get( (String) arg0 );
			code1 = (String) properties.get( (String) arg1 );
			String[] code0s = code0.split( "-" );
			String[] code1s = code1.split( "-" );
			code0 = code0s[code0s.length - 1];
			code1 = code1s[code1s.length - 1];
		}
		else
		{
			code0 = (String) arg0;
			code1 = (String) arg1;
		}
		return Collator.getInstance( Locale.CHINESE ).compare( code0, code1 );
	}
}
