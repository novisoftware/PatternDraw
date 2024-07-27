package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.util.HashMap;

import javax.swing.JMenu;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;

public class SeparatorAdder extends AbstractElementFactory {
	final String menuName;
	
	public SeparatorAdder() {
		this.menuName = null;
	}
	
	public SeparatorAdder(String internal) {
		this.menuName = internal;
	}
	
	@Override
	public void addMenuList(JMenu menu, HashMap<String, JMenu> workHm2, EditDiagramPanel editPanel,
			MListener mListener, int x, int y) {
		
		System.out.println("-----A  menuName = " + menuName );
		
		if (this.menuName == null) {
			menu.addSeparator();
		}
		else {
			workHm2.get(menuName).addSeparator();
		}
	}
}
