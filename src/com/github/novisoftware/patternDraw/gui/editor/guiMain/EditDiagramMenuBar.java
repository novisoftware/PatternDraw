package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class EditDiagramMenuBar extends JMenuBar {
	final JMenu fileMenu;
	final JMenu runMenu;
	final JMenu windowMenu;
	final JMenu helpMenu;
	public EditDiagramMenuBar() {
		this.fileMenu = new JMenu("ファイル");
		this.add(this.fileMenu);
		// ファイルを開く( nop )
		JMenuItem open = new JMenuItem("開く");
		this.fileMenu.add(open);
		JMenuItem overWrite = new JMenuItem("上書き保存");
		this.fileMenu.add(overWrite);
		this.fileMenu.addSeparator();
		JMenuItem exit = new JMenuItem("終了");
		this.fileMenu.add(exit);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
				// 未保存のファイルがあれば、ダイアログを出す
				System.exit(0);
			}
		});

		this.runMenu = new JMenu("実行");
		this.add(this.runMenu);
		this.windowMenu = new JMenu("ウィンドウ");
		this.add(this.windowMenu);
		this.helpMenu = new JMenu("ヘルプ");
		this.add(this.helpMenu);
		JMenuItem about = new JMenuItem("このソフトウェアについて");
		this.helpMenu.add(about);
	}
}
