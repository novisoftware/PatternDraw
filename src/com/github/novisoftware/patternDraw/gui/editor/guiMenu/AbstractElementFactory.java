package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.util.HashMap;

import javax.swing.JMenu;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;

public abstract class AbstractElementFactory {
	public abstract void addMenuList(
			JMenu menu,
			HashMap<String, JMenu> workHm2,
			final EditDiagramPanel editPanel,
			final MListener mListener,
			int x,
			int y
			);
}
