
package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.dialog.UnitModifyDialog;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.CustomUnit;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.PinyinComparator;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.GraphicsUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class CustomUnitPage extends SimpleTabPage
{

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
	private Button applyButton;
	private Button soldierCardButton;
	private ImageData soldierCardImage;

	private CCombo soldierCardImageCombo;
	private String faction = null;
	private CCombo factionCombo;
	private SortMap factionMap;
	private Text generalDesc;

	private SortMap generalUnitMap;

	private Text idText;
	private ImageCanvas imageCanvas;
	ModifyListener nameListener = new ModifyListener( ) {

		public void modifyText( ModifyEvent e )
		{
			checkEnableStatus( );
		}

	};
	private Text nameText;
	private Unit soldier = null;
	private Button soldierButton;
	private ImageData soldierImage = null;
	private Button soldierImageButton;

	private CCombo soldierImageCombo;
	private CCombo bingyingCombo;
	private Button yesButton;
	private Button noButton;
	private SortMap soldierUnitMap;

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
		
		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"注意：只有步兵兵营等级最高为5级，其他兵种兵营等级最高为4级。" );
		TableWrapData data = new TableWrapData( TableWrapData.FILL );
		data.maxWidth = 600;
		noteText.setLayoutData( data );
		
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
			soldierCardImageCombo.setEnabled( true );
			soldierCardButton.setEnabled( true );
			soldierImageCombo.setEnabled( true );
			soldierButton.setEnabled( true );
			soldierImageButton.setEnabled( true );
			generalDesc.setEnabled( true );
			factionCombo.setEnabled( true );
			yesButton.setEnabled( true );
			noButton.setEnabled( true );
			bingyingCombo.setEnabled( true );
			if ( faction != null && soldier != null )
			{
				applyButton.setEnabled( true );
			}
			else
				applyButton.setEnabled( false );
		}
		else
		{
			soldierButton.setEnabled( false );
			soldierCardImageCombo.setEnabled( false );
			soldierCardButton.setEnabled( false );
			soldierImageCombo.setEnabled( false );
			soldierButton.setEnabled( false );
			soldierImageButton.setEnabled( false );
			generalDesc.setEnabled( false );
			applyButton.setEnabled( false );
			factionCombo.setEnabled( false );
			yesButton.setEnabled( false );
			noButton.setEnabled( false );
			bingyingCombo.setEnabled( false );
		}
	}

	private void createPatchArea( )
	{
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit( )
				.createSection( container.getBody( ), Section.EXPANDED );
		td = new TableWrapData( TableWrapData.FILL );
		patchSection.setLayoutData( td );
		patchSection.setText( "请按提示步骤创建新兵种：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "1.新兵种名称（中文）：" );

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
		gd.verticalSpan = 8;
		gd.widthHint = 160;
		imageCanvas.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "2.新兵种名称（拼音，全英文）：" );
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
		WidgetUtil.getToolkit( ).createLabel( patchClient, "3.选择新兵种所属势力：" );
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

		WidgetUtil.getToolkit( ).createLabel( patchClient, "4.新兵种是否为将军卫队：" );

		Composite chArea = WidgetUtil.getToolkit( )
				.createComposite( patchClient );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		chArea.setLayoutData( gd );

		layout = new GridLayout( );
		layout.numColumns = 2;
		layout.marginWidth = layout.marginHeight = 0;
		layout.makeColumnsEqualWidth = true;
		chArea.setLayout( layout );

		yesButton = WidgetUtil.getToolkit( ).createButton( chArea,
				"是",
				SWT.RADIO );
		gd = new GridData( );
		gd.widthHint = 80;
		yesButton.setLayoutData( gd );

		noButton = WidgetUtil.getToolkit( ).createButton( chArea,
				"否",
				SWT.RADIO );
		noButton.setSelection( true );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "5.设置新兵种属性：" );
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
				UnitModifyDialog dialog = new UnitModifyDialog( "设置新兵种属性" );
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

		WidgetUtil.getToolkit( ).createLabel( patchClient, "6.选择新兵种兵营等级（可选）：" );
		bingyingCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		bingyingCombo.setLayoutData( gd );
		bingyingCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{

				checkEnableStatus( );
			}
		} );
		bingyingCombo.setItems( new String[]{
				"所有兵营", "2级以上兵营", "3级以上兵营", "4级以上兵营", "5级兵营"
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "7.设置新兵种兵牌(可选)：" );
		soldierCardImageCombo = WidgetUtil.getToolkit( )
				.createCCombo( patchClient, SWT.READ_ONLY );
		soldierCardImageCombo.setText( "宽：48像素，高：64像素" );
		soldierCardImageCombo.addFocusListener( new FocusAdapter( ) {

			public void focusLost( FocusEvent e )
			{
				if ( soldierCardImageCombo.getText( ).length( ) == 0 )
				{
					soldierCardImageCombo.setText( "宽：48像素，高：64像素" );
				}
			}
		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		soldierCardImageCombo.setLayoutData( gd );

		soldierCardImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( soldierCardImageCombo.getSelectionIndex( ) != -1 )
				{
					imageCanvas.clear( );
					String soldierType = (String) soldierUnitMap.getKeyList( )
							.get( soldierCardImageCombo.getSelectionIndex( ) );
					if ( soldierType != null )
					{
						Unit soldier = UnitParser.getUnit( soldierType );
						String dictionary = soldier.getDictionary( );
						String[] factions = (String[]) soldier.getFactions( )
								.toArray( new String[0] );
						for ( int i = 0; i < factions.length; i++ )
						{
							String faction = factions[i].equalsIgnoreCase( "all" ) ? "romans_scipii"
									: factions[i];
							faction = faction.equalsIgnoreCase( "barbarian" ) ? "slave"
									: faction;
							File file = new File( Patch.GAME_ROOT
									+ "\\alexander\\data\\ui\\units\\"
									+ faction
									+ "\\#"
									+ dictionary
									+ ".tga" );
							if ( file.exists( ) && file.length( ) > 0 )
							{
								try
								{
									ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( file ) ) );
									imageCanvas.setImageData( image );
									soldierCardImage = image;
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

		soldierCardButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				SWT.PUSH,
				true );
		soldierCardButton.setText( "自定义" );
		soldierCardButton.addSelectionListener( new SelectionAdapter( ) {

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
					soldierCardImageCombo.clearSelection( );
					soldierCardImageCombo.setText( path );
					soldierCardImage = null;

					File imageFile = new File( soldierCardImageCombo.getText( )
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

							soldierCardImage = GraphicsUtil.resizeImage( imageData,
									48,
									64,
									true );
							imageCanvas.setImageData( soldierCardImage );
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

		WidgetUtil.getToolkit( ).createLabel( patchClient, "9.设置新兵种图片(可选)：" );
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
					imageCanvas.clear( );
					String soldierType = soldierImageCombo.getSelectionIndex( ) < generalUnitMap.size( ) ? (String) generalUnitMap.getKeyList( )
							.get( soldierImageCombo.getSelectionIndex( ) )
							: (String) soldierUnitMap.getKeyList( )
									.get( soldierImageCombo.getSelectionIndex( )
											- generalUnitMap.size( ) );
					if ( soldierType != null )
					{
						Unit soldier = UnitParser.getUnit( soldierType );
						String dictionary = soldier.getDictionary( );
						String[] factions = (String[]) soldier.getFactions( )
								.toArray( new String[0] );
						for ( int i = 0; i < factions.length; i++ )
						{
							String faction = factions[i].equalsIgnoreCase( "all" ) ? "romans_scipii"
									: factions[i];
							faction = faction.equalsIgnoreCase( "barbarian" ) ? "slave"
									: faction;
							File file = new File( Patch.GAME_ROOT
									+ "\\alexander\\data\\ui\\unit_info\\"
									+ faction
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

		Label label = WidgetUtil.getToolkit( ).createLabel( patchClient,
				"10.设置兵种介绍(可选)：" );
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
				BakUtil.bakData( "自定义新兵种：" + nameText.getText( ) );

				CustomUnit customUnit = new CustomUnit( );
				customUnit.setDisplayName( nameText.getText( ).trim( ) );
				customUnit.setName( idText.getText( ).trim( ) );
				customUnit.setFaction( (String) factionMap.getKeyList( )
						.get( factionCombo.getSelectionIndex( ) ) );
				customUnit.setGeneralUnit( yesButton.getSelection( ) );
				customUnit.setBuildingLevel( bingyingCombo.getSelectionIndex( ) );
				if ( bingyingCombo.getSelectionIndex( ) == -1
						&& yesButton.getSelection( ) )
				{
					customUnit.setSpecialGeneralUnit( true );
				}
				else
				{
					customUnit.setSpecialGeneralUnit( false );
				}
				if ( soldierImage != null )
					customUnit.setSoldierImage( soldierImage );
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
								customUnit.setSoldierImage( image );
								break;
							}
							catch ( IOException e1 )
							{
								e1.printStackTrace( );
							}
						}
					}
				}

				if ( !yesButton.getSelection( ) )
				{
					if ( soldierCardImage != null )
						customUnit.setSoldierCardImage( soldierCardImage );
					else
					{
						String dictionary = soldier.getDictionary( );
						String[] factions = UnitUtil.getFactionsFromSoldierType( soldier.getType( ) );
						for ( int i = 0; i < factions.length; i++ )
						{
							File file = new File( Patch.GAME_ROOT
									+ "\\alexander\\data\\ui\\units\\"
									+ factions[i]
									+ "\\#"
									+ dictionary
									+ ".tga" );
							if ( file.exists( ) && file.length( ) > 0 )
							{
								try
								{
									ImageData image = TgaLoader.loadImage( new BufferedInputStream( new FileInputStream( file ) ) );
									customUnit.setSoldierCardImage( image );
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

				customUnit.setSoldier( soldier );
				customUnit.setDescription( generalDesc.getText( ).trim( ) );
				customUnit.createCustomUtil( );

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

	private void createTitle( )
	{
		WidgetUtil.createFormText( container.getBody( ),
				"本页面用于自定义新兵种，配置完毕后重启游戏即可生效。" );
	}

	public String getDisplayName( )
	{
		return "创建新兵种";
	}

	private void initPage( )
	{
		generalUnitMap = UnitUtil.getAvailableGeneralUnits( );

		soldierUnitMap = UnitUtil.getAllSoldierUnits( );
		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) soldierUnitMap.get( i ) );
			soldierCardImageCombo.add( unitName );
		}

		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}

		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) soldierUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}

		checkEnableStatus( );
	}

	public void refresh( )
	{
		super.refresh( );
		refreshPage( );
	}

	private void refreshPage( )
	{
		generalUnitMap = UnitUtil.getGeneralUnits( );
		soldierUnitMap = UnitUtil.getAllSoldierUnits( );

		factionMap = UnitUtil.getFactionMap( );

		soldierCardImageCombo.setItems( new String[0] );
		soldierImageCombo.setItems( new String[0] );

		String faction = factionCombo.getText( );

		factionCombo.removeAll( );
		for ( int i = 0; i < factionMap.getKeyList( ).size( ); i++ )
		{
			factionCombo.add( (String) factionMap.get( i ) );
		}

		if ( factionMap.containsValue( faction ) )
			factionCombo.setText( faction );

		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) soldierUnitMap.get( i ) );
			soldierCardImageCombo.add( unitName );
		}

		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}

		for ( int i = 0; i < soldierUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) soldierUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}

		checkEnableStatus( );
	}

	private void resetStatus( )
	{
		nameText.setText( "" );
		idText.setText( "" );
		generalDesc.setText( "" );
		factionCombo.clearSelection( );
		factionCombo.setText( "" );
		soldierCardImageCombo.clearSelection( );
		soldierCardImageCombo.setText( "" );
		soldierImageCombo.clearSelection( );
		soldierImageCombo.setText( "" );
		bingyingCombo.clearSelection( );
		bingyingCombo.setText( "" );
		if ( soldier != null )
		{
			soldier = null;
			soldierButton.setText( "设置（未设置）" );
		}
		soldier = null;
		soldierCardImage = null;
		soldierImage = null;
		faction = null;
		imageCanvas.clear( );
		noButton.setSelection( true );
		yesButton.setSelection( false );
		checkEnableStatus( );
	}
}
