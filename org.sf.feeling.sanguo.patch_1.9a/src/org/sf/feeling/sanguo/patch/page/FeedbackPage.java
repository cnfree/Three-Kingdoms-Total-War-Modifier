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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class FeedbackPage extends TabPage
{

	private Browser browser;

	public void buildUI(Composite parent)
	{
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl("http://www.1mod.org/forum.php?mod=viewthread&tid=37918&extra=pageD1%3D&page=1");
	}

	public Composite getControl()
	{
		return browser;
	}

	public String getDisplayName()
	{
		return "修改器反馈中心";
	}

}
