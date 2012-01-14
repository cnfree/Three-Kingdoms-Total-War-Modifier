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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.FileConstants;
import org.sf.feeling.sanguo.patch.util.FileUtil;
import org.sf.feeling.sanguo.patch.util.PinyinComparator;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.util.SortMap;

public class HardAdjustPage extends SimpleTabPage {

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

		FormText noteText = WidgetUtil
				.createFormText(
						container.getBody(),
						"<form><p>说明：<br/>1、游戏简易版无任何驿站加成，不喜欢曹总暴兵的也可选择简易版。<br/>2、 禁止驿站暴兵后，仍然觉得曹总兵太多的，可以尝试修改曹总兵种的<a>造兵回合</a>，比如设置青州兵的造兵回合为3回合。</p></form>",
						true, true);
		TableWrapData data = new TableWrapData(TableWrapData.FILL);
		data.maxWidth = 600;
		noteText.setLayoutData(data);

		noteText.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				Patch.getInstance().select(0);
			}

		});
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

//		{
//			if (FileConstants.scriptFile.exists()) {
//				final Button forceAlliedBtn = WidgetUtil.getToolkit()
//						.createButton(patchClient, "解除雾隐补丁吕布曹操强制同盟(吕布难度增加)",
//								SWT.CHECK);
//				GridData gd = new GridData();
//				gd.horizontalSpan = 3;
//				forceAlliedBtn.setLayoutData(gd);
//				final Button forceAlliedApply = WidgetUtil.getToolkit()
//						.createButton(patchClient, "应用", SWT.PUSH);
//				forceAlliedApply.setEnabled(false);
//				final Button forceAlliedRestore = WidgetUtil.getToolkit()
//						.createButton(patchClient, "还原", SWT.PUSH);
//
//				forceAlliedApply.addSelectionListener(new SelectionAdapter() {
//
//					public void widgetSelected(SelectionEvent e) {
//						forceAlliedApply.setEnabled(false);
//						BakUtil.bakData("解除雾隐补丁吕布曹操强制同盟");
//						String regex = "(console_command)(\\s+)(diplomatic_stance)(\\s+)(ostrogoths)(\\s+)(goths)(\\s+)(allied)";
//						FileUtil.replaceFile(FileConstants.scriptFile, regex,
//								regex, "");
//						forceAlliedApply.setEnabled(true);
//					}
//				});
//
//				forceAlliedRestore.addSelectionListener(new SelectionAdapter() {
//
//					public void widgetSelected(SelectionEvent e) {
//						forceAlliedRestore.setEnabled(false);
//						BakUtil.restoreCurrectVersionBakFile();
//						forceAlliedRestore.setEnabled(true);
//					}
//				});
//
//				forceAlliedBtn.addSelectionListener(new SelectionAdapter() {
//
//					public void widgetSelected(SelectionEvent e) {
//						forceAlliedApply.setEnabled(forceAlliedBtn
//								.getSelection());
//					}
//
//				});
//			}
//		}
		{
			final Button toushiBtn = WidgetUtil.getToolkit().createButton(
					patchClient, "禁用投石车", SWT.CHECK);

			final SortMap factionProperty = FileUtil
					.loadProperties("faction");
			final SortMap factionMap = new SortMap();
			Iterator iter = factionProperty.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				factionMap.put(factionProperty.get(key), key);
			}
			Collections.sort(factionMap.getKeyList(), new Comparator() {
				public int compare(Object o1, Object o2) {
					String name1 = (String) factionMap.get(o1);
					String name2 = (String) factionMap.get(o2);
					return PinyinComparator.compare(name1, name2);
				}
			});

			final CCombo toushiCombo = WidgetUtil.getToolkit().createCCombo(
					patchClient, SWT.READ_ONLY);

			for (int i = 0; i < factionMap.getKeyList().size(); i++) {
				toushiCombo.add((String) factionMap.get(i));
			}

			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			toushiCombo.setLayoutData(gd);
			toushiCombo.setEnabled(false);

			final Button toushiApply = WidgetUtil.getToolkit().createButton(
					patchClient, "应用", SWT.PUSH);
			toushiApply.setEnabled(false);
			final Button toushiRestore = WidgetUtil.getToolkit().createButton(
					patchClient, "还原", SWT.PUSH);

			toushiApply.addSelectionListener(new SelectionAdapter() {
				private void appendToString(StringBuffer buffer, String temp,
						String replacement) {
					Pattern pattern = Pattern
							.compile("engine(\\s+)(heavy_onager)");
					Matcher matcher = pattern.matcher(temp);
					if (matcher.find()) {
						int start = matcher.end();
						buffer.append(temp.substring(0, start));
						String lastString = temp.substring(start);
						Pattern pattern1 = Pattern
								.compile("(ownership)(.+)(\\s)");
						Matcher matcher1 = pattern1.matcher(lastString);
						if (matcher1.find()) {
							int begin = matcher1.start();
							int end = matcher1.end();
							buffer.append(lastString.substring(0, begin));
							if (matcher1.group().trim().indexOf(",") > -1) {
								String[] splits = matcher1.group().trim()
										.split(",");
								if (replacement
										.equals(splits[splits.length - 1]
												.trim())) {
									String str = matcher1.group().substring(
											0,
											matcher1.group().lastIndexOf(
													replacement))
											+ matcher1
													.group()
													.substring(
															matcher1
																	.group()
																	.lastIndexOf(
																			replacement)
																	+ replacement
																			.length());
									if (str.trim().lastIndexOf(",") == str
											.trim().length() - 1)
										str = str.substring(0, str
												.lastIndexOf(","))
												+ str.substring(str
														.lastIndexOf(",") + 1);
									buffer.append(str);
								} else {
									String str = matcher1
											.group()
											.replaceAll(
													replacement
															+ "(\\s*)(,)(\\s{0,1})",
													"");

									buffer.append(str);
								}
							} else
								buffer.append(matcher1.group());
							appendToString(buffer, lastString.substring(end),
									replacement);
						} else {
							appendToString(buffer, lastString, replacement);
						}
					} else
						buffer.append(temp);
				}

				public void widgetSelected(SelectionEvent e) {
					if (toushiCombo.getSelectionIndex() != -1) {
						toushiApply.setEnabled(false);
						BakUtil.bakData("禁用投石车:" + toushiCombo.getText());
						// FileUtil.bakFile(FileConstants.unitFile);
						String code = (String)factionProperty.get(toushiCombo
								.getText());
						try {
							int sizeL = (int) FileConstants.unitFile.length();
							int chars_read = 0;
							BufferedReader in = new BufferedReader(
									new InputStreamReader(new FileInputStream(
											FileConstants.unitFile), "GBK"));
							char[] data = new char[sizeL];
							while (in.ready()) {
								chars_read += in.read(data, chars_read, sizeL
										- chars_read);
							}
							in.close();
							char[] v = new char[chars_read];
							System.arraycopy(data, 0, v, 0, chars_read);
							String temp = new String(v);
							StringBuffer sbr = new StringBuffer();
							appendToString(sbr, temp, code);
							PrintWriter out = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(
											new FileOutputStream(
													FileConstants.unitFile),
											"GBK")), false);
							out.print(sbr);
							out.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						toushiApply.setEnabled(true);
					}
				}
			});

			toushiRestore.addSelectionListener(new RestoreListener());

			toushiBtn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					toushiApply.setEnabled(toushiBtn.getSelection());
					toushiCombo.setEnabled(toushiBtn.getSelection());
				}

			});
		}
