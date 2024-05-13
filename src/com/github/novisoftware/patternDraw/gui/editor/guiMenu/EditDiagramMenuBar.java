package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.svg.SvgUtil;
import com.github.novisoftware.patternDraw.utils.Debug;

public class EditDiagramMenuBar extends JMenuBar {
	// ファイル選択ダイアログ
	static private JFileChooser pngFileChooser = new JFileChooser(".");
	static private JFileChooser svgFileChooser = new JFileChooser(".");

	final EditDiagramPanel editPanel;
	final EditDiagramWindow editDiagramWindow;

	final JMenu fileMenu;
	final JMenu runMenu;
	final JMenu windowMenu;
	final JMenu helpMenu;
	public EditDiagramMenuBar(final EditDiagramWindow editDiagramWindow, final EditDiagramPanel editDiagramPanel) {
		this.editDiagramWindow = editDiagramWindow;
		this.editPanel = editDiagramPanel;
		this.fileMenu = new JMenu("ファイル");
		// ファイルを開く( nop )
		JMenuItem open = new JMenuItem("開く");
		this.fileMenu.add(open);
		JMenuItem overWrite = new JMenuItem("上書き保存＜注意：まだnop＞");
		this.fileMenu.add(overWrite);
		JMenuItem saveAs = new JMenuItem("名前を付けて保存＜注意：まだnop＞");
		this.fileMenu.add(saveAs);
		this.fileMenu.addSeparator();
		JMenuItem saveAsPNG = new JMenuItem("画像をPNG出力");
		this.fileMenu.add(saveAsPNG);
		saveAsPNG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int selected = pngFileChooser.showSaveDialog(editDiagramWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = pngFileChooser.getSelectedFile();

					try {
						OutputGraphicsWindow outputGraphicsWindow = OutputGraphicsWindow.getInstance();
						outputGraphicsWindow.outputPNG(file);
					} catch (Exception ex) {
						String message = String.format("保存に失敗しました (%s)",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										editDiagramWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					// この動作は要らない?
					JOptionPane.showMessageDialog(editDiagramWindow, "保存しました。");
				}
			}
		});

		JMenuItem saveAsSVG = new JMenuItem("画像をSVG出力");
		saveAsSVG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int selected = svgFileChooser.showSaveDialog(editDiagramWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = svgFileChooser.getSelectedFile();

					try {
						OutputGraphicsWindow outputGraphicsWindow = OutputGraphicsWindow.getInstance();
						outputGraphicsWindow.outputSVG(file);
					} catch (Exception ex) {
						String message = String.format("保存に失敗しました (%s)",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										editDiagramWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					// この動作は要らない?
					JOptionPane.showMessageDialog(editDiagramWindow, "保存しました。");
				}
			}
		});

		this.fileMenu.add(saveAsSVG);
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

				if (editDiagramPanel.networkDataModel.paramDefList.size() > 0) {
					Debug.println("START");
					EditParamWindow editParamWindow =
					new EditParamWindow(
							editDiagramPanel.networkDataModel.paramDefList);


					// パラメーター値が設定されたときのコールバック
					Runnable callback = new Runnable() {
						@Override
						public void run() {
							editDiagramPanel.networkDataModel.resetVariables(editParamWindow.getVariables());
							editDiagramPanel.networkDataModel.evaluate();
							editDiagramPanel.networkDataModel.runProgram();
						}
					};
					editParamWindow.setCallback(callback);
					editParamWindow.setVisible(true);

					Debug.println("END");
				}
				else {
					Debug.println("START");
					editDiagramPanel.networkDataModel.evaluate();
					editDiagramPanel.networkDataModel.runProgram();
					Debug.println("END");
				}
				outputTextWindow.setVisible(true);
				outputGraphicsWindow.setVisible(true);
				editDiagramPanel.repaint();
			}
		});

		this.windowMenu = new JMenu("ウィンドウ");
		JMenuItem dispParaWin = new JMenuItem("パラメーター定義ウィンドウ");
		this.windowMenu.add(dispParaWin);
		dispParaWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editDiagramPanel.paramDefEditWindow != null) {
					// すでにパラメーター編集画面のオブジェクトがある場合は、再度表示するだけ
					editDiagramPanel.paramDefEditWindow.setVisible(true);
					return;
				}
				System.out.println("パラメーター編集画面のオブジェクトを作成");


				ArrayList<ParameterDefine> params = editDiagramPanel.networkDataModel.paramDefList;

				Runnable callback = new Runnable() {
					@Override
					public void run() {
						System.out.println("パラメーター編集画面のオブジェクトを 破棄");
						editDiagramPanel.paramDefEditWindow = null;
					}
				};

				EditParamDefListWindow frame = new EditParamDefListWindow(params, callback);
				editDiagramPanel.paramDefEditWindow = frame;
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
