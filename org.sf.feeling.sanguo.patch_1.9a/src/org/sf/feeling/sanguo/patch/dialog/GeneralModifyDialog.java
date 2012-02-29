package org.sf.feeling.sanguo.patch.dialog;

import org.eclipse.jface.dialogs.BaseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.sanguo.patch.widget.FormWidgetFactory;
import org.sf.feeling.swt.win32.extension.util.SortMap;
import org.sf.feeling.swt.win32.extension.widgets.ShellWrapper;

public class GeneralModifyDialog extends BaseDialog implements Listener {

	private Control control;
	private ScrolledComposite composite;
	private boolean isMemory;
	public GeneralModifyDialog(String title, boolean isMemory) {
		super(title);
		this.isMemory = isMemory;
	}

	protected Control createDialogArea(Composite parent) {
		composite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setExpandHorizontal(true);
		composite.setExpandVertical(true);

		modify = new GeneralModify(isMemory);
		modify.addListener(this);

		control = modify.createModifyControl(composite);
		control.setLayoutData(new GridData(GridData.FILL_BOTH));

		composite.setContent(control);

		if (generalSkills != null)
			modify.setSkills(general, generalSkills);
		
		composite.addControlListener(new ControlAdapter() {

			public void controlResized(ControlEvent e) {
				computeSize();
			}
		});
		computeSize();

		return composite;
	}

	private void computeSize() {
		composite.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((Composite) control).layout();
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		FormWidgetFactory.getInstance().paintFormStyle((Composite) parent);
		FormWidgetFactory.getInstance().adapt((Composite) parent);
		return control;
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

	// private boolean isVerified = false;

	public void handleEvent(Event event) {
		// if (event.type == SWT.Verify) {
		// if (getOkButton() != null && !getOkButton().isDisposed()) {
		// if (event.doit)
		// getOkButton().setEnabled(true);
		// else
		// getOkButton().setEnabled(false);
		// } else {
		// isVerified = event.doit;
		// }
		// }
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite = (Composite) super.createButtonBar(parent);
		// getOkButton().setEnabled(isVerified);
		return composite;
	}

	private Object result = null;
	private GeneralModify modify;

	public void okPressed() {
		modify.saveGeneralSkills(generalSkills);
		result = generalSkills;
		super.okPressed();
	}

	public Object getResult() {
		return result;
	}

	private SortMap generalSkills = null;
	private String general = null;

	public void setGeneralSkills(String general, SortMap generalSkills) {
		this.generalSkills = generalSkills;
		this.general = general;
	}
}