//		{
//			Label info = WidgetUtil.getToolkit().createLabel(patchClient, true);
//			info.setText("没有投石想rush曹总绝对是天方夜谭，这也正是原版和雾隐补丁最大的区别所在。");
//			GridData gd = new GridData();
//			gd.heightHint = info.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 10;
//			gd.horizontalSpan = 5;
//			info.setLayoutData(gd);
//		}
		{
			final Button baoBingBtn = WidgetUtil.getToolkit().createButton(
					patchClient, "禁止电脑驿站暴兵", SWT.CHECK);

			final CCombo soldierCombo = WidgetUtil.getToolkit().createCCombo(
					patchClient, SWT.READ_ONLY);

			soldierUnitMap = UnitUtil.getSoldierUnits();
			if (soldierUnitMap != null) {
				for (int i = 0; i < soldierUnitMap.getKeyList().size(); i++) {
					soldierCombo.add(ChangeCode.toLong((String) soldierUnitMap
							.get(i)));
				}
			}

			soldierCombo.add("全部兵种", 0);
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 150;
			soldierCombo.setLayoutData(gd);
			soldierCombo.setEnabled(false);

			final Button baoBingApply = WidgetUtil.getToolkit().createButton(
					patchClient, "应用", SWT.PUSH);
			baoBingApply.setEnabled(false);
			final Button baoBingRestore = WidgetUtil.getToolkit().createButton(
					patchClient, "还原", SWT.PUSH);

			baoBingBtn.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					soldierCombo.setEnabled(baoBingBtn.getSelection());
					baoBingApply.setEnabled(baoBingBtn.getSelection());
				}

			});

			baoBingRestore.addSelectionListener(new RestoreListener());
			baoBingApply.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (soldierCombo.getSelectionIndex() == -1)
						return;
					baoBingApply.setEnabled(false);
					BakUtil.bakData("禁止电脑驿站暴兵:" + soldierCombo.getText());

					String line = null;

					StringWriter writer = new StringWriter();
					PrintWriter printer = new PrintWriter(writer);

					String regex = "^\\s*(recruit)(\\s+)";
					if (soldierCombo.getSelectionIndex() > 0) {
						regex = "^\\s*(recruit)(\\s+)(\""
								+ soldierUnitMap.getKeyList().get(
										soldierCombo.getSelectionIndex() - 1)
								+ "\")";
					}

					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(new FileInputStream(
										FileConstants.buildingsFile), "GBK"));
						boolean startTemple = false;
						while ((line = in.readLine()) != null) {
							if (!startTemple) {
								printer.println(line);
								Pattern pattern = Pattern
										.compile("^\\s*(temple_of)");
								Matcher matcher = pattern.matcher(line);
								if (matcher.find()) {
									startTemple = true;
								}
							} else {

								Pattern pattern = Pattern.compile(regex);
								Matcher matcher = pattern.matcher(line);
								if (matcher.find()) {
									continue;
								} else {
									printer.println(line);
									Pattern pattern1 = Pattern
											.compile("^\\s*(construction)");
									Matcher matcher1 = pattern1.matcher(line);
									if (matcher1.find()) {
										startTemple = false;
									}
								}
							}
						}
						in.close();
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(
										FileConstants.buildingsFile), "GBK")),
								false);
						out.print(writer.getBuffer());
						out.close();
						printer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					baoBingApply.setEnabled(true);
				}
			});
		}
		patchSection.setClient(patchClient);
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(),
				"本页面可以通过修改某些脚本，比如强制结盟，限制兵种等等来改变游戏的难易程度，配置完毕后重启游戏即可生效。");
	}

	public String getDisplayName() {
		return "难度调整";
	}
}
