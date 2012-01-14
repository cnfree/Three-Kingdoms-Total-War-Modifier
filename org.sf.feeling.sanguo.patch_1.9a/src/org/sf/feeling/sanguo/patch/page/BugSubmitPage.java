package org.sf.feeling.sanguo.patch.page;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.zip.ZipFileInfo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.gameeden.mail.SmtpMailSender;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.util.BakUtil;
import org.sf.feeling.sanguo.patch.widget.WidgetUtil;

public class BugSubmitPage extends SimpleTabPage {

	List attachmentList = new ArrayList();

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
						"<form><p>提示：因为提交Bug时，修改器会自动收集错误数据，所以若修改器修改而导致游戏无法正常进行时，请通过本页面提交Bug后再进行还原，否则修改器将无法获取错误数据！</p></form>",
						true, false);
		TableWrapData data = new TableWrapData(TableWrapData.FILL);
		data.maxWidth = 600;
		noteText.setLayoutData(data);
	}

	private void createPatchArea() {
		Composite parent = WidgetUtil.getToolkit().createComposite(
				container.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		parent.setLayout(layout);
		parent.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		WidgetUtil.createLabel(parent, "邮件名称：");
		final Text nameText = WidgetUtil.createText(parent,
				"三国全面战争1.9a修改器1.0版Bug提交");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameText.setLayoutData(gd);

		WidgetUtil.createLabel(parent, "邮件附件：");
		final Text attachText = WidgetUtil.getToolkit().createText(parent, "");
		attachText.setEditable(false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		attachText.setLayoutData(gd);
		Button button = WidgetUtil.getToolkit().createButton(parent, "浏览...",
				SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getDefault()
						.getActiveShell(), SWT.NONE);
				dialog.setText("邮件附件");
				String path = dialog.open();
				if (!(path == null || path.length() == 0 || !new File(path)
						.exists())) {
					File file = new File(path);
					attachmentList.add(file);
					String attachs = attachText.getText() + "; "
							+ file.getName();
					if (attachs.charAt(0) == ';')
						attachs = attachs.substring(2);
					attachText.setText(attachs);
				}
			}
		});

		Label contentLabel = WidgetUtil.createLabel(parent, "邮件内容：");
		gd = new GridData();
		gd.verticalAlignment = SWT.BEGINNING;
		contentLabel.setLayoutData(gd);

		final Text contentText = WidgetUtil
				.getToolkit()
				.createText(
						parent,
						"1、游戏的（补丁）版本：\n\n2、出错的功能:\n\n3、重现Bug的详细步骤：\n1.\n2.\n3.\n\n4、描述Bug出现后的现象：\n\n\n",
						SWT.MULTI | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		contentText.setLayoutData(gd);

		final Button submitButton = WidgetUtil.getToolkit().createButton(
				parent, "邮件发送", SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = SWT.CENTER;
		submitButton.setLayoutData(gd);

		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				submitButton.setEnabled(false);
				Patch.getInstance().showInfo("", "正在发送邮件...");
				final String name = nameText.getText();

				String detailText = "\n\n===================================\n";
				if (BakUtil.getBakCurrentVersion() != null) {
					detailText += "CurrentVersion        "
							+ BakUtil.getBakCurrentVersion() + "\n";
				}
				File bakFolder = new File(BakUtil.bakFolderPath);
				if (bakFolder.exists()) {
					File[] children = bakFolder.listFiles();
					if (children != null) {
						for (int i = 0; i < children.length; i++) {
							File file = children[i];
							if (!file.exists()
									|| BakUtil.defalutBakFile.getName().equals(
											file.getName()) || !file.isFile())
								continue;
							try {
								ZipFileInfo zipFile = new ZipFileInfo(file,
										"GBK");
								String comment = zipFile.getComment();
								zipFile.close();
								if (comment != null) {
									detailText += (file.getName().split("\\.")[0]
											+ "        " + comment + "\n");
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
				final String content = contentText.getText() + detailText;
				new Thread() {
					public void run() {
						SmtpMailSender sender = SmtpMailSender
								.createESmtpMailSender("smtp.126.com",
										"cnfree2000@126.com", "cnfree2000", "cnfree");
						if (BakUtil.getCurrentVersionBakFile() != null)
							attachmentList.add(BakUtil
									.getCurrentVersionBakFile());
						File bugBak = BakUtil.bakBugData();
						if(bugBak!=null && bugBak.exists()){
							attachmentList.add(bugBak);
						}
						final boolean successful = sender.sendTextMail(
								"cnfree@126.com", name, content,
								(File[]) attachmentList.toArray(new File[0]));
						
						bugBak.delete();
						
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (Patch.getInstance().getShell().isDisposed())
									return;
								if (!successful)
									MessageDialog.openError(Patch.getInstance()
											.getShell(), "错误",
											"邮件发送失败，请检查您的网络配置！");
								else {
									MessageDialog.openInformation(Patch
											.getInstance().getShell(), "提示",
											"邮件发送成功，谢谢您的大力支持！");
								}
								attachmentList.clear();
								attachText.setText("");
								Patch.getInstance().hideInfo();
								submitButton.setEnabled(true);
							}
						});
					}
				}.start();
			}
		});
	}

	private void createTitle() {
		WidgetUtil.createFormText(container.getBody(), "本页面可以通过邮件提交新功能或者Bug。");
	}

	public String getDisplayName() {
		return "功能或Bug提交";
	}
}