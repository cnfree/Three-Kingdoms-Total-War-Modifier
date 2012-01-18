
package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.model.FactionDescription;
import org.sf.feeling.sanguo.patch.model.FactionTexture;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BattleUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.DDSLoader;
import org.sf.feeling.swt.win32.extension.graphics.GraphicsUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.graphics.dds.jogl.DDSImage;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.internal.extension.util.ColorCache;

public class FactionEditPage extends SimpleTabPage
{

	private SortMap factionMap = UnitUtil.getFactionMap( );

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

	private CCombo startImageCombo;

	private Button startImageButton;

	private ImageData startImage;

	private SortMap factionDescriptionMap;

	private CCombo leaderCombo;

	private CCombo leaderFontCombo;

	private CCombo leaderFontSizeCombo;

	private SortMap generalMap;

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
		initPage( );
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
						gc.setTextAntialias( SWT.ON );
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
					gc.setTextAntialias( SWT.ON );
					gc.setLineWidth( 10 );
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

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置开场画面头像：" );
		startImageCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		startImageCombo.setText( "宽：112像素，高：112像素" );
		startImageCombo.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( startImageCombo.getText( ).length( ) == 0 )
				{
					startImageCombo.setText( "宽：112像素，高：112像素" );
				}
			}
		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		startImageCombo.setLayoutData( gd );
		startImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				startImage = null;
				if ( startImageCombo.getSelectionIndex( ) != -1 )
				{
					String generalCode = (String) factionMap.getKeyList( )
							.get( startImageCombo.getSelectionIndex( ) );
					setStartImage( generalCode );
					if ( startImage != null )
						imageCanvas.setImageData( startImage );
				}
			}
		} );

		startImageButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				SWT.PUSH,
				true );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		startImageButton.setLayoutData( gd );
		startImageButton.setText( "自定义" );
		startImageButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				FileDialog dialog = new FileDialog( Display.getDefault( )
						.getActiveShell( ), SWT.NONE );
				dialog.setFilterExtensions( new String[]{
					"*.jpg;*.jpeg;*.png;*.bmp;*.gif;*.tga"
				} ); // Windows
				dialog.setFilterNames( new String[]{
					"图片"
				} );
				String path = dialog.open( );
				if ( path != null && new File( path ).exists( ) )
				{
					startImageCombo.clearSelection( );
					startImageCombo.setText( path );
					startImage = null;
					File imageFile = new File( startImageCombo.getText( )
							.trim( ) );
					if ( imageFile.exists( ) && imageFile.isFile( ) )
					{
						try
						{
							if ( imageFile.getName( )
									.toLowerCase( )
									.endsWith( ".tga" ) )
							{
								ImageData imageData = TgaLoader.loadImage( new FileInputStream( imageFile ),
										true,
										true );
								startImage = computeStartImage( imageData );
								imageCanvas.setImageData( startImage );
							}
							else
							{
								ImageLoader loader = new ImageLoader( );
								ImageData imageData = loader.load( imageFile.getAbsolutePath( ) )[0];
								startImage = computeStartImage( imageData );
								imageCanvas.setImageData( startImage );
							}
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置势力君主：" );
		leaderCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		leaderCombo.setLayoutData( gd );
		ModifyListener leaderModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				modifyLeader( );
			}

		};
		leaderCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				modifyLeader( );
			}

		} );

		leaderFontCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		leaderFontCombo.setItems( fontNames );
		leaderFontCombo.setText( "黑体" );
		gd = new GridData( );
		gd.widthHint = 80;
		leaderFontCombo.setLayoutData( gd );
		leaderFontCombo.addModifyListener( leaderModifyListener );

		leaderFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			leaderFontSizeCombo.add( "" + i );
		}
		leaderFontSizeCombo.setText( "24" );

		gd = new GridData( );
		gd.widthHint = 60;
		leaderFontSizeCombo.setLayoutData( gd );
		leaderFontSizeCombo.addModifyListener( leaderModifyListener );

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

	protected ImageData computeStartImage( ImageData imageData )
	{
		Image image = new Image( null, imageData );
		float ratio = 1.0f;
		if ( imageData.width > imageData.height )
		{
			if ( imageData.height > 112 )
			{
				ratio = 112 * ratio / imageData.height;
			}
			else
			{
				ratio = 112 * ratio / imageData.width;
			}
		}
		else
		{
			if ( imageData.width > 112 )
			{
				ratio = 112 * ratio / imageData.height;
			}
			else
			{
				ratio = 112 * ratio / imageData.width;
			}
		}

		ImageData resizeImageData = GraphicsUtil.resize( image,
				(int) ( imageData.width * ratio + 0.5f ),
				(int) ( imageData.height * ratio + 0.5f ) );

		image.dispose( );

		int offsetX = 0;
		int offsetY = 0;

		if ( resizeImageData.width > resizeImageData.height )
		{
			offsetX = ( 112 - resizeImageData.width ) / 2;
		}
		else
		{
			offsetY = ( 112 - resizeImageData.height ) / 2;
		}

		try
		{
			ImageData tgaData = TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/log.tga" ),
					true );
			byte[] data = tgaData.data;

			for ( int x = 8; x < 120; x++ )
			{
				for ( int y = 8; y < 120; y++ )
				{
					int posX = x - 8 - offsetX;
					int posY = y - 8 - offsetY;

					if ( Math.pow( 64 - x, 2 ) + Math.pow( 64 - y, 2 ) <= 57 * 57 )
					{
						RGB rgb = resizeImageData.palette.getRGB( resizeImageData.getPixel( posX,
								posY ) );
						int index = ( y * 128 + x ) * 4;
						data[index + 1] = (byte) rgb.blue;
						data[index + 2] = (byte) rgb.green;
						data[index + 3] = (byte) rgb.red;
					}
				}
			}
			return tgaData;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	protected void setStartImage( String factionCode )
	{
		String logo = ( (FactionDescription) BattleUtil.getFactionDescriptionMap( )
				.get( factionCode ) ).getLoading_logo( );
		try
		{
			ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( new File( FileConstants.dataFile,
					logo ) ) ),
					true );
			startImage = image;
		}
		catch ( Exception e1 )
		{
			e1.printStackTrace( );
		}
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

	private void initPage( )
	{
		factionDescriptionMap = BattleUtil.getFactionDescriptionMap( );
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		generalMap = UnitUtil.getAvailableGenerals( );
		factionDescriptionMap = BattleUtil.getFactionDescriptionMap( );
		factionMap = UnitUtil.getFactionMap( );
		int index = startImageCombo.getSelectionIndex( );
		startImageCombo.removeAll( );
		for ( int i = 0; i < factionDescriptionMap.size( ); i++ )
		{
			String faction = (String) factionMap.get( (String) factionMap.getKeyList( )
					.get( i ) );
			startImageCombo.add( faction );
		}

		if ( index != -1 && index < startImageCombo.getItemCount( ) )
			startImageCombo.select( index );

		index = leaderCombo.getSelectionIndex( );
		leaderCombo.removeAll( );
		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			leaderCombo.add( generalName );
		}

		if ( index != -1 && index < startImageCombo.getItemCount( ) )
			leaderCombo.select( index );
	}

	private void modifyLeader( )
	{
		if ( leaderCombo.getSelectionIndex( ) > -1 )
		{
			String leader = (String) generalMap.getKeyList( )
					.get( leaderCombo.getSelectionIndex( ) );
			String portrait = ( (General) UnitUtil.getGeneralModels( )
					.get( leader ) ).getPortrait( );
			String faction = idText.getText( );

			try
			{
				File file = new File( FileConstants.factionMapsPath, "map_"
						+ faction.toLowerCase( )
						+ ".tga" );
				InputStream is = null;
				if ( !file.exists( ) )
				{
					is = FactionEditPage.class.getResourceAsStream( "/map_faction.tga" );
				}
				else
				{
					is = new FileInputStream( file );
				}
				ImageData imageData = TgaLoader.loadImage( is, true, true );
				int x, y;
				for ( x = 291; x < imageData.width; x++ )
				{
					for ( y = 0; y < imageData.height; y++ )
					{
						imageData.setPixel( x, y, imageData.getPixel( 290, y ) );
						imageData.setAlpha( x, y, imageData.getAlpha( 290, y ) );
					}
				}

				Image image = new Image( null, imageData );
				GC gc = new GC( image );
				gc.setAdvanced( true );
				gc.setAntialias( SWT.ON );
				gc.setTextAntialias( SWT.ON );
				gc.setInterpolation( SWT.HIGH );

				ImageData portraitImageData = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( new File( FileConstants.customPortraitPath
						+ "\\"
						+ portrait
						+ "\\portrait_young.tga" ) ) ),
						false );
				Image portraitImage = new Image( null, portraitImageData );
				gc.drawImage( portraitImage,
						0,
						0,
						portraitImageData.width,
						portraitImageData.height,
						308 + ( 74 - portraitImageData.width ) / 2,
						28,
						portraitImageData.width,
						portraitImageData.height );

				gc.setTextAntialias( SWT.ON );

				FontData fontData = new FontData( leaderFontCombo.getText( ),
						Integer.parseInt( leaderFontSizeCombo.getText( ) ),
						SWT.BOLD );
				Font font = new Font( null, fontData );
				gc.setFont( font );
				Color color = (Color) ColorCache.getInstance( ).getColor( 255,
						255,
						155 );
				RGB rgb = color.getRGB( );
				gc.setForeground( color );

				Point fontSize = gc.stringExtent( "君主" );
				gc.drawText( "君主",
						( 74 - fontSize.x ) / 2 + 308,
						28 + portraitImageData.height + 5,
						false );

				String name = ChangeCode.toShort( (String) generalMap.get( leader ) );
				if ( name.indexOf( '（' ) > -1 )
					name = name.substring( 0, name.indexOf( '（' ) );
				Point fontSize1 = gc.stringExtent( name );
				for ( ; true; )
				{
					if ( fontSize1.x > fontSize.x + 5 )
					{
						fontData.height = fontData.height - 1;
						font.dispose( );
						font = new Font( null, fontData );
						gc.setFont( font );
						fontSize1 = gc.stringExtent( name );
					}
					else
					{
						break;
					}
				}
				gc.drawText( name, ( 74 - fontSize1.x ) / 2 + 308, 28
						+ portraitImageData.height
						+ 5
						+ fontSize.y
						+ 5, false );

				imageData = image.getImageData( );
				int posX = 308 + ( 74 - portraitImageData.width ) / 2;
				int posY = 28;
				for ( x = posX; x < posX + portraitImageData.width; x++ )
				{
					for ( y = posY; y < posY + portraitImageData.height; y++ )
					{
						imageData.setAlpha( x, y, 255 );
					}
				}

				posX = ( 74 - fontSize.x ) / 2 + 308;
				posY = 28 + portraitImageData.height + 5;

				for ( x = posX - 5; x < imageData.width; x++ )
				{
					for ( y = posY; y < imageData.height; y++ )
					{
						if ( imageData.palette.getRGB( imageData.getPixel( x, y ) )
								.equals( rgb ) )
						{
							imageData.setAlpha( x, y, 255 );
						}
					}

				}

				imageCanvas.setImageData( imageData );
			}
			catch ( IOException e1 )
			{
				e1.printStackTrace( );
			}
		}
	}
}
