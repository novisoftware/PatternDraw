package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.utils.Debug;

public class EditDiagramMenuBar extends JMenuBar {
	final EditDiagramPanel editPanel;

	final JMenu fileMenu;
	final JMenu runMenu;
	final JMenu windowMenu;
	final JMenu helpMenu;
	public EditDiagramMenuBar(final EditDiagramPanel editPanel) {
		this.editPanel = editPanel;
		this.fileMenu = new JMenu("ファイル");
		// ファイルを開く( nop )
		JMenuItem open = new JMenuItem("開く");
		this.fileMenu.add(open);
		JMenuItem overWrite = new JMenuItem("上書き保存＜注意：まだnop＞");
		this.fileMenu.add(overWrite);
		JMenuItem saveAs = new JMenuItem("名前を付けて保存＜注意：まだnop＞");
		this.fileMenu.add(saveAs);
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
		JMenuItem run = new JMenuItem("実行");
		this.runMenu.add(run);
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OutputTextWindow outputTextWindow = OutputTextWindow.getInstance();
				OutputGraphicsWindow outputGraphicsWindow = OutputGraphicsWindow.getInstance();

				if (editPanel.networkDataModel.paramDefList.size() > 0) {
					Debug.println("START");
					EditParamWindow editParamWindow =
					new EditParamWindow(
							editPanel.networkDataModel.paramDefList);


					// パラメーター値が設定されたときのコールバック
					Runnable callback = new Runnable() {
						@Override
						public void run() {
							editPanel.networkDataModel.resetVariables(editParamWindow.getVariables());
							editPanel.networkDataModel.evaluate();
							editPanel.networkDataModel.runProgram();
						}
					};
					editParamWindow.setCallback(callback);
					editParamWindow.setVisible(true);

					Debug.println("END");
				}
				else {
					Debug.println("START");
					editPanel.networkDataModel.evaluate();
					editPanel.networkDataModel.runProgram();
					Debug.println("END");
				}
				outputTextWindow.setVisible(true);
				outputGraphicsWindow.setVisible(true);
				editPanel.repaint();
			}
		});

		this.windowMenu = new JMenu("ウィンドウ");
		JMenuItem dispParaWin = new JMenuItem("パラメーター定義ウィンドウ");
		this.windowMenu.add(dispParaWin);
		dispParaWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editPanel.paramDefEditWindow != null) {
					// すでにパラメーター編集画面のオブジェクトがある場合は、再度表示するだけ
					editPanel.paramDefEditWindow.setVisible(true);
					return;
				}
				System.out.println("パラメーター編集画面のオブジェクトを作成");


				ArrayList<ParameterDefine> params = editPanel.networkDataModel.paramDefList;

				Runnable callback = new Runnable() {
					@Override
					public void run() {
						System.out.println("パラメーター編集画面のオブジェクトを 破棄");
						editPanel.paramDefEditWindow = null;
					}
				};

				EditParamDefListWindow frame = new EditParamDefListWindow(params, callback);
				editPanel.paramDefEditWindow = frame;
				frame.setVisible(true);
				// frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});

		this.helpMenu = new JMenu("ヘルプ");
		JMenuItem about = new JMenuItem("このソフトウェアについて");
		this.helpMenu.add(about);

		this.add(this.fileMenu);
		this.add(this.runMenu);
		this.add(this.windowMenu);
		this.add(this.helpMenu);
	}
}
