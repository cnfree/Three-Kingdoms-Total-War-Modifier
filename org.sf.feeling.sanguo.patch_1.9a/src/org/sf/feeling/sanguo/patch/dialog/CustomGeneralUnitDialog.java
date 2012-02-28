package org.sf.feeling.sanguo.patch.dialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.util.ChangeCode;
import org.sf.feeling.sanguo.patch.util.CustomGeneralUnit;
import org.sf.feeling.sanguo.patch.util.UnitParser;
import org.sf.feeling.sanguo.patch.util.UnitUtil;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.sanguo.patch.widget.ImageCanvas;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class CustomGeneralUnitDialog extends BaseDialog {

	ModifyListener nameListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			checkEnableStatus();
		}

	};
	private Text nameText;
	private CCombo soldierImageCombo;
	private ImageCanvas imageCanvas;
	private SortMap generalUnitMap;
	private SortMap factionMap;
	private Text idText;
	private Button soldierButton;
	private Button soldierImageButton;
	private Text generalDesc;
	private CCombo factionCombo;

	private Unit soldier = null;
	private ImageData soldierImage = null;
	private ScrolledForm container;
	private Composite patchClient;

	public CustomGeneralUnitDialog(String title) {
		super(title);
	}

	protected Control createDialogArea(Composite parent) {
		container = WidgetUtil.getToolkit().createScrolledForm(parent);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 20;
		layout.marginHeight = 15;
		container.getBody().setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		patchClient = WidgetUtil.getToolkit().createComposite(
				container.getBody());
		layout = new GridLayout();
		layout.numColumns = 4;
		patchClient.setLayout(layout);
		patchClient.setLayoutData(new GridData(GridData.FILL_BOTH));

		WidgetUtil.getToolkit().createLabel(patchClient, "1.将军卫队名称（中文）：");

		nameText = WidgetUtil.getToolkit().createText(patchClient, "");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);

		nameText.addModifyListener(nameListener);

		imageCanvas = WidgetUtil.getToolkit().createImageCanvas(patchClient,
				SWT.NONE);
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.verticalSpan = 7;
		gd.widthHint = 160;
		imageCanvas.setLayoutData(gd);

		WidgetUtil.getToolkit().createLabel(patchClient, "2.将军卫队名称（拼音）：");
		idText = WidgetUtil.getToolkit().createText(patchClient, "");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		idText.setLayoutData(gd);
		idText.addModifyListener(nameListener);

		WidgetUtil.getToolkit().createLabel(patchClient, "3.选择卫队势力：");
		factionCombo = WidgetUtil.getToolkit().createCCombo(patchClient,
				SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		factionCombo.setLayoutData(gd);
		factionCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkEnableStatus();
			}
		});

		WidgetUtil.getToolkit().createLabel(patchClient, "4.创建将军卫队：");
		soldierButton = WidgetUtil.getToolkit().createButton(patchClient, "设置",
				SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 180;
		gd.horizontalSpan = 2;
		soldierButton.setLayoutData(gd);
		soldierButton.setEnabled(false);
		soldierButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				UnitModifyDialog dialog = new UnitModifyDialog("设置新武将卫队");
				if (soldier != null)
					dialog.setSoldier(soldier);
				if (dialog.open() == Window.OK) {
					soldier = (Unit) dialog.getResult();
					checkEnableStatus();
				}
			}

		});

		WidgetUtil.getToolkit().createLabel(patchClient, "5.设置将军卫队图片(可选)：");
		soldierImageCombo = WidgetUtil.getToolkit().createCCombo(patchClient,
				SWT.READ_ONLY);
		soldierImageCombo.setText("宽：160像素，高：210像素");
		soldierImageCombo.addFocusListener(new FocusAdapter() {

			public void focusLost(FocusEvent e) {
				if (soldierImageCombo.getText().length() == 0) {
					soldierImageCombo.setText("宽：160像素，高：210像素");
				}
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 160;
		soldierImageCombo.setLayoutData(gd);
		soldierImageCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (soldierImageCombo.getSelectionIndex() != -1) {
					String soldierType = (String) generalUnitMap.getKeyList()
							.get(soldierImageCombo.getSelectionIndex());
					if (soldierType != null) {
						Unit soldier = UnitParser.getUnit(soldierType);
						String dictionary = soldier.getType( );
						String[] factions = UnitUtil
								.getFactionsFromSoldierType(soldierType);
						for (int i = 0; i < factions.length; i++) {
							File file = new File(Patch.GAME_ROOT
									+ "\\alexander\\data\\ui\\unit_info\\"
									+ factions[i] + "\\" + dictionary
									+ "_info.tga");
							if (file.exists() && file.length() > 0) {
								try {
									ImageData image = TgaLoader
											.loadImage(new BufferedInputStream(
													new FileInputStream(file)));
									imageCanvas.setImageData(image);
									soldierImage = image;
									break;
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
				checkEnableStatus();
			}
		});

		soldierImageButton = WidgetUtil.getToolkit().createButton(patchClient,
				SWT.PUSH, true);
		soldierImageButton.setText("自定义");
		soldierImageButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getDefault()
						.getActiveShell(), SWT.NONE);
				dialog
						.setFilterExtensions(new String[] { "*.jpg;*.jpeg;*.png;*.bmp;*.gif;*.tga" }); // Windows
				dialog.setFilterNames(new String[] { "图片" });
				String path = dialog.open();
				if (path != null && new File(path).exists()) {
					soldierImage = null;
					soldierImageCombo.clearSelection();
					soldierImageCombo.setText(path);
					File imageFile = new File(soldierImageCombo.getText()
							.trim());
					if (imageFile.exists() && imageFile.isFile()) {
						try {
							if (imageFile.getName().toLowerCase().endsWith(
									".tga")) {
								ImageData imageData = TgaLoader.loadImage(
										new FileInputStream(imageFile), true,
										true).scaledTo(160, 210);
								imageCanvas.setImageData(imageData);
								soldierImage = imageData;
							} else {
								ImageLoader loader = new ImageLoader();
								ImageData imageData = loader.load(imageFile
										.getAbsolutePath())[0].scaledTo(160,
										210);
								imageCanvas.setImageData(imageData);
								soldierImage = imageData;
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				checkEnableStatus();
			}
		});

		Label label = WidgetUtil.getToolkit().createLabel(patchClient,
				"6.设置卫队描述(可选)：");
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.grabExcessVerticalSpace = false;
		label.setLayoutData(gd);

		generalDesc = WidgetUtil.getToolkit().createText(patchClient, "",
				SWT.MULTI | SWT.FLAT | SWT.WRAP | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.widthHint = 300;
		gd.horizontalSpan = 3;
		generalDesc.setLayoutData(gd);

		generalUnitMap = UnitUtil.getAvailableGeneralUnits();
		for (int i = 0; i < generalUnitMap.getKeyList().size(); i++) {
			String unitName = ChangeCode.toLong((String) generalUnitMap.get(i));
			soldierImageCombo.add(unitName);
		}

		factionMap = UnitUtil.getFactionMap();
		for (int i = 0; i < factionMap.getKeyList().size(); i++) {
			factionCombo.add((String) factionMap.get(i));

		}

		checkEnableStatus();

		container.setContent(patchClient);

		container.addControlListener(new ControlAdapter() {

			public void controlResized(ControlEvent e) {
				computeSize();
			}
		});
		computeSize();

		return container;
	}

	protected void computeSize() {
		container.setMinSize(patchClient.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((Composite) patchClient).layout();
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		FormWidgetFactory.getInstance().paintFormStyle((Composite) parent);
		FormWidgetFactory.getInstance().adapt((Composite) parent);
		return control;
	}

	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		checkEnableStatus();
		return control;
	}

	protected void okPressed() {

		BakUtil.bakData("创建将军卫队：" + nameText.getText());
		CustomGeneralUnit customGeneral = new CustomGeneralUnit();
		customGeneral.setDisplayName(nameText.getText().trim());
		customGeneral.setName(idText.getText().trim());
		customGeneral.setFaction((String) factionMap.getKeyList().get(
				factionCombo.getSelectionIndex()));
		if (soldierImage != null)
			customGeneral.setGeneralSoldierImage(soldierImage);
		else {
			String dictionary = soldier.getDictionary();
			String[] factions = UnitUtil
					.getFactionsFromSoldierType(soldier.getType( ));
			for (int i = 0; i < factions.length; i++) {
				File file = new File(Patch.GAME_ROOT
						+ "\\alexander\\data\\ui\\unit_info\\" + factions[i] + "\\"
						+ dictionary + "_info.tga");
				if (file.exists() && file.length() > 0) {
					try {
						ImageData image = TgaLoader
								.loadImage(new BufferedInputStream(
										new FileInputStream(file)));
						customGeneral.setGeneralSoldierImage(image);
						break;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		customGeneral.setGeneralSoldier(soldier);
		customGeneral.setDescription(generalDesc.getText().trim());
		customGeneral.createCustomGeneral();
		result = nameText.getText();
		super.okPressed();
	}

	private Object result;

	public Object getResult() {
		return result;
	}

	public int open() {
		if (getShell() == null) {
			create();
			getShell().setImages(((Shell) getShell().getParent()).getImages());
			ShellWrapper wrapper = new ShellWrapper(getShell());
			wrapper.installTheme();
		}
		if (initDialog()) {
			return super.open();
		}
		return Dialog.CANCEL;
	}

	private void checkEnableStatus() {
		String soldierType = "Custom " + idText.getText().trim();
		if (nameText.getText().trim().length() > 0
				&& idText.getText().trim().length() > 0
				&& UnitUtil.getUnitDictionary(soldierType) == null) {
			soldierButton.setEnabled(true);
			soldierImageCombo.setEnabled(true);
			soldierButton.setEnabled(true);
			soldierImageButton.setEnabled(true);
			generalDesc.setEnabled(true);
			factionCombo.setEnabled(true);
			if (soldier != null && factionCombo.getSelectionIndex() > -1) {
				if (getOkButton() != null)
					getOkButton().setEnabled(true);
			} else {
				if (getOkButton() != null)
					getOkButton().setEnabled(false);
			}
		} else {
			soldierButton.setEnabled(false);
			soldierImageCombo.setEnabled(false);
			soldierButton.setEnabled(false);
			soldierImageButton.setEnabled(false);
			generalDesc.setEnabled(false);
			factionCombo.setEnabled(false);
			if (getOkButton() != null)
				getOkButton().setEnabled(false);
		}
	}
}
