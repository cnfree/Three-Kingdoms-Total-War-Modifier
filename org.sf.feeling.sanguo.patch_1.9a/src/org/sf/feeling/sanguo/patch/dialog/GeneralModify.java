
package org.sf.feeling.sanguo.patch.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.GeneralParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class GeneralModify
{

	private final static String[] tongshuai = new String[]{
			"J-0101-CaoCao",
			"J-0120-SiMaYi",
			"J-3201-ZhuGeLiang",
			"J-0302-ZhouYu",
			"J-0321-LuXun"
	};
	private final static String[] adou = new String[]{
			"J-1601-HanXuan",
			"J-1606-LiuDu",
			"J-1706-YangSong",
			"J-0801-LiuZhang",
			"J-0613-GongSunGong"
	};
	private final static String[] wangzuo = new String[]{
			"J-0309-ZhangZhao",
			"J-0110-XunYi",
			"J-3102-ChenQun",
			"J-0310-ZhangHong",
			"J-3302-ZhugeJin"
	};
	private final static String[] jiangjun = new String[]{
			"J-0401-LvBu",
			"J-0203-ZhangFei",
			"J-0403-ZhangLiao",
			"J-0610-ZhaoYun",
			"J-0202-GuanYu"
	};
	private final static String[] junshi = new String[]{
			"J-0112-GuoJia",
			"J-0510-TianFeng",
			"J-0511-JuShou",
			"J-0903-JiaXu",
			"J-0410-ChenGong"
	};

	private final Random random = new Random( );
	private Button applyButton;
	private Button restoreButton;
	private Composite patchClient;
	private Group[] groups = new Group[3];

	VerifyListener numberVerifyListener = new VerifyListener( ) {

		public void verifyText( VerifyEvent event )
		{
			if ( event.text.length( ) <= 0 )
			{
				return;
			}
			int beginIndex = Math.min( event.start, event.end );
			int endIndex = Math.max( event.start, event.end );
			String inputtedText = ( (CCombo) event.widget ).getText( );
			String newString = inputtedText.substring( 0, beginIndex );

			newString += event.text;
			newString += inputtedText.substring( endIndex );
			try
			{
				if ( newString.length( ) > 0 )
					Integer.parseInt( newString );
			}
			catch ( Exception e )
			{
				event.doit = false;
				return;
			}
			event.doit = true;
		}
	};

	Listener listener = new Listener( ) {

		public void handleEvent( Event event )
		{
			Object obj = event.widget.getData( );
			if ( obj instanceof String
					&& event.widget.getData( (String) obj ) instanceof CCombo
					&& event.widget instanceof Button )
			{
				( (CCombo) event.widget.getData( (String) obj ) ).setEnabled( ( (Button) event.widget ).getSelection( ) );
			}
		}
	};

	private CCombo generalCombo;

	private boolean isMemory = false;

	public GeneralModify( boolean isMemory )
	{
		this.isMemory = isMemory;
	}

	public Control createModifyControl( Composite parent )
	{
		Composite clientContainer = WidgetUtil.getToolkit( )
				.createComposite( parent );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 10;
		clientContainer.setLayout( layout );

		WidgetUtil.getToolkit( ).createLabel( clientContainer, "选择武将：" );

		generalCombo = WidgetUtil.getToolkit( ).createCCombo( clientContainer,
				SWT.READ_ONLY );
		GridData gd = new GridData( );
		gd.widthHint = 120;
		generalCombo.setLayoutData( gd );
		final Button[] buttons = new Button[8];
		buttons[0] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"儒帅",
				SWT.PUSH );
		buttons[2] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"王佐",
				SWT.PUSH );
		buttons[3] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"熊虎",
				SWT.PUSH );
		buttons[4] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"军师",
				SWT.PUSH );
		buttons[1] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"黄昏",
				SWT.PUSH );
		buttons[5] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"潘凤",
				SWT.PUSH );
		buttons[6] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"白板",
				SWT.PUSH );
		buttons[7] = WidgetUtil.getToolkit( ).createButton( clientContainer,
				"随机",
				SWT.PUSH );
		for ( int i = 0; i < buttons.length; i++ )
		{
			buttons[i].addSelectionListener( new SelectionAdapter( ) {

				public void widgetSelected( SelectionEvent e )
				{
					int index = generalCombo.getSelectionIndex( );
					String general = null;
					if ( index > -1 )
						general = (String) generalMap.getKeyList( ).get( index );
					for ( int i = 0; i < buttons.length; i++ )
					{
						if ( buttons[i] == e.widget )
						{
							switch ( i )
							{
								case 0 :
									List list = new ArrayList( );
									list.addAll( Arrays.asList( tongshuai ) );
									if ( general != null )
										list.remove( general );
									initGeneral( (String) list.get( random.nextInt( list.size( ) ) ) );
									break;
								case 1 :
									list = new ArrayList( );
									list.addAll( Arrays.asList( adou ) );
									if ( general != null )
										list.remove( general );
									initGeneral( (String) list.get( random.nextInt( list.size( ) ) ) );
									break;
								case 2 :
									list = new ArrayList( );
									list.addAll( Arrays.asList( wangzuo ) );
									if ( general != null )
										list.remove( general );
									initGeneral( (String) list.get( random.nextInt( list.size( ) ) ) );
									break;
								case 3 :
									list = new ArrayList( );
									list.addAll( Arrays.asList( jiangjun ) );
									if ( general != null )
										list.remove( general );
									initGeneral( (String) list.get( random.nextInt( list.size( ) ) ) );
									break;
								case 4 :
									list = new ArrayList( );
									list.addAll( Arrays.asList( junshi ) );
									if ( general != null )
										list.remove( general );
									initGeneral( (String) list.get( random.nextInt( list.size( ) ) ) );
									break;
								case 5 :
									Control[] children = patchClient.getChildren( );
									for ( int j = 0; j < children.length; j++ )
									{
										Control control = children[j];
										if ( control instanceof CCombo )
										{
											( (CCombo) control ).select( 1 );
										}
									}
									for ( int j = 0; j < groups.length; j++ )
									{
										children = groups[j].getChildren( );
										for ( int z = 0; z < children.length; z++ )
										{
											Control control = children[z];
											if ( control instanceof Button )
											{
												Button button = (Button) control;
												button.setSelection( true );
												Object obj = button.getData( );
												if ( obj instanceof String
														&& button.getData( (String) obj ) instanceof CCombo )
												{
													if ( button.getSelection( ) )
													{
														CCombo combo = (CCombo) button.getData( (String) obj );
														combo.select( 1 );
														combo.setEnabled( true );
													}
												}
											}
										}
									}
									break;
								case 6 :
									children = patchClient.getChildren( );
									for ( int j = 0; j < children.length; j++ )
									{
										Control control = children[j];
										if ( control instanceof CCombo )
										{
											( (CCombo) control ).select( ( (CCombo) control ).getItemCount( ) - 1 );
										}
									}
									for ( int j = 0; j < groups.length; j++ )
									{
										children = groups[j].getChildren( );
										for ( int z = 0; z < children.length; z++ )
										{
											Control control = children[z];
											if ( control instanceof Button )
											{
												Button button = (Button) control;
												button.setSelection( false );
												Object obj = button.getData( );
												if ( obj instanceof String
														&& button.getData( (String) obj ) instanceof CCombo )
												{
													CCombo combo = (CCombo) button.getData( (String) obj );
													combo.select( 0 );
													combo.setEnabled( false );
												}
											}
										}
									}
									break;
								case 7 :
									children = patchClient.getChildren( );
									for ( int j = 0; j < children.length; j++ )
									{
										Control control = children[j];
										if ( control instanceof CCombo )
										{
											int count = ( (CCombo) control ).getItemCount( );
											( (CCombo) control ).select( nextInt( count - 1 ) + 1 );
										}
									}
									int skillsCount = random.nextInt( 5 ) + 1;
									int skillsAllcount = 48;
									int number = 0;
									for ( int j = 0; j < groups.length
											|| number < skillsCount; j++ )
									{
										if ( j >= groups.length )
											j = 0;
										children = groups[j].getChildren( );
										for ( int z = 0; z < children.length; z++ )
										{
											Control control = children[z];
											if ( control instanceof Button )
											{
												if ( random.nextInt( skillsAllcount ) <= skillsCount
														&& number < skillsCount )
												{
													Button button = (Button) control;
													button.setSelection( true );
													Object obj = button.getData( );
													if ( obj instanceof String
															&& button.getData( (String) obj ) instanceof CCombo )
													{
														CCombo combo = (CCombo) button.getData( (String) obj );
														int count = combo.getItemCount( );
														combo.select( random.nextInt( count - 1 ) + 1 );
														combo.setEnabled( true );
														number++;
													}
												}
												else
												{
													Button button = (Button) control;
													button.setSelection( false );
													Object obj = button.getData( );
													if ( obj instanceof String
															&& button.getData( (String) obj ) instanceof CCombo )
													{
														CCombo combo = (CCombo) button.getData( (String) obj );
														combo.select( 0 );
														combo.setEnabled( false );
													}
												}
											}
										}
									}
									break;
							}
							break;
						}
					}
				}
			} );
		}

		generalMap = UnitUtil.getAvailableGenerals( );
		for ( int i = 0; i < generalMap.getKeyList( ).size( ); i++ )
		{
			String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
			generalCombo.add( generalName );
		}

		patchClient = WidgetUtil.getToolkit( )
				.createComposite( clientContainer );
		layout = new GridLayout( );
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.numColumns = 8;
		patchClient.setLayout( layout );

		gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 10;
		patchClient.setLayoutData( gd );
		if ( !isMemory )
			patchClient.setEnabled( false );

		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "体力：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jn1000", combo );
			combo.setData( "Jn1001", combo );
			combo.setData( "Jn1000" );
			initNumberCombo( combo, 1, 15 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "智略：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jn2000", combo );
			combo.setData( "Jn2001", combo );
			combo.setData( "Jn2000" );
			initNumberCombo( combo, 0, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "统帅：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jn3000", combo );
			combo.setData( "Jn3001", combo );
			combo.setData( "Jn3000" );
			initNumberCombo( combo, 0, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "攻击：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jn4000", combo );
			combo.setData( "Jn4001", combo );
			combo.setData( "Jn4000" );
			initNumberCombo( combo, 0, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "防御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jn5000", combo );
			combo.setData( "Jn5001", combo );
			combo.setData( "Jn5000" );
			initNumberCombo( combo, 0, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "骑兵：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA1000", combo );
			combo.setData( "JnA1000" );
			initNumberCombo( combo, -3, 2 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "步兵：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA2000", combo );
			combo.setData( "JnA2000" );
			initNumberCombo( combo, -3, 2 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "埋伏：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA3000", combo );
			combo.setData( "JnA3000" );
			initNumberCombo( combo, -3, 0 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "水军：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA4000", combo );
			combo.setData( "JnA4000" );
			initNumberCombo( combo, -3, 0 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "攻城：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA5000", combo );
			combo.setData( "JnA5000" );
			initNumberCombo( combo, -4, -1 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "守城：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA6000", combo );
			combo.setData( "JnA6000" );
			initNumberCombo( combo, -3, 0 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "仁德：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA7000", combo );
			combo.setData( "JnA7000" );
			initNumberCombo( combo, 0, 8 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "忠诚：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA8000", combo );
			combo.setData( "JnA8000" );
			initNumberCombo( combo, -3, 6 );
			combo.remove( "0" );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "潜力：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "JnA9000", combo );
			combo.setData( "JnA9000" );
			initEndianNumberCombo( combo, 0, 8 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "个人追求：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "Jnxg1000" );
			combo.setData( "Jnxg1000", combo );
			combo.setData( "Jnxg2000", combo );
			combo.setData( "Jnxg3000", combo );
			combo.setData( "Jnxg4000", combo );
			combo.setData( "Jnxg5000", combo );
			combo.setData( "Jnxg6000", combo );
			combo.setData( "Jnxg7000", combo );
			combo.add( "" );
			combo.add( "霸业1" );
			combo.add( "霸业2" );
			combo.add( "霸业3" );
			combo.add( "义信1" );
			combo.add( "义信2" );
			combo.add( "义信3" );
			combo.add( "奋争1" );
			combo.add( "奋争2" );
			combo.add( "奋争3" );
			combo.add( "豪强1" );
			combo.add( "豪强2" );
			combo.add( "豪强3" );
			combo.add( "名望1" );
			combo.add( "名望2" );
			combo.add( "名望3" );
			combo.add( "安身1" );
			combo.add( "安身2" );
			combo.add( "安身3" );
			combo.add( "异族1" );
			combo.add( "异族2" );
			combo.add( "异族3" );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "经验：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 80;
			combo.setLayoutData( gd );
			combo.setData( "exp", combo );
			combo.setData( "exp" );
			initEndianNumberCombo( combo, 0, 9 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "" );
			WidgetUtil.getToolkit( ).createLabel( patchClient, "" );
		}

		groups[0] = WidgetUtil.getToolkit( ).createGroup( patchClient, "固定技" );
		layout = new GridLayout( );
		layout.numColumns = 6;
		groups[0].setLayout( layout );

		gd = new GridData( );
		gd.horizontalSpan = 8;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		groups[0].setLayoutData( gd );

		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"巧夺：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1010JnQD-A", combo );
			button.setData( "A1010JnQD-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"夜袭：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1020JnYX-A", combo );
			button.setData( "A1020JnYX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"神勇：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1030JnSY-A", combo );
			button.setData( "A1030JnSY-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"强健：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1040JnQJ-A", combo );
			button.setData( "A1040JnQJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"精妙：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1050JnJM-A", combo );
			button.setData( "A1050JnJM-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"解毒：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1060JnJD-A", combo );
			button.setData( "A1060JnJD-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"风水：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1070JnFS-A", combo );
			button.setData( "A1070JnFS-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"后勤：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1080JnHQ-A", combo );
			button.setData( "A1080JnHQ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"连击：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1090JnLJ-A", combo );
			button.setData( "A1090JnLJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[0],
					"铁壁：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[0],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "A1100JnTB-A", combo );
			button.setData( "A1100JnTB-A" );
		}

		groups[1] = WidgetUtil.getToolkit( ).createGroup( patchClient, "辅助技" );
		layout = new GridLayout( );
		layout.numColumns = 6;
		groups[1].setLayout( layout );

		gd = new GridData( );
		gd.horizontalSpan = 8;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		groups[1].setLayoutData( gd );
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"农业：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2010JnNY-A", combo );
			button.setData( "B2010JnNY-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"农贸：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2020JnNM-A", combo );
			button.setData( "B2020JnNM-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"征税：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2030JnZS-A", combo );
			button.setData( "B2030JnZS-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"商业：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2040JnSY-A", combo );
			button.setData( "B2040JnSY-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"仁政：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1] );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2050JnRZ-A", combo );
			button.setData( "B2050JnRZ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"安民：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2060JnAM-A", combo );
			button.setData( "B2060JnAM-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"治安：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2070JnZA-A", combo );
			button.setData( "B2070JnZA-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"采矿：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2080JnCK-A", combo );
			button.setData( "B2080JnCK-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"筑城：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2090JnZC-A", combo );
			button.setData( "B2090JnZC-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"征兵：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2100JnZB-A", combo );
			button.setData( "B2100JnZB-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"驯兽：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2110JnXS-A", combo );
			button.setData( "B2110JnXS-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"谍报：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2120JnDB-A", combo );
			button.setData( "B2120JnDB-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"围城：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2130JnWC-A", combo );
			button.setData( "B2130JnWC-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"司金：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2140JnSJ-A", combo );
			button.setData( "B2140JnSJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"军医：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2150JnJY-A", combo );
			button.setData( "B2150JnJY-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"洞察：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2160JnDC-A", combo );
			button.setData( "B2160JnDC-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"侦查：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2170JnZC-A", combo );
			button.setData( "B2170JnZC-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[1],
					"亲蛮：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[1],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "B2180JnQM-A", combo );
			button.setData( "B2180JnQM-A" );
		}

		groups[2] = WidgetUtil.getToolkit( ).createGroup( patchClient, "引发技" );
		layout = new GridLayout( );
		layout.numColumns = 6;
		groups[2].setLayout( layout );

		gd = new GridData( );
		gd.horizontalSpan = 8;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		groups[2].setLayoutData( gd );
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"富豪：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3010JnFH-A", combo );
			button.setData( "C3010JnFH-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"富商：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3020JnFS-A", combo );
			button.setData( "C3020JnFS-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"屯田：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3030JnTT-A", combo );
			button.setData( "C3030JnTT-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"军市：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3040JnJS-A", combo );
			button.setData( "C3040JnJS-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"嗜酒：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3050JnSJ-A", combo );
			button.setData( "C3050JnSJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"诗想：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3060JnSX-A", combo );
			button.setData( "C3060JnSX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"步将：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3070JnBJ-A", combo );
			button.setData( "C3070JnBJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"骑将：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3080JnQJ-A", combo );
			button.setData( "C3080JnQJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"逆袭：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3090JnNX-A", combo );
			button.setData( "C3090JnNX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"破袭：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3100JnPX-A", combo );
			button.setData( "C3100JnPX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"强击：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3110JnQJ-A", combo );
			button.setData( "C3110JnQJ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"巧变：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3120JnQB-A", combo );
			button.setData( "C3120JnQB-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"石墙：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );

			initSkillCombo( combo );
			button.setData( "C3130JnSQ-A", combo );
			button.setData( "C3130JnSQ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"沉着：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3140JnCZ-A", combo );
			button.setData( "C3140JnCZ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"补充：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3150JnBC-A", combo );
			button.setData( "C3150JnBC-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"昂扬：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3160JnAY-A", combo );
			button.setData( "C3160JnAY-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"急袭：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3170JnJX-A", combo );
			button.setData( "C3170JnJX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"奋迅：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3180JnFX-A", combo );
			button.setData( "C3180JnFX-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"长驱：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3190JnCQ-A", combo );
			button.setData( "C3190JnCQ-A" );
		}
		{
			Button button = WidgetUtil.getToolkit( ).createButton( groups[2],
					"运送：",
					SWT.CHECK );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( groups[2],
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 100;
			combo.setLayoutData( gd );
			initSkillCombo( combo );
			button.setData( "C3200JnYS-A", combo );
			button.setData( "C3200JnYS-A" );
		}

		generalCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( generalCombo.getSelectionIndex( ) != -1 )
				{
					patchClient.setEnabled( true );
					String general = (String) generalMap.getKeyList( )
							.get( generalCombo.getSelectionIndex( ) );
					if ( general != null )
					{
						initGeneral( general );
						Event event = new Event( );
						event.type = SWT.Verify;
						event.doit = true;
						notifyEvent( event );
						return;
					}
				}
				else
					patchClient.setEnabled( false );

				Event event = new Event( );
				event.type = SWT.Verify;
				event.doit = false;
				notifyEvent( event );
				return;
			}
		} );

		initSkills( );

		return clientContainer;
	}

	private void initSkillCombo( CCombo combo )
	{
		combo.add( "" );
		combo.add( "高" );
		combo.add( "中" );
		combo.add( "低" );
	}

	public void saveGeneral( String general, SortMap skills )
	{
		if ( general != null )
		{
			// FileUtil.bakFile(FileConstants.stratFile, ".unit.patch.bak")
			// .deleteOnExit();
			saveGeneralSkills( skills );
			GeneralParser.setGeneralSkills( general, skills );
		}
	}

	public void saveGeneralSkills( SortMap skills )
	{
		Control[] children = patchClient.getChildren( );
		for ( int i = 0; i < children.length; i++ )
		{
			Control control = children[i];
			if ( control instanceof CCombo )
			{
				int index = ( (CCombo) control ).getSelectionIndex( );
				if ( isMemory && index <= 0 )
					index = 1;
				else if ( !isMemory
						&& ( (CCombo) control ).getSelectionIndex( ) <= 0 )
				{
					continue;
				}
				int count = ( (CCombo) control ).getItemCount( );
				if ( control.getData( ) instanceof String )
				{
					String skill = (String) control.getData( );
					if ( "Jnxg1000".equals( skill ) )
					{
						int skillIndex = 0;
						for ( int j = 1; j < 8; j++ )
						{
							if ( skills.containsKey( "Jnxg" + j + "000" ) )
							{
								skillIndex = skills.getIndexOf( "Jnxg"
										+ j
										+ "000" );
								skills.remove( "Jnxg" + j + "000" );
								break;
							}
						}
						skills.putAt( "Jnxg" + ( index + 2 ) / 3 + "000",
								( index % 3 == 0 ? 3 : index % 3 ) + "",
								skillIndex );
					}
					else if ( "Jn1000".equals( skill )
							|| "Jn2000".equals( skill )
							|| "Jn3000".equals( skill )
							|| "Jn4000".equals( skill )
							|| "Jn5000".equals( skill ) )
					{
						for ( int j = 1; j < 6; j++ )
						{
							if ( ( "Jn" + j + "000" ).equals( skill ) )
							{
								int skillIndex = 0;
								if ( skills.containsKey( ( "Jn" + j + "000" ) ) )
								{
									skillIndex = skills.getIndexOf( ( "Jn" + j + "000" ) );
									skills.remove( ( "Jn" + j + "000" ) );
								}
								else if ( skills.containsKey( ( "Jn" + j + "001" ) ) )
								{
									skillIndex = skills.getIndexOf( ( "Jn" + j + "001" ) );
									skills.remove( ( "Jn" + j + "001" ) );
								}
								int sep = j == 1 ? 8 : 9;
								if ( count - index <= sep )
								{
									skills.putAt( ( "Jn" + j + "000" ), count
											- index
											+ "", skillIndex );
								}
								else
								{
									skills.putAt( ( "Jn" + j + "001" ), count
											- index
											- sep
											+ "", skillIndex );
								}
							}
						}
					}
					else if ( "exp".equals( skill ) )
					{
						skills.put( skill, index - 1+ "" );
					}
					else
					{
						skills.put( skill, count - index + "" );
					}
				}
			}
		}
		for ( int j = 0; j < groups.length; j++ )
		{
			children = groups[j].getChildren( );
			for ( int i = 0; i < children.length; i++ )
			{
				Control control = children[i];
				if ( control instanceof Button )
				{
					Button button = (Button) control;
					Object obj = button.getData( );
					if ( obj instanceof String
							&& button.getData( (String) obj ) instanceof CCombo )
					{
						if ( button.getSelection( ) )
						{
							CCombo combo = (CCombo) button.getData( (String) obj );
							if ( combo.getSelectionIndex( ) != 0 )
								skills.put( obj,
										combo.getItemCount( )
												- combo.getSelectionIndex( )
												+ "" );
						}
						else
							skills.remove( obj );
					}
				}
			}
		}
	}

	private void initGeneral( String general )
	{
		if ( general != null )
		{
			SortMap skills = GeneralParser.getGeneralSkills( general );
			setSkills( general, skills );
			initSkills( );
		}
	}

	private void initNumberCombo( CCombo combo, int min, int max )
	{
		for ( int i = max; i >= min; i-- )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
		combo.addVerifyListener( numberVerifyListener );
	}

	private void initEndianNumberCombo( CCombo combo, int min, int max )
	{
		for ( int i = min; i <= max; i++ )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
		combo.addVerifyListener( numberVerifyListener );
	}

	List listeners = new ArrayList( );
	private SortMap skills = new SortMap( );
	private SortMap generalMap;

	public void addListener( Listener listener )
	{
		if ( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( VerifyListener listener )
	{
		if ( listeners.contains( listener ) )
		{
			listeners.remove( listener );
		}
	}

	private void notifyEvent( Event event )
	{
		for ( int i = 0; i < listeners.size( ); i++ )
		{
			( (Listener) listeners.get( i ) ).handleEvent( event );
		}
	}

	public void setSkills( String general, SortMap skills )
	{
		if ( skills == null )
			return;
		this.skills = skills;
		if ( patchClient != null && !patchClient.isDisposed( ) )
		{
			if ( general != null && generalCombo != null )
			{
				generalCombo.select( generalMap.getIndexOf( general ) );
			}
			initSkills( );
			if(generalCombo.getSelectionIndex( )!=-1){
				patchClient.setEnabled( true );
			}
			else patchClient.setEnabled( false );
		}
	}

	public SortMap getSkills( )
	{
		return skills;
	}

	private void initSkills( )
	{
		Control[] children = patchClient.getChildren( );
		for ( int i = 0; i < children.length; i++ )
		{
			Control control = children[i];
			if ( control instanceof CCombo )
			{
				if ( isMemory )
					( (CCombo) control ).select( 1 );
				else
					( (CCombo) control ).select( 0 );
			}
		}
		if ( skills.size( ) > 0 )
		{
			Iterator iter = skills.getKeyList( ).iterator( );
			while ( iter.hasNext( ) )
			{
				String effect = (String) iter.next( );
				String value = (String) skills.get( effect );
				if ( "Jnxg1000".equals( effect ) )
				{
					value = "霸业" + value;
				}
				else if ( "Jnxg2000".equals( effect ) )
				{
					value = "义信" + value;
				}
				else if ( "Jnxg3000".equals( effect ) )
				{
					value = "奋争" + value;
				}
				else if ( "Jnxg4000".equals( effect ) )
				{
					value = "豪强" + value;
				}
				else if ( "Jnxg5000".equals( effect ) )
				{
					value = "名望" + value;
				}
				else if ( "Jnxg6000".equals( effect ) )
				{
					value = "安身" + value;
				}
				else if ( "Jnxg7000".equals( effect ) )
				{
					value = "异族" + value;
				}

				for ( int i = 0; i < children.length; i++ )
				{
					Control control = children[i];
					Object data = control.getData( effect );
					if ( data instanceof CCombo )
					{
						if ( "Jnxg1000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg2000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg3000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg4000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg5000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg6000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jnxg7000".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else if ( "Jn1001".equals( effect ) )
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value )
									- 8 );
						}
						else if ( "Jn2001".equals( effect ) )
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value )
									- 9 );
						}
						else if ( "Jn3001".equals( effect ) )
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value )
									- 9 );
						}
						else if ( "Jn4001".equals( effect ) )
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value )
									- 9 );
						}
						else if ( "Jn5001".equals( effect ) )
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value )
									- 9 );
						}
						else if ( "exp".equals( effect ) )
						{
							( (CCombo) data ).setText( value );
						}
						else
						{
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value ) );
						}
					}
				}
			}
		}

		for ( int j = 0; j < groups.length; j++ )
		{
			children = groups[j].getChildren( );
			for ( int i = 0; i < children.length; i++ )
			{
				Control control = children[i];
				if ( control instanceof CCombo )
				{
					( (CCombo) control ).select( 0 );
					( (CCombo) control ).setEnabled( false );
				}
				else if ( control instanceof Button )
				{
					( (Button) control ).removeListener( SWT.Selection,
							listener );
					( (Button) control ).setSelection( false );
					( (Button) control ).addListener( SWT.Selection, listener );
				}
			}
			if ( skills.size( ) > 0 )
			{
				Iterator iter = skills.getKeyList( ).iterator( );
				while ( iter.hasNext( ) )
				{
					String effect = (String) iter.next( );
					String value = (String) skills.get( effect );
					for ( int i = 0; i < children.length; i++ )
					{
						Control control = children[i];
						Object data = control.getData( effect );
						if ( data instanceof CCombo )
						{
							( (Button) control ).setSelection( true );
							( (CCombo) data ).select( ( (CCombo) data ).getItemCount( )
									- Integer.parseInt( value ) );
							( (CCombo) data ).setEnabled( true );
						}
					}
				}
			}
		}
	}

	public void refresh( )
	{
		if ( generalCombo != null )
		{
			String selectGeneral = "";
			int index = generalCombo.getSelectionIndex( );
			if ( index > -1 )
				selectGeneral = (String) generalMap.getKeyList( ).get( index );
			generalMap = UnitUtil.getAvailableGenerals( );
			generalCombo.setItems( new String[0] );
			for ( int i = 0; i < generalMap.size( ); i++ )
			{
				String generalName = ChangeCode.toLong( (String) generalMap.get( i ) );
				generalCombo.add( generalName );
			}
			int newIndex = generalMap.getIndexOf( selectGeneral );
			if ( newIndex != -1 )
			{
				generalCombo.select( newIndex );
				generalCombo.notifyListeners( SWT.Selection, new Event( ) );
			}
		}
	}

	private int nextInt( int value )
	{
		double result = (double) value * Math.pow( random.nextFloat( ), 2 );
		if ( result / value > 0.95 )
			result = value;
		return value - (int) ( result );
	}

}
