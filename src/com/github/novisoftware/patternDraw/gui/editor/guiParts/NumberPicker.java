package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.Debug;

/**
 * 単連結グラフの先頭の場合は、
 * 数字をドラッグすると単連結グラフが動くようにしたい
 *
 */
public class NumberPicker extends P002__AbstractIcon {
	final P001_IconGuiInterface parent;
	final EditDiagramPanel editPanel;
	final int x;
	final int y;
	final Rectangle rect;
	final public int number;

	NumberPicker(P001_IconGuiInterface parent, EditDiagramPanel editPanel, int number, int x, int y, Rectangle rect) {
		this.parent = this;
		this.editPanel = editPanel;
		this.number = number;
		this.x = x;
		this.y = y;
		this.rect = rect;
	}

	boolean isHit(int mx, int my) {
		boolean ret =
				(x <= mx && mx <= x + rect.width)
				&&
				(y - rect.height <= my && my <= y);
		// Debug.println("isHit (NumberPicker " + number + ") is called. mx,my = " + mx + "," + my + "  x,y = " + x + "," + y);

		return ret;
	}

	ArrayList<P021____AbstractGraphNodeElement> eList = null;

	public void dragged(int moveX, int moveY) {
		if (this.eList == null) {
			eList = this.editPanel.networkDataModel.graphGroup.get(this.number);
		}

		for (P021____AbstractGraphNodeElement e : this.eList) {
			e.x += moveX;
			e.y += moveY;
		}
	}

	/**
	 * TODO
	 *
	 * 使わないので、削る等の見直しをした方がいい。
	 */
	public int getCenterX() {
		return x + rect.width / 2;
	}

	public int getCenterY() {
		return y + rect.height / 2;
	}
}