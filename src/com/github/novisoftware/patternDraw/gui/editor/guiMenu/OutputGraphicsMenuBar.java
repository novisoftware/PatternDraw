package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.utils.FileWriteUtil;

public class OutputGraphicsMenuBar extends JMenuBar {
	// ファイル選択ダイアログ
	static private JFileChooser pngFileChooser = new JFileChooser(".");
	static private JFileChooser svgFileChooser = new JFileChooser(".");

	final OutputGraphicsWindow outputGraphicsWindow;

	final JMenu fileMenu;
	public OutputGraphicsMenuBar(final OutputGraphicsWindow outputGraphicsWindow) {
		final OutputGraphicsMenuBar thisObj = this;

		this.outputGraphicsWindow = outputGraphicsWindow;
		this.fileMenu = new JMenu("ファイル");
		JMenuItem saveAsPNG = new JMenuItem("画像をPNG出力");
		this.fileMenu.add(saveAsPNG);

		saveAsPNG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int selected = pngFileChooser.showSaveDialog(thisObj.outputGraphicsWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = pngFileChooser.getSelectedFile();
					file = FileWriteUtil.checkSuffix(file, "png");
					if (file.exists()) {
						int confirmResult =
							JOptionPane.showConfirmDialog(thisObj.outputGraphicsWindow,
	                                "すでにファイルが存在しますが、上書きしますか?",
	                                "保存",
	                                JOptionPane.YES_NO_OPTION,
	                                JOptionPane.WARNING_MESSAGE);
						if (confirmResult != JOptionPane.YES_OPTION) {
							return;
						}
					}
					try {
						// 画像ファイルを保存
						outputGraphicsWindow.outputPNG(file);
					} catch (Exception ex) {
						String message = String.format("保存に失敗しました。\n%s",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										thisObj.outputGraphicsWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						// 実行パラメーターログの保存用ファイル
						File varFile = FileWriteUtil.replaceSuffix(file, "paramlog.json");
						outputGraphicsWindow.outputParameterLog(varFile, file);
					} catch (Exception ex) {
						String message = String.format("パラメーターログ出力に失敗しました。\n%s",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										thisObj.outputGraphicsWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					// この動作は要らない?
					JOptionPane.showMessageDialog(thisObj.outputGraphicsWindow, "保存しました。");
				}
			}
		});

		JMenuItem saveAsSVG = new JMenuItem("画像をSVG出力");
		this.fileMenu.add(saveAsSVG);
		saveAsSVG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int selected = svgFileChooser.showSaveDialog(thisObj.outputGraphicsWindow);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = svgFileChooser.getSelectedFile();
					file = FileWriteUtil.checkSuffix(file, "svg");

					if (file.exists()) {
						int confirmResult =
							JOptionPane.showConfirmDialog(thisObj.outputGraphicsWindow,
	                                "すでにファイルが存在しますが、上書きしますか?",
	                                "保存",
	                                JOptionPane.YES_NO_OPTION,
	                                JOptionPane.WARNING_MESSAGE);
						if (confirmResult != JOptionPane.YES_OPTION) {
							return;
						}
					}

					try {
						OutputGraphicsWindow outputGraphicsWindow = OutputGraphicsWindow.getInstance();
						outputGraphicsWindow.outputSVG(file);
					} catch (Exception ex) {
						String message = String.format("保存に失敗しました (%s)",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										thisObj.outputGraphicsWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						// 実行パラメーターログの保存用ファイル
						File varFile = FileWriteUtil.replaceSuffix(file, "paramlog.json");
						outputGraphicsWindow.outputParameterLog(varFile, file);
					} catch (Exception ex) {
						String message = String.format("パラメーターログ出力に失敗しました。\n%s",
								ex.getMessage());
						JOptionPane
								.showMessageDialog(
										thisObj.outputGraphicsWindow,
										message,
										"Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}

					// この動作は要らない?
					JOptionPane.showMessageDialog(thisObj.outputGraphicsWindow, "保存しました。");
				}
			}
		});

		this.add(this.fileMenu);
	}
}
