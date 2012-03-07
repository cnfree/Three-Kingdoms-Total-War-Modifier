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

package org.sf.feeling.sanguo.patch.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sf.feeling.sanguo.patch.page.AboutPage;
import org.sf.feeling.sanguo.patch.page.BakAndRestorePage;
import org.sf.feeling.sanguo.patch.page.BaowuModifyPage;
import org.sf.feeling.sanguo.patch.page.BasicPatchPage;
import org.sf.feeling.sanguo.patch.page.BuildingBonusPage;
import org.sf.feeling.sanguo.patch.page.CodePage;
import org.sf.feeling.sanguo.patch.page.CustomGeneralPage;
import org.sf.feeling.sanguo.patch.page.FactionEditPage;
import org.sf.feeling.sanguo.patch.page.FeedbackPage;
import org.sf.feeling.sanguo.patch.page.GeneralEditPage;
import org.sf.feeling.sanguo.patch.page.GeneralUnitPage;
import org.sf.feeling.sanguo.patch.page.HardAdjustPage;
import org.sf.feeling.sanguo.patch.page.ModelPatchPage;
import org.sf.feeling.sanguo.patch.page.ScriptPatchPage;
import org.sf.feeling.sanguo.patch.page.StartPatchPage;
import org.sf.feeling.sanguo.patch.page.TiebaPage;
import org.sf.feeling.sanguo.patch.page.UnitModifyPage;
import org.sf.feeling.sanguo.patch.page.UnitPatchPage;
import org.sf.feeling.sanguo.patch.page.UsagePage;

public class CategoryProviderFactory
{

	private static CategoryProviderFactory instance = new CategoryProviderFactory( );

	protected CategoryProviderFactory( )
	{
	}

	/**
	 * 
	 * @return The unique CategoryProviderFactory instance
	 */
	public static CategoryProviderFactory getInstance( )
	{
		return instance;
	}

	private String[] getCategoryNames( )
	{
		String[] names = new String[]{
				"基本修改",
				"开局初始化修改",
				"脚本修改",
				"难度调整",
				"模型导入",
				"建筑加成修改",
				"兵种修改",
				"宝物修改",
				"势力兵种添加",
				"创建新武将",
				"编辑武将",
				"收买武将",
				"编辑势力",
				"代码查找",
				"备份与还原",
				// "功能或Bug提交",
				"三国全战百度贴吧",
				"修改器反馈中心",
				"修改器使用手册",
				"注意事项"
		};
		List list = new ArrayList( );
		list.addAll( Arrays.asList( names ) );
		return (String[]) list.toArray( new String[0] );
	}

	private Class[] getCategoryClasses( )
	{
		Class[] classes = new Class[]{
				BasicPatchPage.class,
				StartPatchPage.class,
				ScriptPatchPage.class,
				HardAdjustPage.class,
				ModelPatchPage.class,
				BuildingBonusPage.class,
				UnitModifyPage.class,
				BaowuModifyPage.class,
				UnitPatchPage.class,
				CustomGeneralPage.class,
				GeneralEditPage.class,
				GeneralUnitPage.class,
				FactionEditPage.class,
				CodePage.class,
				BakAndRestorePage.class,
				// BugSubmitPage.class,
				TiebaPage.class,
				FeedbackPage.class,
				UsagePage.class,
				AboutPage.class,
		};
		List list = new ArrayList( );
		list.addAll( Arrays.asList( classes ) );
		return (Class[]) list.toArray( new Class[0] );
	}

	/**
	 * Get CategoryProvider according to input element name
	 */
	public ICategoryProvider getCategoryProvider( )
	{
		return new CategoryProvider( getCategoryNames( ), getCategoryClasses( ) );

	}
}
