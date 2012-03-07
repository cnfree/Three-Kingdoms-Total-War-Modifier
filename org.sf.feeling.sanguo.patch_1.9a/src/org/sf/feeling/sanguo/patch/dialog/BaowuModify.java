
package org.sf.feeling.sanguo.patch.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BaowuParser;
import org.sf.feeling.sanguo.patch.util.CustomComparator;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BaowuModify
{

	private SortMap baowuProperty = FileUtil.loadProperties( "baowu" );
	private Button applyButton;
	private Button restoreButton;
	private Composite patchClient;

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

	private CCombo baowuCombo;

	List listeners = new ArrayList( );

	public void addListener( Listener listener )
	{
		if ( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public Control createModifyControl( Composite parent )
	{
		Composite clientContainer = WidgetUtil.getToolkit( )
				.createComposite( parent );
		GridLayout layout = new GridLayout( );
		layout.numColumns = 4;
		clientContainer.setLayout( layout );

		Label baowuLabel = WidgetUtil.getToolkit( )
				.createLabel( clientContainer, "选择宝物：" );

		baowuCombo = WidgetUtil.getToolkit( ).createCCombo( clientContainer,
				SWT.READ_ONLY );
		GridData gd = new GridData( );
		gd.widthHint = 150;
		baowuCombo.setLayoutData( gd );

		List soldierList = new ArrayList( );
		Iterator iter = baowuProperty.keySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			soldierList.add( iter.next( ) );
		}
		Collections.sort( soldierList, new CustomComparator( baowuProperty ) {

			public int compare( Object arg0, Object arg1 )
			{
				String code0 = null;
				String code1 = null;
				if ( properties != null )
				{
					code0 = (String) properties.get( arg0 );
					code1 = (String) properties.get( arg1 );
				}
				else
				{
					code0 = (String) arg0;
					code1 = (String) arg1;
				}
				return code0.compareToIgnoreCase( code1 );
			}
		} );

		baowuCombo.setItems( (String[]) soldierList.toArray( new String[0] ) );

		gd = new GridData( );
		gd.horizontalSpan = 2;
		WidgetUtil.getToolkit( )
				.createLabel( clientContainer, "" )
				.setLayoutData( gd );

		patchClient = WidgetUtil.getToolkit( )
				.createComposite( clientContainer );
		layout = new GridLayout( );
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.numColumns = 4;
		patchClient.setLayout( layout );

		gd = new GridData( GridData.FILL_BOTH );
		gd.horizontalSpan = 4;
		patchClient.setLayoutData( gd );
		patchClient.setEnabled( false );
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "统军作战：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Command" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "治国安邦：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Management" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "威望影响：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Influence" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "将军卫队人数：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "PersonalSecurity" );
			initNumberCombo( combo, -30, 30 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "将军卫队经验：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "BodyguardValour" );
			initNumberCombo( combo, -9, 9 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "将军生命点数：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "HitPoints" );
			initNumberCombo( combo, -100, 100 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "骑兵统御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "CavalryCommand" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "步兵统御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "InfantryCommand" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "突袭时统御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Ambush" );
			initNumberCombo( combo, -10, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "攻城器点数：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "SiegeEngineering" );
			initNumberCombo( combo, 0, 500, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "伤病治愈：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "BattleSurgery" );
			initNumberCombo( combo, 0, 100, 5 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "部队士气：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "TroopMorale" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "部队攻击：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Attack" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "部队防御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Defence" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "攻城器攻击：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "SiegeAttack" );
			initNumberCombo( combo, -100, 100, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "攻城器防御：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "SiegeDefence" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "公共健康：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Health" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "公共安全：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "PublicSecurity" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "法律：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Law" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "动乱(负的好)：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Unrest" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "贫穷肮脏(负的好)：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Squalor" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "农业收成：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Farming" );
			initNumberCombo( combo, -200, 200, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "贸易收入：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Trading" );
			initNumberCombo( combo, -200, 200, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "农产品收入：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "GrainTrading" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "奴隶贸易：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "SlaveTrading" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "税收：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "TaxCollection" );
			initNumberCombo( combo, -200, 200, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "破城得金：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Looting" );
			initNumberCombo( combo, -200, 200, 10 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "修建建筑便宜：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Construction" );
			initNumberCombo( combo, -200, 200, 10 );
		}
		{
			Label infoLabel = WidgetUtil.getToolkit( )
					.createLabel( patchClient, "部队训练费用减少：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "TrainingUnits" );
			initNumberCombo( combo, -20, 20, 2 );

			gd = new GridData( );
			gd.widthHint = infoLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 1;
			baowuLabel.setLayoutData( gd );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "间谍训练费用减少：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "TrainingAgents" );
			initNumberCombo( combo, -20, 20, 2 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "移动距离：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "MovementPoints" );
			initNumberCombo( combo, -20, 20, 2 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "视觉范围：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "LineOfSight" );
			initNumberCombo( combo, -20, 20 );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "忠诚：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "BribeResistance" );
			combo.setItems( new String[]{
					"", "10000", "1000", "100", "0", "-100", "-1000", "-10000"
			} );
		}
		{
			WidgetUtil.getToolkit( ).createLabel( patchClient, "生育力：" );
			CCombo combo = WidgetUtil.getToolkit( ).createCCombo( patchClient,
					SWT.READ_ONLY );
			gd = new GridData( );
			gd.widthHint = 150;
			combo.setLayoutData( gd );
			combo.setData( "Fertility" );
			initNumberCombo( combo, -100, 100 );
		}
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
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.END;
		applyButton.setLayoutData( gd );
		applyButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				if ( baowuCombo.indexOf( baowuCombo.getText( ) ) != -1 )
				{
					applyButton.setEnabled( false );
					BakUtil.bakData( "宝物数据修改：" + baowuCombo.getText( ) );
					patchClient.setEnabled( true );
					String baowuType = null;
					if ( baowuProperty.containsKey( baowuCombo.getText( ) ) )
					{
						baowuType = (String) baowuProperty.get( baowuCombo.getText( ) );
					}
					if ( baowuType != null )
					{
						saveBaowu( baowuType );
						Event event = new Event( );
						event.type = SWT.Verify;
						event.doit = true;
						notifyEvent( event );
					}
					applyButton.setEnabled( true );
				}
			}
		} );

		restoreButton = WidgetUtil.getToolkit( ).createButton( buttonGroup,
				"还原",
				SWT.PUSH );
		gd = new GridData( );
		gd.grabExcessHorizontalSpace = true;
		restoreButton.setLayoutData( gd );
		restoreButton.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				restoreButton.setEnabled( false );
				if ( FileConstants.baowuFile.exists( ) )
				{
					BakUtil.restoreCurrectVersionBakFile( );
				}
				String text = baowuCombo.getText( );
				baowuCombo.setText( "" );
				baowuCombo.setText( text );
				restoreButton.setEnabled( true );
			}
		} );

		baowuCombo.addModifyListener( new ModifyListener( ) {

			public void modifyText( ModifyEvent e )
			{
				if ( baowuCombo.indexOf( baowuCombo.getText( ) ) != -1 )
				{
					patchClient.setEnabled( true );
					String baowuType = null;
					if ( baowuProperty.containsKey( baowuCombo.getText( ) ) )
					{
						baowuType = (String) baowuProperty.get( baowuCombo.getText( ) );
					}
					if ( baowuType != null )
					{
						initBaowu( baowuType );
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

		return clientContainer;
	}

	private void initBaowu( String baowuType )
	{
		if ( baowuType != null )
		{
			HashMap attributes = BaowuParser.getBaowuEffects( baowuType );
			Control[] children = patchClient.getChildren( );
			for ( int i = 0; i < children.length; i++ )
			{
				Control control = children[i];
				if ( control instanceof CCombo )
				{
					( (CCombo) control ).select( 0 );
				}
			}
			if ( attributes.size( ) > 0 )
			{
				Iterator iter = attributes.keySet( ).iterator( );
				while ( iter.hasNext( ) )
				{
					String effect = (String) iter.next( );
					String value = (String) attributes.get( effect );
					for ( int i = 0; i < children.length; i++ )
					{
						Control control = children[i];
						if ( control instanceof CCombo )
						{
							if ( effect.equals( control.getData( ) ) )
							{
								( (CCombo) control ).setText( value );
							}
						}
					}
				}
			}
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

	private void initNumberCombo( CCombo combo, int min, int max, int step )
	{
		for ( int i = max; i >= min; i -= step )
		{
			combo.add( "" + i );
		}
		combo.add( "", 0 );
		combo.addVerifyListener( numberVerifyListener );
	}

	private void notifyEvent( Event event )
	{
		for ( int i = 0; i < listeners.size( ); i++ )
		{
			( (Listener) listeners.get( i ) ).handleEvent( event );
		}
	}

	public void removeListener( VerifyListener listener )
	{
		if ( listeners.contains( listener ) )
		{
			listeners.remove( listener );
		}
	}

	public void saveBaowu( String baowuType )
	{
		if ( baowuType != null )
		{
			// FileUtil.bakFile(FileConstants.baowuFile);
			HashMap attributes = BaowuParser.getBaowuEffects( baowuType );
			Control[] children = patchClient.getChildren( );
			for ( int i = 0; i < children.length; i++ )
			{
				Control control = children[i];
				if ( control instanceof CCombo
						&& ( (CCombo) control ).getText( ).trim( ).length( ) > 0 )
				{
					if ( control.getData( ) != null )
					{
						attributes.put( control.getData( ),
								( (CCombo) control ).getText( ).trim( ) );
					}
				}
			}
			BaowuParser.saveBaowu( baowuType, attributes );
		}
	}

}
