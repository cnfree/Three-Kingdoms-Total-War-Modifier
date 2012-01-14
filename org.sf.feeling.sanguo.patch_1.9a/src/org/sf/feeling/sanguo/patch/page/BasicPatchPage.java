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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.shell.ShellFolder;
import org.sf.feeling.swt.win32.extension.shell.ShellLink;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class BasicPatchPage extends SimpleTabPage {

	private static final String re1 = "(stat_cost)"; // Variable Name 1
	private static final String re2 = "(\\s+)"; // White Space 1
	private static final String re3 = "(\\d+)"; // Integer Number 1
	private static final String re4 = "(\\s*)"; // White Space 2
	private static final String re5 = "(,)"; // Any Single Character 1
	private static final String re6 = "(construction)"; // Variable Name 1
	private SortMap soldierUnitMap;

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
		TableWrapData td;
		Section patchSection = WidgetUtil.getToolkit().createSection(
				container.getBody(), Section.EXPANDED);
		td = new TableWrapData(TableWrapData.FILL);
		patchSection.setLayoutData(td);
		patchSection.setText("功能列表：");
		WidgetUtil.getToolkit().createCompositeSeparator(patchSection);
		Composite patchClient = WidgetUtil.getToolkit().createComposite(
				patchSection);
		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		patchClient.setLayout(layout);

		final Button zaoBingBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "造兵回合修改", SWT.CHECK);

		final CCombo soldierCombo = WidgetUtil.getToolkit().createCCombo(
				patchClient, SWT.READ_ONLY);

		soldierUnitMap = UnitUtil.getAllSoldierUnits();
		if (soldierUnitMap != null) {
			for (int i = 0; i < soldierUnitMap.getKeyList().size(); i++) {
				soldierCombo.add(ChangeCode.toLong((String) soldierUnitMap
						.get(i)));
			}
		}
		soldierCombo.add("全部兵种", 0);
		soldierCombo.select(0);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		soldierCombo.setLayoutData(gd);
		soldierCombo.setEnabled(false);

		final CCombo zaoBingCombo = WidgetUtil.getToolkit().createCCombo(
				patchClient, SWT.READ_ONLY);
		zaoBingCombo.setEnabled(false);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		zaoBingCombo.setLayoutData(gd);

		String[] items = new String[100];
		for (int i = 0; i < 100; i++)
			items[i] = i + "回合";

		zaoBingCombo.setItems(items);
		zaoBingCombo.select(0);

		final Button zaoBingApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.PUSH);
		zaoBingApply.setEnabled(false);
		final Button zaoBingRestore = WidgetUtil.getToolkit().createButton(
				patchClient, "还原", SWT.PUSH);
		zaoBingApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (soldierCombo.getSelectionIndex() == -1
						|| zaoBingCombo.getSelectionIndex() == -1)
					return;
				zaoBingApply.setEnabled(false);
				if (FileConstants.unitFile.exists()) {
					if (soldierCombo.getSelectionIndex() == 0) {
						BakUtil.bakData("造兵回合修改：全部兵种" + zaoBingCombo.getText());
						FileUtil.replaceFile(FileConstants.unitFile, re1 + re2
								+ re3 + re4 + re5, re3,
								"" + zaoBingCombo.getSelectionIndex());
					} else {
						BakUtil.bakData("造兵回合修改：" + soldierCombo.getText()
								+ zaoBingCombo.getText());
						String soldierType = (String) soldierUnitMap
								.getKeyList().get(
										soldierCombo.getSelectionIndex() - 1);
						int huihe = zaoBingCombo.getSelectionIndex();
						Unit soldier = UnitParser.getUnit(soldierType);
						String[] cost = soldier.getCost();
						cost[0] = "" + huihe;
						soldier.setCost(cost);
						UnitParser.saveSoldier(soldier);
					}
				}
				zaoBingApply.setEnabled(true);
			}
		});
		zaoBingRestore.addSelectionListener(new RestoreListener());

		zaoBingBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				soldierCombo.setEnabled(zaoBingBtn.getSelection());
				zaoBingCombo.setEnabled(zaoBingBtn.getSelection());
				zaoBingApply.setEnabled(zaoBingBtn.getSelection());
			}

		});

		final Button zaoChengBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "建筑修建回合修改", SWT.CHECK);

		final CCombo zaoChengCombo = WidgetUtil.getToolkit().createCCombo(
				patchClient, true);
		items = new String[100];
		for (int i = 0; i < 100; i++)
			items[i] = (i + 1) + "回合";
		zaoChengCombo.setItems(items);
		zaoChengCombo.select(0);
		zaoChengCombo.setEnabled(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		zaoChengCombo.setLayoutData(gd);

		final Button zaoChengApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.FLAT);
		zaoChengApply.setEnabled(false);
		zaoChengApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (FileConstants.buildingsFile.exists()) {
					zaoChengApply.setEnabled(false);
					BakUtil.bakData("建筑修建回合修改：" + zaoChengCombo.getText());
					FileUtil.replaceFile(FileConstants.buildingsFile, re6 + re2
							+ re3, re3, ""
							+ (zaoChengCombo.getSelectionIndex() + 1));
					zaoChengApply.setEnabled(true);
				}
			}

		});
		final Button zaoChengRestore = WidgetUtil.getToolkit().createButton(
				patchClient, "还原", SWT.FLAT);

		zaoChengRestore.addSelectionListener(new RestoreListener());

		zaoChengBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				zaoChengCombo.setEnabled(zaoChengBtn.getSelection());
				zaoChengApply.setEnabled(zaoChengBtn.getSelection());
			}

		});
		{
			final Button windowBtn = WidgetUtil.getToolkit().createButton(
					patchClient, "创建游戏窗口化启动快捷方式到桌面（Alt+Tab键切换窗口）", SWT.CHECK);
			gd = new GridData();
			gd.horizontalSpan = 3;
			windowBtn.setLayoutData(gd);
			final Button windowApply = WidgetUtil.getToolkit().createButton(
					patchClient, "应用", SWT.PUSH);
			windowApply.setEnabled(false);
			windowApply.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					String filePath = ShellFolder.DESKTOP
							.getAbsolutePath(container.handle)
							+ File.separator
							+ "三国全面战争" + ".lnk";

					ShellLink.createShortCut(
							Patch.GAME_APPLICATION.getAbsolutePath(), filePath);
					ShellLink.setShortCutArguments(filePath,
							"-ne -nm -show_err");
					ShellLink.setShortCutDescription(filePath,
							"Created the link file by cnfree2000");
					ShellLink.setShortCutWorkingDirectory(filePath,
							Patch.GAME_ROOT.getAbsolutePath());

					try {
						FileUtil.writeToBinarayFile(
								new File(Patch.GAME_ROOT.getAbsolutePath()
										+ "\\三国全面战争.lnk"), new FileInputStream(
										filePath));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
			windowBtn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					windowApply.setEnabled(windowBtn.getSelection());
				}

			});
		}
		patchSection.setClient(patchClient);
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面用于设置游戏的基本数据，配置完毕后重启游戏即可生效。");
	}

	public String getDisplayName() {
		return "基本修改";
	}
}
