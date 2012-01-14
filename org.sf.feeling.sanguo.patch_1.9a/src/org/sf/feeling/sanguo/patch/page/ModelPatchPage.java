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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.BattleUtil;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.MapUtil;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class ModelPatchPage extends SimpleTabPage {

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

		FormText noteText = WidgetUtil
				.createFormText(
						container.getBody(),
						"<form><p>注意:<br/>1、该页面功能仅适用于原版和雾隐版！其他补丁兵模已满，无法导入。<br/>2、女武将模型和赵云模型可以应用到兵种士兵模型和将军模型修改。</p></form>",
						true, false);
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
		patchSection.setText("功能列表：");
		WidgetUtil.getToolkit().createCompositeSeparator(patchSection);
		Composite patchClient = WidgetUtil.getToolkit().createComposite(
				patchSection);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		patchClient.setLayout(layout);

		GridData gd = new GridData();
		gd.widthHint = 200;

		final Button xianzhenBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "茄子精华版弩兵陷阵营（会对吕布势力陷阵营和高顺卫队产生影响）", SWT.CHECK);

		final Button xianzhenApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.PUSH);
		xianzhenApply.setEnabled(false);
		final Button xianzhenRestore = WidgetUtil.getToolkit().createButton(
				patchClient, "还原", SWT.PUSH);

		xianzhenApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				xianzhenApply.setEnabled(false);
				String[] xianzhenFactions = UnitUtil
						.getFactionsByUnitType("Xianzhenyin Lvbu");
				String[] gaoShunFactions = UnitUtil
						.getFactionsByUnitType("JiangJun_ZhongYuan Aa_0402-GaoShun");
				BakUtil.bakData("茄子精华版弩兵陷阵营导入");
				if (FileConstants.unitFile.exists()) {
					Unit soldier = UnitParser.getUnit("Xianzhenyin Lvbu");
					if (soldier != null) {
						soldier.getSoldier()[0] = "Xianzhenyin_Lvbu_Crossbow";
						soldier.getSoldier()[1] = "30";
						soldier.setUnitClass("missile");
						List attributes = soldier.getAttributes();
						if (!attributes.contains("frighten_foot"))
							attributes.add("frighten_foot");
						if (!attributes.contains("can_withdraw"))
							attributes.add("can_withdraw");
						if (!attributes.contains("can_swim"))
							attributes.add("can_swim");
						soldier.setPrimary(new String[] { "12", "0",
								"crossbow", "190", "30", "missile", "archery",
								"piercing", "none", "25", "1" });
						soldier.setPrimaryAttr(Arrays.asList(new String[] {
								"ap", "launching" }));
						soldier.setSecond(new String[] { "14", "5", "no", "0",
								"0", "melee", "blade", "piercing", "spear",
								"25", "1" });
						soldier.setSecondAttr(Arrays
								.asList(new String[] { "spear" }));
						soldier.setPrimaryArmour(new String[] { "10", "11",
								"6", "metal" });
						soldier.setFormation(Arrays.asList(new String[] { "1",
								"2", "2", "3", "4", "square", "shield_wall" }));
						UnitParser.saveSoldier(soldier);
					}

					soldier = UnitParser
							.getUnit("JiangJun_ZhongYuan Aa_0402-GaoShun");
					if (soldier != null) {
						soldier.getSoldier()[0] = "Xianzhenyin_Lvbu_Horse";
						soldier.setCategory("cavalry");
						soldier.setUnitClass("missile");
						List attributes = soldier.getAttributes();
						attributes.remove("can_sap");
						if (!attributes.contains("frighten_foot"))
							attributes.add("frighten_foot");
						if (!attributes.contains("can_withdraw"))
							attributes.add("can_withdraw");
						if (!attributes.contains("can_swim"))
							attributes.add("can_swim");
						if (!attributes.contains("power_charge"))
							attributes.add("power_charge");
						if (!attributes.contains("very_hardy"))
							attributes.add("very_hardy");
						soldier.setPrimary(new String[] { "12", "0",
								"crossbow", "190", "30", "missile", "archery",
								"piercing", "none", "25", "1" });
						soldier.setPrimaryAttr(Arrays.asList(new String[] {
								"ap", "launching" }));
						soldier.setSecond(new String[] { "12", "10", "no", "0",
								"0", "melee", "blade", "piercing", "spear",
								"25", "1" });
						soldier.setSecondAttr(Arrays.asList(new String[] {
								"ap", "launching" }));
						soldier.setPrimaryArmour(new String[] { "15", "12",
								"4", "metal" });
						soldier
								.setFormation(Arrays.asList(new String[] {
										"1.5", "4", "3", "6", "4", "square",
										"wedge" }));
						soldier.setMount("sanguo horse jjhm9");
						soldier.setMountEffect(Arrays.asList(new String[] {
								"elephant -4", "camel -4", "horse +2" }));
						soldier.setChargeDist(70);
						UnitParser.saveSoldier(soldier);
					}
				}
				if (FileConstants.battleFile.exists()
						&& !(FileUtil.containMatchString(
								FileConstants.battleFile,
								"(Xianzhenyin_Lvbu_Horse)") || FileUtil
								.containMatchString(FileConstants.battleFile,
										"(Xianzhenyin_Lvbu_Crossbow)"))) {
					updateAvailableModels();
					FileUtil
							.appendToFile(
									FileConstants.battleFile,
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/xianzhenying_battle.txt"));
					try {
						for (int i = 0; i < xianzhenFactions.length; i++) {
							UnitUtil.modifyBattleFile(xianzhenFactions[i],
									"Xianzhenyin_Lvbu_Crossbow");
						}
						for (int i = 0; i < gaoShunFactions.length; i++) {
							UnitUtil.modifyBattleFile(gaoShunFactions[i],
									"Xianzhenyin_Lvbu_Horse");
							UnitUtil.modifyBattleFile(gaoShunFactions[i],
									(String) UnitUtil.getMountTypeToModelMap()
											.get("sanguo horse jjhm9"));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				File modelRootFile = new File(Patch.GAME_ROOT
						+ "\\patch\\alexander\\data\\models_unit\\sanguo");
				if (modelRootFile.exists() && modelRootFile.isFile()) {
					modelRootFile.delete();
				}
				if (!modelRootFile.exists()) {
					modelRootFile.mkdirs();
				}
				if (modelRootFile.exists() && modelRootFile.isDirectory()) {
					FileUtil
							.writeToBinarayFile(
									new File(modelRootFile.getAbsolutePath()
											+ "\\Xianzhenyin_Lvbu.cas"),
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu.cas"));
					FileUtil
							.writeToBinarayFile(
									new File(modelRootFile.getAbsolutePath()
											+ "\\Xianzhenyin_Lvbu1.cas"),
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu1.cas"));
					FileUtil
							.writeToBinarayFile(
									new File(modelRootFile.getAbsolutePath()
											+ "\\Xianzhenyin_Lvbu2.cas"),
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu2.cas"));
					FileUtil
							.writeToBinarayFile(
									new File(modelRootFile.getAbsolutePath()
											+ "\\Xianzhenyin_Lvbu3.cas"),
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/Xianzhenyin_Lvbu3.cas"));
				}
				MapUtil.initMap();
				xianzhenApply.setEnabled(true);
			}
		});

		xianzhenRestore.addSelectionListener(new RestoreListener());

		xianzhenBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				xianzhenApply.setEnabled(xianzhenBtn.getSelection());
			}

		});

		final Button womenModelBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "雾影补丁女武将模型（包含貂蝉，甄姬，孙尚香，吕玲琦，张星彩）", SWT.CHECK);

		final Button womenModelApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.PUSH);
		womenModelApply.setEnabled(false);
		final Button womenModelRestore = WidgetUtil.getToolkit().createButton(
				patchClient, "还原", SWT.PUSH);
		womenModelRestore.addSelectionListener(new RestoreListener());

		womenModelBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				womenModelApply.setEnabled(womenModelBtn.getSelection());
			}

		});

		womenModelApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				womenModelApply.setEnabled(false);
				BakUtil.bakData("雾影补丁女武将模型导入");

				if (FileConstants.battleFile.exists()
						&& !(FileUtil.containMatchString(
								FileConstants.battleFile, "(DiaoChan_Patch)")
								|| FileUtil.containMatchString(
										FileConstants.battleFile,
										"(SunShangXiang_Patch)")
								|| FileUtil.containMatchString(
										FileConstants.battleFile,
										"(ZhenJi_Patch)")
								|| FileUtil.containMatchString(
										FileConstants.battleFile,
										"(YueLi_Patch)")
								|| FileUtil.containMatchString(
										FileConstants.battleFile,
										"(XingCai_Patch)")
								|| FileUtil.containMatchString(
										FileConstants.battleFile,
										"(YueLi_Horse_Patch)") || FileUtil
								.containMatchString(FileConstants.battleFile,
										"(XingCai_Horse_Patch)"))) {
					updateAvailableModels();
					if (!FileUtil.containMatchString(FileConstants.battleFile,
							"type\\s+DiaoChan_test")) {
						FileUtil
								.appendToFile(
										FileConstants.battleFile,
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/women_battle.txt"));
					}

					FileUtil
							.appendToFile(
									FileConstants.battleFile,
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/women_horse_battle.txt"));

					File modelRootFile = new File(Patch.GAME_ROOT
							+ "\\patch\\alexander\\data\\models_unit\\sanguo");
					if (modelRootFile.exists() && modelRootFile.isFile()) {
						modelRootFile.delete();
					}
					if (!modelRootFile.exists()) {
						modelRootFile.mkdirs();
					}
					if (modelRootFile.exists() && modelRootFile.isDirectory()) {
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\DiaoChan.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/DiaoChan.cas"));
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\SunShangXiang.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/SunShangXiang.cas"));
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\XingCai.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/XingCai.cas"));
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\YueLi.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/YueLi.cas"));
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\ZhenJi.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/ZhenJi.cas"));
					}

					File textureRootFile = new File(
							Patch.GAME_ROOT
									+ "\\patch\\alexander\\data\\models_unit\\sanguo\\textures");
					if (textureRootFile.exists() && textureRootFile.isFile()) {
						textureRootFile.delete();
					}
					if (!textureRootFile.exists()) {
						textureRootFile.mkdirs();
					}
					if (textureRootFile.exists()
							&& textureRootFile.isDirectory()) {
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\DiaoChan.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/DiaoChan.tga.dds"));
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\SunShangXiang.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/SunShangXiang.tga.dds"));
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\XingCai.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/XingCai.tga.dds"));
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\YueLi.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/YueLi.tga.dds"));
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\ZhenJi.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/ZhenJi.tga.dds"));
					}
					MapUtil.initMap();
				}
				womenModelApply.setEnabled(true);
			}
		});

		final Button zhaoyunModelBtn = WidgetUtil.getToolkit().createButton(
				patchClient, "雾影补丁赵云模型（模型会应用到赵云将军卫队）", SWT.CHECK);

		final Button zhaoyunModelApply = WidgetUtil.getToolkit().createButton(
				patchClient, "应用", SWT.PUSH);
		zhaoyunModelApply.setEnabled(false);
		final Button zhaoyunModelRestore = WidgetUtil.getToolkit()
				.createButton(patchClient, "还原", SWT.PUSH);
		zhaoyunModelRestore.addSelectionListener(new RestoreListener());

		zhaoyunModelBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				zhaoyunModelApply.setEnabled(zhaoyunModelBtn.getSelection());
			}

		});

		zhaoyunModelApply.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				zhaoyunModelApply.setEnabled(false);
				BakUtil.bakData("雾影补丁赵云模型导入");

				if (FileConstants.battleFile.exists()
						&& !FileUtil.containMatchString(
								FileConstants.battleFile,
								"type\\s+ZhaoYun_general1")) {
					updateAvailableModels();

					List factions = BattleUtil
							.getModelFactions("ZhaoYun_general");

					List modelTypeList = new ArrayList();
					modelTypeList.add("ZhaoYun_general");
					BattleUtil.removeModelTypes(modelTypeList);

					FileUtil
							.appendToFile(
									FileConstants.battleFile,
									ModelPatchPage.class
											.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/zhaoyun_battle.txt"));

					try {
						for (int i = 0; i < factions.size(); i++) {
							UnitUtil.modifyBattleFile((String) factions.get(i),
									"ZhaoYun_general");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					File modelRootFile = new File(Patch.GAME_ROOT
							+ "\\patch\\alexander\\data\\models_unit\\sanguo");
					if (modelRootFile.exists() && modelRootFile.isFile()) {
						modelRootFile.delete();
					}
					if (!modelRootFile.exists()) {
						modelRootFile.mkdirs();
					}
					if (modelRootFile.exists() && modelRootFile.isDirectory()) {
						FileUtil
								.writeToBinarayFile(
										new File(modelRootFile
												.getAbsolutePath()
												+ "\\ZhaoYun.cas"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/ZhaoYun.cas"));
					}

					File textureRootFile = new File(
							Patch.GAME_ROOT
									+ "\\patch\\alexander\\data\\models_unit\\sanguo\\textures");
					if (textureRootFile.exists() && textureRootFile.isFile()) {
						textureRootFile.delete();
					}
					if (!textureRootFile.exists()) {
						textureRootFile.mkdirs();
					}
					if (textureRootFile.exists()
							&& textureRootFile.isDirectory()) {
						FileUtil
								.writeToBinarayFile(
										new File(textureRootFile
												.getAbsolutePath()
												+ "\\ZhaoYun.tga.dds"),
										ModelPatchPage.class
												.getResourceAsStream("/org/sf/feeling/sanguo/patch/models/ZhaoYun.tga.dds"));
					}

					Unit unit = UnitParser
							.getUnit("JiangJun_HeBei Aa_0610-ZhaoYun");
					if (unit != null) {
						List officers = unit.getOfficers();
						officers.clear();
						officers.add("ZhaoYun_general");
						UnitParser.saveSoldier(unit);
					}

					MapUtil.initMap();
				}
				zhaoyunModelApply.setEnabled(true);
			}
		});
		patchSection.setClient(patchClient);
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面用于导入部分模型到游戏，配置完毕后重启游戏即可生效。");
	}

	public String getDisplayName() {
		return "模型导入";
	}

	private void updateAvailableModels() {
		final SortMap unitModelMap = new SortMap();
		SortMap generalModelMap = new SortMap();
		List soldierList = new ArrayList();
		if (FileConstants.unitFile.exists()) {
			try {
				String line = null;

				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(FileConstants.unitFile), "GBK"));
				boolean startSoldier = false;
				List models = new ArrayList();
				SortMap modelMap = generalModelMap;
				while ((line = in.readLine()) != null) {
					if (line.split(";").length == 0) {
						continue;
					}
					if (!startSoldier) {
						Pattern pattern = Pattern.compile("^\\s*(type)(\\s+)");
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							modelMap = generalModelMap;
							startSoldier = true;
						}
					} else {
						Pattern pattern = Pattern
								.compile("^\\s*(soldier)(\\s+)");
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							soldierList.add(line.split(";")[0].replaceAll(
									"soldier", "").trim().split(",")[0].trim());
							continue;
						}

						pattern = Pattern.compile("^\\s*(officer)(\\s+)");
						matcher = pattern.matcher(line);
						if (matcher.find()) {
							models.add(line.split(";")[0].replaceAll("officer",
									"").trim());
							continue;
						}

						pattern = Pattern.compile("^\\s*(ownership)(\\s+)");
						matcher = pattern.matcher(line);
						if (matcher.find()) {
							for (int i = 0; i < models.size(); i++) {
								Object solider = models.get(i);
								if (modelMap.containsKey(solider)) {
									int value = (Integer
											.parseInt((String) modelMap
													.get(solider)) + 1);
									modelMap.put(solider, "" + value);
								} else
									modelMap.put(solider, "1");
							}
							models.clear();
							startSoldier = false;
							continue;
						}
						pattern = Pattern.compile("^\\s*(attributes)(\\s+)");
						matcher = pattern.matcher(line);
						if (matcher.find()) {
							if (line.indexOf("general_unit") == -1) {
								modelMap = unitModelMap;
							}
							continue;
						}
					}
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < unitModelMap.getKeyList().size(); i++) {
				String key = (String) unitModelMap.getKeyList().get(i);
				if (soldierList.contains(key)
						|| generalModelMap.getKeyList().contains(key)) {
					unitModelMap.remove(key);
					i--;
				}
			}

			unitModelMap.remove("ZhongNian_general");
			if (unitModelMap.size() > 0) {
				try {
					StringWriter writer = new StringWriter();
					PrintWriter printer = new PrintWriter(writer);
					String line = null;
					BufferedReader in = new BufferedReader(
							new InputStreamReader(new FileInputStream(
									FileConstants.unitFile), "GBK"));
					while ((line = in.readLine()) != null) {
						if (line.split(";").length == 0) {
							printer.println(line);
							continue;
						}
						Pattern pattern = Pattern
								.compile("^\\s*(officer)(\\s+)");
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							String officer = line.split(";")[0].replaceAll(
									"officer", "").trim();
							if (unitModelMap.getKeyList().contains(officer)) {
								printer.println(line.replaceAll(officer,
										"ZhongNian_general"));
								continue;
							}
						}
						printer.println(line);
					}
					in.close();
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									FileConstants.unitFile), "GBK")), false);
					out.print(writer.getBuffer());
					out.close();
					printer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				BattleUtil.removeModelTypes(unitModelMap.getKeyList());
			}
		}
	}
}
