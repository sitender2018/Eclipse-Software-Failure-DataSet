/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.forms.widgets;

import java.text.BreakIterator;
import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

/**
 * @version 1.0
 * @author
 */
public class TextSegment extends ParagraphSegment {
	private String colorId;

	private String fontId;

	private String text;

	protected boolean underline;

	private boolean wrapAllowed = true;

	protected Vector areaRectangles = new Vector();

	class AreaRectangle {
		Rectangle rect;

		int from, to;

		public AreaRectangle(Rectangle rect, int from, int to) {
			this.rect = rect;
			this.from = from;
			this.to = to;
		}

		public boolean contains(int x, int y) {
			return rect.contains(x, y);
		}

		public String getText() {
			if (from == 0 && to == -1)
				return TextSegment.this.getText();
			if (from > 0 && to == -1)
				return TextSegment.this.getText().substring(from);
			return TextSegment.this.getText().substring(from, to);
		}
	}

	class SelectionRange {
		public int start;

		public int stop;

		public SelectionRange() {
			reset();
		}

		public void reset() {
			start = -1;
			stop = -1;
		}
	}

	public TextSegment(String text, String fontId) {
		this(text, fontId, null);
	}

	public TextSegment(String text, String fontId, String colorId) {
		this.text = cleanup(text);
		this.fontId = fontId;
		this.colorId = colorId;
	}

