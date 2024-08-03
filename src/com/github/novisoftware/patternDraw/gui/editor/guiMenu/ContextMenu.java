package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.novisoftware.patternDraw.core.Rpn;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P001_IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P010___ConnectTerminal;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P015__AbstractIcon2;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P030____ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditParamDefListWindow;
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
			if (icon instanceof P020___AbstractElement) {
				P020___AbstractElement ti = (P020___AbstractElement) icon;

				// 変数参照を取得
				if (KindId.VARIABLE_SET.equals(ti.getKindId())) {
					menuItem = new JMenuItem("変数の参照を作成");
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							P020___AbstractElement c = (P020___AbstractElement)ti.getCopy();
							c.x = ti.x + ti.w + 60;
							// c.y = ti.y + ti.h + 3;
							c.id = editPanel.networkDataModel.generateUniqueName(c.id);

							c.setKindId(KindId.VARIABLE_REFER);

							P022_____RpnGraphNodeElement r =
							(P022_____RpnGraphNodeElement)c;
							r.setRpn(r.getRpn().convSetToRecallVariable());

							editPanel.networkDataModel.getElements().add(c);
							// 注: 複製では単連結グラフが増えるため必要。
							editPanel.networkDataModel.analyze();
							editPanel.repaint();
						}
					});
					this.add(menuItem);
					this.addSeparator();
				}
			}
			if (icon instanceof P022_____RpnGraphNodeElement) {
				final P022_____RpnGraphNodeElement rpnE = (P022_____RpnGraphNodeElement)icon;
				final Rpn rpn = rpnE.getRpn();
				if (rpn.hasMacro()) {
					final Integer nowRepeatN = rpn.getRepeatN();
					
					JMenu menu = new JMenu("入力の数を変更");
					
					for (int i = 2; i <= 8; i++) {
						final int i_ = i;
						boolean mark = nowRepeatN != null && (nowRepeatN == i);
						menuItem = GuiUtil.createMenuItem("" + i, mark);
						menuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								rpn.setRepeatN(i_);
								rpnE.setRpn(rpn);
								editPanel.networkDataModel.analyze();
								editPanel.repaint();
							}
						});
						menu.add(menuItem);
					}
					this.add(menu);
					this.addSeparator();
				}
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
			if (icon instanceof P015__AbstractIcon2) {
				P015__AbstractIcon2 ti = (P015__AbstractIcon2) icon;

				// 複製
				menuItem = new JMenuItem("複製");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						P015__AbstractIcon2 c = ti.getCopy();
						c.x = ti.x + ti.w + 5;
						c.y = ti.y + ti.h + 3;
						c.id = editPanel.networkDataModel.generateUniqueName(c.id);

						editPanel.networkDataModel.addElement(c);
						// 注: 複製では単連結グラフが増えるため必要。
						editPanel.networkDataModel.analyze();
						editPanel.repaint();
					}
				});
				this.add(menuItem);

				// 削除
				menuItem = new JMenuItem("削除");
				menuItem.addActionListener(new ActionListener() {
					void removeIcon(P015__AbstractIcon2 icon) {
						// オブジェクト一覧から削除
						editPanel.networkDataModel.getElements().remove(icon);
						// 参照を削除
						for (P015__AbstractIcon2 ei : editPanel.networkDataModel.getElements()) {
							if (ei instanceof P021____AbstractGraphNodeElement) {
								P021____AbstractGraphNodeElement ele = (P021____AbstractGraphNodeElement) ei;

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
						if (ti instanceof P030____ControlElement && ((P030____ControlElement) ti).controllerGroup != null) {
							for (P030____ControlElement c : ((P030____ControlElement) ti).controllerGroup) {
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
						Debug.println("パラメーター編集画面のオブジェクトがあったので 再表示 するだけ");

						editPanel.paramDefEditWindow.setVisible(true);
						return;
					}
					Debug.println("パラメーター編集画面のオブジェクトを作成");


					ArrayList<ParameterDefine> params = editPanel.networkDataModel.paramDefList;

					Runnable callback = new Runnable() {
						@Override
						public void run() {
							Debug.println("パラメーター編集画面のオブジェクトを 破棄");
							editPanel.paramDefEditWindow = null;
						}
					};

					EditParamDefListWindow frame = new EditParamDefListWindow(
							editPanel.networkDataModel,
							params,
							callback);
					editPanel.paramDefEditWindow = frame;
					frame.setVisible(true);
					frame.setLocation(900, 40);
					// frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				}
			});
			this.add(menuItem);
		}

		if (Debug.SHOW_DEBUG_SWITCH) {
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
	
	
				menuItem = GuiUtil.createMenuItem("printf系のデバッグ情報を出力",  Debug.enable);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Debug.enable = !Debug.enable;
					}
				});
				this.add(menuItem);
			}
		}
	}

	static ArrayList<AbstractElementFactory> partsList = null;

	static public JMenu elementGenerateMenu(final EditDiagramPanel editPanel, final MListener mListener, int x, int y) {
		if (partsList == null) {
			MenuGenerator g = new MenuGenerator();
			partsList = g.generateMenuList(editPanel);
		}

		// メニュー要素を展開する
		// (定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する)
		ArrayList<AbstractElementFactory> expandedParts = ElementFactory.partsListExtend(editPanel, partsList);

		JMenu menu = new JMenu("部品を追加");
		HashMap<String, JMenu> workHm2 = new HashMap<>();

		// メニュー組み立て
		for (final AbstractElementFactory nParts : expandedParts) {
			nParts.addMenuList(menu, workHm2, editPanel, mListener, x, y);
		}

		return menu;
	}
}