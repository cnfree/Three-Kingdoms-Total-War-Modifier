
package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.dialog.BaowuHolderModifyDialog;
import org.sf.feeling.sanguo.patch.dialog.GeneralModifyDialog;
import org.sf.feeling.sanguo.patch.dialog.JueweiDialog;
import org.sf.feeling.sanguo.patch.dialog.PositionDialog;
import org.sf.feeling.sanguo.patch.dialog.SkillDialog;
import org.sf.feeling.sanguo.patch.dialog.UnitModifyDialog;
import org.sf.feeling.sanguo.patch.model.General;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.CustomGeneral;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.PinyinComparator;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.GraphicsUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class CustomGeneralPage extends SimpleTabPage
{

	private final SortMap jueweiProperty = FileUtil.loadProperties( "juewei" );
	private SortMap factionMap;
	private CCombo smallImageCombo;
	private CCombo bigImageCombo;
	private CCombo soldierImageCombo;
	private ImageCanvas imageCanvas;

	private Unit soldier = null;
	private SortMap skills = null;
	private ImageData smallImage = null;
	private ImageData bigImage = null;
	private ImageData soldierImage = null;
	private String[] baowus = null;
	private String[] jueweis = null;
	private String faction = null;

	ModifyListener nameListener = new ModifyListener( ) {

		public void modifyText( ModifyEvent e )
		{
			checkEnableStatus( );
		}

	};

	private Text nameText;
	private Text idText;
	private Button soldierButton;
	private Button soldierImageButton;
	private Button smallButton;
	private Button skillButton;
	private Button bigButton;
	private Text generalDesc;
	private Button ch1Button;
	private Button ch2Button;
	private Button ch3Button;
	private Button ch4Button;
	private Button applyButton;
	private CCombo jueweiCombo;
	private Button baowuButton;
	private SortMap availableGeneralMap;
	private SortMap generalUnitMap;
	private SortMap officerMap;
	private CCombo battleModelCombo;
	private CCombo generalModelCombo;
	private Button positionButton;
	private Spinner posXSpinner;
	private Spinner posYSpinner;
	private CCombo factionCombo;
	private Button tejiButton;
	private Button jueweiButton;

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

	private void checkEnableStatus( )
	{
		String soldierType = "Custom " + idText.getText( ).trim( );
		if ( nameText.getText( ).trim( ).length( ) > 0
				&& idText.getText( ).trim( ).length( ) > 0
				&& UnitUtil.getUnitDictionary( soldierType ) == null )
		{
			soldierButton.setEnabled( true );
			smallImageCombo.setEnabled( true );
			smallButton.setEnabled( true );
			skillButton.setEnabled( true );
			bigImageCombo.setEnabled( true );
			bigButton.setEnabled( true );
			soldierImageCombo.setEnabled( true );
			soldierButton.setEnabled( true );
			soldierImageButton.setEnabled( true );
			generalDesc.setEnabled( true );
			ch1Button.setEnabled( true );
			ch2Button.setEnabled( true );
			ch3Button.setEnabled( true );
			ch4Button.setEnabled( true );
			baowuButton.setEnabled( true );
			jueweiCombo.setEnabled( true );
			jueweiButton.setEnabled( true );
			positionButton.setEnabled( true );
			posXSpinner.setEnabled( true );
			posYSpinner.setEnabled( true );
			generalModelCombo.setEnabled( true );
			battleModelCombo.setEnabled( true );
			factionCombo.setEnabled( true );
			tejiButton.setEnabled( true );
			if ( faction != null
					&& bigImage != null
					&& soldier != null
					&& skills != null
					&& skills.getKeyList( ).size( ) > 0 )
			{
				applyButton.setEnabled( true );
			}
			else
				applyButton.setEnabled( false );
		}
		else
		{
			soldierButton.setEnabled( false );
			smallImageCombo.setEnabled( false );
			smallButton.setEnabled( false );
			skillButton.setEnabled( false );
			bigImageCombo.setEnabled( false );
			bigButton.setEnabled( false );
			soldierImageCombo.setEnabled( false );
			soldierButton.setEnabled( false );
			soldierImageButton.setEnabled( false );
			generalDesc.setEnabled( false );
			ch1Button.setEnabled( false );
			ch2Button.setEnabled( false );
			ch3Button.setEnabled( false );
			ch4Button.setEnabled( false );
			applyButton.setEnabled( false );
			baowuButton.setEnabled( false );
			jueweiCombo.setEnabled( false );
			jueweiButton.setEnabled( false );
			positionButton.setEnabled( false );
			posXSpinner.setEnabled( false );
			posYSpinner.setEnabled( false );
			generalModelCombo.setEnabled( false );
			battleModelCombo.setEnabled( false );
			battleModelCombo.setEnabled( false );
			factionCombo.setEnabled( false );
			tejiButton.setEnabled( false );
		}
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "请按提示步骤创建新武将：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "1.新武将姓名（中文）：" );

		nameText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		nameText.setLayoutData( gd );

		nameText.addModifyListener( nameListener );
		nameText.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( idText.getText( ).trim( ).length( ) == 0 )
				{
					HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat( );
					defaultFormat.setCaseType( HanyuPinyinCaseType.LOWERCASE );
					defaultFormat.setToneType( HanyuPinyinToneType.WITHOUT_TONE );
					try
					{
						idText.setText( PinyinHelper.toHanyuPinyinString( nameText.getText( )
								.trim( ),
								defaultFormat,
								"" ) );
					}
					catch ( BadHanyuPinyinOutputFormatCombination e1 )
					{
						e1.printStackTrace( );
					}
				}
			}
		} );

		imageCanvas = WidgetUtil.getToolkit( ).createImageCanvas( patchClient,
				SWT.NONE );
		gd = new GridData( GridData.FILL_VERTICAL );
		gd.verticalSpan = 14;
		gd.widthHint = 160;
		imageCanvas.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "2.新武将姓名（拼音，全英文）：" );
		idText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		idText.setLayoutData( gd );
		idText.addModifyListener( nameListener );
		idText.addVerifyListener( new VerifyListener( ) {

			public void verifyText( VerifyEvent e )
			{
				if ( e.text != null )
				{
					for ( int i = 0; i < e.text.length( ); i++ )
					{
						char ch = e.text.toLowerCase( ).charAt( i );
						if ( ch < 'a' || ch > 'z' )
						{
							e.doit = false;
							return;
						}
					}
				}
			}
		} );
		WidgetUtil.getToolkit( ).createLabel( patchClient, "3.选择新武将势力：" );
		factionCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		factionCombo.setLayoutData( gd );
		factionCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( factionCombo.getSelectionIndex( ) != -1 )
				{
					if ( factionCombo.getSelectionIndex( ) != -1 )
					{
						faction = (String) factionMap.getKeyList( )
								.get( factionCombo.getSelectionIndex( ) );
					}
				}
				checkEnableStatus( );
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "4.设置新武将坐标（X，Y）：" );

		Composite positionContainer = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		positionContainer.setLayoutData( gd );

		layout = new GridLayout( );
		layout.marginWidth = layout.marginHeight = 0;
		layout.marginLeft = 2;
		layout.numColumns = 3;
		positionContainer.setLayout( layout );

		posXSpinner = WidgetUtil.getToolkit( )
				.createSpinner( positionContainer );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 150;
		posXSpinner.setLayoutData( gd );
		posXSpinner.setEnabled( false );
		initSpinner( posXSpinner, 0, 189, 0, 1 );

		posYSpinner = WidgetUtil.getToolkit( )
				.createSpinner( positionContainer );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 150;
		posYSpinner.setLayoutData( gd );
		posYSpinner.setEnabled( false );
		initSpinner( posYSpinner, 0, 179, 0, 1 );

		positionButton = WidgetUtil.getToolkit( )
				.createButton( positionContainer, SWT.PUSH, true );
		positionButton.setEnabled( false );
		positionButton.setText( "自定义" );
		int width = positionButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
		positionButton.setText( "选择" );
		gd = new GridData( );
		gd.widthHint = width;
		positionButton.setLayoutData( gd );
		positionButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				PositionDialog dialog = new PositionDialog( "选择相邻武将" );
				if ( dialog.open( ) == Dialog.OK )
				{
					String general = (String) dialog.getResult( );
					if ( general != null )
					{
						General model = (General) UnitUtil.getGeneralModels( )
								.get( general );
						try
						{
							Point point = computeGeneralPosition( new Point( Integer.parseInt( model.getPosX( ) ),
									Integer.parseInt( model.getPosY( ) ) ),
									new Random( ).nextInt( 2 ) == 0 ? true
											: false,
									new Random( ).nextInt( 2 ) == 0 ? true
											: false );
							posXSpinner.setSelection( point.x );
							posYSpinner.setSelection( point.y );
						}
						catch ( NumberFormatException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "5.设置新武将大头像：" );
		bigImageCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		bigImageCombo.setText( "宽：69像素，高：96像素" );
		bigImageCombo.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( bigImageCombo.getText( ).length( ) == 0 )
				{
					bigImageCombo.setText( "宽：69像素，高：96像素" );
				}
			}
		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		bigImageCombo.setLayoutData( gd );

		bigImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				bigImage = null;
				if ( bigImageCombo.getSelectionIndex( ) != -1 )
				{
					String generalCode = (String) availableGeneralMap.getKeyList( )
							.get( bigImageCombo.getSelectionIndex( ) );
					setBigImage( generalCode );
					if ( bigImage != null )
						imageCanvas.setImageData( bigImage );
				}
				checkEnableStatus( );
			}
		} );

		bigButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				SWT.PUSH,
				true );
		bigButton.setText( "自定义" );
		bigButton.addSelectionListener( new SelectionAdapter( ) {

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
					bigImageCombo.clearSelection( );
					bigImageCombo.setText( path );
					bigImage = null;

					File imageFile = new File( bigImageCombo.getText( ).trim( ) );
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

							bigImage = GraphicsUtil.resizeImage( imageData,
									69,
									96,
									true );
							imageCanvas.setImageData( bigImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					checkEnableStatus( );
				}
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "6.设置新武将小头像(可选)：" );
		smallImageCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		smallImageCombo.setText( "宽：44像素，高：63像素" );
		smallImageCombo.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( smallImageCombo.getText( ).length( ) == 0 )
				{
					smallImageCombo.setText( "宽：44像素，高：63像素" );
				}
			}
		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		smallImageCombo.setLayoutData( gd );
		smallImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				smallImage = null;
				if ( smallImageCombo.getSelectionIndex( ) != -1 )
				{
					String generalCode = (String) availableGeneralMap.getKeyList( )
							.get( smallImageCombo.getSelectionIndex( ) );
					setSmallImage( generalCode );
					if ( smallImage != null )
						imageCanvas.setImageData( smallImage );
				}
				checkEnableStatus( );
			}
		} );

		smallButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				SWT.PUSH,
				true );
		smallButton.setText( "自定义" );
		smallButton.addSelectionListener( new SelectionAdapter( ) {

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
					smallImageCombo.clearSelection( );
					smallImageCombo.setText( path );
					smallImage = null;
					File imageFile = new File( smallImageCombo.getText( )
							.trim( ) );
					if ( imageFile.exists( ) && imageFile.isFile( ) )
					{
						try
						{
							ImageData imageData;
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

							smallImage = GraphicsUtil.resizeImage( imageData,
									44,
									63,
									true );
							imageCanvas.setImageData( smallImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
					checkEnableStatus( );
				}
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "7.设置新武将能力：" );
		skillButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置（未设置）",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		skillButton.setLayoutData( gd );
		skillButton.setEnabled( false );
		skillButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				GeneralModifyDialog dialog = new GeneralModifyDialog( "设置新武将能力",
						true );
				if ( skills == null )
				{
					skills = new SortMap( );
				}
				dialog.setGeneralSkills( null, skills );
				if ( dialog.open( ) == Window.OK )
				{
					skills = (SortMap) dialog.getResult( );
					checkEnableStatus( );
					skillButton.setText( "设置" );
				}
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "8.设置新武将卫队：" );
		soldierButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置（未设置）",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		soldierButton.setLayoutData( gd );
		soldierButton.setEnabled( false );
		soldierButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				UnitModifyDialog dialog = new UnitModifyDialog( "设置新武将卫队" );
				if ( soldier != null )
					dialog.setSoldier( soldier );
				if ( dialog.open( ) == Window.OK )
				{
					soldier = (Unit) dialog.getResult( );
					soldierButton.setText( "设置" );
					checkEnableStatus( );
				}
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "9.设置将军卫队图片(可选)：" );
		soldierImageCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		soldierImageCombo.setText( "宽：160像素，高：210像素" );
		soldierImageCombo.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( soldierImageCombo.getText( ).length( ) == 0 )
				{
					soldierImageCombo.setText( "宽：160像素，高：210像素" );
				}
			}
		} );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		soldierImageCombo.setLayoutData( gd );
		soldierImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( soldierImageCombo.getSelectionIndex( ) != -1 )
				{
					String soldierType = (String) generalUnitMap.getKeyList( )
							.get( soldierImageCombo.getSelectionIndex( ) );
					if ( soldierType != null )
					{
						Unit soldier = UnitParser.getUnit( soldierType );
						String dictionary = soldier.getDictionary( );
						String[] factions = UnitUtil.getFactionsFromSoldierType( soldierType );
						for ( int i = 0; i < factions.length; i++ )
						{
							File file = new File( Patch.GAME_ROOT
									+ "\\alexander\\data\\ui\\unit_info\\"
									+ factions[i]
									+ "\\"
									+ dictionary
									+ "_info.tga" );
							if ( file.exists( ) && file.length( ) > 0 )
							{
								try
								{
									ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( file ) ) );
									imageCanvas.setImageData( image );
									soldierImage = image;
									break;
								}
								catch ( IOException e1 )
								{
									e1.printStackTrace( );
								}
							}
						}
					}
				}
				checkEnableStatus( );
			}
		} );

		soldierImageButton = WidgetUtil.getToolkit( )
				.createButton( patchClient, SWT.PUSH, true );
		soldierImageButton.setText( "自定义" );
		soldierImageButton.addSelectionListener( new SelectionAdapter( ) {

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
					soldierImage = null;
					soldierImageCombo.clearSelection( );
					soldierImageCombo.setText( path );
					File imageFile = new File( soldierImageCombo.getText( )
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
							soldierImage = GraphicsUtil.resizeImage( imageData,
									160,
									210,
									true );
							imageCanvas.setImageData( soldierImage );
						}
						catch ( IOException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
				checkEnableStatus( );
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "10.设置战役地图模型(可选)：" );
		generalModelCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		generalModelCombo.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "11.设置战场地图模型(可选)：" );
		battleModelCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 2;
		battleModelCombo.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "12.设置新武将爵位(可选)：" );
		jueweiCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		jueweiCombo.setLayoutData( gd );
		jueweiCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				jueweis = null;
			}
		} );

		jueweiButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				SWT.PUSH,
				true );
		jueweiButton.setText( "自定义" );
		jueweiButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				JueweiDialog dialog = new JueweiDialog( "设置武将爵位" );
				if ( jueweis != null )
					dialog.setGeneralJueweis( jueweis );
				else
				{
					if ( jueweiCombo.getSelectionIndex( ) > -1 )
					{
						dialog.setGeneralJueweis( (String[]) jueweiProperty.get( jueweiCombo.getItem( jueweiCombo.getSelectionIndex( ) ) )
								.toString( )
								.split( "\\s+" ) );
					}
					else
					{
						dialog.setGeneralJueweis( (String[]) jueweiProperty.get( jueweiCombo.getItem( 0 ) )
								.toString( )
								.split( "\\s+" ) );
					}
				}
				if ( dialog.open( ) == Window.OK )
				{
					jueweis = (String[]) dialog.getResult( );
				}
				if ( jueweis != null
						&& jueweis.length > 0
						&& jueweis[jueweis.length - 1] != null )
				{
					if ( jueweis[jueweis.length - 1].startsWith( "·" ) )
						jueweis[jueweis.length - 1] = jueweis[jueweis.length - 1].replaceFirst( "·",
								"" );
					jueweiCombo.setText( jueweis[jueweis.length - 1] );
				}
				checkEnableStatus( );
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "13.设置新武将宝物(可选)：" );
		baowuButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		baowuButton.setLayoutData( gd );
		baowuButton.setEnabled( false );
		baowuButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				BaowuHolderModifyDialog dialog = new BaowuHolderModifyDialog( "设置新武将宝物" );
				if ( baowus != null )
					dialog.setGeneralBaowus( baowus );
				if ( dialog.open( ) == Window.OK )
				{
					baowus = (String[]) dialog.getResult( );
					checkEnableStatus( );
				}
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "14.设置新武将特技(可选)：" );
		tejiButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		tejiButton.setLayoutData( gd );
		tejiButton.setEnabled( false );
		tejiButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				SkillDialog dialog = new SkillDialog( "设置新武将特技" );
				if ( skills == null )
				{
					skills = new SortMap( );
				}
				dialog.setGeneralSkills( skills );
				dialog.open( );
			}

		} );

		Label chLabel = WidgetUtil.getToolkit( ).createLabel( patchClient,
				"15.设置新武将身份(可选)：" );
		gd = new GridData( );
		chLabel.setLayoutData( gd );

		Composite chArea = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 3;
		chArea.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 4;
		layout.marginWidth = layout.marginHeight = 0;
		chArea.setLayout( layout );

		ch1Button = WidgetUtil.getToolkit( ).createButton( chArea,
				"名门望族",
				SWT.CHECK );
		ch1Button.setData( "Ch2001" );
		ch2Button = WidgetUtil.getToolkit( ).createButton( chArea,
				"汉室宗亲",
				SWT.CHECK );
		ch2Button.setData( "Ch6001" );
		ch2Button.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( ch4Button.getSelection( ) )
					ch4Button.setSelection( !ch2Button.getSelection( ) );
			}
		} );
		ch3Button = WidgetUtil.getToolkit( ).createButton( chArea,
				"西川大将",
				SWT.CHECK );
		ch3Button.setData( "Ch4006" );
		ch4Button = WidgetUtil.getToolkit( ).createButton( chArea,
				"元老重臣(吴)",
				SWT.CHECK );
		ch4Button.setData( "Ch6005" );
		ch4Button.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( ch2Button.getSelection( ) )
					ch2Button.setSelection( !ch4Button.getSelection( ) );
			}
		} );

		Label label = WidgetUtil.getToolkit( ).createLabel( patchClient,
				"16.设置新武将列传(可选)：" );
		gd = new GridData( );
		gd.verticalAlignment = SWT.TOP;
		gd.grabExcessVerticalSpace = true;
		label.setLayoutData( gd );

		generalDesc = WidgetUtil.getToolkit( ).createText( patchClient,
				"",
				SWT.MULTI | SWT.FLAT | SWT.WRAP | SWT.V_SCROLL );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.heightHint = 100;
		gd.widthHint = 300;
		gd.horizontalSpan = 3;
		generalDesc.setLayoutData( gd );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = 4;
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
				BakUtil.bakData( "自定义武将：" + nameText.getText( ) );
				CustomGeneral customGeneral = new CustomGeneral( );
				customGeneral.setDisplayName( nameText.getText( ).trim( ) );
				customGeneral.setName( idText.getText( ).trim( ) );
				customGeneral.setFaction( faction );

				Point point = computeGeneralPosition( new Point( posXSpinner.getSelection( ),
						posYSpinner.getSelection( ) ),
						true,
						true );
				customGeneral.setPosX( point.x );
				customGeneral.setPosY( point.y );

				if ( generalModelCombo.getSelectionIndex( ) != -1 )
				{
					customGeneral.setStrat_model( (String) officerMap.getKeyList( )
							.get( generalModelCombo.getSelectionIndex( ) ) );
				}
				if ( battleModelCombo.getSelectionIndex( ) != -1 )
				{
					customGeneral.setBattle_model( (String) officerMap.getKeyList( )
							.get( battleModelCombo.getSelectionIndex( ) ) );
				}
				if ( soldierImage != null )
					customGeneral.setGeneralSoldierImage( soldierImage );
				else
				{
					String dictionary = soldier.getDictionary( );
					String[] factions = UnitUtil.getFactionsFromSoldierType( soldier.getType( ) );
					for ( int i = 0; i < factions.length; i++ )
					{
						File file = new File( Patch.GAME_ROOT
								+ "\\alexander\\data\\ui\\unit_info\\"
								+ factions[i]
								+ "\\"
								+ dictionary
								+ "_info.tga" );
						if ( file.exists( ) && file.length( ) > 0 )
						{
							try
							{
								ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( file ) ) );
								customGeneral.setGeneralSoldierImage( image );
								break;
							}
							catch ( IOException e1 )
							{
								e1.printStackTrace( );
							}
						}
					}
				}
				customGeneral.setGeneralImages( new ImageData[]{
						bigImage, smallImage
				} );
				customGeneral.setGeneralSoldier( soldier );
				customGeneral.setGeneralBaowus( baowus );
				if ( jueweis != null )
				{
					customGeneral.setGeneralJueweis( jueweis );
				}
				else if ( jueweiCombo.getSelectionIndex( ) > -1 )
				{
					customGeneral.setGeneralJueweis( (String[]) jueweiProperty.get( jueweiCombo.getText( ) )
							.toString( )
							.split( "\\s+" ) );
				}
				else
				{
					customGeneral.setGeneralJueweis( (String[]) jueweiProperty.get( jueweiProperty.keySet( )
							.toArray( )[new Random( ).nextInt( jueweiProperty.size( ) )] )
							.toString( )
							.split( "\\s+" ) );
				}
				customGeneral.setGeneralSkills( skills );
				List chenghaos = new ArrayList( );
				if ( ch1Button.getSelection( ) )
					chenghaos.add( (String) ch1Button.getData( ) );
				if ( ch2Button.getSelection( ) )
					chenghaos.add( (String) ch2Button.getData( ) );
				if ( ch3Button.getSelection( ) )
					chenghaos.add( (String) ch3Button.getData( ) );
				if ( ch4Button.getSelection( ) )
					chenghaos.add( (String) ch4Button.getData( ) );
				customGeneral.setGeneralChenghaos( (String[]) chenghaos.toArray( new String[0] ) );
				customGeneral.setGeneralDescription( generalDesc.getText( )
						.trim( ) );

				customGeneral.createCustomGeneral( );
				MapUtil.initMap( );
				applyButton.setEnabled( true );

				resetStatus( );
				refreshPage( );
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

	private void initSpinner( Spinner combo, int min, int max, int digit,
			int step )
	{
		combo.setMinimum( min );
		combo.setMaximum( max );
		combo.setDigits( digit );
		combo.setIncrement( step );
	}

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于自定义新武将，配置完毕后需重新开局方可生效。" );
	}

	public String getDisplayName( )
	{
		return "创建新武将";
	}

	private void initPage( )
	{
		availableGeneralMap = UnitUtil.getAvailableGenerals( );
		for ( int i = 0; i < availableGeneralMap.getKeyList( ).size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) availableGeneralMap.get( i ) );
			smallImageCombo.add( generalName );
			bigImageCombo.add( generalName );
		}
		if ( jueweiProperty != null )
		{
			List list = new ArrayList( );
			Iterator iter = jueweiProperty.keySet( ).iterator( );
			while ( iter.hasNext( ) )
			{
				list.add( iter.next( ) );
			}
			Collections.sort( list, new JueweiComparator( ) );
			String[] generals = (String[]) list.toArray( new String[0] );
			jueweiCombo.setItems( generals );
			list.clear( );
		}

		generalUnitMap = UnitUtil.getAvailableGeneralUnits( );
		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}
		checkEnableStatus( );
	}

	public class JueweiComparator implements Comparator
	{

		public int compare( Object arg0, Object arg1 )
		{
			String code0 = null;
			String code1 = null;

			code0 = (String) arg0;
			code1 = (String) arg1;

			int result = PinyinComparator.compare( ""
					+ code0.charAt( code0.length( ) - 1 ),
					"" + code1.charAt( code1.length( ) - 1 ) );

			if ( result != 0 )
				return result;
			else
				return PinyinComparator.compare( code0, code1 );

		}
	}

	private void setBigImage( String general )
	{
		String portrait = ( (General) UnitUtil.getGeneralModels( )
				.get( general ) ).getPortrait( );
		try
		{
			ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( new File( FileConstants.customPortraitPath
					+ "\\"
					+ portrait
					+ "\\portrait_young.tga" ) ) ),
					false );
			bigImage = image;
		}
		catch ( Exception e1 )
		{
			e1.printStackTrace( );
		}
	}

	private void setSmallImage( String general )
	{
		String portrait = ( (General) UnitUtil.getGeneralModels( )
				.get( general ) ).getPortrait( );
		try
		{
			ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( new File( FileConstants.customPortraitPath
					+ "\\"
					+ portrait
					+ "\\card_young.tga" ) ) ),
					false );
			smallImage = image;
		}
		catch ( Exception e1 )
		{
			e1.printStackTrace( );
		}
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		availableGeneralMap = UnitUtil.getAvailableGenerals( );
		generalUnitMap = UnitUtil.getAvailableGeneralUnits( );
		officerMap = UnitUtil.getAvailableOfficers( );
		factionMap = UnitUtil.getFactionMap( );

		smallImageCombo.setItems( new String[0] );
		bigImageCombo.setItems( new String[0] );
		soldierImageCombo.setItems( new String[0] );
		generalModelCombo.setItems( new String[0] );
		battleModelCombo.setItems( new String[0] );

		String faction = factionCombo.getText( );

		factionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( faction ) )
			factionCombo.setText( faction );

		for ( int i = 0; i < availableGeneralMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) availableGeneralMap.get( i ) );
			smallImageCombo.add( generalName );
			bigImageCombo.add( generalName );
		}
		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}
		for ( int i = 0; i < officerMap.size( ); i++ )
		{
			this.generalModelCombo.add( (String) officerMap.get( i ) );
			this.battleModelCombo.add( (String) officerMap.get( i ) );
		}

		checkEnableStatus( );
	}

	private Point computeGeneralPosition( Point point, boolean x, boolean y )
	{
		return computeGeneralPosition( point, x, y, true );
	}

	private Point computeGeneralPosition( Point point, boolean x, boolean y,
			boolean xory )
	{
		Iterator iter = UnitUtil.getGeneralModels( ).values( ).iterator( );

		while ( iter.hasNext( ) )
		{
			General temp = (General) iter.next( );
			if ( ( temp.getPosX( ).equals( Integer.toString( point.x ) ) && temp.getPosY( )
					.equals( Integer.toString( point.y ) ) )
					|| UnitUtil.getUnAvailableGeneralPoints( ).contains( point ) )
			{
				return reComputeGeneralPosition( point, x, y, xory );
			}
		}

		return point;
	}

	private Point reComputeGeneralPosition( Point point, boolean x, boolean y,
			boolean xory )
	{
		if ( point.x == 189 )
		{
			x = false;
		}
		if ( point.y == 179 )
		{
			y = false;
		}
		if ( xory )
		{
			if ( x )
			{
				point.x = point.x + 2;
			}
			else
			{
				point.x = point.x - 2;
			}
		}
		else
		{
			if ( y )
			{
				point.y = point.y + 2;
			}
			else
			{
				point.y = point.y - 2;
			}
		}
		return computeGeneralPosition( point, x, y, !xory );
	}

	private void resetStatus( )
	{
		nameText.setText( "" );
		idText.setText( "" );
		generalDesc.setText( "" );
		factionCombo.clearSelection( );
		factionCombo.setText( "" );
		smallImageCombo.clearSelection( );
		smallImageCombo.setText( "" );
		bigImageCombo.clearSelection( );
		bigImageCombo.setText( "" );
		soldierImageCombo.clearSelection( );
		soldierImageCombo.setText( "" );
		generalModelCombo.clearSelection( );
		generalModelCombo.setText( "" );
		battleModelCombo.clearSelection( );
		battleModelCombo.setText( "" );
		jueweiCombo.clearSelection( );
		jueweiCombo.setText( "" );
		ch1Button.setSelection( false );
		ch2Button.setSelection( false );
		ch3Button.setSelection( false );
		ch4Button.setSelection( false );
		posXSpinner.setSelection( 0 );
		posYSpinner.setSelection( 0 );
		if ( skills != null )
		{
			skills.clear( );
			skillButton.setText( "设置（未设置）" );
		}
		if ( soldier != null )
		{
			soldier = null;
			soldierButton.setText( "设置（未设置）" );
		}
		skills = null;
		baowus = null;
		jueweis = null;
		soldier = null;
		smallImage = null;
		bigImage = null;
		soldierImage = null;
		faction = null;
		imageCanvas.clear( );
		checkEnableStatus( );
	}
}
