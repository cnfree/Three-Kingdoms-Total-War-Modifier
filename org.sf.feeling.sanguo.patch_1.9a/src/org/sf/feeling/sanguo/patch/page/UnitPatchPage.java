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

import java.io.IOException;
import java.util.List;

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
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class UnitPatchPage extends SimpleTabPage {

	private SortMap soldierUnitMap;
	private SortMap factionMap;

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

		final Button toushiBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "势力兵种添加", SWT.CHECK);

		final CCombo factionCombo = WidgetUtil.getToolkit().createCCombo(
				patchClient, SWT.READ_ONLY);
		factionMap = UnitUtil.getFactionMap();
		for (int i = 0; i < factionMap.getKeyList().size(); i++) {
			factionCombo.add((String) factionMap.get(i));
		}
		GridData gd = new GridData();
		gd.widthHint = 150;
		factionCombo.setLayoutData(gd);
		factionCombo.setEnabled(false);

		final CCombo soldierCombo = WidgetUtil.getToolkit().createCCombo(
				patchClient, SWT.READ_ONLY);
		soldierUnitMap = UnitUtil.getAllSoldierUnits();
		if (soldierUnitMap != null) {
			for (int i = 0; i < soldierUnitMap.getKeyList().size(); i++) {
				soldierCombo.add(ChangeCode.toLong((String) soldierUnitMap
						.get(i)));
			}
		}
		gd = new GridData();
		gd.widthHint = 150;
		soldierCombo.setLayoutData(gd);
		soldierCombo.setEnabled(false);

		final Button soldierApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.PUSH);
		soldierApply.setEnabled(false);
		final Button soldierRestore = WidgetUtil.getToolkit().createButton(
				patchClient, "还原", SWT.PUSH);

		soldierApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (factionCombo.getSelectionIndex() != -1
						&& soldierCombo.getSelectionIndex() != -1) {
					soldierApply.setEnabled(false);
					BakUtil.bakData("势力兵种添加：" + factionCombo.getText() + "<--"
							+ soldierCombo.getText());
					String factionCode = (String) factionMap.getKeyList().get(
							factionCombo.getSelectionIndex());
					String soldierType = (String) soldierUnitMap.getKeyList()
							.get(soldierCombo.getSelectionIndex());
					Unit soldier = UnitParser.getUnit(soldierType);
					try {
						if (!soldier.getFactions().contains(factionCode)) {
							UnitUtil.modifyUnitFile(factionCode, soldier
									.getType());
							UnitUtil.modifyBattleFile(factionCode, soldier
									.getSoldier()[0]);
							List officers = soldier.getOfficers();
							if (officers != null && officers.size() > 0) {
								for (int i = 0; i < officers.size(); i++) {
									UnitUtil.modifyBattleFile(factionCode,
											(String)officers.get(i));
								}
							}

							String mount = soldier.getMount();
							if (mount != null)
								UnitUtil.modifyBattleFile(factionCode,
										(String) UnitUtil
												.getMountTypeToModelMap().get(
														mount));
							String[] buildings = null;
							if ("missile".equals(soldier.getUnitClass())
									|| "siege".equals(soldier.getCategory())) {
								buildings = new String[] { "practice_field",
										"archery_range", "catapult_range",
										"siege_engineer" };
							} else if ("infantry".equals(soldier.getCategory())
									|| "handler".equals(soldier.getCategory())) {
								buildings = new String[] { "muster_field",
										"militia_barracks", "city_barracks",
										"army_barracks", "royal_barracks" };
							} else if ("cavalry".equals(soldier.getCategory())) {
								buildings = new String[] { "stables",
										"cavalry_barracks", "hippodrome",
										"circus_maximus" };
							} else if ("ship".equals(soldier.getCategory())) {
								buildings = new String[] { "port",
										"shipwright", "dockyard" };
							} else if ("non_combatant".equals(soldier
									.getCategory())) {
								buildings = new String[] { "governors_house",
										"governors_villa", "governors_palace",
										"proconsuls_palace", "imperial_palace" };
							}
							if (buildings != null) {
								UnitUtil.addUnitToBuildings(soldierType,
										factionCode, buildings);
							}
							UnitUtil.saveBigBingPai(factionCode,
									(String) soldier.getFactions().get(0),
									soldier.getDictionary());
							UnitUtil.saveSmallBingPai(factionCode,
									(String) soldier.getFactions().get(0),
									soldier.getDictionary());
							MapUtil.initMap();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					soldierApply.setEnabled(true);
				}
			}
		});

		soldierRestore.addSelectionListener(new RestoreListener());

		toushiBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				soldierApply.setEnabled(toushiBtn.getSelection());
				factionCombo.setEnabled(toushiBtn.getSelection());
				soldierCombo.setEnabled(toushiBtn.getSelection());
			}

		});
		patchSection.setClient(patchClient);
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面用于添加兵种到指定势力，配置完毕后重启游戏即可生效。");
	}

	public String getDisplayName() {
		return "势力兵种添加";
	}
}
