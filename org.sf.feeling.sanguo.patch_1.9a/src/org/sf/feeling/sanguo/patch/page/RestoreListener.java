/*******************************************************************************
 * Copyright (c) 2007 cnfree.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  cnfree  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.sanguo.patch.page;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.sf.feeling.sanguo.patch.util.BakUtil;

public class RestoreListener extends SelectionAdapter
{

	public void widgetSelected( SelectionEvent e )
	{
		if ( e.widget instanceof Control )
			( (Control) e.widget ).setEnabled( false );
		BakUtil.restoreCurrectVersionBakFile( );
		if ( e.widget instanceof Control )
			( (Control) e.widget ).setEnabled( true );
	}
}
