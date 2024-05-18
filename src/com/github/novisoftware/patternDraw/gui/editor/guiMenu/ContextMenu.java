package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P010___ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P010___ConnectTerminal;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P001_IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;

public class ContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	int x;
	int y;

	public ContextMenu(final EditDiagramPanel editPanel, final P001_IconGuiInterface icon, int x, int y) {
		this.x = x;
		this.y = y;

		JMenuItem menuItem;

		if (icon != null) {
			if (icon instanceof P022_____RpnGraphNodeElement) {
				menuItem = new JMenuItem("テスト");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				this.add(menuItem);
			}
		}

		if (icon != null) {
			if (icon instanceof P022_____RpnGraphNodeElement) {
				final P022_____RpnGraphNodeElement tone = (P022_____RpnGraphNodeElement) icon;
				if (tone.hasParameter()) {
					menuItem = new JMenuItem("参照を全て削除");
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							tone.paramMapInfo.clear();
							tone.paramMapObj.clear();

							editPanel.networkDataModel.analyze();
							editPanel.repaint();
						}
					});
					this.add(menuItem);
				}
			}

			if (icon instanceof P010___ConnectTerminal) {
				final P010___ConnectTerminal connect = (P010___ConnectTerminal) icon;
				menuItem = new JMenuItem("参照を削除");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						connect.getNode().paramMapInfo.remove(connect.getParaName());
						connect.getNode().paramMapObj.remove(connect.getParaName());
						editPanel.networkDataModel.analyze();
						editPanel.repaint();
					}
				});
				this.add(menuItem);
			}
		}

		if (icon != null) {
			if (icon instanceof P020___AbstractElement) {
				P020___AbstractElement ti = (P020___AbstractElement) icon;

				// 複製
				menuItem = new JMenuItem("複製");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						P020___AbstractElement c = ti.getCopy();
						c.y = ti.y + ti.h + 3;
						c.id = editPanel.networkDataModel.generateUniqueName(c.id);

						editPanel.networkDataModel.getElements().add(c);
						// 注: 複製では単連結グラフが増えるため必要。
						editPanel.networkDataModel.analyze();
						editPanel.repaint();
					}
				});
				this.add(menuItem);

				// 削除
				menuItem = new JMenuItem("削除");
				menuItem.addActionListener(new ActionListener() {
					void removeIcon(P020___AbstractElement icon) {
						// オブジェクト一覧から削除
						editPanel.networkDataModel.getElements().remove(icon);
						// 参照を削除
						for (P020___AbstractElement ei : editPanel.networkDataModel.getElements()) {
							if (ei instanceof P022_____RpnGraphNodeElement) {
								P022_____RpnGraphNodeElement ele = (P022_____RpnGraphNodeElement) ei;

								for (P010___ConnectTerminal connector : ele.connectors) {
									P021____AbstractGraphNodeElement src = ele.paramMapObj.get(connector.getParaName());
									if (src == icon) {
										ele.paramMapInfo.remove(connector.getParaName());
										ele.paramMapObj.remove(connector.getParaName());
									}
								}

							}
						}
					}

					public void actionPerformed(ActionEvent event) {
						if (ti instanceof P010___ControlElement && ((P010___ControlElement) ti).controllerGroup != null) {
							for (P010___ControlElement c : ((P010___ControlElement) ti).controllerGroup) {
								this.removeIcon(c);
							}
						} else {
							this.removeIcon(ti);
						}
						editPanel.networkDataModel.analyze();
						editPanel.repaint();
					}
				});
				this.add(menuItem);
			}
		}

		/*
		 * if (icon == null) { this.add(
		 * ContextMenu.elementGenerateMenu(editPanel, x, y) ); }
		 */

		if (icon == null) {
			// this.addSeparator();
			menuItem = new JMenuItem("パラメーターを定義");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (editPanel.paramDefEditWindow != null) {
						System.out.println("パラメーター編集画面のオブジェクトがあったので 再表示 するだけ");

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
					frame.setLocation(900, 40);
					// frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				}
			});
			this.add(menuItem);
		}

		if (icon == null) {
			this.addSeparator();
			menuItem = GuiUtil.createMenuItem("開発者用デバッグ情報を表示", editPanel.isVisibleDebugInfo);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editPanel.isVisibleDebugInfo = !editPanel.isVisibleDebugInfo;
					editPanel.repaint();
				}
			});
			this.add(menuItem);
		}
	}

	static ArrayList<ElementFactory> partsList = null;

	static public JMenu elementGenerateMenu(final EditDiagramPanel editPanel, final MListener mListener, int x, int y) {
		if (partsList == null) {
			MenuGenerator g = new MenuGenerator();
			partsList = g.generateMenuList(editPanel);
		}

		/*
		 * for (Parts a : partsList) { System.out.println(a.description);
		 * System.out.println(a.rpn); System.out.println(); }
		 */

		// メニュー要素を展開する
		// (定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する)
		ArrayList<ElementFactory> expandedParts = ElementFactory.partsListExtend(editPanel, partsList);

		JMenu menu = new JMenu("部品を追加");
		HashMap<String, JMenu> workHm2 = new HashMap<>();

		// メニュー組み立て
		for (final ElementFactory nParts : expandedParts) {
			String n = nParts.dispName;
			JMenuItem menuItem;
			if (n.indexOf('/') == -1) {
				menuItem = new JMenuItem(nParts.dispName);
				menu.add(menuItem);
			} else {
				String[] splited = n.split("/");

				String name = splited[splited.length - 1];
				String folder = n.substring(0, n.length() - 1 - name.length());

				if (!workHm2.containsKey(folder)) {
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
					editPanel.networkDataModel.analyze();
					editPanel.repaint();

					// 閉じるので。
					mListener.isPopupExists = false;

					/*
					 * ElementIcon t =
					 * nParts.getNewElement(editPanel.networkDataModel.
					 * generateUniqueName(nParts.getKindName() + "0"), x, y);
					 * editPanel.networkDataModel.getElements().add(t);
					 * editPanel.networkDataModel.evaluate();
					 * editPanel.repaint();
					 */

				}
			});
		}

		return menu;
	}
}