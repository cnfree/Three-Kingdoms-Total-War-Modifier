
package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.GeneralParser;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ColorSelector;
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

	private SortMap factionMap;
	private SortMap leaderImageMap = FileUtil.loadProperties( "leaderimage" );
	private SortMap factionTextMap = FileUtil.loadProperties( "factiontext" );
	private SortMap descriptionMap = FileUtil.loadProperties( "factiondescription" );
	private SortMap cultureMap = FileUtil.loadProperties( "culture" );

	ModifyListener nameListener = new ModifyListener( ) {

		public void modifyText( ModifyEvent e )
		{
			checkEnableStatus( );
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
	private ImageData normal48Image;
	private ImageData grey48Image;
	private ImageData roll48Image;
	private ImageData select48Image;
	private ImageData normal24Image;
	private ImageData grey24Image;
	private ImageData roll24Image;
	private ImageData select24Image;

	private SortMap factionDescriptionMap;

	private CCombo leaderCombo;

	private CCombo leaderFontCombo;

	private CCombo leaderFontSizeCombo;

	private SortMap generalMap;
	private ColorSelector bigCaptionBannerColorSelector;
	private ColorSelector smallCaptionBannerColorSelector;
	private ColorSelector battleBannerColorSelector;
	private ColorSelector factionTextColorSelector;
	private CCombo factionTextFontCombo;
	private CCombo factionTextFontSizeCombo;
	private Text factionTextText;
	private CCombo cultureCombo;
	private Button applyButton;
	private CCombo factionCombo;

	private ImageData bigCaptionBannerImage;
	private ImageData smallCaptionBannerImage;
	private ImageData battleBannerImage;
	private ImageData stratBannerImage;
	private ImageData factionTextImage;
	private ImageData leaderImage;
	private ImageData factionImage;

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
		checkEnableStatus( );
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
		layout.numColumns = 6;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "选择编辑势力：" );
		factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		factionCombo.setLayoutData( gd );

		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( factionCombo.getSelectionIndex( ) > -1 )
				{
					idText.setText( (String) factionMap.getKeyList( )
							.get( factionCombo.getSelectionIndex( ) ) );
					nameText.setText( factionCombo.getText( ) );
					FactionDescription desc = (FactionDescription) BattleUtil.getFactionDescriptionMap( )
							.get( idText.getText( ) );
					String culture = (String) cultureMap.get( desc.getCulture( ) );
					if ( culture != null )
						cultureCombo.setText( culture );
					nameText.setEnabled( true );
				}
				else
				{
					idText.setText( "" );
					nameText.setText( "" );
					cultureCombo.setText( "" );
					nameText.setEnabled( false );
				}
				checkEnableStatus( );
			}
		} );

		imageCanvas = WidgetUtil.getToolkit( ).createImageCanvas( patchClient,
				SWT.NONE );
		gd = new GridData( GridData.FILL_VERTICAL );
		gd.verticalSpan = 11;
		gd.widthHint = 256;
		gd.minimumHeight = 256;
		imageCanvas.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "势力代码：" );
		idText = WidgetUtil.getToolkit( ).createText( patchClient,
				"",
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		idText.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置势力名称：" );

		nameText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		nameText.setLayoutData( gd );

		nameText.addModifyListener( nameListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置势力文化：" );

		cultureCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
		cultureCombo.setLayoutData( gd );
		for ( int i = 0; i < cultureMap.getKeyList( ).size( ); i++ )
		{
			cultureCombo.add( (String) cultureMap.get( i ) );
		}

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑派系大旗帜：" );
		bigCaptionBannerText = WidgetUtil.getToolkit( )
				.createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		bigCaptionBannerText.setLayoutData( gd );
		ModifyListener bigCaptionBannerModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				bigCaptionBannerImage = createCaptionBannerImage( );
				if ( bigCaptionBannerImage != null )
					imageCanvas.setImageData( bigCaptionBannerImage );
			}

		};
		bigCaptionBannerText.addModifyListener( bigCaptionBannerModifyListener );

		bigCaptionBannerColorSelector = new ColorSelector( patchClient );
		gd = new GridData( );
		gd.widthHint = 60;
		bigCaptionBannerColorSelector.getButton( ).setLayoutData( gd );
		bigCaptionBannerColorSelector.setColorValue( new RGB( 31, 31, 31 ) );
		bigCaptionBannerColorSelector.addModifyListener( bigCaptionBannerModifyListener );

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
		gd.widthHint = 60;
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
				smallCaptionBannerImage = createSmallCatpionBannerImage( );
				if ( smallCaptionBannerImage != null )
					imageCanvas.setImageData( smallCaptionBannerImage );
			}

		};
		smallCaptionBannerText.addModifyListener( smallCaptionModifyListener );

		smallCaptionBannerColorSelector = new ColorSelector( patchClient );
		gd = new GridData( );
		gd.widthHint = 60;
		smallCaptionBannerColorSelector.getButton( ).setLayoutData( gd );
		smallCaptionBannerColorSelector.setColorValue( new RGB( 21, 21, 21 ) );
		smallCaptionBannerColorSelector.addModifyListener( smallCaptionModifyListener );

		smallCaptionBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		smallCaptionBannerFontCombo.setItems( fontNames );
		smallCaptionBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 60;
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
		gd.horizontalSpan = 2;
		stratBannerText.setLayoutData( gd );

		ModifyListener stratModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				stratBannerImage = createStratBannerImage( );
				if ( stratBannerImage != null )
					imageCanvas.setImageData( stratBannerImage );
			}
		};
		stratBannerText.addModifyListener( stratModifyListener );

		stratBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		stratBannerFontCombo.setItems( fontNames );
		stratBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 60;
		stratBannerFontCombo.setLayoutData( gd );
		stratBannerFontCombo.addModifyListener( stratModifyListener );

		stratBannerFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			stratBannerFontSizeCombo.add( "" + i );
		}
		stratBannerFontSizeCombo.setText( "42" );

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
				battleBannerImage = createBattleBannerImage( );
				if ( battleBannerImage != null )
				{
					imageCanvas.setImageData( battleBannerImage );
				}
			}
		};
		battleBannerText.addModifyListener( battleModifyListener );

		battleBannerColorSelector = new ColorSelector( patchClient );
		gd = new GridData( );
		gd.widthHint = 60;
		battleBannerColorSelector.getButton( ).setLayoutData( gd );
		battleBannerColorSelector.setColorValue( new RGB( 21, 21, 21 ) );
		battleBannerColorSelector.addModifyListener( battleModifyListener );

		battleBannerFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		battleBannerFontCombo.setItems( fontNames );
		battleBannerFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 60;
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
		gd.horizontalSpan = 3;
		gd.widthHint = 180;
		startImageCombo.setLayoutData( gd );
		startImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				startImage = null;
				if ( startImageCombo.getSelectionIndex( ) != -1 )
				{
					String factionCode = (String) factionMap.getKeyList( )
							.get( startImageCombo.getSelectionIndex( ) );
					setStartImage( factionCode );
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
							ImageData imageData = null;
							if ( imageFile.getName( )
									.toLowerCase( )
									.endsWith( ".tga" ) )
							{
								imageData = TgaLoader.loadImage( new FileInputStream( imageFile ),
										true,
										true );

							}
							else
							{
								ImageLoader loader = new ImageLoader( );
								imageData = loader.load( imageFile.getAbsolutePath( ) )[0];
							}
							if ( imageData != null )
							{
								factionImage = getFactionImage( imageData );
								startImage = computeStartImage( imageData );
								normal48Image = compute48Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol48.tga" ),
												true ) );
								Image normalImage = new Image( null,
										normal48Image );
								Image grayImage = new Image( null,
										normalImage,
										SWT.IMAGE_GRAY );
								grey48Image = grayImage.getImageData( );
								normalImage.dispose( );
								grayImage.dispose( );

								normal24Image = compute24Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol24.tga" ),
												true ) );
								normalImage = new Image( null, normal24Image );
								grayImage = new Image( null,
										normalImage,
										SWT.IMAGE_GRAY );
								grey24Image = grayImage.getImageData( );
								normalImage.dispose( );
								grayImage.dispose( );

								roll48Image = compute48Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol48_roll.tga" ),
												true ) );
								roll24Image = compute24Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol24_roll.tga" ),
												true ) );

								select48Image = compute48Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol48_select.tga" ),
												true ) );
								select24Image = compute24Image( imageData,
										TgaLoader.loadImage( FactionEditPage.class.getResourceAsStream( "/symbol24_select.tga" ),
												true ) );

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
		gd.horizontalSpan = 2;
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
		gd.widthHint = 60;
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

		WidgetUtil.getToolkit( ).createLabel( patchClient, "编辑势力外交文字：" );
		factionTextText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		factionTextText.setLayoutData( gd );
		ModifyListener factionTextModifyListener = new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				factionTextImage = createFactionTextImage( );
				if ( factionTextImage != null )
					imageCanvas.setImageData( factionTextImage );
			}
		};
		factionTextText.addModifyListener( factionTextModifyListener );

		factionTextColorSelector = new ColorSelector( patchClient );
		gd = new GridData( );
		gd.widthHint = 60;
		factionTextColorSelector.getButton( ).setLayoutData( gd );
		factionTextColorSelector.setColorValue( new RGB( 0, 0, 255 ) );
		factionTextColorSelector.addModifyListener( factionTextModifyListener );

		factionTextFontCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		factionTextFontCombo.setItems( fontNames );
		factionTextFontCombo.setText( "楷体" );
		gd = new GridData( );
		gd.widthHint = 60;
		factionTextFontCombo.setLayoutData( gd );
		factionTextFontCombo.addModifyListener( factionTextModifyListener );

		factionTextFontSizeCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		for ( int i = 8; i < 72; i++ )
		{
			factionTextFontSizeCombo.add( "" + i );
		}
		factionTextFontSizeCombo.setText( "16" );

		gd = new GridData( );
		gd.widthHint = 60;
		factionTextFontSizeCombo.setLayoutData( gd );
		factionTextFontSizeCombo.addModifyListener( factionTextModifyListener );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 6;
		Composite buttonGroup = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		buttonGroup.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		buttonGroup.setLayout( layout );

		applyButton = WidgetUtil.getToolkit( ).createButton( buttonGroup,
				"应用",
				SWT.PUSH );
		applyButton.setEnabled( false );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.END;
		applyButton.setLayoutData( gd );
		applyButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				applyButton.setEnabled( false );
				String factionName = nameText.getText( ).trim( );
				String faction = factionCombo.getText( ).trim( );

				List txtFiles = new ArrayList( );
				if ( !faction.equals( factionName ) )
				{
					txtFiles.add( FileConstants.expandedBiFile );
					txtFiles.add( FileConstants.campaignDescriptionFile );
					txtFiles.add( FileConstants.factionPropertiesFile );
				}

				FactionDescription desc = (FactionDescription) BattleUtil.getFactionDescriptionMap( )
						.get( idText.getText( ) );
				String culture = (String) cultureMap.get( desc.getCulture( ) );

				if ( !culture.equals( cultureCombo.getText( ) ) )
				{
					txtFiles.add( FileConstants.descrFactionsFile );
				}

				List imageFiles = new ArrayList( );
				if ( bigCaptionBannerText.getText( ).trim( ).length( ) > 0 )
				{
					imageFiles.add( new File( FileConstants.captainBannerPath,
							"captain_portrait_" + idText.getText( ) + ".tga" ) );
				}
				if ( smallCaptionBannerText.getText( ).trim( ).length( ) > 0 )
				{
					imageFiles.add( new File( FileConstants.captainBannerPath,
							"captain_card_" + idText.getText( ) + ".tga" ) );
				}
				if ( stratBannerText.getText( ).trim( ).length( ) > 0 )
				{
					FactionDescription factionDesc = (FactionDescription) BattleUtil.getFactionDescriptionMap( )
							.get( idText.getText( ) );
					int index = Integer.parseInt( factionDesc.getStandard_index( ) ) / 4 + 1;
					imageFiles.add( new File( FileConstants.stratBannerPath,
							"symbols" + index + ".tga.dds" ) );
				}
				if ( battleBannerText.getText( ).trim( ).length( ) > 0 )
				{
					FactionTexture texture = (FactionTexture) BattleUtil.getFactionTextureMap( )
							.get( idText.getText( ) );
					imageFiles.add( new File( FileConstants.dataFile,
							texture.getStandard_texture( ) + ".dds" ) );
				}
				if ( factionTextText.getText( ).trim( ).length( ) > 0 )
				{
					for ( int i = 0; i < FileConstants.cultures.length; i++ )
					{
						String haredpage = FileConstants.uiPath
								+ "\\"
								+ FileConstants.cultures[i]
								+ "\\interface\\sharedpage_01.tga";
						imageFiles.add( new File( haredpage ) );
					}
				}
				if ( startImageCombo.getSelectionIndex( ) > -1 )
				{
					if ( !startImageCombo.getText( ).trim( ).equals( faction ) )
					{
						saveFactionLogoImages( imageFiles );
					}
				}
				else if ( startImageCombo.getText( ).trim( ).length( ) > 0 )
				{
					File file = new File( startImageCombo.getText( ).trim( ) );
					if ( file.exists( ) )
					{
						saveFactionLogoImages( imageFiles );
					}
				}
				if ( leaderCombo.getSelectionIndex( ) > -1 )
				{
					imageFiles.add( new File( FileConstants.factionMapsPath,
							"map_"
									+ idText.getText( ).trim( ).toLowerCase( )
									+ ".tga" ) );
				}

				List bakList = new ArrayList( );
				bakList.addAll( txtFiles );
				bakList.addAll( imageFiles );

				if ( imageFiles.size( ) > 0 )
				{
					if ( txtFiles.size( ) > 0 )
					{
						BakUtil.bakDataAndResources( "编辑势力："
								+ faction
								+ "-->"
								+ factionName,
								(File[]) bakList.toArray( new File[0] ) );
					}
					else
					{
						BakUtil.bakDataAndResources( "编辑势力：" + faction,
								(File[]) bakList.toArray( new File[0] ) );
					}
				}

				if ( !culture.equals( cultureCombo.getText( ) ) )
				{
					if ( FileConstants.descrFactionsFile.exists( ) )
					{
						try
						{
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.descrFactionsFile ),
									"GBK" ) );
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							boolean startFaction = false;
							boolean isEnd = false;
							while ( ( line = in.readLine( ) ) != null )
							{

								if ( !isEnd )
								{
									if ( startFaction == false )
									{
										Pattern pattern = Pattern.compile( "^\\s*faction\\s+"
												+ idText.getText( ),
												Pattern.CASE_INSENSITIVE );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											startFaction = true;
										}
									}
									else
									{
										Pattern pattern = Pattern.compile( "^\\s*culture\\s+",
												Pattern.CASE_INSENSITIVE );
										Matcher matcher = pattern.matcher( line );
										if ( matcher.find( ) )
										{
											printer.println( "culture						"
													+ cultureMap.getKeyList( )
															.get( cultureCombo.getSelectionIndex( ) ) );
											startFaction = true;
											isEnd = true;
											continue;
										}
									}
								}
								printer.println( line );
							}
							in.close( );
							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.descrFactionsFile ),
									"GBK" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
							printer.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}

				}

				if ( txtFiles.size( ) > 0 )
				{
					FileUtil.replaceFile( FileConstants.factionPropertiesFile,
							faction + "=" + idText.getText( ).trim( ),
							faction + "=" + idText.getText( ).trim( ),
							factionName + "=" + idText.getText( ).trim( ),
							"UTF-8" );
					FileUtil.replaceFile( FileConstants.expandedBiFile,
							ChangeCode.toShort( factionCombo.getText( ).trim( ) ),
							".+",
							ChangeCode.toShort( factionName ),
							"UTF-16LE" );

					if ( FileConstants.campaignDescriptionFile.exists( ) )
					{
						try
						{
							BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( FileConstants.campaignDescriptionFile ),
									"UTF-16LE" ) );
							String line = null;
							StringWriter writer = new StringWriter( );
							PrintWriter printer = new PrintWriter( writer );
							String description = (String) descriptionMap.get( idText.getText( )
									.trim( ) );
							while ( ( line = in.readLine( ) ) != null )
							{

								Pattern pattern = Pattern.compile( "^\\s*\\{\\s*"
										+ description );
								Matcher matcher = pattern.matcher( line );
								if ( matcher.find( ) )
								{
									if ( line.toLowerCase( )
											.indexOf( ( description + "_TITLE" ).toLowerCase( ) ) > -1 )
									{
										String general = null;
										String leader = leaderCombo.getText( )
												.trim( );
										if ( leaderCombo.getSelectionIndex( ) > -1 )
										{
											general = (String) generalMap.getKeyList( )
													.get( leaderCombo.getSelectionIndex( ) );

										}
										else
										{
											general = (String) UnitUtil.getFactionLeaderMap( )
													.get( idText.getText( )
															.trim( ) );
										}

										leader = (String) generalMap.get( general );
										if ( leader.indexOf( '（' ) > -1 )
											leader = leader.substring( 0,
													leader.indexOf( '（' ) );

										printer.println( line.substring( 0,
												line.indexOf( '▁' ) + 1 )
												+ ChangeCode.toShort( nameText.getText( )
														.trim( )
														+ "军▁君主："
														+ leader ) );
										line = in.readLine( );
										printer.println( line );
										line = in.readLine( );

										String juewei = (String) GeneralParser.getGeneralSkills( general )
												.getKeyList( )
												.get( 2 );
										String leaderDescriptor = GeneralParser.getGeneralDescriptor( juewei );
										int index = line.indexOf( "\\n\\n" );
										if ( index > -1 )
										{
											printer.println( line.substring( 0,
													index )
													+ "\\n\\n勢力君主："
													+ leaderDescriptor );
										}
										continue;
									}
								}
								printer.println( line );
							}
							in.close( );
							PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( FileConstants.campaignDescriptionFile ),
									"UTF-16LE" ) ),
									false );
							out.print( writer.getBuffer( ) );
							out.close( );
							printer.close( );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}

				}

				if ( bigCaptionBannerText.getText( ).trim( ).length( ) > 0 )
				{
					if ( bigCaptionBannerImage != null )
					{
						try
						{
							TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.captainBannerPath,
									"captain_portrait_"
											+ idText.getText( )
											+ ".tga" ) ),
									bigCaptionBannerImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				if ( smallCaptionBannerText.getText( ).trim( ).length( ) > 0 )
				{
					if ( smallCaptionBannerImage != null )
					{
						try
						{
							TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.captainBannerPath,
									"captain_card_"
											+ idText.getText( )
											+ ".tga" ) ),
									smallCaptionBannerImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				if ( stratBannerText.getText( ).trim( ).length( ) > 0 )
				{
					if ( stratBannerImage != null )
					{
						FactionDescription factionDesc = (FactionDescription) BattleUtil.getFactionDescriptionMap( )
								.get( idText.getText( ) );
						int standardIndex = Integer.parseInt( factionDesc.getStandard_index( ) );
						int index = standardIndex / 4 + 1;
						int position = standardIndex % 4;
						File file = new File( FileConstants.stratBannerPath,
								"symbols" + index + ".tga.dds" );
						try
						{
							RGB white = new RGB( 255, 255, 255 );
							DDSImage image = DDSLoader.loadDDSImage( new FileInputStream( file ) );
							ImageData imageData = DDSLoader.getImageData( image );
							int offsetX = 64 * ( position % 2 );
							int offsetY = 64 * ( position / 2 );
							for ( int i = 0; i < 64; i++ )
							{
								for ( int j = 0; j < 64; j++ )
								{
									if ( white.equals( stratBannerImage.palette.getRGB( stratBannerImage.getPixel( i,
											j ) ) ) )
									{
										imageData.data[( ( i + offsetX ) + ( j + offsetY ) * 128 ) * 4 + 3] = (byte) 255;
									}
									else
									{
										imageData.data[( ( i + offsetX ) + ( j + offsetY ) * 128 ) * 4 + 3] = 0;
									}
								}
							}

							DDSLoader.saveImage( new FileOutputStream( file ),
									imageData,
									image.getPixelFormat( ),
									image.getNumMipMaps( ) > 1 );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				if ( battleBannerText.getText( ).trim( ).length( ) > 0 )
				{
					if ( battleBannerImage != null )
					{
						try
						{
							FactionTexture texture = (FactionTexture) BattleUtil.getFactionTextureMap( )
									.get( idText.getText( ) );
							File ddsFile = new File( FileConstants.dataFile,
									texture.getStandard_texture( ) + ".dds" );
							DDSImage image = DDSLoader.loadDDSImage( new FileInputStream( ddsFile ) );
							DDSLoader.saveImage( new FileOutputStream( ddsFile ),
									battleBannerImage,
									image.getPixelFormat( ),
									image.getNumMipMaps( ) > 1 );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}

				}
				if ( factionTextText.getText( ).trim( ).length( ) > 0 )
				{
					if ( factionTextImage != null )
					{
						try
						{
							for ( int i = 0; i < FileConstants.cultures.length; i++ )
							{
								String sharedpage = FileConstants.uiPath
										+ "\\"
										+ FileConstants.cultures[i]
										+ "\\interface\\sharedpage_01.tga";
								ImageData image = TgaLoader.loadImage( new FileInputStream( new File( sharedpage ) ) );
								String[] dest = factionTextMap.get( idText.getText( )
										.trim( ) )
										.toString( )
										.split( "," );
								int destY = Integer.parseInt( dest[0] );
								int destX = Integer.parseInt( dest[1] );

								int offsetX = ( factionTextImage.width - 34 ) / 2;
								int offsetY = ( factionTextImage.height - 33 ) / 2;

								for ( int x = 0; x < 33; x++ )
								{
									for ( int y = 0; y < 33; y++ )
									{
										if ( x >= factionTextImage.width
												|| y >= factionTextImage.height )
											continue;
										image.setPixel( destX
												* 33
												+ x
												- offsetX
												+ 257,
												destY * 33 + y - offsetY + 257,
												image.palette.getPixel( factionTextImage.palette.getRGB( factionTextImage.getPixel( x,
														y ) ) ) );
									}
								}
								TgaLoader.saveImage( new FileOutputStream( new File( sharedpage ) ),
										image );
							}
						}
						catch ( Exception e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				if ( startImageCombo.getSelectionIndex( ) > -1 )
				{
					if ( startImage != null )
					{
						try
						{
							String logo = ( (FactionDescription) BattleUtil.getFactionDescriptionMap( )
									.get( idText.getText( ).trim( ) ) ).getLoading_logo( );
							TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.dataFile,
									logo ) ),
									startImage );
							saveMenuImages( );

							String factionCode = (String) factionMap.getKeyList( )
									.get( startImageCombo.getSelectionIndex( ) );

							String[] dest = leaderImageMap.get( idText.getText( )
									.trim( ) )
									.toString( )
									.split( "," );
							int destY = Integer.parseInt( dest[0] );
							int destX = Integer.parseInt( dest[1] );

							String[] src = leaderImageMap.get( factionCode )
									.toString( )
									.split( "," );
							int srcY = Integer.parseInt( src[0] );
							int srcX = Integer.parseInt( src[1] );

							for ( int i = 0; i < FileConstants.cultures.length; i++ )
							{
								String stratpage = FileConstants.uiPath
										+ "\\"
										+ FileConstants.cultures[i]
										+ "\\interface\\stratpage_02.tga";
								ImageData image = TgaLoader.loadImage( new FileInputStream( new File( stratpage ) ) );
								for ( int x = 0; x < 53; x++ )
								{
									for ( int y = 0; y < 53; y++ )
									{
										image.setPixel( destX * 53 + x, destY
												* 53
												+ y, image.getPixel( srcX
												* 53
												+ x, srcY * 53 + y ) );
									}
								}
								TgaLoader.saveImage( new FileOutputStream( new File( stratpage ) ),
										image );
							}
						}
						catch ( Exception e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				else if ( startImageCombo.getText( ).trim( ).length( ) > 0 )
				{
					if ( startImage != null )
					{
						try
						{
							String logo = ( (FactionDescription) BattleUtil.getFactionDescriptionMap( )
									.get( idText.getText( ).trim( ) ) ).getLoading_logo( );
							TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.dataFile,
									logo ) ),
									startImage );
							saveCustomMenuImages( );

							for ( int i = 0; i < FileConstants.cultures.length; i++ )
							{
								String stratpage = FileConstants.uiPath
										+ "\\"
										+ FileConstants.cultures[i]
										+ "\\interface\\stratpage_02.tga";
								ImageData image = TgaLoader.loadImage( new FileInputStream( new File( stratpage ) ) );
								String[] dest = leaderImageMap.get( idText.getText( )
										.trim( ) )
										.toString( )
										.split( "," );
								int destY = Integer.parseInt( dest[0] );
								int destX = Integer.parseInt( dest[1] );

								int offsetX = 0;
								int offsetY = 0;
								if ( factionImage.width > factionImage.height )
								{
									offsetX = ( 53 - factionImage.width ) / 2;
								}
								else
								{
									offsetY = ( 53 - factionImage.height ) / 2;
								}

								for ( int x = 0; x < 53; x++ )
								{
									for ( int y = 0; y < 53; y++ )
									{

										image.setPixel( destX * 53 + x,
												destY * 53 + y,
												image.palette.getPixel( factionImage.palette.getRGB( factionImage.getPixel( x
														- offsetX,
														y - offsetY ) ) ) );
									}
								}
								TgaLoader.saveImage( new FileOutputStream( new File( stratpage ) ),
										image );
							}
						}
						catch ( Exception e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				if ( leaderCombo.getSelectionIndex( ) > -1 )
				{
					if ( leaderImage != null )
					{
						try
						{
							TgaLoader.saveImage( new FileOutputStream( new File( FileConstants.factionMapsPath,
									"map_"
											+ idText.getText( )
													.trim( )
													.toLowerCase( )
											+ ".tga" ) ),
									leaderImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				MapUtil.initMap( );
				applyButton.setEnabled( true );
				checkEnableStatus( );
				refresh( );
			}
		} );

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
				refreshPage( );
				restoreButton.setEnabled( true );
			}
		} );

		patchSection.setClient( patchClient );
	}

	protected ImageData getFactionImage( ImageData imageData )
	{
		Image image = new Image( null, imageData );
		float ratio = 1.0f;
		if ( imageData.width > imageData.height )
		{
			if ( imageData.height > 53 )
			{
				ratio = 53 * ratio / imageData.height;
			}
			else
			{
				ratio = 53 * ratio / imageData.width;
			}
		}
		else
		{
			if ( imageData.width > 53 )
			{
				ratio = 53 * ratio / imageData.height;
			}
			else
			{
				ratio = 53 * ratio / imageData.width;
			}
		}

		ImageData resizeImageData = GraphicsUtil.resize( image,
				(int) ( imageData.width * ratio + 0.5f ),
				(int) ( imageData.height * ratio + 0.5f ) );
		image.dispose( );

		return resizeImageData;
	}

	private void checkEnableStatus( )
	{
		if ( nameText.getText( ).trim( ).length( ) > 0
				&& idText.getText( ).trim( ).length( ) > 0 )
		{
			bigCaptionBannerText.setEnabled( true );
			bigCaptionBannerFontCombo.setEnabled( true );
			bigCaptionBannerFontSizeCombo.setEnabled( true );
			smallCaptionBannerText.setEnabled( true );
			smallCaptionBannerFontCombo.setEnabled( true );
			smallCaptionBannerFontSizeCombo.setEnabled( true );
			battleBannerText.setEnabled( true );
			battleBannerFontCombo.setEnabled( true );
			battleBannerFontSizeCombo.setEnabled( true );
			stratBannerText.setEnabled( true );
			stratBannerFontCombo.setEnabled( true );
			stratBannerFontSizeCombo.setEnabled( true );
			startImageCombo.setEnabled( true );
			startImageButton.setEnabled( true );
			leaderCombo.setEnabled( true );
			leaderFontCombo.setEnabled( true );
			leaderFontSizeCombo.setEnabled( true );
			bigCaptionBannerColorSelector.setEnabled( true );
			smallCaptionBannerColorSelector.setEnabled( true );
			battleBannerColorSelector.setEnabled( true );
			cultureCombo.setEnabled( true );
			factionTextText.setEnabled( true );
			factionTextColorSelector.setEnabled( true );
			factionTextFontCombo.setEnabled( true );
			factionTextFontSizeCombo.setEnabled( true );
			applyButton.setEnabled( true );
		}
		else
		{
			bigCaptionBannerText.setEnabled( false );
			bigCaptionBannerFontCombo.setEnabled( false );
			bigCaptionBannerFontSizeCombo.setEnabled( false );
			smallCaptionBannerText.setEnabled( false );
			smallCaptionBannerFontCombo.setEnabled( false );
			smallCaptionBannerFontSizeCombo.setEnabled( false );
			battleBannerText.setEnabled( false );
			battleBannerFontCombo.setEnabled( false );
			battleBannerFontSizeCombo.setEnabled( false );
			stratBannerText.setEnabled( false );
			stratBannerFontCombo.setEnabled( false );
			stratBannerFontSizeCombo.setEnabled( false );
			startImageCombo.setEnabled( false );
			startImageButton.setEnabled( false );
			leaderCombo.setEnabled( false );
			leaderFontCombo.setEnabled( false );
			leaderFontSizeCombo.setEnabled( false );
			bigCaptionBannerColorSelector.setEnabled( false );
			smallCaptionBannerColorSelector.setEnabled( false );
			battleBannerColorSelector.setEnabled( false );
			cultureCombo.setEnabled( false );
			factionTextText.setEnabled( false );
			factionTextColorSelector.setEnabled( false );
			factionTextFontCombo.setEnabled( false );
			factionTextFontSizeCombo.setEnabled( false );
			applyButton.setEnabled( false );
		}
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

					if ( posX < 0
							|| posX >= resizeImageData.width
							|| posY < 0
							|| posY >= resizeImageData.height )
						continue;

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

	protected ImageData compute48Image( ImageData imageData,
			ImageData orginImage )
	{
		Image image = new Image( null, imageData );
		float ratio = 1.0f;
		if ( imageData.width > imageData.height )
		{
			if ( imageData.height > 42 )
			{
				ratio = 42 * ratio / imageData.height;
			}
			else
			{
				ratio = 42 * ratio / imageData.width;
			}
		}
		else
		{
			if ( imageData.width > 42 )
			{
				ratio = 42 * ratio / imageData.height;
			}
			else
			{
				ratio = 42 * ratio / imageData.width;
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
			offsetX = ( 42 - resizeImageData.width ) / 2;
		}
		else
		{
			offsetY = ( 42 - resizeImageData.height ) / 2;
		}

		ImageData tgaData = orginImage;
		byte[] data = orginImage.data;

		for ( int x = 9; x < 50; x++ )
		{
			for ( int y = 9; y < 50; y++ )
			{
				int posX = x - 9 - offsetX;
				int posY = y - 9 - offsetY;

				if ( posX < 0
						|| posX >= resizeImageData.width
						|| posY < 0
						|| posY >= resizeImageData.height )
					continue;

				if ( Math.pow( 30 - x, 2 ) + Math.pow( 30 - y, 2 ) <= 21 * 21 )
				{
					RGB rgb = resizeImageData.palette.getRGB( resizeImageData.getPixel( posX,
							posY ) );
					int index = ( y * 59 + x ) * 4;
					data[index + 1] = (byte) rgb.blue;
					data[index + 2] = (byte) rgb.green;
					data[index + 3] = (byte) rgb.red;
				}
			}
		}
		return tgaData;
	}

	protected ImageData compute24Image( ImageData imageData,
			ImageData orginImage )
	{
		Image image = new Image( null, imageData );
		float ratio = 1.0f;
		if ( imageData.width > imageData.height )
		{
			if ( imageData.height > 22 )
			{
				ratio = 22 * ratio / imageData.height;
			}
			else
			{
				ratio = 22 * ratio / imageData.width;
			}
		}
		else
		{
			if ( imageData.width > 22 )
			{
				ratio = 22 * ratio / imageData.height;
			}
			else
			{
				ratio = 22 * ratio / imageData.width;
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
			offsetX = ( 22 - resizeImageData.width ) / 2;
		}
		else
		{
			offsetY = ( 22 - resizeImageData.height ) / 2;
		}

		ImageData tgaData = orginImage;
		byte[] data = orginImage.data;

		for ( int x = 5; x < 25; x++ )
		{
			for ( int y = 5; y < 25; y++ )
			{
				int posX = x - 5 - offsetX;
				int posY = y - 5 - offsetY;

				if ( posX < 0
						|| posX >= resizeImageData.width
						|| posY < 0
						|| posY >= resizeImageData.height )
					continue;

				if ( Math.pow( 15 - x, 2 ) + Math.pow( 15 - y, 2 ) <= 11 * 11 )
				{
					RGB rgb = resizeImageData.palette.getRGB( resizeImageData.getPixel( posX,
							posY ) );
					int index = ( y * 30 + x ) * 4;
					data[index + 1] = (byte) rgb.blue;
					data[index + 2] = (byte) rgb.green;
					data[index + 3] = (byte) rgb.red;
				}
			}
		}
		return tgaData;
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

		int index = factionCombo.getSelectionIndex( );

		String faction = factionCombo.getText( );

		factionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( faction ) )
			factionCombo.setText( faction );

		index = startImageCombo.getSelectionIndex( );
		startImageCombo.removeAll( );
		for ( int i = 0; i < factionDescriptionMap.size( ); i++ )
		{
			faction = (String) factionMap.get( (String) factionMap.getKeyList( )
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

		if ( index != -1 && index < leaderCombo.getItemCount( ) )
			leaderCombo.select( index );
	}

	private void modifyLeader( )
	{
		if ( leaderCombo.getSelectionIndex( ) > -1 )
		{
			leaderImage = createLeaderImage( );

			if ( leaderImage != null )
			{
				imageCanvas.setImageData( leaderImage );
			}
		}
	}

	private ImageData createLeaderImage( )
	{
		ImageData leaderImage = null;
		String leader = (String) generalMap.getKeyList( )
				.get( leaderCombo.getSelectionIndex( ) );
		String portrait = ( (General) UnitUtil.getGeneralModels( ).get( leader ) ).getPortrait( );
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
			gc.dispose( );
			font.dispose( );
			leaderImage = imageData;
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}
		return leaderImage;
	}

	private ImageData createCaptionBannerImage( )
	{
		ImageData captionBannerImage = null;
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
			Font font = new Font( null, fontData );
			gc.setFont( font );
			gc.setForeground( ColorCache.getInstance( )
					.getColor( bigCaptionBannerColorSelector.getColorValue( ) ) );
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

			captionBannerImage = oldImage.getImageData( );
			font.dispose( );
			gc.dispose( );
			oldImage.dispose( );
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}

		return captionBannerImage;
	}

	private ImageData createSmallCatpionBannerImage( )
	{
		ImageData smallCaptionBannerImage = null;
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
			Font font = new Font( null, fontData );
			gc.setFont( font );
			gc.setForeground( ColorCache.getInstance( )
					.getColor( smallCaptionBannerColorSelector.getColorValue( ) ) );
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
			smallCaptionBannerImage = oldImage.getImageData( );
			gc.dispose( );
			font.dispose( );
			oldImage.dispose( );
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}
		return smallCaptionBannerImage;
	}

	private ImageData createStratBannerImage( )
	{
		ImageData stratBanner = null;
		Image image = new Image( null, 64, 64 );
		GC gc = new GC( image );
		gc.setAdvanced( true );
		gc.setAntialias( SWT.ON );
		FontData fontData = new FontData( stratBannerFontCombo.getText( ),
				Integer.parseInt( stratBannerFontSizeCombo.getText( ) ),
				SWT.BOLD );
		Font font = new Font( null, fontData );
		gc.setFont( font );
		gc.setBackground( ColorCache.getInstance( ).getColor( 0, 0, 0 ) );
		gc.fillRectangle( 0, 0, 64, 64 );
		gc.setForeground( ColorCache.getInstance( ).getColor( 255, 255, 255 ) );
		if ( stratBannerText.getText( ).trim( ).length( ) > 0 )
		{
			char[] chars = ChangeCode.toShort( stratBannerText.getText( )
					.trim( ) ).toCharArray( );
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
		stratBanner = image.getImageData( );

		gc.dispose( );
		font.dispose( );
		image.dispose( );
		return stratBanner;
	}

	private ImageData createBattleBannerImage( )
	{
		ImageData battleBanner = null;
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
			Font font = new Font( null, fontData );
			gc.setFont( font );
			gc.setForeground( ColorCache.getInstance( )
					.getColor( battleBannerColorSelector.getColorValue( ) ) );
			if ( battleBannerText.getText( ).trim( ).length( ) > 0 )
			{
				char[] chars = ChangeCode.toShort( battleBannerText.getText( )
						.trim( ) ).toCharArray( );
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
			battleBanner = image.getImageData( );
			font.dispose( );
			gc.dispose( );
			image.dispose( );
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace( );
		}
		return battleBanner;
	}

	private void saveFactionLogoImages( List imageFiles )
	{
		for ( int i = 0; i < FileConstants.cultures.length; i++ )
		{
			String haredpage = FileConstants.uiPath
					+ "\\"
					+ FileConstants.cultures[i]
					+ "\\interface\\stratpage_02.tga";
			imageFiles.add( new File( haredpage ) );
		}

		String logo = ( (FactionDescription) BattleUtil.getFactionDescriptionMap( )
				.get( idText.getText( ).trim( ) ) ).getLoading_logo( );
		imageFiles.add( new File( FileConstants.dataFile, logo ) );

		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_48", "symbol48_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ ".tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_48", "symbol48_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_grey.tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_48", "symbol48_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_roll.tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_48", "symbol48_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_select.tga" ) );

		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_24", "symbol24_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ ".tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_24", "symbol24_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_grey.tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_24", "symbol24_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_roll.tga" ) );
		imageFiles.add( new File( FileConstants.menuSymbolsPath
				+ "\\FE_buttons_24", "symbol24_"
				+ idText.getText( ).trim( ).toLowerCase( )
				+ "_select.tga" ) );
	}

	private ImageData createFactionTextImage( )
	{
		ImageData factionTextImage;
		Image image = new Image( null, 29, 29 );
		GC gc = new GC( image );
		gc.setAdvanced( true );
		gc.setTextAntialias( SWT.ON );

		RGB rgb = factionTextColorSelector.getColorValue( );
		gc.setBackground( ColorCache.getInstance( )
				.getColor( (int) ( rgb.red > 127 ? 255 : ( 255 * 0.85 ) ),
						(int) ( rgb.green > 127 ? 255 : ( 255 * 0.85 ) ),
						(int) ( rgb.blue > 127 ? 255 : ( 255 * 0.85 ) ) ) );
		gc.setForeground( ColorCache.getInstance( )
				.getColor( (int) ( rgb.red > 127 ? 255 : ( 255 * 0.4 ) ),
						(int) ( rgb.green > 127 ? 255 : ( 255 * 0.4 ) ),
						(int) ( rgb.blue > 127 ? 255 : ( 255 * 0.4 ) ) ) );
		gc.fillGradientRectangle( 0, 0, 29, 29, true );
		gc.setForeground( ColorCache.getInstance( ).getColor( rgb ) );

		Font font = null;
		try
		{
			FontData fontData = new FontData( factionTextFontCombo.getText( ),
					Integer.parseInt( factionTextFontSizeCombo.getText( ) ),
					SWT.BOLD );
			font = new Font( null, fontData );
			gc.setFont( font );
		}
		catch ( NumberFormatException e1 )
		{
		}

		if ( factionTextText.getText( ).length( ) > 0 )
		{
			Point point = gc.textExtent( factionTextText.getText( 0, 0 ) );
			gc.drawText( ChangeCode.toShort( factionTextText.getText( 0, 0 ) ),
					( 29 - point.x ) / 2,
					( 29 - point.y ) / 2,
					true );
		}

		factionTextImage = image.getImageData( );

		if ( font != null )
			font.dispose( );
		image.dispose( );
		gc.dispose( );

		return factionTextImage;
	}

	protected void saveMenuImages( ) throws IOException, FileNotFoundException
	{
		String factionCode = (String) factionMap.getKeyList( )
				.get( startImageCombo.getSelectionIndex( ) );
		String[] size = new String[]{
				"48", "24"
		};
		String[] status = new String[]{
				"", "_grey", "_roll", "_select"
		};
		for ( int i = 0; i < size.length; i++ )
		{
			for ( int j = 0; j < status.length; j++ )
			{
				String dir = FileConstants.menuSymbolsPath
						+ "\\FE_buttons_"
						+ size[i];
				String outFile = "symbol"
						+ size[i]
						+ "_"
						+ idText.getText( ).trim( ).toLowerCase( )
						+ status[j]
						+ ".tga";
				String inFile = "symbol"
						+ size[i]
						+ "_"
						+ factionCode.toLowerCase( )
						+ status[j]
						+ ".tga";
				TgaLoader.saveImage( new FileOutputStream( new File( dir,
						outFile ) ),
						TgaLoader.loadImage( new FileInputStream( new File( dir,
								inFile ) ) ) );
			}
		}
	}

	protected void saveCustomMenuImages( ) throws IOException,
			FileNotFoundException
	{
		ImageData[] images = new ImageData[]{
				normal48Image,
				normal24Image,
				grey48Image,
				grey24Image,
				roll48Image,
				roll24Image,
				select48Image,
				select24Image
		};
		String[] size = new String[]{
				"48", "24"
		};
		String[] status = new String[]{
				"", "_grey", "_roll", "_select"
		};
		for ( int i = 0; i < size.length; i++ )
		{
			for ( int j = 0; j < status.length; j++ )
			{
				String dir = FileConstants.menuSymbolsPath
						+ "\\FE_buttons_"
						+ size[i];
				String outFile = "symbol"
						+ size[i]
						+ "_"
						+ idText.getText( ).trim( ).toLowerCase( )
						+ status[j]
						+ ".tga";
				TgaLoader.saveImage( new FileOutputStream( new File( dir,
						outFile ) ), images[j * size.length + i] );
			}
		}
	}
}
