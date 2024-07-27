package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.util.HashMap;

import javax.swing.JMenu;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.utils.Debug;

/**
 * メニューに区切り線を追加する
 */
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
		
		if (this.menuName == null) {
			menu.addSeparator();
		}
		else {
			JMenu subMenu = workHm2.get(menuName);
			if (subMenu != null) {
				subMenu.addSeparator();
			}
			else {
				// 開発時のエラーなので、即終了させる。
				System.out.println("内部定義エラー。　メニュー名 " + menuName + " に対応するメニューがありません。");
				System.exit(1);
			}
		}
	}
}
