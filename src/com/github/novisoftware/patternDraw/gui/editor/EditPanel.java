package com.github.novisoftware.patternDraw.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.gui.editor.EditFrame.MListener;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;

public class EditPanel extends JPanel {
	/**
	 * JPanel上にデバッグ情報を表示する
	 */
	public boolean isVisibleDebugInfo = true;

	public NetworkDataModel networkDataModel;

	Font font1 = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

	// マウスドラッグ中に、関係線を変更する場合、表示する
	IconGuiInterface workLineFrom = null;
	int workLineX, workLineY;

	public ElementIcon getElementIcon(String name) {
		for (ElementIcon t : networkDataModel.getElements()) {
			if (t.id.equals(name)) {
				return t;
			}
		}
		return null;
	}

	public Set<String> getToneNames() {
		TreeSet<String> s = new TreeSet<>();

		for (ElementIcon t : networkDataModel.getElements()) {
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
		 * 配置されている部品を描画します。 関係線一式を先に描画し、その後、アイコン形状を描画します。
		 */
		g2.setColor(Color.GRAY);
		for (int phase = 0; phase < 2; phase++) {
			for (ElementIcon t : networkDataModel.getElements()) {
				t.paintWithPhase(g2, phase);
			}
		}
	}

	IconGuiInterface checkXY(int x, int y) {
		IconGuiInterface hit = null;
		for (ElementIcon t : networkDataModel.getElements()) {
			IconGuiInterface check = t.getTouchedObject(x, y);
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

	EditPanel(String filename) {
		this.networkDataModel = new NetworkDataModel(this, filename);
		this.networkDataModel.load();
		this.networkDataModel.evaluate();

		MListener listener = new MListener(this);
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
	}
}