package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P001_IconGuiInterface;
import com.github.novisoftware.patternDraw.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;

public class EditDiagramPanel extends JPanel {
	/**
	 * 計算をするためのデータ構造。
	 */
	public NetworkDataModel networkDataModel;
	/**
	 * JPanel上にデバッグ情報を表示する
	 */
	public boolean isVisibleDebugInfo = false;

	/**
	 * サブウィンドウ(パラメーター定義の編集画面)
	 */
	public EditParamDefListWindow paramDefEditWindow = null;

	Font font1 = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

	// マウスドラッグ中に関係線を変更する場合、表示する
	P001_IconGuiInterface workLineFrom = null;
	int workLineX, workLineY;

	final MListener listener;

	EditDiagramPanel(String filename) {
		this.networkDataModel = new NetworkDataModel(this, filename);
		if (filename != null) {
			this.networkDataModel.load();
			this.networkDataModel.analyze();
		}

		this.listener = new MListener(this);
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
	}

	/**
	 * @return ドラッグ対象オブジェクト
	 */
	public P001_IconGuiInterface getHandledObject() {
		return this.listener.handled;
	}


	public P020___AbstractElement getElementIcon(String name) {
		for (P020___AbstractElement t : networkDataModel.getElements()) {
			if (t.id.equals(name)) {
				return t;
			}
		}
		return null;
	}

	public Set<String> getToneNames() {
		TreeSet<String> s = new TreeSet<>();

		for (P020___AbstractElement t : networkDataModel.getElements()) {
			s.add(t.id);
		}

		return s;
	}

	static Color workLineColor = new Color(0x70, 0x70, 0xFF);

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setFont(font);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// ドラッグドロップ中の作業線
		if (workLineFrom != null) {
			// g2.setColor(Color.BLUE);
			g2.setColor(workLineColor);
			g2.drawLine(workLineFrom.getCenterX(), workLineFrom.getCenterY(), workLineX, workLineY);
		}

		/**
		 * 配置されている部品を描画します。
		 * 関係線一式を先に描画し( phase == 0 )、その後、アイコン形状を描画します( phase == 2 )。
		 */
		g2.setColor(Color.GRAY);
		for (int phase = 0; phase < 2; phase++) {
			for (P020___AbstractElement t : networkDataModel.getElements()) {
				t.paintWithPhase(g2, phase);
			}
		}
	}

	P001_IconGuiInterface checkXY(int x, int y) {
		P001_IconGuiInterface hit = null;
		for (P020___AbstractElement t : networkDataModel.getElements()) {
			P001_IconGuiInterface check = t.getTouchedObject(x, y);
			if (check != null) {
				// System.out.println("h mouse(" + x + "," + y +") obj(" + t.x +
				// "," + t.y +")" );
				hit = check;

				// 描画順と検索順は同じ。
				// breakがあると、重なったときに「下側」のを拾うことになる。
				// break;
			}
		}

		return hit;
	}
}