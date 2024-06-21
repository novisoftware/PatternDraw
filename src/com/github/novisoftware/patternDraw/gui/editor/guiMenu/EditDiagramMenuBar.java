package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.github.novisoftware.patternDraw.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.utils.Debug;

public class EditDiagramMenuBar extends JMenuBar {
	// ファイル選択ダイアログ
	static private JFileChooser saveAsFileChooser = null;

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
		JMenuItem overWrite = new JMenuItem("上書き保存");
		JMenuItem saveAs = new JMenuItem("名前を付けて保存");

		this.fileMenu.add(open);
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// TODO
				// いまはそもそそもアンドゥバッファもないし、保存済かどうか管理していないけれど、
				// 未保存の編集があったら聞く
				if (editPanel.networkDataModel.getFilename() != null) {
					int confirmResult =
						JOptionPane.showConfirmDialog(editDiagramWindow,
                                "現在編集中の内容は消えますがよろしいですか？",
                                "確認",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
					if (confirmResult != JOptionPane.YES_OPTION) {
						return;
					}
				}

				if (saveAsFileChooser == null) {
					String oldFilename = editPanel.networkDataModel.getFilename();
					if (oldFilename != null) {
						// saveAsFileChooser =  new JFileChooser(new File(oldFilename).getParent());
						saveAsFileChooser =  new JFileChooser(oldFilename);
					}
					else {
						saveAsFileChooser = new JFileChooser(".");
					}
				}

				int selected = saveAsFileChooser.showOpenDialog(editDiagramWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = saveAsFileChooser.getSelectedFile();
					if (file == null) {
						// 選択されなかった場合
						return;
					}

					NetworkDataModel newModel = new NetworkDataModel(editDiagramPanel, file.getAbsolutePath());
					editDiagramPanel.networkDataModel = newModel;
					editDiagramPanel.networkDataModel.load();
					editDiagramPanel.networkDataModel.analyze();
					editDiagramPanel.repaint();

					overWrite.setEnabled(true);
				}
			}
		});
		this.fileMenu.add(overWrite);

		if (editPanel.networkDataModel.getFilename() == null) {
			overWrite.setEnabled(false);
		}
		overWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					Debug.println("filename = " + editPanel.networkDataModel.getFilename());
					editPanel.networkDataModel.save();
				} catch (IOException ex) {
					String message = String.format("保存に失敗しました。\n%s",
							ex.getMessage());
					JOptionPane
							.showMessageDialog(
									editDiagramWindow,
									message,
									"Error",
									JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});

		this.fileMenu.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (saveAsFileChooser == null) {
					String oldFilename = editPanel.networkDataModel.getFilename();
					if (oldFilename != null) {
						// saveAsFileChooser =  new JFileChooser(new File(oldFilename).getParent());
						saveAsFileChooser =  new JFileChooser(oldFilename);
					}
					else {
						saveAsFileChooser = new JFileChooser(".");
					}
				}

				int selected = saveAsFileChooser.showSaveDialog(editDiagramWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = saveAsFileChooser.getSelectedFile();
					if (file == null) {
						// 選択されなかった場合
						return;
					}
					if (file.exists()) {
						int confirmResult =
							JOptionPane.showConfirmDialog(editDiagramWindow,
	                                "すでにファイルが存在しますが、上書きしますか?",
	                                "保存",
	                                JOptionPane.YES_NO_OPTION,
	                                JOptionPane.WARNING_MESSAGE);
						if (confirmResult != JOptionPane.YES_OPTION) {
							return;
						}
					}
					try {
						editPanel.networkDataModel.setFilename(file.getAbsolutePath());
						editPanel.networkDataModel.save();
					} catch (Exception ex) {
						String message = String.format("保存に失敗しました。\n%s",
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
					outputGraphicsWindow.editParamWindow = editParamWindow;
					outputGraphicsWindow.editDiagramWindow = editDiagramWindow;

					// パラメーター値が設定されたときのコールバック
					Runnable callback = new Runnable() {
						@Override
						public void run() {
							editDiagramPanel.networkDataModel.resetVariables(editParamWindow.getVariables());
							editDiagramPanel.networkDataModel.analyze();
							editDiagramPanel.networkDataModel.runProgram();
						}
					};
					editParamWindow.setCallback(callback);
					editParamWindow.setVisible(true);
					
					if (editParamWindow.isOk()) {
						callback.run();
					}

					Debug.println("END");
				}
				else {
					outputGraphicsWindow.editParamWindow = null;
					outputGraphicsWindow.editDiagramWindow = null;
					Debug.println("START");
					editDiagramPanel.networkDataModel.analyze();
					editDiagramPanel.networkDataModel.runProgram();
					Debug.println("END");
				}
				outputTextWindow.setVisible(true);
				outputGraphicsWindow.setVisible(true);
				editDiagramPanel.repaint();
			}
		});

		this.windowMenu = new JMenu("ウィンドウ");
		JMenuItem dispParaWin = new JMenuItem("パラメーターの一覧");
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
