package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.EditParamDefWindow;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.GuiPreference;

/**
 * パラメーター一覧の定義ウィンドウ。
 */
public class EditParamDefListWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 50;
	public static final int WINDOW_POS_Y = 50;
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;

	final JPanel jp;

	/**
	 * パラメーター一覧
	 */
	public final ArrayList<ParameterDefine> params;

	/**
	 * 子ウィンドウ(個別パラメーターの設定画面)
	 */
	public EditParamDefWindow inputParamDefWindow = null;

	/**
	 * 追加ボタン
	 */
	final JButton addButton;

	public ParamDefListPanel paramDefListPanel;
	
	public EditParamDefListWindow(
			final ArrayList<ParameterDefine> params,
			Runnable closeCallback) {
		super();
		this.setTitle("パラメーターの一覧");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.params = params;

		// レイアウト
		jp = new JPanel();
		jp.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		jp.setBackground(GuiPreference.BG_COLOR);
		jp.setForeground(GuiPreference.TEXT_COLOR);

		
		this.add(jp);
		// Container pane = this.getContentPane();
		Container pane = jp;// .getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));
		jp.setLayout(new FlowLayout(FlowLayout.LEADING));

		pane.add(new JLabel2("パラメーターの一覧"));
		pane.add(horizontalRule(7));

		this.paramDefListPanel = new ParamDefListPanel(this, params);
		paramDefListPanel.setBackground(GuiPreference.BG_COLOR);
		paramDefListPanel.setForeground(GuiPreference.TEXT_COLOR);
		Dimension panelDimension = new Dimension(WINDOW_WIDTH - 4, 400);
		
		paramDefListPanel.setMinimumSize(panelDimension);
		paramDefListPanel.setPreferredSize(panelDimension);
		paramDefListPanel.setSize(panelDimension);
		pane.add(paramDefListPanel);
		pane.add(horizontalRule(7));

		// Container pane = this.getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.addButton = generateAddButton();
		pane.add(addButton);


		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (closeCallback != null) {
					closeCallback.run();
				}
			}
		});

	}

	public static class ParamDefListPanel extends JPanel  {
		ArrayList<ParameterDefine> params;
		ArrayList<ParamDefDisplay> paramDefDisplay;
		// awt/swingのLayoutとは関係ない
		LayoutInfo layoutInfo;
		private MListener listener;
		public EditParamDefListWindow editParamDefListWindow;
		
		static final int Y_INTERVAL =  (GuiPreference.LABEL_FONT_SIZE + 12 );

		ParamDefListPanel(EditParamDefListWindow editParamDefListWindow, ArrayList<ParameterDefine> params) {
			super();
			
			this.editParamDefListWindow = editParamDefListWindow;
			
			layoutInfo = new LayoutInfo();
			layoutInfo.startPos.add(10);
			layoutInfo.startPos.add(150);
			layoutInfo.startPos.add(340);
			layoutInfo.startPos.add(150);
			
			this.params = params;
			updateParamDefDisplays();


			this.listener = new MListener(this);
			this.addMouseListener(listener);
			this.addMouseMotionListener(listener);
		}

		String[] title = {
				"変数名",
				"説明",
				"型",
				"デフォルト値"
		};

		public void updateParamDefDisplays() {
			paramDefDisplay = new ArrayList<ParamDefDisplay>();
			paramDefDisplay.add(new ParamDefDisplay(title));

			int index = 0;
			for (ParameterDefine p : params) {
				paramDefDisplay.add(new ParamDefDisplay(this, p, index));
				index ++;
			}
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.clearRect(0, 0, 1000, 1000);
			int y = 0;
			for (ParamDefDisplay p : paramDefDisplay) {
				y = y + 1  * Y_INTERVAL;
				p.x = 0;
				p.y = y;
			}

			
			for (ParamDefDisplay p : paramDefDisplay) {
				p.paint(g2, layoutInfo);
			}
		}
		
		public ParamDefDisplay getHandledObj(int y) {
			for (ParamDefDisplay p : this.paramDefDisplay) {
				if (p.y - 20 < y && y < p.y ) {
					return p;
				}
			}
			return null;
		}
	}
	

	static class LayoutInfo {
		ArrayList<Integer> startPos;
		
		LayoutInfo() {
			startPos = new ArrayList<Integer>();
		}
	}
	
	static class ParamDefDisplay {
		static final BasicStroke STROKE = new BasicStroke(2f);

		int index;

		boolean isDragging = false;
		boolean isDragging2 = false;
		int dragX;
		int dragY;
		int dragStartX;
		int dragStartY;
		int indexToUpdate;

		int x;
		int y;
		ArrayList<String> labels;
		ParameterDefine para;
		private ParamDefListPanel paramDefListPanel;

		public ParamDefDisplay(ParamDefListPanel paramDefListPanel, ParameterDefine para, int index) {
			this.paramDefListPanel = paramDefListPanel;
			this.para = para;
			this.index = index;
			updateLabels();
		}

		public ParamDefDisplay(String[] initArray) {
			this.index = -1;
			labels = new ArrayList<String>();
			for (String s : initArray) {
				labels.add(s);
			}
		}

		

		void updateLabels() {
			labels = new ArrayList<String>();
			// 変数名
			labels.add(para.name);
			// 説明
			labels.add(para.description);
			// 型
			labels.add(Value.valueTypeToDescString(para.valueType));
			// デフォルト値
			labels.add(para.defaultValue);
		}

		
		void paint(Graphics2D g2, LayoutInfo layoutInfo) {
			g2.setFont(GuiPreference.LABEL_FONT);
			if (this.isDragging) {
				g2.setColor(Color.GRAY);
			}
			else {
				g2.setColor(Color.BLACK);
			}

			
			{
				int x = 0;
				for (int i = 0 ; i < 4 ; i++ ) {
					x += layoutInfo.startPos.get(i);
					g2.drawString(labels.get(i), x, this.y);
				}
			}
			
			if (this.isDragging) {
				this.isDragging2 = true;
				g2.setColor(Color.BLACK);

				int x = 0;
				for (int i = 0 ; i < 4 ; i++ ) {
					x += layoutInfo.startPos.get(i);
					g2.drawString(labels.get(i),
							x +
							this.dragX - this.dragStartX,
							y +
							this.dragY - this.dragStartY);
				}
				
				int yDiff = this.dragY - this.dragStartY;
				int indexDiff = (int)Math.round(1.0 * yDiff/ParamDefListPanel.Y_INTERVAL);
				if (indexDiff != 0) {
					if (indexDiff + this.index < -1) {
						indexDiff = - this.index - 1;
					}
					if (indexDiff + this.index > paramDefListPanel.params.size() - 1) {
						indexDiff = paramDefListPanel.params.size() - this.index - 1;
					}

					this.indexToUpdate = indexDiff + this.index;

					int newY = 6 + this.y + indexDiff * ParamDefListPanel.Y_INTERVAL;
					g2.setColor(Color.GRAY);
					g2.setStroke(STROKE);
					g2.drawLine(5, newY, 500, newY);
				} else {
					this.indexToUpdate = this.index;
				}
				
			}
		}

		public void updateIndex() {
			if (! isDragging2) {
				// 注:
				// 実際にドラッグ操作のインタラクションをしていなかった場合は NOP で返す。
				return;
			}
			
			if (this.indexToUpdate == this.index) {
				return;
			}
			if (this.indexToUpdate < this.index) {
				ParameterDefine p = this.paramDefListPanel.params.remove(this.index);
				this.paramDefListPanel.params.add(this.indexToUpdate + 1, p);
			}
			if (this.indexToUpdate > this.index) {
				ParameterDefine p = this.paramDefListPanel.params.get(this.index);
				// 注: remove より前に add するので 全体の要素数は 1 多くなっている。このための +1 。
				this.paramDefListPanel.params.add(this.indexToUpdate + 1, p);
				this.paramDefListPanel.params.remove(this.index);
			}
			
			this.paramDefListPanel.updateParamDefDisplays();
		}
	}
	
	public static class MListener implements MouseListener, MouseMotionListener {
		ParamDefListPanel paramDefListPanel;
		ParamDefDisplay handledObj;

		MListener(ParamDefListPanel paramDefListPanel) {
			this.paramDefListPanel = paramDefListPanel;
			this.handledObj = null;
		}


		@Override
		public void mouseDragged(MouseEvent e) {
			if (this.handledObj == null) {
				return;
			}
			this.handledObj.dragX = e.getX();
			this.handledObj.dragY = e.getY();
			paramDefListPanel.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			ParamDefDisplay p = paramDefListPanel.getHandledObj(e.getY());
			if (p == null) {
				return;
			}
			this.paramDefListPanel.editParamDefListWindow.createInputWindow(p.para, false);
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
	    	if (this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow != null) {
				// 入力中のウィンドウがあったら、それを前面にだす。
	    		// ドラッグ等の操作をさせない
	    		this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow.setVisible(true);
	    		return;
	    	}
			
			int y = e.getY();
			
			ParamDefDisplay p = paramDefListPanel.getHandledObj(y);
			if (p == null) {
				return;
			}
			this.handledObj = p;
			this.handledObj.isDragging = true;
			this.handledObj.dragStartX = e.getX();
			this.handledObj.dragStartY = y;
			this.handledObj.dragX = this.handledObj.dragStartX;
			this.handledObj.dragY = this.handledObj.dragStartY;
			paramDefListPanel.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (this.handledObj == null) {
				return;
			}
			if (!this.handledObj.isDragging2 ||
					(this.handledObj.isDragging2 && this.handledObj.index == this.handledObj.indexToUpdate)) {
				this.handledObj.isDragging = false;
				this.handledObj.isDragging2 = false;
				paramDefListPanel.repaint();
				// あまり動かなかったドラッグの場合は、クリック扱いにする
				this.paramDefListPanel.editParamDefListWindow.createInputWindow(handledObj.para, false);
				return;
			}

			this.handledObj.updateIndex();
			this.handledObj = null;
			paramDefListPanel.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	public void subWindowDisposeNotify() {
		inputParamDefWindow = null;
		addButton.setEnabled(true);
	}

	private void createInputWindow(ParameterDefine para, boolean isNew) {
		HashSet<String> variableNameSet = new HashSet<String>();
		for (ParameterDefine p : params) {
			if (p != para) {
				variableNameSet.add(p.name);
			}
		}

    	if (inputParamDefWindow == null) {
			inputParamDefWindow = new EditParamDefWindow(this, para, params, variableNameSet, isNew);
    	}
		inputParamDefWindow.setVisible(true);
		addButton.setEnabled(false);
	}


	/**
	 * 追加ボタンを作る
	 *
	 * @param pane
	 */
	JButton generateAddButton() {
		JButton buttonCancel = new JButton(GuiPreference.ADD_BUTTON_STRING);
		buttonCancel.setFont(GuiPreference.ADD_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParameterDefine parameter = new ParameterDefine("", "", "0", ValueType.INTEGER,
						false, "", false, "", "");
				params.add(parameter);
				createInputWindow(parameter, true);
			}
		});

		return buttonCancel;
	}

	static public void main(String args[]) {
		ArrayList<ParameterDefine> params = new ArrayList<ParameterDefine>();

		// このmainは、このウィンドウだけを立ち上げて動作確認するためのもの。
		EditParamDefListWindow frame = new EditParamDefListWindow(params, null);
		frame.setVisible(true);
		frame.setLocation(900, 40);
		return;
	}
}
