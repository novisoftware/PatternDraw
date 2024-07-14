package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.github.novisoftware.patternDraw.core.NetworkDataModel;
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
	public static final int WINDOW_WIDTH = 820;
	public static final int WINDOW_HEIGHT = 600;
	public static final int RULE_MARGIN = 9;
	static final BasicStroke CARET_STROKE = new BasicStroke(2f);
	static final BasicStroke RULE_STROKE_N = new BasicStroke(1f);
	static final BasicStroke RULE_STROKE_B = new BasicStroke(2f);
//	static final Color RULE_COLOR = new Color(100, 100, 100);
	static final Color RULE_COLOR_N = new Color(160, 160, 160);
	static final Color RULE_COLOR_B = new Color(100, 100, 100);
	static final Color ON_MOUSE_BACKGROUND_COLOR = new Color(240, 240, 240);
	static final Color ON_MOUSE_BACKGROUND_COLOR2 = new Color(0x80E0E0E0, true);

	final JPanel jp;
	final NetworkDataModel networkDataModel;

	/**
	 * パラメーター一覧
	 */
	public final ArrayList<ParameterDefine> params;

	/**
	 * 子ウィンドウ(個別パラメーターの設定画面)
	 */
	public EditParamDefWindow inputParamDefWindow = null;

	public ParamDefListPanel paramDefListPanel;
	
	public EditParamDefListWindow(
			NetworkDataModel networkDataModel,
			final ArrayList<ParameterDefine> params,
			Runnable closeCallback) {
		super();
		this.setTitle("パラメーターの一覧");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.networkDataModel = networkDataModel;
		this.params = params;

		// レイアウト
		jp = new JPanel();
		jp.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		jp.setBackground(GuiPreference.BG_COLOR);
		jp.setForeground(GuiPreference.TEXT_COLOR);

		JScrollPane scrollPane = new JScrollPane(jp);
		this.add(scrollPane);
		// Container pane = this.getContentPane();
		Container pane = jp;// .getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		// new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

		pane.add(boxSpacer(10, 7));
		pane.add(new JLabel2("パラメーターの一覧"));
		pane.add(boxSpacer(10, 7));

		this.paramDefListPanel = new ParamDefListPanel(this, params);
		/*
		Dimension panelDimension = new Dimension(WINDOW_WIDTH - 4, 400);
		paramDefListPanel.setMinimumSize(panelDimension);
		paramDefListPanel.setPreferredSize(panelDimension);
		paramDefListPanel.setSize(panelDimension);
		*/
		pane.add(paramDefListPanel);
		// pane.add(horizontalRule(7));

		// Container pane = this.getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));



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
			this.setBackground(GuiPreference.BG_COLOR);
			this.setForeground(GuiPreference.TEXT_COLOR);

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
		String[] appender = {
				"新しいパラメーターを追加"
		};
		public ParamDefDisplay onMouseObj;

		public void updateParamDefDisplays() {
			this.setPreferredSize(new Dimension(WINDOW_WIDTH - 40, Y_INTERVAL  * params.size() + 40));
			
			paramDefDisplay = new ArrayList<ParamDefDisplay>();
			paramDefDisplay.add(new ParamDefDisplay(this, title, -1, false));

			int index = 0;
			for (ParameterDefine p : params) {
				paramDefDisplay.add(new ParamDefDisplay(this, p, index));
				index ++;
			}

			paramDefDisplay.add(new ParamDefDisplay(this, appender, params.size(), true));
		}


		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.clearRect(0, 0, 1000, 1000);
			int y = 0;
			boolean isFirst = true;
			for (ParamDefDisplay p : paramDefDisplay) {
				y = y + 1  * Y_INTERVAL;
				p.x = 0;
				p.y = y;

				if (isFirst) {
					isFirst = false;
					y += RULE_MARGIN;
				}
			}

			ParamDefDisplay p0 = paramDefDisplay.get(0);
			int ADDER = 8;
			g2.setColor(RULE_COLOR_N);
			g2.setStroke(RULE_STROKE_N);
			g2.drawLine(5, p0.y - Y_INTERVAL  + ADDER, WINDOW_WIDTH - 30, p0.y - Y_INTERVAL + ADDER);
			g2.setColor(RULE_COLOR_B);
			g2.setStroke(RULE_STROKE_B);
			g2.drawLine(5, p0.y + ADDER , WINDOW_WIDTH - 30, p0.y + ADDER);

			ParamDefDisplay pN = paramDefDisplay.get(paramDefDisplay.size() - 1);
			g2.setColor(RULE_COLOR_N);
			g2.setStroke(RULE_STROKE_N);
			int yPos = pN.y + ADDER
					;
					// + Y_INTERVAL;
			/*
					// 0行の場合は空白を加算
					+ (p0 == pN ? Y_INTERVAL : 0);
		*/
					
			g2.drawLine(5, yPos, WINDOW_WIDTH - 30, yPos);

			
			for (ParamDefDisplay p : paramDefDisplay) {
				p.paint(g2, layoutInfo);
			}
		}
		
		public ParamDefDisplay getHandledObj(int y) {
			for (ParamDefDisplay p : this.paramDefDisplay) {
				if (p.paramDefListPanel == null) {
					// 見出し行は、インタラクションの対象にしない
					continue;
				}
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
		boolean isHeader = false;
		boolean isAppender = false;
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

		// 内容
		public ParamDefDisplay(ParamDefListPanel paramDefListPanel, ParameterDefine para, int index) {
			this.paramDefListPanel = paramDefListPanel;
			this.para = para;
			this.index = index;
			updateLabels();
		}

		// ヘッダー
		public ParamDefDisplay(ParamDefListPanel paramDefListPanel, String[] initArray, int index, boolean isAppender) {
			this.paramDefListPanel = paramDefListPanel;
			this.index = index;
			this.isHeader = ! isAppender;
			this.isAppender = isAppender;
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
			if (!this.isDragging && !this.isHeader) {
				if (this.paramDefListPanel.onMouseObj == this) {
					g2.setColor(ON_MOUSE_BACKGROUND_COLOR);
					g2.fillRoundRect(this.x,
							this.y - ParamDefListPanel.Y_INTERVAL + 9,
							WINDOW_WIDTH - 30,
							ParamDefListPanel.Y_INTERVAL - 5,
							4, 4);
				}
			}

			g2.setFont(GuiPreference.LABEL_FONT);
			if (this.isDragging) {
				g2.setColor(Color.GRAY);
			}
			else {
				g2.setColor(Color.BLACK);
			}

			{
				int x = 0;
				for (int i = 0 ; i < labels.size() ; i++ ) {
					x += layoutInfo.startPos.get(i);
					g2.drawString(labels.get(i), x, this.y);
				}
			}
			
			if (this.isDragging && (!this.isAppender) && (!this.isHeader)) {
				g2.setColor(ON_MOUSE_BACKGROUND_COLOR2);
				g2.fillRoundRect(this.x
						+ this.dragX - this.dragStartX,
						this.y - ParamDefListPanel.Y_INTERVAL + 9
						+ this.dragY - this.dragStartY,
						WINDOW_WIDTH - 30,
						ParamDefListPanel.Y_INTERVAL - 5,
						4, 4);
				
				g2.setColor(Color.BLACK);
				int x = 0;
				for (int i = 0 ; i < labels.size() ; i++ ) {
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
					this.isDragging2 = true;
					if (indexDiff != -1) {
						// 移動が 0 の場合と、移動が -1 の場合は実質的に順番の入れ替わりが発生しない。
						// このため「線」を描画しない。
						// 移動が -1 の場合は、途中でドラッグが発生したフラグを設定する
						// (クリック扱いから外す)

						if (indexDiff + this.index < -1) {
							indexDiff = - this.index - 1;
						}
						if (indexDiff + this.index > paramDefListPanel.params.size() - 1) {
							indexDiff = paramDefListPanel.params.size() - this.index - 1;
						}
	
						this.indexToUpdate = indexDiff + this.index;
	
						int newY = 6 + this.y + indexDiff * ParamDefListPanel.Y_INTERVAL;
						g2.setColor(Color.GRAY);
						g2.setStroke(CARET_STROKE);
						g2.drawLine(5, newY, WINDOW_WIDTH - 10, newY);
					}
				} else {
					this.indexToUpdate = this.index;
				}
				
			}
		}

		public boolean updateIndex() {
			if (! isDragging2) {
				// 注:
				// 実際にドラッグ操作のインタラクションをしていなかった場合は NOP で制御を戻す。
				return false;
			}
			
			if (this.indexToUpdate == this.index) {
				return false;
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
			
			return true;
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
			ParamDefDisplay pOld =  paramDefListPanel.onMouseObj;
			ParamDefDisplay p = paramDefListPanel.getHandledObj(e.getY());
			paramDefListPanel.onMouseObj = p;
			if (pOld != p) {
				paramDefListPanel.repaint();
			}
			// System.out.println("on mouse obj = "  +p);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow != null) {
				this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow.setVisible(true);
				return;
			}

			if (e.getButton() == MouseEvent.BUTTON1) {
				ParamDefDisplay p = paramDefListPanel.getHandledObj(e.getY());
				if (p == null) {
					return;
				}
				if (p.para != null) {
					this.paramDefListPanel.editParamDefListWindow.createInputWindow(p.para, false);
				} else {
					ParameterDefine parameter = createNewParameter();
					this.paramDefListPanel.editParamDefListWindow.params.add(parameter);
					this.paramDefListPanel.editParamDefListWindow.createInputWindow(
							parameter, true);
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow != null) {
				this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow.setVisible(true);
				return;
			}
			
			if (e.getButton() == MouseEvent.BUTTON1) {
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
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow != null) {
				this.paramDefListPanel.editParamDefListWindow.inputParamDefWindow.setVisible(true);
				return;
			}
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (this.handledObj == null) {
					return;
				}
				// if (!this.handledObj.isDragging2 ||
				//		(this.handledObj.isDragging2 && this.handledObj.index == this.handledObj.indexToUpdate)) {
				if (!this.handledObj.isDragging2) {
					// あまり動かなかったドラッグの場合は、クリック扱いにする
					ParameterDefine para = handledObj.para;
					this.handledObj.isDragging = false;
					this.handledObj.isDragging2 = false;
					this.handledObj = null;
					paramDefListPanel.repaint();
					if (para != null) {
						this.paramDefListPanel.editParamDefListWindow.createInputWindow(para, false);
					} else {
						ParameterDefine parameter = createNewParameter();
						this.paramDefListPanel.editParamDefListWindow.params.add(parameter);
						this.paramDefListPanel.editParamDefListWindow.createInputWindow(
								parameter, true);
					}
					return;
				}

				if (this.handledObj.updateIndex()) {
					paramDefListPanel.repaint();
				}
				else {
					this.handledObj.isDragging = false;
					this.handledObj.isDragging2 = false;
				}
				this.handledObj = null;
			}
		}

		static ParameterDefine createNewParameter() {
			return new ParameterDefine("", "", "0", ValueType.INTEGER,
					false, "", false, "", "");
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
	}

	public void createInputWindow(ParameterDefine para, boolean isNew) {
		HashSet<String> variableNameSet = new HashSet<String>();
		for (ParameterDefine p : params) {
			if (p != para) {
				variableNameSet.add(p.name);
			}
		}

    	if (inputParamDefWindow == null) {
			inputParamDefWindow = new EditParamDefWindow(this,
					this.networkDataModel,
					para,
					this.params,
					variableNameSet,
					isNew);
    	}
		inputParamDefWindow.setVisible(true);
	}
}
