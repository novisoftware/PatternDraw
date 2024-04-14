package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ContextMenu;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;

public class EditFrame extends JFrame {
	EditPanel editPanel;

	public EditFrame(String filename) {
		Common.setIconImage(this);

		this.setSize(1500, 700);
		this.editPanel = new EditPanel(filename);
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
		final EditPanel editPanel__;
		MListener(EditPanel editPanel) {
			this.editPanel__ = editPanel;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				IconGuiInterface obj = editPanel__.checkXY(e.getX(), e.getY());
				if (obj != null) {
					if (obj instanceof AbstractElement) {
						AbstractElement element = (AbstractElement)obj;
						ElementEditFrame f = new ElementEditFrame(element, editPanel__);
						f.setVisible(true);
					}
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON3) {
				IconGuiInterface t = editPanel__.checkXY(e.getX(), e.getY());
				// nullでも、nullでなくても
				ContextMenu popup = new ContextMenu(editPanel__, t, e.getX(), e.getY());
				popup.show( editPanel__, e.getX(), e.getY() );
			}

		}

		IconGuiInterface handled = null;
		int old_x, old_y;

		@Override
		public void mousePressed(MouseEvent e) {
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
		OutputTextFrame outputFrame = OutputTextFrame.getInstance();
		OutputGraphicsFrame outputGraphicsFrame = OutputGraphicsFrame.getInstance();
		outputGraphicsFrame.setVisible(true);

		EditFrame frame = new EditFrame(args[0]);
		frame.setSize(1500, 800);
		frame.setVisible(true);
	}
}
