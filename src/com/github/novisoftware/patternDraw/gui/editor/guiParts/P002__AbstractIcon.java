package com.github.novisoftware.patternDraw.gui.editor.guiParts;

public abstract class P002__AbstractIcon implements P001_IconGuiInterface {

	private boolean isOnMouse = false;

	@Override
	public void setOnMouse(boolean b) {
		this.isOnMouse = b;
	}

	protected boolean isOnMouse() {
		return isOnMouse;
	}
}
