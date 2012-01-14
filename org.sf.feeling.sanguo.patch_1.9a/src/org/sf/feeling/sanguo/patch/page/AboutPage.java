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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class AboutPage extends SimpleTabPage {

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
	}

	private void createPatchArea() {

		FormText noteText = WidgetUtil
				.createFormText(
						container.getBody(),
						"<form><p><span color=\"note\">注意：</span></p>"
								+ "<li>任何修改或还原操作都需要重启游戏，开局初始化修改需重新开局。</li>"
								+ "<li>修改前请确认已进行过原始档备份。</li>"
								+ "<li>不建议手动修改游戏文件后使用本修改器。</li>"
								+ "<li>不建议启动游戏后使用本修改器修改，这样会导致修改器响应缓慢。</li>"
								+ "<li>修改页面的还原功能只能还原游戏到最后一次修改备份。建议使用<a>高级备份还原</a>功能进行还原。</li>"
								+ "<li>还原游戏原始备份档，可恢复游戏数据到安装原始状态，但可能需要重新开档。</li>"
								+ "<li>本修改器仅适用于三国全面战争1.9a版，其他版本请下载三国全面战争修改器1.7a3.0版</li>"
								+ "<li>修改后第一次重启游戏有可能会加载数据失败，请尝试再次重启游戏。</li>"
								+ "<li><span color=\"note\">每次修改都应当进入游戏进行测试。</span>修改器并不保证修改一定能够成功，只能保证失败后还原一定能恢复（前提是会使用高级还原功能）到游戏正常状态（可能需重新开档）。</li>"
								+ "<li><span color=\"warning\">本修改器绿色（即免安装，任何安装版均为盗版）无毒，但某些操作比较敏感，可能会被360等杀毒软件拦截。</span></li>"
								+ "</form>", true, true);
		noteText.setColor("note", Display.getDefault().getSystemColor(
				SWT.COLOR_RED));
		noteText.setColor("warning", Display.getDefault().getSystemColor(
				SWT.COLOR_BLUE));
		noteText.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				Patch.getInstance().select(
						Patch.getInstance().getPageCount() - 3);
			}

		});
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面介绍使用修改器的注意事项，第一次使用修改器之前请仔细阅读！");
	}

	public String getDisplayName() {
		return "注意事项";
	}

}
