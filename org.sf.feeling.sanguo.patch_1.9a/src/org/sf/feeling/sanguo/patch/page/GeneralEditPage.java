
package org.sf.feeling.sanguo.patch.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
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
import org.sf.feeling.sanguo.patch.util.BaowuParser;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.GeneralEditor;
import org.sf.feeling.sanguo.patch.util.GeneralParser;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralEditPage extends SimpleTabPage
{

	private CCombo generalCombo;
	private CCombo smallImageCombo;
	private CCombo bigImageCombo;
	private CCombo soldierImageCombo;
	private ImageCanvas imageCanvas;

	private Unit soldier = null;
	private SortMap skills = null;
	private ImageData smallImage = null;
	private ImageData bigImage = null;
	private ImageData soldierImage = null;
	private String general = null;
	private String[] baowus = null;
	private String[] jueweis = null;

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
	private Button applyButton;
	private Button jueweiButton;
	private Button baowuButton;
	private SortMap generalMap;
	private SortMap generalUnitMap;
	private SortMap officerMap;
	private CCombo battleModelCombo;
	private CCombo generalModelCombo;
	private Button positionButton;
	private Spinner posXSpinner;
	private Spinner posYSpinner;

	private SortMap generalModelMap;

	final ModifyListener spinnerListener = new ModifyListener( ) {

		public void modifyText( final ModifyEvent e )
		{
			General model = (General) UnitUtil.getGeneralModels( )
					.get( general );

			Iterator iter = UnitUtil.getGeneralModels( ).values( ).iterator( );
			while ( iter.hasNext( ) )
			{
				General temp = (General) iter.next( );
				if ( temp == model )
					continue;
				if ( temp.getPosX( )
						.equals( Integer.toString( posXSpinner.getSelection( ) ) )
						&& temp.getPosY( )
								.equals( Integer.toString( posYSpinner.getSelection( ) ) ) )
				{

					Display.getDefault( ).asyncExec( new Runnable( ) {

						public void run( )
						{
							if ( e.widget == posXSpinner )
							{
								posXSpinner.setSelection( posXSpinner.getSelection( ) + 1 );
							}
							else if ( e.widget == posYSpinner )
							{
								posYSpinner.setSelection( posYSpinner.getSelection( ) + 1 );
							}
						}
					} );
					break;
				}
			}
			checkEnableStatus( );
		}
	};
	private Button tejiButton;
	private FormText soldierText;

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
		if ( nameText.getText( ).trim( ).length( ) > 0
				&& idText.getText( ).trim( ).length( ) > 0 )
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
			baowuButton.setEnabled( true );
			jueweiButton.setEnabled( true );
			battleModelCombo.setEnabled( true );
			generalModelCombo.setEnabled( true );
			positionButton.setEnabled( true );
			posXSpinner.setEnabled( true );
			posYSpinner.setEnabled( true );
			tejiButton.setEnabled( true );

			if ( general != null
					&& bigImage != null
					&& skills != null
					&& skills.getKeyList( ).size( ) > 0
					&& posXSpinner.getSelection( ) > 0
					&& posYSpinner.getSelection( ) > 0 )
			{
				String generalUnit = UnitUtil.getGeneralUnitType( general );
				if ( generalUnitMap.containsKey( generalUnit )
						&& soldier != null )
					applyButton.setEnabled( true );
				// 自定义武将
				else if ( !generalUnitMap.containsKey( generalUnit ) )
				{
					if ( soldier != null )
					{
						String soldierType = "Custom "
								+ idText.getText( ).trim( );
						if ( UnitUtil.getUnitDictionary( soldierType ) != null )
						{
							applyButton.setEnabled( false );
						}
						else
						{
							applyButton.setEnabled( true );
						}
					}
					else
					{
						applyButton.setEnabled( true );
						soldierImageCombo.setEnabled( false );
						soldierImageButton.setEnabled( false );
					}
				}
				else
					applyButton.setEnabled( false );
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
			applyButton.setEnabled( false );
			baowuButton.setEnabled( false );
			jueweiButton.setEnabled( false );
			battleModelCombo.setEnabled( false );
			generalModelCombo.setEnabled( false );
			positionButton.setEnabled( false );
			posXSpinner.setEnabled( false );
			posYSpinner.setEnabled( false );
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
		patchSection.setText( "编辑武将（需要保留的项目请不要做任何设置操作）：" );
		WidgetUtil.getToolkit( ).createCompositeSeparator( patchSection );
		Composite patchClient = WidgetUtil.getToolkit( )
				.createComposite( patchSection );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		patchClient.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "选择编辑武将：" );
		generalCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		generalCombo.setLayoutData( gd );

		imageCanvas = WidgetUtil.getToolkit( ).createImageCanvas( patchClient,
				SWT.NONE );
		gd = new GridData( GridData.FILL_VERTICAL );
		gd.verticalSpan = 14;
		gd.widthHint = 160;
		imageCanvas.setLayoutData( gd );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "武将编号：" );
		idText = WidgetUtil.getToolkit( ).createText( patchClient,
				"",
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		idText.setLayoutData( gd );
		idText.addModifyListener( nameListener );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置武将姓名：" );

		nameText = WidgetUtil.getToolkit( ).createText( patchClient, "" );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		nameText.setLayoutData( gd );

		nameText.addModifyListener( nameListener );

		FormText positionText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将坐标（X，Y）：</p></form>",
				true,
				false );
		positionText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		positionText.setLayoutData( new GridData( ) );

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
		initSpinner( posXSpinner, 0, 200, 0, 1 );

		posYSpinner = WidgetUtil.getToolkit( )
				.createSpinner( positionContainer );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 150;
		posYSpinner.setLayoutData( gd );
		posYSpinner.setEnabled( false );
		initSpinner( posYSpinner, 0, 200, 0, 1 );

		posXSpinner.addModifyListener( spinnerListener );
		posYSpinner.addModifyListener( spinnerListener );

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
							posXSpinner.setSelection( Integer.parseInt( model.getPosX( ) ) );
							posYSpinner.setSelection( Integer.parseInt( model.getPosY( ) ) );
						}
						catch ( NumberFormatException e1 )
						{
							e1.printStackTrace( );
						}
					}
				}
			}
		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置武将大头像：" );
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
					String generalCode = (String) generalMap.getKeyList( )
							.get( bigImageCombo.getSelectionIndex( ) );
					setBigImage( generalCode );
					if ( bigImage != null )
						imageCanvas.setImageData( bigImage );
				}
				checkEnableStatus( );
			}
		} );
		bigImageCombo.setEnabled( false );

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
							if ( imageFile.getName( )
									.toLowerCase( )
									.endsWith( ".tga" ) )
							{
								ImageData imageData = TgaLoader.loadImage( new FileInputStream( imageFile ),
										true,
										true )
										.scaledTo( 69, 96 );
								imageCanvas.setImageData( imageData );
								bigImage = imageData;
							}
							else
							{
								ImageLoader loader = new ImageLoader( );
								ImageData imageData = loader.load( imageFile.getAbsolutePath( ) )[0].scaledTo( 69,
										96 );
								imageCanvas.setImageData( imageData );
								bigImage = imageData;
							}
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
		bigButton.setEnabled( false );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置武将小头像" );
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
		smallImageCombo.setEnabled( false );

		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 160;
		smallImageCombo.setLayoutData( gd );
		smallImageCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				smallImage = null;
				if ( smallImageCombo.getSelectionIndex( ) != -1 )
				{
					String generalCode = (String) generalMap.getKeyList( )
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
							if ( imageFile.getName( )
									.toLowerCase( )
									.endsWith( ".tga" ) )
							{
								ImageData imageData = TgaLoader.loadImage( new FileInputStream( imageFile ),
										true,
										true )
										.scaledTo( 44, 63 );
								imageCanvas.setImageData( imageData );
								smallImage = imageData;
							}
							else
							{
								ImageLoader loader = new ImageLoader( );
								ImageData imageData = loader.load( imageFile.getAbsolutePath( ) )[0].scaledTo( 44,
										63 );
								imageCanvas.setImageData( imageData );
								smallImage = imageData;
							}
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
		smallButton.setEnabled( false );

		FormText skillText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将能力：</p></form>",
				true,
				false );
		skillText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		skillText.setLayoutData( new GridData( ) );

		skillButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		skillButton.setLayoutData( gd );
		skillButton.setEnabled( false );
		skillButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				GeneralModifyDialog dialog = new GeneralModifyDialog( "设置武将能力" );
				if ( skills != null )
				{
					dialog.setGeneralSkills( general, skills );
				}
				if ( dialog.open( ) == Window.OK )
				{
					skills = (SortMap) dialog.getResult( );
					checkEnableStatus( );
				}
			}

		} );

		soldierText = WidgetUtil.createFormText( patchClient,
				"<form><p>设置武将卫队：</p></form>",
				true,
				false );
		gd = new GridData( );
		gd.horizontalAlignment = SWT.FILL;
		soldierText.setLayoutData( gd );

		soldierButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		soldierButton.setLayoutData( gd );
		soldierButton.setEnabled( false );
		soldierButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				UnitModifyDialog dialog = new UnitModifyDialog( "设置武将卫队" );
				if ( soldier != null )
					dialog.setSoldier( soldier );
				if ( dialog.open( ) == Window.OK )
				{
					soldier = (Unit) dialog.getResult( );
					checkEnableStatus( );
				}
			}

		} );

		WidgetUtil.getToolkit( ).createLabel( patchClient, "设置将军卫队图片：" );
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
						String dictionary = UnitUtil.getUnitDictionary( soldierType );
						String[] factions = UnitUtil.getFactionsFromSoldierDictionary( dictionary );
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
					"*.jpg;*.jpeg;*.png;*.bmp;*.gif;*.tga;*.dds"
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
							if ( imageFile.getName( )
									.toLowerCase( )
									.endsWith( ".tga" ) )
							{
								ImageData imageData = TgaLoader.loadImage( new FileInputStream( imageFile ),
										true,
										true )
										.scaledTo( 160, 210 );
								imageCanvas.setImageData( imageData );
								soldierImage = imageData;
							}
							else
							{
								ImageLoader loader = new ImageLoader( );
								ImageData imageData = loader.load( imageFile.getAbsolutePath( ) )[0].scaledTo( 160,
										210 );
								imageCanvas.setImageData( imageData );
								soldierImage = imageData;
							}
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

		FormText generalModelText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将战役地图模型：</p></form>",
				true,
				false );
		generalModelText.setColor( "note", Display.getDefault( )
				.getSystemColor( SWT.COLOR_RED ) );
		generalModelText.setLayoutData( new GridData( ) );

		generalModelCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		generalModelCombo.setLayoutData( gd );

		FormText battleModelText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将战场地图模型：</p></form>",
				true,
				false );
		battleModelText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		battleModelText.setLayoutData( new GridData( ) );

		battleModelCombo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
				SWT.READ_ONLY );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		battleModelCombo.setLayoutData( gd );

		FormText jueweiText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将爵位：</p></form>",
				true,
				false );
		jueweiText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		jueweiText.setLayoutData( new GridData( ) );

		jueweiButton = WidgetUtil.getToolkit( ).createButton( patchClient,
				"设置",
				SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		jueweiButton.setLayoutData( gd );

		jueweiButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				JueweiDialog dialog = new JueweiDialog( "设置武将爵位" );
				if ( jueweis != null )
					dialog.setGeneralJueweis( jueweis );
				if ( dialog.open( ) == Window.OK )
				{
					jueweis = (String[]) dialog.getResult( );
				}
			}

		} );

		FormText baowuText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将宝物：</p></form>",
				true,
				false );
		baowuText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		baowuText.setLayoutData( new GridData( ) );

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
				BaowuHolderModifyDialog dialog = new BaowuHolderModifyDialog( "设置武将宝物" );
				if ( baowus != null )
					dialog.setGeneralBaowus( baowus );
				if ( dialog.open( ) == Window.OK )
				{
					baowus = (String[]) dialog.getResult( );
					checkEnableStatus( );
				}
			}

		} );

		FormText tejiText = WidgetUtil.createFormText( patchClient,
				"<form><p><span color=\"note\">*</span>设置武将特技：</p></form>",
				true,
				false );
		tejiText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
		tejiText.setLayoutData( new GridData( ) );

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
				SkillDialog dialog = new SkillDialog( "设置武将特技" );
				if ( skills == null )
				{
					skills = new SortMap( );
				}
				dialog.setGeneralSkills( skills );
				dialog.open( );
			}

		} );

		Label label = WidgetUtil.getToolkit( ).createLabel( patchClient,
				"设置武将列传：" );
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
				boolean changeGeneralImage = true;
				boolean changeSoldierImage = false;

				String generalUnit = UnitUtil.getGeneralUnitType( general );
				if ( generalUnitMap.containsKey( generalUnit )
						&& soldierImageCombo.getSelectionIndex( ) != generalUnitMap.getIndexOf( generalUnit )
						&& soldierImage != null )
				{
					changeSoldierImage = true;
				}

				if ( smallImageCombo.getSelectionIndex( ) == generalMap.getIndexOf( general )
						&& bigImageCombo.getSelectionIndex( ) == generalMap.getIndexOf( general ) )
					changeGeneralImage = false;
				if ( !changeGeneralImage && !changeSoldierImage )
				{
					BakUtil.bakData( "编辑武将：" + generalCombo.getText( ) );
				}
				else
				{
					List imageFiles = new ArrayList( );
					if ( changeGeneralImage )
					{
						imageFiles.addAll( Arrays.asList( BakUtil.getGeneralsImageFiles( new String[]{
							general
						} ) ) );
					}
					if ( changeSoldierImage )
					{
						imageFiles.addAll( Arrays.asList( BakUtil.getUnitImageFiles( UnitUtil.getGeneralFaction( general ),
								generalUnit ) ) );
					}
					BakUtil.bakDataAndResources( "编辑武将：" + nameText.getText( ),
							(File[]) imageFiles.toArray( new File[0] ) );
				}
				GeneralEditor customGeneral = new GeneralEditor( );
				customGeneral.setDisplayName( nameText.getText( ).trim( ) );
				customGeneral.setName( general );
				customGeneral.setGeneral( general );
				if ( soldier != null )
				{
					if ( !generalUnitMap.containsKey( generalUnit ) )
					{
						if ( soldierImage != null )
							customGeneral.setGeneralSoldierImage( soldierImage );
						else
						{
							String dictionary = soldier.getDictionary( );
							String[] factions = UnitUtil.getFactionsFromSoldierDictionary( dictionary );
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
					}
					else if ( changeSoldierImage )
					{
						customGeneral.setGeneralSoldierImage( soldierImage );
					}
				}
				if ( changeGeneralImage )
				{
					ImageData[] images = new ImageData[2];
					if ( smallImageCombo.getSelectionIndex( ) != generalMap.getIndexOf( general ) )
						images[1] = smallImage;
					if ( bigImageCombo.getSelectionIndex( ) != generalMap.getIndexOf( general ) )
						images[0] = bigImage;
					customGeneral.setGeneralImages( images );
				}

				General model = (General) generalModelMap.get( general );

				if ( !model.getPosX( )
						.equals( "" + posXSpinner.getSelection( ) )
						|| !model.getPosY( ).equals( ""
								+ posYSpinner.getSelection( ) ) )
				{
					customGeneral.setPosX( "" + posXSpinner.getSelection( ) );
					customGeneral.setPosY( "" + posYSpinner.getSelection( ) );
				}
				boolean modelChange = false;
				String generalModel = null;
				String battleModel = null;
				if ( generalModelCombo.getSelectionIndex( ) > -1 )
				{
					generalModel = (String) officerMap.getKeyList( )
							.get( generalModelCombo.getSelectionIndex( ) );
					if ( !generalModel.equals( model.getStrat_model( ) ) )
					{
						modelChange = true;
					}
					else
					{
						generalModel = null;
					}
				}
				if ( battleModelCombo.getSelectionIndex( ) > -1 )
				{
					battleModel = (String) officerMap.getKeyList( )
							.get( battleModelCombo.getSelectionIndex( ) );
					if ( !battleModel.equals( model.getBattle_model( ) ) )
					{
						modelChange = true;
					}
					else
					{
						battleModel = null;
					}
				}
				if ( modelChange )
				{
					customGeneral.setGeneralModel( generalModel, battleModel );
				}

				customGeneral.setGeneralSoldier( soldier );
				customGeneral.setGeneralBaowus( baowus );
				customGeneral.setGeneralSkills( skills );
				customGeneral.setGeneralJueweis( jueweis );
				customGeneral.setGeneralDescription( generalDesc.getText( )
						.trim( ) );
				customGeneral.editGeneral( );
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

	private void createTitle( )
	{
		FormText noteText = WidgetUtil.createFormText( container.getBody( ),
				"<form><p>本页面用于编辑武将，配置完毕后带有 <span color=\"note\">*</span> 号的设置需重新开局方可生效。</p></form>",
				true,
				false );
		noteText.setColor( "note",
				Display.getDefault( ).getSystemColor( SWT.COLOR_RED ) );
	}

	public String getDisplayName( )
	{
		return "编辑武将";
	}

	private void initPage( )
	{
		generalMap = UnitUtil.getAvailableGenerals( );
		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			generalCombo.add( generalName );
			smallImageCombo.add( generalName );
			bigImageCombo.add( generalName );
		}
		generalCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				String general = (String) generalMap.getKeyList( )
						.get( generalCombo.getSelectionIndex( ) );
				idText.setText( general );
				nameText.setText( generalCombo.getText( ) );

				GeneralEditPage.this.general = general;

				General model = (General) generalModelMap.get( general );

				try
				{
					posXSpinner.removeModifyListener( spinnerListener );
					posYSpinner.removeModifyListener( spinnerListener );

					posXSpinner.setSelection( Integer.parseInt( model.getPosX( ) ) );
					posYSpinner.setSelection( Integer.parseInt( model.getPosY( ) ) );

					posXSpinner.addModifyListener( spinnerListener );
					posYSpinner.addModifyListener( spinnerListener );
				}
				catch ( NumberFormatException e1 )
				{
					e1.printStackTrace( );
				}

				GeneralEditPage.this.skills = GeneralParser.getGeneralSkills( general );
				soldier = UnitParser.getUnit( UnitUtil.getGeneralUnitType( general ) );
				bigImageCombo.setText( generalCombo.getText( ) );
				smallImageCombo.setText( generalCombo.getText( ) );
				setSmallImage( general );
				setBigImage( general );
				imageCanvas.setImageData( null );
				imageCanvas.clear( );
				String generalUnit = UnitUtil.getGeneralUnitType( general );
				if ( generalUnitMap.containsKey( generalUnit ) )
				{
					soldierImageCombo.setText( ChangeCode.toLong( (String) generalUnitMap.get( generalUnit ) ) );
					soldierImageCombo.setEnabled( true );
					soldierImageButton.setEnabled( true );
					soldierButton.setText( "设置" );
					soldierText.setText( "设置武将卫队：", false, false );
				}
				else
				{
					soldierButton.setText( "创建将军卫队" );
					soldierText.setText( "<form><p><span color=\"note\">*</span>设置武将卫队：</p></form>",
							true,
							false );
					soldierText.setColor( "note", Display.getDefault( )
							.getSystemColor( SWT.COLOR_RED ) );
					soldierImageCombo.clearSelection( );
					soldierImageCombo.setText( "" );
					soldierImageCombo.setEnabled( false );
					soldierImageButton.setEnabled( false );
					soldierImage = null;
					soldier = null;
				}
				generalModelCombo.deselectAll( );
				battleModelCombo.deselectAll( );

				if ( model != null )
				{
					String strat_model = model.getStrat_model( );
					String battle_model = model.getBattle_model( );
					if ( strat_model != null )
					{
						int index = officerMap.getKeyList( )
								.indexOf( strat_model );
						if ( index != -1 )
						{
							generalModelCombo.select( index );
						}
						else
						{
							generalModelCombo.deselectAll( );
						}
					}
					if ( battle_model != null )
					{
						int index = officerMap.getKeyList( )
								.indexOf( battle_model );
						if ( index != -1 )
						{
							battleModelCombo.select( index );
						}
						else
						{
							battleModelCombo.deselectAll( );
						}
					}
				}

				baowus = BaowuParser.getGeneralBaowus( general );
				jueweis = GeneralParser.getGeneralJueweis( (String) skills.getKeyList( )
						.get( 2 ) );
				generalDesc.setText( "" );
				checkEnableStatus( );
			}
		} );

		generalUnitMap = UnitUtil.getAvailableGeneralUnits( );
		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}
		checkEnableStatus( );
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
		generalMap = UnitUtil.getAvailableGenerals( );
		generalUnitMap = UnitUtil.getAvailableGeneralUnits( );
		officerMap = UnitUtil.getAvailableOfficers( );
		generalModelMap = UnitUtil.getGeneralModels( );
		generalCombo.setItems( new String[0] );
		smallImageCombo.setItems( new String[0] );
		bigImageCombo.setItems( new String[0] );
		soldierImageCombo.setItems( new String[0] );
		generalModelCombo.setItems( new String[0] );
		battleModelCombo.setItems( new String[0] );
		for ( int i = 0; i < generalMap.size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			generalCombo.add( generalName );
			smallImageCombo.add( generalName );
			bigImageCombo.add( generalName );
		}
		for ( int i = 0; i < generalUnitMap.getKeyList( ).size( ); i++ )
		{
			String unitName = ChangeCode.toLong( (String) generalUnitMap.get( i ) );
			soldierImageCombo.add( unitName );
		}
		int index = generalMap.getIndexOf( general );
		if ( index != -1 )
		{
			generalCombo.select( index );
			generalCombo.notifyListeners( SWT.Selection, new Event( ) );
		}

		General model = (General) generalModelMap.get( general );
		for ( int i = 0; i < officerMap.size( ); i++ )
		{
			this.generalModelCombo.add( (String) officerMap.get( i ) );
			this.battleModelCombo.add( (String) officerMap.get( i ) );
		}
		if ( model != null )
		{
			String strat_model = model.getStrat_model( );
			String battle_model = model.getBattle_model( );
			if ( strat_model != null )
			{
				index = officerMap.getKeyList( ).indexOf( strat_model );
				if ( index != -1 )
				{
					generalModelCombo.select( index );
				}
			}
			if ( battle_model != null )
			{
				index = officerMap.getKeyList( ).indexOf( battle_model );
				if ( index != -1 )
				{
					battleModelCombo.select( index );
				}
			}
		}

		checkEnableStatus( );
	}

	private void initSpinner( Spinner combo, int min, int max, int digit,
			int step )
	{
		combo.setMinimum( min );
		combo.setMaximum( max );
		combo.setDigits( digit );
		combo.setIncrement( step );
	}
}
