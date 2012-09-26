
package com.actuate.development.tool.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.widgets.Display;

import com.actuate.development.tool.dialog.ErrorLogDialog;

public class LogUtil
{

	public static void recordErrorMsg( Exception e, final boolean block )
	{
		recordErrorMsg( "Error when running the program:", e, block );
	}

	public static void recordErrorMsg( final String message, Exception e,
			final boolean block )
	{
		StringWriter writer = new StringWriter( );
		PrintWriter ps = new PrintWriter( writer );
		e.printStackTrace( ps );
		try
		{
			writer.close( );
		}
		catch ( IOException e1 )
		{
		}
		final String errorMsg = writer.toString( );
		if ( errorMsg != null && errorMsg.trim( ).length( ) > 0 )
		{
			Display.getDefault( ).syncExec( new Runnable( ) {

				public void run( )
				{
					ErrorLogDialog errorDialog = new ErrorLogDialog( null );
					errorDialog.setMessage( message );
					errorDialog.setText( errorMsg );
					errorDialog.setBlockOnOpen( block );
					errorDialog.open( );
				}
			} );

		}
	}
}
