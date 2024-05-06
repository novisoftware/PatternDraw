package com.github.novisoftware.patternDraw.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.P001_IconGuiInterface;

public class GuiUtil {
	public static final String FRAME_TITLE_BASE = "pattern drawing: ";

	public static final String RESOURCE_FRAME_ICON = "/iconimage/icon2.png";
//	public static final String RESOURCE_FRAME_ICON = "/iconimage/app_icon.png";

	public static final ImageIcon iconChecked = createCheckedMark(true);
	public static final ImageIcon iconUnchecked = createCheckedMark(false);

	/*
	public static void setIconImage(JFrame frame) {
		// Windows向けにウィンドウタイトルバーのアイコンイメージを設定する。
		// （Macでは表示されない）
		try {
			frame.setIconImage(ImageIO.read(frame.getClass().getResource(
					GuiUtil.RESOURCE_FRAME_ICON)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

	static private HashMap<String, BufferedImage> cache = new HashMap<>();

	public static BufferedImage getImage(String s, JFrame frame) {
		BufferedImage r = cache.get(s);
		if (r != null) {
			return r;
		}

		try {
			r = ImageIO.read(frame.getClass().getResource(s));
			cache.put(s, r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return r;
	}

	public static BufferedImage getImage(String s, P001_IconGuiInterface frame) {
		BufferedImage r = cache.get(s);
		if (r != null) {
			return r;
		}

		System.err.println("icon resource = " + s);

		try {
			r = ImageIO.read(frame.getClass().getResource(s));
			cache.put(s, r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return r;
	}

	/**
	 * JMenuItemコンストラクタのラッパー。 marked の真偽により丸印 ● の有無を描画し分ける。
	 *
	 * @param label
	 * @param marked
	 * @return
	 */
	public static JMenuItem createMenuItem(String label, boolean marked) {
		if (marked) {
			JMenuItem item = new JMenuItem(label, IconMark(marked));
			item.setVerticalTextPosition(JMenuItem.CENTER);

			return item;
		}
		else {
			return new JMenuItem(label);
		}
	}

	public static ImageIcon IconMark(boolean marked) {
		return marked ? GuiUtil.iconChecked : GuiUtil.iconUnchecked;
	}

	/**
	 * 単なる丸印を描画する
	 *
	 * @param marked
	 * @return
	 */
	private static ImageIcon createCheckedMark(boolean marked) {
		final int h = 16;
		// final int h = 22;
		final int w = 20;
		final int r = 5;
		// final int r = 13;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (marked) {
			// デバッグ時に使用。
			// g2.setColor(Color.WHITE);
			// g2.fillRect(0, 0,  w-1,  h-1);
			g2.setColor(Color.BLACK);
			g2.fillOval(w/2 - r, h/2 -r, r*2, r*2);
		}

		return new ImageIcon( image );
	}

	/**
	 * 文字列を' 'で区切って連結する
	 *
	 * @param s
	 * @return
	 */
	public static String strJoin(ArrayList<String> s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = s.size() ; i < n ; i++ ) {
			if (i != 0) {
				sb.append(' ');
			}
			sb.append(s.get(i));
		}

		return sb.toString();
	}

	public static class StringWidthUtil {
		private HashMap<String, Integer> widthCache = new HashMap<>();

		/**
		 * 文字の横幅(ドット数)を求め、キャッシュします。
		 * 基準にするフォントは引数 g2 に設定してある必要があります。
		 *
		 * @param s
		 * @param g2
		 * @return
		 */
		public int strWidth(String s, Graphics2D g2) {
			Integer w = widthCache.get(s);
			if (w != null) {
				return w;
			}

			FontMetrics fm = g2.getFontMetrics();
			Rectangle rect = fm.getStringBounds(s, g2).getBounds();
			widthCache.put(s, rect.width);
			return rect.width;
		}
	}

	public static class StringRectUtil {
		private HashMap<String, Rectangle> rectCache = new HashMap<>();

		/**
		 * 文字の横幅・高さ(ドット数)を求め、キャッシュします。
		 * 基準にするフォントは引数 g2 に設定してある必要があります。
		 *
		 * @param s
		 * @param g2
		 * @return
		 */
		public Rectangle str2rect(String s, Graphics2D g2) {
			Rectangle w = rectCache.get(s);
			if (w != null) {
				return w;
			}

			FontMetrics fm = g2.getFontMetrics();
			Rectangle rect = fm.getStringBounds(s, g2).getBounds();
			rectCache.put(s, rect);
			return rect;
		}
	}

	public static String concatStrings(ArrayList<String> list) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0 ; i < list.size(); i++) {
			if (i != 0) {
				sb.append(' ');
			}

			sb.append(list.get(i));
		}

		return sb.toString();
	}

	public static String trim(String src) {
		return src.replaceFirst("^ +", "").replaceFirst(" +$", "");
	}

	/**
	 * 重複しない名前を生成する。
	 *
	 * @param set
	 * @param base
	 * @return
	 */
	public static String generateUniqName(List<String> set, String base) {
		// やっつけ仕事
		for (char c = 'a' ; c <= 'z' ; c ++) {
			if (! set.contains(base + c)) {
				return base + c;
			}
		}

		return null;
	}

}
