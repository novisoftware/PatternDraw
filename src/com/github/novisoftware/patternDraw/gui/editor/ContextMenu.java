package com.github.novisoftware.patternDraw.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon.KindId;
import com.github.novisoftware.patternDraw.gui.editor.parts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;


class ContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	int x;
	int y;

	ContextMenu(final EditPanel editPanel, final IconGuiInterface icon, int x, int y) {
		this.x = x;
		this.y = y;

		JMenuItem menuItem;

		if (icon != null) {
			if (icon instanceof GraphNodeElement) {
				menuItem = new JMenuItem("テスト");
				menuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
							}
						});
				this.add( menuItem );
			}
		}

		if (icon != null) {
			if (icon instanceof GraphNodeElement) {
				final GraphNodeElement tone = (GraphNodeElement)icon;
				if (tone.hasParameter()) {
					menuItem = new JMenuItem("参照を全て削除");
					menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									tone.paramMapInfo.clear();
									tone.paramMapObj.clear();

									editPanel.networkDataModel.evaluate();
									editPanel.repaint();
								}
							});
					this.add( menuItem );
				}
			}

			if (icon instanceof GraphConnector) {
				final GraphConnector connect = (GraphConnector)icon;
				menuItem = new JMenuItem("参照を削除");
				menuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								connect.getNode().paramMapInfo.remove( connect.getParaName() );
								connect.getNode().paramMapObj.remove( connect.getParaName() );
								editPanel.networkDataModel.evaluate();
								editPanel.repaint();
							}
						});
				this.add( menuItem );
			}
		}

		if (icon != null) {
			if (icon instanceof ElementIcon) {
				ElementIcon ti = (ElementIcon)icon;

				// 複製
				menuItem = new JMenuItem("複製");
				menuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								ElementIcon c = ti.getCopy();
								c.y = ti.y + ti.h + 3;
								c.id = editPanel.networkDataModel.generateUniqueName(c.id);;

								editPanel.networkDataModel.getElements().add(c);
								editPanel.networkDataModel.evaluate();
								editPanel.repaint();
							}
						});
				this.add( menuItem );

				// 削除
				menuItem = new JMenuItem("削除");
				menuItem.addActionListener(new ActionListener() {
							void removeIcon(ElementIcon icon) {
								// オブジェクト一覧から削除
								editPanel.networkDataModel.getElements().remove(icon);
								// 参照を削除
								for (ElementIcon ei: editPanel.networkDataModel.getElements()) {
									if (ei instanceof GraphNodeElement) {
										GraphNodeElement ele = (GraphNodeElement)ei;

										for (GraphConnector connector : ele.connectors) {
											GraphNodeElement src = ele.paramMapObj.get(connector.getParaName());
											if (src == icon) {
												ele.paramMapInfo.remove(connector.getParaName());
												ele.paramMapObj.remove(connector.getParaName());
											}
										}

									}
								}
							}

							public void actionPerformed(ActionEvent event) {
								if (ti instanceof ControlBlock && ((ControlBlock)ti).controllerGroup != null) {
									for (ControlBlock c : ((ControlBlock)ti).controllerGroup) {
										this.removeIcon(c);
									}
								}
								else {
									this.removeIcon(ti);
								}
								editPanel.networkDataModel.evaluate();
								editPanel.repaint();
							}
						});
				this.add( menuItem );
			}
		}

		if (icon == null) {
			this.add( ContextMenu.elementGenerateMenu(editPanel, x, y) );
		}

		if (icon == null) {
			this.addSeparator();
			menuItem = new JMenuItem("評価(うごかす)");
			menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							// tonePanel.tonePaletDataModel.save();

							Debug.println("START");
							editPanel.networkDataModel.evaluate();
							editPanel.networkDataModel.runProgram();
							Debug.println("END");

							editPanel.repaint();
						}
					});
			this.add( menuItem );
		}

		if (icon == null) {
			this.addSeparator();
			menuItem = Common.createMenuItem("開発者用デバッグ情報を表示", editPanel.isVisibleDebugInfo);
			menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							editPanel.isVisibleDebugInfo = ! editPanel.isVisibleDebugInfo;
							editPanel.repaint();
						}
					});
			this.add( menuItem );
		}
		if (icon == null) {
			this.addSeparator();
			menuItem = new JMenuItem("保存");
			menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							editPanel.networkDataModel.save();
						}
					});
			this.add( menuItem );
		}
		if (icon == null) {
			this.addSeparator();
			menuItem = new JMenuItem("終了");
			menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							System.exit(0);
						}
					});
			this.add( menuItem );
		}
	}


	static ArrayList<ElementGenerator> partsList = null;
	static public JMenu elementGenerateMenu(final EditPanel editPanel, int x, int y) {
		String input = "../partsElement.txt";

		if (partsList == null) {
			partsList = ElementGenerator.loadElementPartsList(editPanel, input);
		}

		/*
		for (Parts a : partsList) {
			System.out.println(a.description);
			System.out.println(a.rpn);
			System.out.println();
		}
		*/

		// メニュー要素を展開する
		// (定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する)
		ArrayList<ElementGenerator> expandedParts = ElementGenerator.partsListExtend(editPanel, partsList);

		JMenu menu = new JMenu("部品を追加");
		HashMap<String,JMenu> workHm2 = new HashMap<>();

		// メニュー組み立て
		for (final ElementGenerator nParts : expandedParts) {
			String n = nParts.dispName;
			JMenuItem menuItem;
			if (n.indexOf('/') == -1) {
				menuItem = new JMenuItem(nParts.dispName);
				menu.add(menuItem);
			}
			else {
				String[] splited = n.split("/");

				String name = splited[splited.length-1];
				String folder = n.substring(0, n.length() - 1 - name.length());

				if (! workHm2.containsKey(folder)) {
					JMenu addMenu = new JMenu(folder);
					menu.add(addMenu);
					workHm2.put(folder, addMenu);
				}
				JMenu parentMenu = workHm2.get(folder);

				menuItem = new JMenuItem(name);
				parentMenu.add(menuItem);
			}
			menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						nParts.createNewElement(editPanel, x, y);
						editPanel.networkDataModel.evaluate();
						editPanel.repaint();

						/*
						ElementIcon t =
								nParts.getNewElement(editPanel.networkDataModel.generateUniqueName(nParts.getKindName() + "0"), x, y);
						editPanel.networkDataModel.getElements().add(t);
						editPanel.networkDataModel.evaluate();
						editPanel.repaint();
						*/

					}
				});
		}

		return menu;
	}
}