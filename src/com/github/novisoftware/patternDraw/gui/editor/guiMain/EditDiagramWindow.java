package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.NumberPicker;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P001_IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P010___ConnectTerminal;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P030____ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.AbstractInputConstantWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.InputConstantBooleanWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.InputConstantScalarWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.InputConstantStringWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.InputOtherTypeWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.inputConstant.InputVariableSetWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ContextMenu;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.EditDiagramMenuBar;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.utils.Debug;

public class EditDiagramWindow extends JFrame2 {
	EditDiagramMenuBar editMenuBar;
	EditDiagramPanel editPanel;

	public EditDiagramWindow(String filename) {
		super();

		this.setSize(1500, 700);
		this.editPanel = new EditDiagramPanel(filename);
		this.editMenuBar = new EditDiagramMenuBar(this, this.editPanel);

		this.setJMenuBar(this.editMenuBar);
		this.setTitle("ダイヤグラムを編集: " + this.editPanel.networkDataModel.title);
		this.editPanel.setPreferredSize(new Dimension(1500,2000));
		this.editPanel.setSize(1500,2000);
		JScrollPane sp = new JScrollPane(this.editPanel);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED  );
		sp.setPreferredSize(this.editPanel.getPreferredSize());
		this.add(sp);
	}

	public static class MListener implements MouseListener, MouseMotionListener {
		JFrame inputWindow = null;
		WindowListener inputWindowCloseListener;

		// Popupを閉じたくて押したときに、また Popupが開かないように制御したい
		// この変数が true の場合は Popup を開かない。
		// TODO
		// ただし、 Popup が閉じるのを確実には拾えていないので、何回か押す必要がある場合が発生する。
		public boolean isPopupExists = false;

		/**
		 * 定数値の編集用のウィンドウは、疑似的なモーダル画面にする。
		 */
		final EditDiagramPanel editPanel__;
		MListener(EditDiagramPanel editPanel) {
			this.editPanel__ = editPanel;

			final MListener thisMListener = this;
			this.inputWindowCloseListener = new WindowListener() {
				@Override
				public void windowClosed(WindowEvent e) {
					thisMListener.inputWindow = null;
				}

				@Override
				public void windowOpened(WindowEvent e) {
				}

				@Override
				public void windowClosing(WindowEvent e) {
				}

				@Override
				public void windowIconified(WindowEvent e) {
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
				}

				@Override
				public void windowActivated(WindowEvent e) {
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
				}
			};
		}

		/**
		 * 入力中のウィンドウがある場合は、それを表に出すだけにする
		 *
		 * @return
		 */
		private boolean commonCheck() {
			if (this.inputWindow != null) {
				// 入力中のウィンドウがある場合は、それを表に出すだけにする
				this.inputWindow.setVisible(true);
				return true;
			}
			return false;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (commonCheck()) {
				return;
			}

			if (e.getButton() == MouseEvent.BUTTON1) {
				P001_IconGuiInterface obj = editPanel__.checkXY(e.getX(), e.getY());
				if (obj != null) {
					// TODO
					// オブジェクト側に処理を書く方が素直かも知れない。
					if (obj instanceof P022_____RpnGraphNodeElement) {
						P022_____RpnGraphNodeElement element = (P022_____RpnGraphNodeElement)obj;
						if (element.getKindId() == KindId.CONSTANT) {
							PointerInfo pointerInfo = MouseInfo.getPointerInfo();
							Point p = pointerInfo.getLocation();

							AbstractInputConstantWindow f = null;

							if (Value.ValueType.INTEGER.equals(element.getValueType())
									|| Value.ValueType.FLOAT.equals(element.getValueType())
									|| Value.ValueType.NUMERIC.equals(element.getValueType())) {
								f = new InputConstantScalarWindow(element, editPanel__);
							}
							else if (Value.ValueType.BOOLEAN.equals(element.getValueType())) {
								f = new InputConstantBooleanWindow(element, editPanel__);
							}
							else {
								f = new InputConstantStringWindow(element, editPanel__);
							}
							f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							f.addWindowListener(inputWindowCloseListener);
							f.setLocation(p);
							f.setVisible(true);
							this.inputWindow = f;
						}
						else if (element.getKindId() == KindId.COMMENT) {
							PointerInfo pointerInfo = MouseInfo.getPointerInfo();
							Point p = pointerInfo.getLocation();

							AbstractInputConstantWindow f = null;

							f = new InputConstantStringWindow(element, editPanel__);
							f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							f.addWindowListener(inputWindowCloseListener);
							f.setLocation(p);
							f.setVisible(true);
							this.inputWindow = f;
						} else if (element.getKindId() == KindId.VARIABLE_SET) {
								PointerInfo pointerInfo = MouseInfo.getPointerInfo();
								Point p = pointerInfo.getLocation();

								AbstractInputConstantWindow f = null;

								f = new InputVariableSetWindow(element, editPanel__);
								f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
								f.addWindowListener(inputWindowCloseListener);
								f.setLocation(p);
								f.setVisible(true);
								this.inputWindow = f;
						} else {
							PointerInfo pointerInfo = MouseInfo.getPointerInfo();
							Point p = pointerInfo.getLocation();

							InputOtherTypeWindow f = new InputOtherTypeWindow(element, editPanel__);
							f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							f.addWindowListener(inputWindowCloseListener);
							f.setLocation(p);
							f.setVisible(true);
							this.inputWindow = f;
						}
					}
					else if (obj instanceof P030____ControlElement) {
						P030____ControlElement element = (P030____ControlElement)obj;
						PointerInfo pointerInfo = MouseInfo.getPointerInfo();
						Point p = pointerInfo.getLocation();

						InputOtherTypeWindow f = new InputOtherTypeWindow(element, editPanel__);
						f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						f.addWindowListener(inputWindowCloseListener);
						f.setLocation(p);
						f.setVisible(true);
						this.inputWindow = f;
					}

					// TODO: FuncGraphNode の場合は、特に反応させないけれど、
					// なにか設定したほうが良いか自体から、考える。
				}
				else {
					if (isPopupExists) {
						// Popupを閉じたくて押したときに、また Popupが開かないように制御したい
						isPopupExists = false;
					} else {
						isPopupExists = true;
						JMenu generateMenu = ContextMenu.elementGenerateMenu(this.editPanel__, this, e.getX(), e.getY());
						JPopupMenu popup = new JPopupMenu();
						popup.add(generateMenu);
						popup.show( editPanel__, e.getX(), e.getY() );
					}
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON3) {
				if (isPopupExists) {
					// Popupを閉じたくて押したときに、また Popupが開かないように制御したい
					isPopupExists = false;
				} else {
					isPopupExists = true;
					P001_IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
					// nullでも、nullでなくても
					ContextMenu popup = new ContextMenu(editPanel__, t, e.getX(), e.getY());
					popup.show( editPanel__, e.getX(), e.getY() );
				}
			}

		}

		P001_IconGuiInterface handled = null;
		int old_x, old_y;

		@Override
		public void mousePressed(MouseEvent e) {
			Debug.println("mouse pressed(1)");

			if (commonCheck()) {
				return;
			}

			Debug.println("mouse pressed(2)");

			P001_IconGuiInterface t__ = editPanel__.checkXY(e.getX(), e.getY());
			if (t__ == null) {
				return;
			}

			if (t__ instanceof P020___AbstractElement) {
				P020___AbstractElement t = (P020___AbstractElement)t__;

				if (t != null) {
					handled = t;
					old_x = e.getX();
					old_y = e.getY();
					// System.out.println("h " + t.id);

					P020___AbstractElement hd = t;

					if (e.getButton() == MouseEvent.BUTTON3) {
						editPanel__.workLineFrom = hd;
						editPanel__.workLineX = hd.getCenterX();
						editPanel__.workLineY = hd.getCenterY();
					}
				}
			}
			else if (t__ instanceof P010___ConnectTerminal) {
				P010___ConnectTerminal hd = (P010___ConnectTerminal)t__;

				handled = hd;
				old_x = e.getX();
				old_y = e.getY();

				if (e.getButton() == MouseEvent.BUTTON1) {
					editPanel__.workLineFrom = hd;
					editPanel__.workLineX = hd.getCenterX();
					editPanel__.workLineY = hd.getCenterY();
				}
			}
			else if (t__ instanceof NumberPicker) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					NumberPicker hd = (NumberPicker)t__;
					Debug.println("mousePressed in NumberPicker " + hd.number);

					handled = hd;
					old_x = e.getX();
					old_y = e.getY();
				}

				// 数字の部分から線は引かない
				/*
				if (e.getButton() == MouseEvent.BUTTON1) {
					editPanel__.workLineFrom = hd;
					editPanel__.workLineX = hd.getCenterX();
					editPanel__.workLineY = hd.getCenterY();
				}
				*/
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (commonCheck()) {
				return;
			}
			if (handled == null) {
				editPanel__.workLineFrom = null;
				editPanel__.networkDataModel.analyze();
				editPanel__.repaint();

				return;
			}

			if (handled instanceof P030____ControlElement && editPanel__.workLineFrom != null) {
				/*
				Controller from = (Controller)handled;
				// 仮実装
				if (from.connectors.size() != 0) {
					IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
					if (t != null && t != from) {

						// 仮実装
						if (t instanceof GraphNodeElement) {
							GraphNodeElement to = (GraphNodeElement)t;

							// TODO 仮
							// 適当な空のところに設定する

							for (GraphConnector connector : to.connectors) {
								String key = connector.getParaName();

								if (from.paramMapInfo.get(key) == null) {
									from.paramMapInfo.put(key, to.id);
									from.paramMapObj.put(key, to);

									break;
								}
							}

						}
					}
				}
				*/
			}

			// fromがElement、toが端子の場合
			else if (handled instanceof P021____AbstractGraphNodeElement && editPanel__.workLineFrom != null) {
				P021____AbstractGraphNodeElement from = (P021____AbstractGraphNodeElement)handled;
				P001_IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
				if (t != null && t != from && t instanceof P010___ConnectTerminal) {
					P010___ConnectTerminal conn = (P010___ConnectTerminal)t;
					if (conn.getNode() != from) {
						P021____AbstractGraphNodeElement to = conn.getNode();

						to.paramMapInfo.put(conn.getParaName(), from.id);
						to.paramMapObj.put(conn.getParaName(), from);
					}

					editPanel__.networkDataModel.analyze();
					editPanel__.repaint();
				}
			}

			// fromが端子、toがElementの場合
			else if (handled instanceof P010___ConnectTerminal && editPanel__.workLineFrom != null) {
				P010___ConnectTerminal conn = (P010___ConnectTerminal)handled;
				P001_IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
				if (t != null && t != conn && t != conn.getNode() && t instanceof P021____AbstractGraphNodeElement) {
					// fromがオブジェクト、toが端子の場合
					P021____AbstractGraphNodeElement element = (P021____AbstractGraphNodeElement)t;

					conn.getNode().paramMapInfo.put(conn.getParaName(), element.id);
					conn.getNode().paramMapObj.put(conn.getParaName(), element);

					editPanel__.networkDataModel.analyze();
					editPanel__.repaint();
				}
			}

			// fromがElement、toが端子の場合
			else if (handled instanceof P022_____RpnGraphNodeElement && editPanel__.workLineFrom == null) {
				P022_____RpnGraphNodeElement from = (P022_____RpnGraphNodeElement)handled;
				// if (from.groupHead != null) {
					editPanel__.networkDataModel.analyze();
					editPanel__.repaint();
				// }
			} else if (handled != null) {
				editPanel__.networkDataModel.analyze();
				editPanel__.repaint();
			}

			handled = null;
			editPanel__.workLineFrom = null;
			editPanel__.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (commonCheck()) {
				return;
			}
			if (handled != null) {
				if (editPanel__.workLineFrom != null) {
					editPanel__.workLineX = e.getX();
					editPanel__.workLineY = e.getY();
				}
				else {
					if (handled instanceof P020___AbstractElement) {
						P020___AbstractElement h = (P020___AbstractElement)handled;

						// 差分をアイコンオブジェクトに通知する
						h.dragged(e.getX() - old_x, e.getY() - old_y);
						old_x = e.getX();
						old_y = e.getY();
					}
					else if (handled instanceof NumberPicker) {
						NumberPicker h = (NumberPicker)handled;

						// 差分をアイコンオブジェクトに通知する
						h.dragged(e.getX() - old_x, e.getY() - old_y);
						old_x = e.getX();
						old_y = e.getY();
					}
				}

				editPanel__.repaint();
			}
		}

		P001_IconGuiInterface lastTouched = null;

		@Override
		public void mouseMoved(MouseEvent e) {
			P001_IconGuiInterface obj = editPanel__.checkXY(e.getX(), e.getY());

			boolean isChanged = false;
			if (obj != lastTouched) {
				if (lastTouched != null) {
					lastTouched.setOnMouse(false);
				}
				if (obj != null) {
					obj.setOnMouse(true);
				}
				isChanged = true;
				lastTouched = obj;
			}

			if (isChanged) {
				editPanel__.repaint();
			}
		}
	}

	static public void main(String args[]) {
		String filename = null;
		if (args.length == 1) {
			filename = args[0];
		}
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// 処理不要
		}

		// setVisibleしないけれど、インスタンス生成は先にしておく
		OutputTextWindow.getInstance();
		// setVisibleしないけれど、インスタンス生成は先にしておく
		OutputGraphicsWindow.getInstance();

		EditDiagramWindow frame = new EditDiagramWindow(filename);
		frame.setSize(1500, 800);
		frame.setVisible(true);
	}
}
