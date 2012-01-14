/*******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.sanguo.patch.widget;

import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Special Canvas class used to display the image.
 * 
 */
public class ImageCanvas extends Canvas {

	private Image sourceImage;

	private Image screenImage;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 */
	public ImageCanvas(final Composite parent) {
		this(parent, 0);
		initAccessible();
	}

	/**
	 * The constructor.
	 * 
	 * @param parent
	 * @param style
	 * 
	 */
	public ImageCanvas(final Composite parent, int style) {
		super(parent, style);

		addPaintListener(new PaintListener() {

			public void paintControl(final PaintEvent event) {
				paint(event.gc);
			}
		});
		initAccessible();
	}

	void initAccessible() {
		getAccessible().addAccessibleControlListener(
				new AccessibleControlAdapter() {

					public void getChildAtPoint(AccessibleControlEvent e) {
						Point testPoint = toControl(e.x, e.y);
						if (getBounds().contains(testPoint)) {
							e.childID = ACC.CHILDID_SELF;
						}
					}

					public void getLocation(AccessibleControlEvent e) {
						Rectangle location = getBounds();
						Point pt = toDisplay(location.x, location.y);
						e.x = pt.x;
						e.y = pt.y;
						e.width = location.width;
						e.height = location.height;
					}

					public void getChildCount(AccessibleControlEvent e) {
						e.detail = 0;
					}

					public void getRole(AccessibleControlEvent e) {
						e.detail = ACC.ROLE_LABEL;
					}

					public void getState(AccessibleControlEvent e) {
						e.detail = ACC.STATE_NORMAL;
					}

					public void getValue(AccessibleControlEvent e) {
						e.result = "Preview Image"; //$NON-NLS-1$
					}

				});

		AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {

			public void getHelp(AccessibleEvent e) {
				e.result = "Preview Image"; //$NON-NLS-1$
			}

			public void getName(AccessibleEvent e) {
				e.result = "Preview Image"; //$NON-NLS-1$
			}
		};
		getAccessible().addAccessibleListener(accessibleAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		if (sourceImage != null && !sourceImage.isDisposed()) {
			sourceImage.dispose();
		}

		if (screenImage != null && !screenImage.isDisposed()) {
			screenImage.dispose();
		}
	}

	private void paint(GC gc) {
		if (sourceImage != null && !sourceImage.isDisposed()) {
			if (((float) getSize().x / getSize().y) >= ((float) sourceImage
					.getImageData().width / sourceImage.getImageData().height)) {
				
				int width = (int) (getSize().y * ((float) sourceImage
						.getImageData().width / sourceImage.getImageData().height));
				if (width > sourceImage.getImageData().width)
					width = sourceImage.getImageData().width;
				int height = (int) (width * (sourceImage.getImageData().height / (float) sourceImage
						.getImageData().width));
				gc.drawImage(sourceImage, 0, 0,
						sourceImage.getImageData().width, sourceImage
								.getImageData().height,
						(getSize().x - width) / 2 - 1,
						(getSize().y - height) / 2 - 1, width, height);
			} else {
				int height = (int) (getSize().x * ((float) sourceImage
						.getImageData().height / sourceImage.getImageData().width));
				if (height > sourceImage.getImageData().height)
					height = sourceImage.getImageData().height;
				int width = (int) (height * (sourceImage.getImageData().width / (float) sourceImage
						.getImageData().height));
				gc.drawImage(sourceImage, 0, 0,
						sourceImage.getImageData().width, sourceImage
								.getImageData().height,
						(getSize().x - width) / 2 - 1,
						(getSize().y - height) / 2 - 1, width, height);
			}
		}
	}

	/**
	 * Returns the Source image.
	 * 
	 * @return sourceImage.
	 */
	public Image getSourceImage() {
		return sourceImage;
	}

	/**
	 * Reset the image data and update the image.
	 * 
	 * @param data
	 */
	public void setImageData(ImageData data) {
		if (sourceImage != null) {
			sourceImage.dispose();
			sourceImage = null;
		}
		if (data != null)
			sourceImage = new Image(getDisplay(), data);
		redraw();
	}

	/**
	 * Clear the canvas
	 */
	public void clear() {
		if (sourceImage != null) {
			sourceImage.dispose();
			sourceImage = null;
		}
		GC clearGC = new GC(this);
		paint(clearGC);
		clearGC.dispose();
	}

}