package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Preference {
	/**
	 * GUIの文字表示で使いたいフォントのリスト。
	 * 見つかったものがあればそれを使用する。
	 */
	static String[] preferenceFontName = {
			"UD デジタル 教科書体 N-B",
//			"メイリオ",
			"游ゴシック"
	};



	/**
	 * GUIの文字表示に使用するフォント
	 */
	private static int LABEL_FONT_SIZE = 18;
	private static int OK_BUTTON_FONT_SIZE = 24;
	private static int CANCEL_BUTTON_FONT_SIZE = 20;

	public static Font LABEL_FONT = initLabelFont(LABEL_FONT_SIZE);
	public static Font OK_BUTTON_FONT = initLabelFont(OK_BUTTON_FONT_SIZE);
	public static Font CANCEL_BUTTON_FONT = initLabelFont(CANCEL_BUTTON_FONT_SIZE);

	/**
	 * GUIの文字表示に使用するフォントを探す処理。
	 *
	 * @return GUI表示に使用するフォント
	 */
	private static Font initLabelFont(int size) {
		// 使用可能なフォント一覧
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		HashSet<String> avalrableFonts = new HashSet<String>();
		for (String aFontName : ge.getAvailableFontFamilyNames()) {
			avalrableFonts.add(aFontName);

			// 環境で使用可能なフォントの一覧を見たい場合
			// System.out.println(aFontName);
		}

		for (String fontName : preferenceFontName) {
			if (avalrableFonts.contains(fontName)) {
				return new Font(fontName,  Font.BOLD, size);
			}
		}
		return new Font(Font.SANS_SERIF, Font.BOLD, size);
	}

	/**
	 * GUIの背景色
	 */
	public static Color BG_COLOR = Color.WHITE;
	/**
	 * GUIのテキスト色
	 */
	public static Color TEXT_COLOR = new Color(20,20,20);


	// 以降は図形描画のパラメーター
	/**
	 * 細い描画線
	 */
	public static final Color thinFgColor = new Color(0f, 0f, 0f);
	public static final BasicStroke thinStroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	/**
	 * 太い描画線
	 */
	public static final BasicStroke boldStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final Color boldFgStrokeColor = new Color(0.4f, 0.4f, 0.4f);
	/**
	 * 三角形の塗りつぶし色
	 */
	// public static final Color boldFgFillColor_for3 = new Color(0.8f, 0.8f, 1f);
	/**
	 * 四角形の塗りつぶし色
	 */
	// public static final Color boldFgFillColor_for4 = new Color(1f, 0.8f, 0.8f);
	/**
	 * 五角形の塗りつぶし色
	 */
	// public static final Color boldFgFillColor_for5 = new Color(0.7f, 0.9f, 0.8f);
	/**
	 * 該当しない場合の塗りつぶし色
	 */
	// public static final Color boldFgFillColor_dumb = new Color(0.75f, 0.75f, 0.75f);

	/**
	 * アイコン
	 */
	public static String ICON_IMAGE_PATH = "/com/github/novisoftware/patternDraw/gui/resource/icon2.png";

	/**
	 * GUI 部品Look And Feel を設定する
	 *
	 * @param frame
	 */
	public static void setLookAndFeel(JFrame frame) {
		String tryLaf[] = {
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
				"com.sun.java.swing.plaf.motif.MotifLookAndFeel"
		};

		for (String lookAndFeel: tryLaf) {
			try {
				UIManager.setLookAndFeel(lookAndFeel);
				SwingUtilities.updateComponentTreeUI(frame);
				break;
			} catch(Exception e){
				// 処理不要
			}
		}
	}
}
