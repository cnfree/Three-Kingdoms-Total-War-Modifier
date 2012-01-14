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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.dialog.BaowuModify;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class BaowuModifyPage extends SimpleTabPage {

	public void buildUI(Composite parent) {
		super.buildUI(parent);
		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 15;
		layout.verticalSpacing = 20;
		container.getBody().setLayout(layout);

		createTitle();
		createPatchArea();
		
		FormText noteText = WidgetUtil.createFormText(container.getBody(),
				"注意：游戏中宝物描述所显示的卫队人数、经验等特效并不会随宝物数据的修改而改变。");
		TableWrapData data = new TableWrapData(TableWrapData.FILL);
		data.maxWidth = 600;
		noteText.setLayoutData(data);
	}

	private void createPatchArea() {
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit().createSection(
				container.getBody(), Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL);
		patchSection.setLayoutData(td);
		patchSection.setText("宝物数据修改（请勿乱选，不会填请选择空白）");
		WidgetUtil.getToolkit().createCompositeSeparator(patchSection);

		Composite clientContainer = (Composite) new BaowuModify()
				.createModifyControl(patchSection);

		patchSection.setClient(clientContainer);

	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面用于修改宝物属性，配置完毕后重启游戏即可生效。");
	}

	public String getDisplayName() {
		return "宝物修改";
	}

}