	private String cleanup(String text) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n' || c == '\r' || c == '\f') {
				if (i > 0)
					buf.append(' ');
			} else
				buf.append(c);
		}
		return buf.toString();
	}

	public void setWordWrapAllowed(boolean value) {
		wrapAllowed = value;
	}

	public boolean isWordWrapAllowed() {
		return wrapAllowed;
	}

	public boolean isSelectable() {
		return false;
	}

	public String getColorId() {
		return colorId;
	}

	public String getText() {
		return text;
	}

	void setText(String text) {
		this.text = cleanup(text);
	}

	void setColorId(String colorId) {
		this.colorId = colorId;
	}

	void setFontId(String fontId) {
		this.fontId = fontId;
	}

	public boolean contains(int x, int y) {
		for (int i = 0; i < areaRectangles.size(); i++) {
			AreaRectangle ar = (AreaRectangle) areaRectangles.get(i);
			if (ar.contains(x, y))
				return true;
		}
		return false;
	}

	public Rectangle getBounds() {
		int x = 0, y = 0;
		int width = 0, height = 0;

		for (int i = 0; i < areaRectangles.size(); i++) {
			AreaRectangle ar = (AreaRectangle) areaRectangles.get(i);
			if (i == 0) {
				x = ar.rect.x;
				y = ar.rect.y;
			} else
				x = Math.min(ar.rect.x, x);
			width = Math.max(ar.rect.width, width);
			height += ar.rect.height;
		}
		return new Rectangle(x, y, width, height);
	}

	public boolean advanceLocator(GC gc, int wHint, Locator locator,
			Hashtable objectTable, boolean computeHeightOnly) {
		Font oldFont = null;
		if (fontId != null) {
			oldFont = gc.getFont();
			Font newFont = (Font) objectTable.get(fontId);
			if (newFont != null)
				gc.setFont(newFont);
		}
		FontMetrics fm = gc.getFontMetrics();
		int lineHeight = fm.getHeight();
		boolean newLine = false;

		if (wHint == SWT.DEFAULT || !wrapAllowed) {
			Point extent = gc.textExtent(text);

			if (wHint != SWT.DEFAULT && locator.x + extent.x > wHint) {
				// new line
				locator.x = isSelectable() ? locator.indent + 1
						: locator.indent;
				locator.y += locator.rowHeight;
				if (computeHeightOnly)
					locator.collectHeights();
				locator.rowHeight = 0;
				locator.leading = 0;
				newLine = true;
			}
			int width = extent.x;
			if (isSelectable())
				width += 2;
			locator.x += width;
			locator.width = width;
			locator.height = extent.y;
			locator.rowHeight = Math.max(locator.rowHeight, extent.y);
			locator.leading = Math.max(locator.leading, fm.getLeading());
			return newLine;
		}

		BreakIterator wb = BreakIterator.getLineInstance();
		wb.setText(text);

		int saved = 0;
		int last = 0;

		int width = 0;

		Point lastExtent = null;

		for (int loc = wb.first(); loc != BreakIterator.DONE; loc = wb.next()) {
			String word = text.substring(saved, loc);
			Point extent = gc.textExtent(word);

			if (locator.x + extent.x > wHint) {
				// overflow
				String savedWord = text.substring(saved, last);
				if (lastExtent == null)
					lastExtent = gc.textExtent(savedWord);
				int lineWidth = locator.x + lastExtent.x;
				if (isSelectable())
					lineWidth += 2;

				saved = last;
				locator.rowHeight = Math.max(locator.rowHeight, lastExtent.y);
				locator.leading = Math.max(locator.leading, fm.getLeading());
				if (computeHeightOnly)
					locator.collectHeights();
				locator.x = isSelectable() ? locator.indent + 1
						: locator.indent;
				locator.y += locator.rowHeight;
				locator.rowHeight = 0;
				locator.leading = 0;
				width = Math.max(width, lineWidth);
				newLine = true;
			}
			last = loc;
			lastExtent = extent;
		}
		String lastString = text.substring(saved, last);
		Point extent = gc.textExtent(lastString);
		int lineWidth = extent.x;
		if (isSelectable())
			lineWidth += 2;
		locator.x += lineWidth;
		locator.width = width;
		locator.height = lineHeight;
		locator.rowHeight = Math.max(locator.rowHeight, extent.y);
		locator.leading = Math.max(locator.leading, fm.getLeading());
		if (oldFont != null) {
			gc.setFont(oldFont);
		}
		return newLine;
	}

	public void paint(GC gc, int width, Locator locator,
			Hashtable resourceTable, boolean selected, SelectionData selData) {
		Font oldFont = null;
		Color oldColor = null;
		Color oldBg = null;

		areaRectangles.clear();

		if (fontId != null) {
			oldFont = gc.getFont();
			Font newFont = (Font) resourceTable.get(fontId);
			if (newFont != null)
				gc.setFont(newFont);
		}
		if (colorId != null) {
			oldColor = gc.getForeground();
			Color newColor = (Color) resourceTable.get(colorId);
			if (newColor != null)
				gc.setForeground(newColor);
		}
		oldBg = gc.getBackground();
		FontMetrics fm = gc.getFontMetrics();
		int lineHeight = fm.getHeight();
		int descent = fm.getDescent();

		if (!wrapAllowed) {
			paintLineWithoutWrapping(gc, width, locator, selected, selData,
					oldColor, fm, lineHeight, descent);
		} else {
			BreakIterator wb = BreakIterator.getLineInstance();
			wb.setText(text);

			int lineStart = 0;
			int lastLoc = 0;
			SelectionRange selRange = new SelectionRange();

			for (int breakLoc = wb.first(); breakLoc != BreakIterator.DONE; breakLoc = wb
					.next()) {
				if (breakLoc == 0)
					continue;
				String word = text.substring(lineStart, breakLoc);
				Point extent = gc.textExtent(word);
				int ewidth = extent.x;
				if (isSelectable())
					ewidth += 2;
				computeSelectionRange(gc, locator, word, ewidth, selData,
						selRange);
				if (locator.x + ewidth > width) {
					// overflow
					String prevLine = text.substring(lineStart, lastLoc);
					Point prevExtent = gc.textExtent(prevLine);
					int ly = locator.getBaseline(lineHeight - fm.getLeading());
					Rectangle br = new Rectangle(locator.x - 1, ly,
							prevExtent.x + 2, lineHeight - descent + 3);

					drawString(gc, prevLine, prevExtent.x, locator, ly,
							selRange, selData);
					int prevWidth = prevExtent.x;
					if (isSelectable())
						prevWidth += 2;

					if (underline) {
						int lineY = ly + lineHeight - descent + 1;
						gc.drawLine(locator.x, lineY, locator.x + prevWidth,
								lineY);
					}
					if (selected) {
						if (colorId != null)
							gc.setForeground(oldColor);
						gc.drawFocus(br.x, br.y, br.width, br.height);
						Color newColor = (Color) resourceTable.get(colorId);
						if (newColor != null)
							gc.setForeground(newColor);
					}
					areaRectangles
							.add(new AreaRectangle(br, lineStart, lastLoc));

					locator.rowHeight = Math.max(locator.rowHeight,
							prevExtent.y);
					locator.resetCaret();
					if (isSelectable())
						locator.x += 1;
					locator.y += locator.rowHeight;
					locator.rowCounter++;
					locator.rowHeight = 0;
					lineStart = lastLoc;
					selRange.reset();
				}
				lastLoc = breakLoc;
			}
			// paint the last line
			String lastLine = text.substring(lineStart, lastLoc);
			int ly = locator.getBaseline(lineHeight - fm.getLeading());
			Point lastExtent = gc.textExtent(lastLine);
			int lastWidth = lastExtent.x;
			if (isSelectable())
				lastWidth += 2;
			Rectangle br = new Rectangle(locator.x - 1, ly, lastExtent.x + 2,
					lineHeight - descent + 3);
			selRange.reset();
			computeSelectionRange(gc, locator, lastLine, lastWidth, selData,
					selRange);
			drawString(gc, lastLine, lastWidth, locator, ly, selRange, selData);
			areaRectangles.add(new AreaRectangle(br, lineStart, lastLoc));
			if (underline) {
				int lineY = ly + lineHeight - descent + 1;
				gc.drawLine(locator.x, lineY, locator.x + lastExtent.x, lineY);
			}
			if (selected) {
				if (colorId != null)
					gc.setForeground(oldColor);
				gc.drawFocus(br.x, br.y, br.width, br.height);
			}
			locator.x += lastWidth;
			locator.rowHeight = Math.max(locator.rowHeight, lastExtent.y);
		}
		if (oldFont != null) {
			gc.setFont(oldFont);
		}
		if (oldColor != null) {
			gc.setForeground(oldColor);
		}
	}

	/**
	 * @param gc
	 * @param width
	 * @param locator
	 * @param selected
	 * @param selData
	 * @param color
	 * @param fm
	 * @param lineHeight
	 * @param descent
	 */
	private void paintLineWithoutWrapping(GC gc, int width, Locator locator,
			boolean selected, SelectionData selData, Color color,
			FontMetrics fm, int lineHeight, int descent) {
		Point extent = gc.textExtent(text);
		int ewidth = extent.x;
		if (isSelectable())
			ewidth += 2;
		if (locator.x + ewidth > width) {
			// new line
			locator.resetCaret();
			if (isSelectable())
				locator.x += 1;
			locator.y += locator.rowHeight;
			locator.rowHeight = 0;
			locator.rowCounter++;
		}
		int ly = locator.getBaseline(fm.getHeight() - fm.getLeading());
		SelectionRange selRange = new SelectionRange();
		computeSelectionRange(gc, locator, text, ewidth, selData, selRange);
		drawString(gc, text, ewidth, locator, ly, selRange, selData);
		if (underline) {
			int lineY = ly + lineHeight - descent + 1;
			gc.drawLine(locator.x, lineY, locator.x + extent.x, lineY);
		}
		Rectangle br = new Rectangle(locator.x - 1, ly, extent.x + 2,
				lineHeight - descent + 3);
		areaRectangles.add(new AreaRectangle(br, 0, -1));
		if (selected) {
			if (colorId != null)
				gc.setForeground(color);
			gc.drawFocus(br.x, br.y, br.width, br.height);
		}
		locator.x += ewidth;
		locator.width = ewidth;
		locator.height = lineHeight;
		locator.rowHeight = Math.max(locator.rowHeight, extent.y);
	}

	private int convertOffsetToStringIndex(GC gc, String s, int x, int swidth,
			int selOffset) {
		int index = s.length();
		while (index > 0 && x + swidth > selOffset) {
			index--;
			String ss = s.substring(0, index);
			swidth = gc.textExtent(ss).x;
		}
		return index;
	}

	private void computeSelectionRange(GC gc, Locator locator, String s,
			int swidth, SelectionData selData, SelectionRange selRange) {
		if (selData != null && selData.isEnclosed()) {
			if (selRange.start == -1 && selData.isFirstSelectionRow(locator)) {
				// compute selection start
				int leftOffset = selData.getLeftOffset(locator);
				if (locator.x + swidth > leftOffset) {
					selRange.start = convertOffsetToStringIndex(gc, s,
							locator.x, swidth, leftOffset);
				}
			}
			if (selRange.stop == -1 && selData.isLastSelectionRow(locator)) {
				// compute selection stop
				int rightOffset = selData.getRightOffset(locator);
				if (locator.x + swidth > rightOffset) {
					selRange.stop = convertOffsetToStringIndex(gc, s,
							locator.x, swidth, rightOffset);
				}
			}
		}
	}

	private void drawString(GC gc, String s, int slength, Locator locator,
			int ly, SelectionRange selRange, SelectionData selData) {
		Color savedBg = gc.getBackground();
		Color savedFg = gc.getForeground();
		int x = locator.x;

		if (selData != null) {
			boolean firstRow = selData.isFirstSelectionRow(locator);
			boolean lastRow = selData.isLastSelectionRow(locator);
			boolean selectedRow = selData.isSelectedRow(locator);

			if ((firstRow && x + slength < selData.getLeftOffset(locator))
					|| lastRow && x > selData.getRightOffset(locator)) {
				gc.drawString(s, x, ly, true);
				return;
			}

			if (selRange.stop >= s.length())
				selRange.stop = -1;
			if (selRange.start == 0)
				selRange.start = -1;
			if (firstRow && selRange.start != -1) {
				String left = s.substring(0, selRange.start);
				gc.drawString(left, x, ly, true);
				x += gc.textExtent(left).x;
			}
			if (selData.isSelectedRow(locator)) {
				int lindex = selRange.start != -1 ? selRange.start : 0;
				int rindex = selRange.stop != -1 ? selRange.stop : s.length();
				String mid = s.substring(lindex, rindex);
				Point extent = gc.textExtent(mid);
				gc.setForeground(selData.fg);
				gc.setBackground(selData.bg);
				gc.fillRectangle(x, ly, extent.x, extent.y);
				gc.drawString(mid, x, ly, true);
				x += extent.x;
				gc.setForeground(savedFg);
				gc.setBackground(savedBg);
				selData.addSegment(mid);
			} else {
				gc.drawString(s, x, ly, true);
			}
			if (selData.isLastSelectionRow(locator) && selRange.stop != -1) {
				String right = s.substring(selRange.stop);
				gc.drawString(right, x, ly, true);
			}
		} else {
			gc.drawString(s, locator.x, ly, true);
		}
	}

	// private boolean insideSelection(SelectionData selData, )

	public void paintFocus(GC gc, Color bg, Color fg, boolean selected) {
		if (areaRectangles == null)
			return;
		for (int i = 0; i < areaRectangles.size(); i++) {
			AreaRectangle areaRectangle = (AreaRectangle) areaRectangles.get(i);
			Rectangle br = areaRectangle.rect;
			if (selected) {
				gc.setBackground(bg);
				gc.setForeground(fg);
				gc.drawFocus(br.x, br.y, br.width, br.height);
			} else {
				gc.setForeground(bg);
				gc.drawRectangle(br.x, br.y, br.width - 1, br.height - 1);
			}
		}
	}
}
