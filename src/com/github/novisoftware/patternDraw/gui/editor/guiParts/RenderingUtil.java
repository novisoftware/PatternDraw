package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;

import com.github.novisoftware.patternDraw.utils.GuiPreference;

public class RenderingUtil {
	static public void drawTipsWindowFrame(Graphics2D g2, int drawX, int drawY, int width, int height) {
		int arcWidth = 10;
		int arcHeight = 10;

		g2.setColor(GuiPreference.TIPS_WINDOW_BACKGROUND_COLOR);
		g2.setStroke(GuiPreference.TIPS_WINDOW_FRAME_STROKE);
		g2.fillRoundRect(
				drawX,
				drawY,
				width,
				height,
				arcWidth, arcHeight);
		g2.setColor(GuiPreference.TIPS_WINDOW_FRAME_COLOR);
		g2.drawRoundRect(
				drawX,
				drawY,
				width,
				height,
				arcWidth, arcHeight);
	}

	static class WidthCache {
		Integer width;
	}

	static public void drawTipsWindow(Graphics2D g2, WidthCache wc, int drawX, int drawY, String[] msg) {
		g2.setFont(GuiPreference.TIPS_FONT);
		/*
		if (wc.width == null) {
			FontMetrics fm = g2.getFontMetrics();

			int widthMax = 0;
			for (String s: msg) {
				Rectangle rect = fm.getStringBounds(s, g2).getBounds();
				if (rect.width > widthMax) {
					widthMax = rect.width;
				}
			}

			wc.width = widthMax;
		}

		int width = wc.width;
		*/
		FontMetrics fm = g2.getFontMetrics();

		int widthMax = 0;
		for (String s: msg) {
			Rectangle rect = fm.getStringBounds(s, g2).getBounds();
			if (rect.width > widthMax) {
				widthMax = rect.width;
			}
		}
		int width = widthMax;
		// ↑
		// 注:
		// キャッシュしたほうが良いと思っていたけれど、
		// 表示内容更新時にキャッシュの破棄を入れるのが複雑になりそう。
		// 一旦キャッシュは見合わせ。

		int height = (msg.length) * (GuiPreference.TIPS_FONT_SIZE + 8);
		int xMargin = 20;

		drawTipsWindowFrame(g2, drawX - xMargin, drawY + 10, width + xMargin*2, height + 20);

		g2.setColor(GuiPreference.TIPS_TEXT_COLOR);
		int adder = 0;
		for (String d: msg) {
			g2.drawString(
					d,
					drawX,
					drawY + 10 + 30 + adder
					);
			adder += GuiPreference.TIPS_FONT_SIZE + 8;
		}
	}


	/**
	 * 端子への線を描画します。
	 *
	 * @param g2
	 * @param x0
	 * @param y0
	 * @param x2
	 * @param y2
	 */
	static public void drawConnectorStroke(Graphics2D g2, double x0, double y0, double x2, double y2) {
		// X_RATIO, Y_RATIO は3次元ベジエ曲線のパラメーター作成用のパラメーター。
		double X_RATIO = 55;
		double Y_RATIO = 95;
		double x1a = (x0 * X_RATIO + x2 * (100-X_RATIO)) / 100.0;
		double y1a = (y0 * Y_RATIO + y2 * (100-Y_RATIO)) / 100.0;
		double x1b = (x0 * (100-X_RATIO) + x2 * X_RATIO) / 100.0;
		double y1b = (y0 * (100-Y_RATIO) + y2 * Y_RATIO) / 100.0;

		CubicCurve2D.Double curve1 = new CubicCurve2D.Double(
				x0,y0,x1a,y1a,x1b,y1b,x2,y2);
		g2.draw(curve1);
	}

}
