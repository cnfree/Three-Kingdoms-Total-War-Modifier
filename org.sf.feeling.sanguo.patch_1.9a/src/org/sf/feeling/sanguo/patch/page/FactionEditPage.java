
package org.sf.feeling.sanguo.patch.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.model.FactionTexture;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BattleUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.DDSLoader;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.graphics.dds.jogl.DDSImage;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class FactionEditPage extends SimpleTabPage
{

	private final SortMap factionMap = UnitUtil.getFactionMap( );

	ModifyListener nameListener = new ModifyListener( ) {

		public void modifyText( ModifyEvent e )
		{
			// checkEnableStatus( );
		}

	};

	private Text idText;

	private Text nameText;

	private Text bigCaptionBannerText;

	private ImageCanvas imageCanvas;

	private CCombo bigCaptionBannerFontCombo;

	private CCombo bigCaptionBannerFontSizeCombo;

	private Text smallCaptionBannerText;

	private CCombo smallCaptionBannerFontCombo;

	private CCombo smallCaptionBannerFontSizeCombo;

	private Text battleBannerText;

	private CCombo battleBannerFontCombo;

	private CCombo battleBannerFontSizeCombo;

	private Text stratBannerText;

	private CCombo stratBannerFontCombo;

	private CCombo stratBannerFontSizeCombo;

	public void buildUI( Composite parent )
	{
		super.buildUI( parent );
		TableWrapLayout layout = new TableWrapLayout( );
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 15;
		layout.verticalSpacing = 20;
		container.getBody( ).setLayout( layout );

		createTitle( );
		createPatchArea( );
		// initPage( );
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "编辑势力（需要保留的项目请不要做任何设置操作）：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 5;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "选择编辑势力：" );
		final CCombo factionCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 3;
		factionCombo.setLayoutData( gd );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}

		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( factionCombo.getSelectionIndex( ) > -1 )
					idText.setText( (String) factionMap.getKeyList( )
							.get( factionCombo.getSelectionIndex( ) ) );
				else
					idText.setText( "" );
				nameText.setText( factionCombo.getText( ) );
				if ( nameText.getText( ).indexOf( "公孙" ) != -1 )
				{
					bigCaptionBannerText.setText( "公孙" );
					smallCaptionBannerText.setText( "公孙" );
					stratBannerText.setText( "公孙" );
					battleBannerText.setText( "公孙" );

				}
				else if ( nameText.getText( ).indexOf( "在野" ) != -1 )
				{
					bigCaptionBannerText.setText( "在野" );
					smallCaptionBannerText.setText( "在野" );
					stratBannerText.setText( "在野" );
					battleBannerText.setText( "在野" );
				}
				else if ( nameText.getText( ).indexOf( "朝廷" ) != -1 )
				{
					bigCaptionBannerText.setText( "汉" );
					smallCaptionBannerText.setText( "汉" );
					stratBannerText.setText( "汉" );
					battleBannerText.setText( "汉" );
				}
				else
				{
					bigCaptionBannerText.setText( ""
							+ nameText.getText( ).charAt( 0 ) );
					smallCaptionBannerText.setText( ""
							+ nameText.getText( ).charAt( 0 ) );
					stratBannerText.setText( ""
							+ nameText.getText( ).charAt( 0 ) );
					battleBannerText.setText( ""
							+ nameText.getText( ).charAt( 0 ) );
				}
			}
		} );

		imageCanvas = WidgetUtil.getToolkit( ).createImageCanvas( patchClient,
				SWT.NONE );
		gd = new GridData( GridData.FILL_VERTICAL );
		gd.verticalSpan = 14;
		gd.widthHint = 256;
		gd.minimumHeight = 256;
		imageCanvas.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "势力代码：" );
		idText = WidgetUtil.getToolkit( ).createText( patchClient,
				"",
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 3;
		idText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置势力名称：" );

		nameText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 3;
		nameText.setLayoutData( gd );

		nameText.addModifyListener( nameListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑派系大旗帜：" );
		bigCaptionBannerText = WidgetUtil.getToolkit( )
				.createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		bigCaptionBannerText.setLayoutData( gd );
		ModifyListener bigCaptionBannerModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				File file = new File( FileConstants.captainBannerPath,
						"captain_portrait_" + idText.getText( ) + ".tga" );
				try
				{
					ImageData imageData = TgaLoader.loadImage( new FileInputStream( file ),
							true,
							true );
					int pixel = imageData.getPixel( 26, 60 );
					for ( int x = 20; x < 20 + 18; x++ )
					{
						for ( int y = 24; y < 24 + 41; y++ )
						{
							imageData.setPixel( x, y, pixel );
						}
					}

					Image oldImage = new Image( null, imageData );
					GC gc = new GC( oldImage );
					gc.setAdvanced( true );
					gc.setAntialias( SWT.ON );
					FontData fontData = new FontData( bigCaptionBannerFontCombo.getText( ),
							Integer.parseInt( bigCaptionBannerFontSizeCombo.getText( ) ),
							SWT.BOLD );
					gc.setFont( new Font( null, fontData ) );
					gc.setForeground( new Color( null, 31, 31, 31 ) );
					if ( bigCaptionBannerText.getText( ).trim( ).length( ) > 0 )
					{
						char[] chars = ChangeCode.toShort( bigCaptionBannerText.getText( )
								.trim( ) )
								.toCharArray( );
						Point fontSize = gc.stringExtent( "" + chars[0] );
						gc.drawText( "" + chars[0],
								( 18 - fontSize.x ) / 2 + 21,
								29,
								true );
						if ( chars.length >= 2 )
						{
							gc.drawText( "" + chars[1],
									( 18 - fontSize.x ) / 2 + 21,
									29 + fontSize.y,
									true );
						}
					}
					imageCanvas.setImageData( oldImage.getImageData( ) );
					gc.dispose( );
					oldImage.dispose( );
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace( );
				}
			}

		};
		bigCaptionBannerText.addModifyListener( bigCaptionBannerModifyListener );

		bigCaptionBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		FontData[] fonts = Display.getDefault( ).getFontList( null, true );
		HashSet set = new HashSet( );
		for ( int i = 0; i < fonts.length; i++ )
		{
			if ( fonts[i].getName( )
					.replaceAll( "[a-zA-Z0-9\\s_\\-\\.]+", "" )
					.trim( )
					.length( ) != 0
					&& fonts[i].getName( ).indexOf( '@' ) == -1 )
				set.add( fonts[i].getName( ) );
		}
		String[] fontNames = (String[]) set.toArray( new String[0] );
		Arrays.sort( fontNames );
		bigCaptionBannerFontCombo.setItems( fontNames );
		bigCaptionBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 80;
		bigCaptionBannerFontCombo.setLayoutData( gd );
		bigCaptionBannerFontCombo.addModifyListener( bigCaptionBannerModifyListener );

		bigCaptionBannerFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			bigCaptionBannerFontSizeCombo.add( "" + i );
		}
		bigCaptionBannerFontSizeCombo.setText( "12" );

		gd = new GridData( );
		gd.widthHint = 60;
		bigCaptionBannerFontSizeCombo.setLayoutData( gd );
		bigCaptionBannerFontSizeCombo.addModifyListener( bigCaptionBannerModifyListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑派系小旗帜：" );
		smallCaptionBannerText = WidgetUtil.getToolkit( )
				.createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		smallCaptionBannerText.setLayoutData( gd );
		ModifyListener smallCaptionModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				File file = new File( FileConstants.captainBannerPath,
						"captain_card_romans_julii.tga" );
				try
				{
					ImageData imageData = TgaLoader.loadImage( new FileInputStream( file ),
							true,
							true );
					int pixel = imageData.getPixel( 16, 45 );
					for ( int x = 7; x < 7 + 16; x++ )
					{
						for ( int y = 16; y < 16 + 35; y++ )
						{
							imageData.setPixel( x, y, pixel );
						}
					}

					Image oldImage = new Image( null, imageData );
					GC gc = new GC( oldImage );
					gc.setAdvanced( true );
					gc.setAntialias( SWT.ON );
					FontData fontData = new FontData( smallCaptionBannerFontCombo.getText( ),
							Integer.parseInt( smallCaptionBannerFontSizeCombo.getText( ) ),
							SWT.BOLD );
					gc.setFont( new Font( null, fontData ) );
					gc.setForeground( new Color( null, 31, 31, 31 ) );
					if ( smallCaptionBannerText.getText( ).trim( ).length( ) > 0 )
					{
						char[] chars = ChangeCode.toShort( smallCaptionBannerText.getText( )
								.trim( ) )
								.toCharArray( );
						Point fontSize = gc.stringExtent( "" + chars[0] );
						gc.drawText( "" + chars[0],
								( 16 - fontSize.x ) / 2 + 8,
								17,
								true );
						if ( chars.length >= 2 )
						{
							gc.drawText( "" + chars[1],
									( 16 - fontSize.x ) / 2 + 8,
									17 + fontSize.y,
									true );
						}
					}
					imageCanvas.setImageData( oldImage.getImageData( ) );
					gc.dispose( );
					oldImage.dispose( );
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace( );
				}
			}

		};
		smallCaptionBannerText.addModifyListener( smallCaptionModifyListener );

		smallCaptionBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		smallCaptionBannerFontCombo.setItems( fontNames );
		smallCaptionBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 80;
		smallCaptionBannerFontCombo.setLayoutData( gd );
		smallCaptionBannerFontCombo.addModifyListener( smallCaptionModifyListener );

		smallCaptionBannerFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			smallCaptionBannerFontSizeCombo.add( "" + i );
		}
		smallCaptionBannerFontSizeCombo.setText( "11" );

		gd = new GridData( );
		gd.widthHint = 60;
		smallCaptionBannerFontSizeCombo.setLayoutData( gd );
		smallCaptionBannerFontSizeCombo.addModifyListener( smallCaptionModifyListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑战役旗帜：" );
		stratBannerText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		stratBannerText.setLayoutData( gd );

		ModifyListener stratModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				Image image = new Image( null, 64, 64 );
				GC gc = new GC( image );
				gc.setAdvanced( true );
				gc.setAntialias( SWT.ON );
				FontData fontData = new FontData( stratBannerFontCombo.getText( ),
						Integer.parseInt( stratBannerFontSizeCombo.getText( ) ),
						SWT.BOLD );
				gc.setFont( new Font( null, fontData ) );
				gc.setBackground( new Color( null, 0, 0, 0 ) );
				gc.fillRectangle( 0, 0, 64, 64 );
				gc.setForeground( new Color( null, 255, 255, 255 ) );
				if ( stratBannerText.getText( ).trim( ).length( ) > 0 )
				{
					char[] chars = ChangeCode.toShort( stratBannerText.getText( )
							.trim( ) )
							.toCharArray( );
					Point fontSize = gc.stringExtent( "" + chars[0] );
					if ( chars.length == 1 )
					{
						gc.drawText( "" + chars[0],
								( 64 - fontSize.x ) / 2,
								( 64 - fontSize.y ) / 2,
								true );
					}
					if ( chars.length >= 2 )
					{
						gc.drawText( "" + chars[0],
								( 64 - fontSize.x ) / 2,
								( 32 - fontSize.y ) / 2,
								true );
						gc.drawText( "" + chars[1],
								( 64 - fontSize.x ) / 2,
								( 32 - fontSize.y ) / 2 + 32,
								true );
					}
				}
				imageCanvas.setImageData( image.getImageData( ) );
				gc.dispose( );
				image.dispose( );

			}

		};
		stratBannerText.addModifyListener( stratModifyListener );

		stratBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		stratBannerFontCombo.setItems( fontNames );
		stratBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 80;
		stratBannerFontCombo.setLayoutData( gd );
		stratBannerFontCombo.addModifyListener( stratModifyListener );

		stratBannerFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			stratBannerFontSizeCombo.add( "" + i );
		}
		stratBannerFontSizeCombo.setText( "36" );

		gd = new GridData( );
		gd.widthHint = 60;
		stratBannerFontSizeCombo.setLayoutData( gd );
		stratBannerFontSizeCombo.addModifyListener( stratModifyListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑战场旗帜：" );
		battleBannerText = WidgetUtil.getToolkit( )
				.createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		battleBannerText.setLayoutData( gd );
		ModifyListener battleModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				FactionTexture texture = (FactionTexture) BattleUtil.getFactionTextureMap( )
						.get( idText.getText( ) );
				File file = new File( FileConstants.dataFile,
						texture.getStandard_texture( ) + ".dds" );
				try
				{
					DDSImage ddsImage = DDSLoader.loadDDSImage( new FileInputStream( file ) );
					ImageData imageData = DDSLoader.getImageData( ddsImage );
					int pixel = imageData.getPixel( 126, 140 );
					for ( int x = 125; x < 125 + 53; x++ )
					{
						for ( int y = 20; y < 20 + 123; y++ )
						{
							imageData.setPixel( x, y, pixel );
						}
					}

					Image image = new Image( null, imageData );
					GC gc = new GC( image );
					gc.setAdvanced( true );
					gc.setAntialias( SWT.ON );
					FontData fontData = new FontData( battleBannerFontCombo.getText( ),
							Integer.parseInt( battleBannerFontSizeCombo.getText( ) ),
							SWT.BOLD );
					gc.setFont( new Font( null, fontData ) );
					gc.setForeground( new Color( null, 21, 21, 21 ) );
					if ( battleBannerText.getText( ).trim( ).length( ) > 0 )
					{
						char[] chars = ChangeCode.toShort( battleBannerText.getText( )
								.trim( ) )
								.toCharArray( );
						Point fontSize = gc.stringExtent( "" + chars[0] );
						gc.drawText( "" + chars[0],
								( 53 - fontSize.x ) / 2 + 125,
								30,
								true );
						if ( chars.length >= 2 )
						{
							gc.drawText( "" + chars[1],
									( 53 - fontSize.x ) / 2 + 125,
									30 + fontSize.y,
									true );
						}
					}
					imageCanvas.setImageData( image.getImageData( ) );
					gc.dispose( );
					image.dispose( );
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace( );
				}
			}
		};
		battleBannerText.addModifyListener( battleModifyListener );

		battleBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		battleBannerFontCombo.setItems( fontNames );
		battleBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 80;
		battleBannerFontCombo.setLayoutData( gd );
		battleBannerFontCombo.addModifyListener( battleModifyListener );

		battleBannerFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			battleBannerFontSizeCombo.add( "" + i );
		}
		battleBannerFontSizeCombo.setText( "36" );

		gd = new GridData( );
		gd.widthHint = 60;
		battleBannerFontSizeCombo.setLayoutData( gd );
		battleBannerFontSizeCombo.addModifyListener( battleModifyListener );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 5;
		Composite buttonGroup = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		buttonGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		buttonGroup.setLayout( layout );

		Button applyButton = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, "应用", SWT.PUSH );
		applyButton.setEnabled( false );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.END;
		applyButton.setLayoutData( gd );

		final Button restoreButton = WidgetUtil.getToolkit( )
				.createButton( buttonGroup, "还原", SWT.PUSH );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		restoreButton.setLayoutData( gd );
		restoreButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				restoreButton.setEnabled( false );
				BakUtil.restoreCurrectVersionBakFile( );
				// refreshPage( );
				restoreButton.setEnabled( true );
			}
		} );

		patchSection.setClient( patchClient );
	}

	private void createTitle( )
	{
		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"<form><p>本页面用于编辑势力，配置完毕后重启游戏即可生效。</p></form>",
				true,
				false );
		noteText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
	}

	public String getDisplayName( )
	{
		return "编辑势力";
	}

}
