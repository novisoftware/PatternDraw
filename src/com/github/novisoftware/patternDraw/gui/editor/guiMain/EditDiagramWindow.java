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

import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.InputOtherTypeWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.InputConstantWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ContextMenu;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;

public class EditDiagramWindow extends JFrame {
	EditDiagramPanel editPanel;

	public EditDiagramWindow(String filename) {
		Common.setIconImage(this);

		this.setSize(1500, 700);
		this.editPanel = new EditDiagramPanel(filename);
		this.setTitle(Common.FRAME_TITLE_BASE + " 編集画面: " + this.editPanel.networkDataModel.title);
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
				IconGuiInterface obj = editPanel__.checkXY(e.getX(), e.getY());
				if (obj != null) {
					if (obj instanceof RpnGraphNodeElement) {
						RpnGraphNodeElement element = (RpnGraphNodeElement)obj;
						if (element.getKindId() == KindId.CONSTANT) {
							PointerInfo pointerInfo = MouseInfo.getPointerInfo();
							Point p = pointerInfo.getLocation();

							InputConstantWindow f = new InputConstantWindow(element, editPanel__);
							f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							f.addWindowListener(inputWindowCloseListener);
							f.setLocation(p);
							f.setVisible(true);
							this.inputWindow = f;

						}else {
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
					IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
					// nullでも、nullでなくても
					ContextMenu popup = new ContextMenu(editPanel__, t, e.getX(), e.getY());
					popup.show( editPanel__, e.getX(), e.getY() );
				}
			}

		}

		IconGuiInterface handled = null;
		int old_x, old_y;

		@Override
		public void mousePressed(MouseEvent e) {
			if (commonCheck()) {
				return;
			}

			IconGuiInterface t__ = editPanel__.checkXY(e.getX(), e.getY());
			if (t__ instanceof AbstractElement) {
				AbstractElement t = (AbstractElement)t__;

				if (t != null) {
					handled = t;
					old_x = e.getX();
					old_y = e.getY();
					// System.out.println("h " + t.id);

					AbstractElement hd = t;

					if (e.getButton() == MouseEvent.BUTTON3) {
						editPanel__.workLineFrom = hd;
						editPanel__.workLineX = hd.getCenterX();
						editPanel__.workLineY = hd.getCenterY();
					}
				}
			}

			if (t__ != null && t__ instanceof GraphConnector) {
				GraphConnector hd = (GraphConnector)t__;

				handled = hd;
				old_x = e.getX();
				old_y = e.getY();

				if (e.getButton() == MouseEvent.BUTTON1) {
					editPanel__.workLineFrom = hd;
					editPanel__.workLineX = hd.getCenterX();
					editPanel__.workLineY = hd.getCenterY();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (commonCheck()) {
				return;
			}
			if (handled == null) {
				editPanel__.workLineFrom = null;
				editPanel__.networkDataModel.evaluate();
				editPanel__.repaint();

				return;
			}

			if (handled instanceof ControlElement && editPanel__.workLineFrom != null) {
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
			else if (handled instanceof AbstractGraphNodeElement && editPanel__.workLineFrom != null) {
				AbstractGraphNodeElement from = (AbstractGraphNodeElement)handled;
				IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
				if (t != null && t != from && t instanceof GraphConnector) {
					GraphConnector conn = (GraphConnector)t;
					if (conn.getNode() != from) {
						AbstractGraphNodeElement to = conn.getNode();

						to.paramMapInfo.put(conn.getParaName(), from.id);
						to.paramMapObj.put(conn.getParaName(), from);
					}

					editPanel__.networkDataModel.evaluate();
					editPanel__.repaint();
				}
			}

			// fromが端子、toがElementの場合
			else if (handled instanceof GraphConnector && editPanel__.workLineFrom != null) {
				GraphConnector conn = (GraphConnector)handled;
				IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
				if (t != null && t != conn && t != conn.getNode() && t instanceof AbstractGraphNodeElement) {
					// fromがオブジェクト、toが端子の場合
					AbstractGraphNodeElement element = (AbstractGraphNodeElement)t;

					conn.getNode().paramMapInfo.put(conn.getParaName(), element.id);
					conn.getNode().paramMapObj.put(conn.getParaName(), element);

					editPanel__.networkDataModel.evaluate();
					editPanel__.repaint();
				}
			}

			// fromがElement、toが端子の場合
			else if (handled instanceof RpnGraphNodeElement && editPanel__.workLineFrom == null) {
				RpnGraphNodeElement from = (RpnGraphNodeElement)handled;
				// if (from.groupHead != null) {
					editPanel__.networkDataModel.evaluate();
					editPanel__.repaint();
				// }
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
					if (handled instanceof AbstractElement) {
						AbstractElement h = (AbstractElement)handled;

						// 差分をアイコンオブジェクトに通知する
						h.dragged(e.getX() - old_x, e.getY() - old_y);
						old_x = e.getX();
						old_y = e.getY();
					}
				}

				editPanel__.repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	static public void main(String args[]) {
		if (args.length == 0) {
			System.err.println("引数を指定してください(定義ファイル)。");
			return;
		}
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// 処理不要
		}
		OutputTextWindow outputFrame = OutputTextWindow.getInstance();
		OutputGraphicsWindow outputGraphicsFrame = OutputGraphicsWindow.getInstance();
		outputGraphicsFrame.setVisible(true);

		EditDiagramWindow frame = new EditDiagramWindow(args[0]);
		frame.setSize(1500, 800);
		frame.setVisible(true);
	}
}