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

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.sf.feeling.sanguo.patch.provider.ICategoryProvider;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.Tab;
import org.sf.feeling.sanguo.patch.widget.TabbedPropertyList;
import org.sf.feeling.sanguo.patch.widget.TabbedPropertyTitle;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class BasePage {

	private TabbedPropertyList categoryList;

	private Composite container;

	private ScrolledComposite sComposite;

	private Composite infoPane;

	private ICategoryProvider categoryProvider;

	public void buildUI(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		container.setLayout(layout);

		categoryList = new TabbedPropertyList(container);
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.verticalSpan = 3;
		categoryList.setLayoutData(gd);
		categoryList.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				processListSelected();
			}
		});
		setCategoryProvider(categoryProvider);
		title = new TabbedPropertyTitle(container, FormWidgetFactory
				.getInstance());
		title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sComposite = new ScrolledComposite(container, SWT.H_SCROLL
				| SWT.V_SCROLL);
		sComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		sComposite.setExpandHorizontal(true);
		sComposite.setExpandVertical(true);
		sComposite.addControlListener(new ControlAdapter() {

			public void controlResized(ControlEvent e) {
				computeSize();
			}
		});
		infoPane = new Composite(sComposite, SWT.NONE);
		sComposite.setContent(infoPane);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		infoPane.setLayout(layout);

		bar = new ToolBar(container, SWT.FLAT | SWT.HORIZONTAL);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 5;
		layout.marginHeight = 0;
		bar.setLayout(layout);

		ToolItem infoItem = new ToolItem(bar, SWT.NONE);
		infoLabel = WidgetUtil.getToolkit().createLabel(bar, "", SWT.LEFT);
		infoLabel.setText("");
		infoItem.setControl(infoLabel);

		ToolItem fileItem = new ToolItem(bar, SWT.NONE);
		fileLabel = WidgetUtil.getToolkit().createCLabel(bar, "");
		fileItem.setControl(fileLabel);
		fileLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ToolItem barItem = new ToolItem(bar, SWT.NONE);
		progress = new ProgressBar(bar, SWT.INDETERMINATE);
		barItem.setControl(progress);

		gd = new GridData();
		gd.widthHint = 30;
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 12;
		progress.setLayoutData(gd);

		FormWidgetFactory.getInstance().paintFormStyle(bar);
		FormWidgetFactory.getInstance().adapt(bar);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		bar.setLayoutData(gd);

		hideInfo();
	}

	public void showInfo(String info, String file) {
		GridData gd = (GridData) bar.getLayoutData();
		if (gd.heightHint == 0) {
			gd.heightHint = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			bar.setLayoutData(gd);
			container.layout();
		}
		if (info != null) {
			String oldText = infoLabel.getText();
			infoLabel.setText(info);
			if (!info.equals(oldText))
				bar.layout();
		}
		if (file != null) {
			fileLabel.setText(file);
			if (file.trim().length() > 0)
				progress.setVisible(true);
			else
				progress.setVisible(false);
		} else
			progress.setVisible(false);
	}

	public void hideInfo() {
		showInfo("", "");
		GridData gd = (GridData) bar.getLayoutData();
		gd.heightHint = 0;
		bar.setLayoutData(gd);
		container.layout();
	}

	private void computeSize() {
		// sComposite.setMinSize( infoPane.computeSize( SWT.DEFAULT, SWT.DEFAULT
		// ) );
		infoPane.layout();
	}

	public int getSelectionIndex() {
		return categoryList.getSelectionIndex();
	}

	private void processListSelected() {
		if (categoryProvider == null) {
			return;
		}

		int index = categoryList.getSelectionIndex();
		if (index == -1) {
			return;
		}

		TabPage page = getCategoryPane(categoryList.getSelectionIndex());
		title.setTitle(page.getDisplayName(), null);
		showPage(page);
	}

	public void setSelection(int index) {
		categoryList.setSelection(index);
		TabPage page = getCategoryPane(categoryList.getSelectionIndex());
		title.setTitle(page.getDisplayName(), null);
		showPage(page);
	}

	private TabPage currentPage;

	private TabbedPropertyTitle title;

	private void showPage(TabPage page) {
		if (page != currentPage) {
			if (currentPage != null) {
				((GridData) currentPage.getControl().getLayoutData()).exclude = true;
				currentPage.getControl().setVisible(false);
				currentPage.deActivate();
			}
			((GridData) page.getControl().getLayoutData()).exclude = false;
			page.getControl().setVisible(true);
			currentPage = page;
			computeSize();
			currentPage.activate();
			page.refresh();
		}
	}

	public void setCategoryProvider(ICategoryProvider categoryProvider) {
		this.categoryProvider = categoryProvider;
		if (categoryProvider == null) {
			return;
		}
		if (categoryList == null)
			return;
		ICategoryPage[] pages = categoryProvider.getCategories();
		if (pages.length != 0) {
			Tab[] categoryLabels = new Tab[pages.length];
			for (int i = 0; i < pages.length; i++) {
				categoryLabels[i] = new Tab();
				categoryLabels[i].setText(pages[i].getDisplayLabel());
			}
			categoryList.setElements(categoryLabels);
			if (categoryList.getTabList().length > 0) {
				categoryList.setSelection(0);
			}
		}
	}

	public void refresh() {
		processListSelected();
	}

	HashMap pageMap;

	private ToolBar bar;

	private Label infoLabel;

	private CLabel fileLabel;

	private ProgressBar progress;

	private TabPage getCategoryPane(int index) {
		if (pageMap == null) {
			pageMap = new HashMap(categoryProvider.getCategories().length);
		}
		String key = Integer.toString(index);
		TabPage page = (TabPage) pageMap.get(key);
		if (page == null) {
			page = categoryProvider.getCategories()[index].createPage();
			page.buildUI(infoPane);
			computeSize();
		}
		page.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		pageMap.put(key, page);
		return page;
	}
}