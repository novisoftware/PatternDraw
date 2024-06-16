package com.github.novisoftware.patternDraw.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GuiPreference {
	/**
	 * GUIの文字表示で使いたいフォントのリスト。
	 * 見つかったものがあればそれを使用する。
	 */
	static String[] preferenceFontName_label = {
			"BIZ UDゴシック",
			"UD デジタル 教科書体 N-B",
			"メイリオ",
			"游ゴシック",
			"Takako",
			"Noto Sans CJK JP",
			"Takako"
	};


	static String[] preferenceFontName_in_icon = {
			"BIZ UDゴシック",
			"UD デジタル 教科書体 N-B",
			"メイリオ",
			"游ゴシック",
			"Takako",
			"Noto Sans CJK JP",
			"Takako"
	};


	static String[] preferenceFontName_in_group_id = {
			"Meiryo UI",
			"BIZ UDゴシック",
			"UD デジタル 教科書体 N-B",
			"メイリオ",
			"游ゴシック",
			"Takako",
			"Noto Sans CJK JP",
			"Takako"
	};

	static String[] preferenceFontName_message = {
			"BIZ UDゴシック",
			"BIZ UDPゴシック",
			"UD デジタル 教科書体 N-R",
			"游ゴシック",
			"Noto Sans CJK JP",
			"Takako"
	};


	static String[] preferenceFontName_input = {
		//	"Comic Shanns Regular",
		//	"Comic Sans MS",
		//	"UD デジタル 教科書体 N-R",
			"BIZ UDゴシック",
			"BIZ UDPゴシック",
			"游ゴシック",
			"Noto Sans CJK JP",
			"Takako"
	};

	/**
	 * GUIの文字表示に使用するフォント
	 */
	public static int LABEL_FONT_SIZE = 18;
	public static int TIPS_FONT_SIZE = 16;
	public static int MESSAGE_DISP_FONT_SIZE = 14;
	public static int OK_BUTTON_FONT_SIZE = 24;
	public static int CANCEL_BUTTON_FONT_SIZE = 20;
	public static int COSOLE_FONT_SIZE = 16;

	/**
	 * 連番PNGを生成する
	 */
	public static String OUTPUT_PNGS_BUTTON_STRING = "連番画像を出力";
	public static String RUN_BUTTON_STRING = "実行";
	public static String OK_BUTTON_STRING = "これに決める";
	public static String CANCEL_BUTTON_STRING = "やめる";
	public static String DELETE_BUTTON_STRING = "削除する";

	// JLabel用のフォント
	public static Font LABEL_FONT = initLabelFont(LABEL_FONT_SIZE);

	// TIPS表示用のフォント
	public static Font TIPS_FONT = initLabelFont(TIPS_FONT_SIZE);

	// JTextField用のフォント
	public static Font INPUT_FONT = initLabelFont(preferenceFontName_input, Font.BOLD, COSOLE_FONT_SIZE);

	// 入力項目のバリデーションへのメッセージ用
	public static Font MESSAGE_DISP_FONT = initLabelFont(preferenceFontName_message,  Font.PLAIN, MESSAGE_DISP_FONT_SIZE);
	public static Font OK_BUTTON_FONT = initLabelFont(OK_BUTTON_FONT_SIZE);
	public static Font CANCEL_BUTTON_FONT = initLabelFont(CANCEL_BUTTON_FONT_SIZE);
	public static Font CONSOLE_FONT = initLabelFont(COSOLE_FONT_SIZE);

	public static String ADD_BUTTON_STRING = "追加する";
	private static int ADD_BUTTON_FONT_SIZE = 20;
	public static Font ADD_BUTTON_FONT = initLabelFont(ADD_BUTTON_FONT_SIZE);



	/**
	 * GUIの文字表示に使用するフォントを探す処理。
	 *
	 * @return GUI表示に使用するフォント
	 */
	private static Font initLabelFont(String[] preferenceFontName, int style,int size) {
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
				return new Font(fontName,  style, size);
			}
		}
		return new Font(Font.SANS_SERIF, style, size);
	}

	private static Font initLabelFont(int size) {
		return initLabelFont(preferenceFontName_label, Font.BOLD, size);
	}



	/**
	 * GUIの背景色
	 */
	public static Color BG_COLOR = Color.WHITE;
	/**
	 * GUIのテキスト色
	 */
	public static Color TEXT_COLOR = new Color(20,20,20);

	/**
	 * ダイヤグラムエディタでのTIPS域の囲み枠の色
	 */
	public static Color TIPS_WINDOW_FRAME_COLOR = new Color(0.5f, 0.5f, 0.5f);

	/**
	 * ダイヤグラムエディタでのTIPS域の背景色
	 */
	public static Color TIPS_WINDOW_BACKGROUND_COLOR = new Color(1f, 1f, 1f, 0.8f);

	/**
	 * ダイヤグラムエディタでのTIPS域の囲み枠の角
	 */
	public static int TIPS_WINDOW_ARC_SIZE = 7;

	/**
	 * ダイヤグラムエディタでのTIPS域の囲み枠の太さ
	 */
	public static final BasicStroke TIPS_WINDOW_FRAME_STROKE = new BasicStroke(2f);


	/**
	 * ダイヤグラムエディタでのTIPS域の文字の色
	 */
	public static Color TIPS_TEXT_COLOR = new Color(0.2f, 0.2f, 0.2f);

	/**
	 * 妥当性エラーを表現するメッセージ色
	 */
	public static Color MESSAGE_ERROR_COLOR = new Color(0xF0, 0x20, 0x20);

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

	// 塗りつぶしの色の候補
	// public static final Color boldFgFillColor_for3 = new Color(0.8f, 0.8f, 1f);
	// public static final Color boldFgFillColor_for4 = new Color(1f, 0.8f, 0.8f);
	// public static final Color boldFgFillColor_for5 = new Color(0.7f, 0.9f, 0.8f);
	// public static final Color boldFgFillColor_dumb = new Color(0.75f, 0.75f, 0.75f);

	/**
	 * アイコン
	 */
	public static String ICON_IMAGE_PATH = "/resource/icon2.png";

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



	public final static  Color ICON_BORDER_COLOR = new Color( 0.3f, 0.3f, 0.3f );

	public final static  Color color = new Color( 1f, 0.8f, 0.8f );

	/**
	 * 端子の塗りつぶし色
	 */
	public final static Color CONNECTOR_FILL_COLOR = new Color( 0.5f, 0.5f, 0.5f );

	/**
	 * バックグラウンドの色
	 */
	public final static Color ICON_BACKGROUND_COLOR = new Color( 0.9f, 0.9f, 0.9f );

	public final static Font GROUP_ID_FONT = initLabelFont(preferenceFontName_in_group_id, Font.BOLD, 40);

	public final static Color COMMENT_BACKGROUND_COLOR = new Color( 0.5f, 0.5f, 0.5f );

	public final static Font COMMENT_FONT = initLabelFont(preferenceFontName_in_icon, Font.BOLD, 16);


	public final static Font CONNECTOR_TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

	/**
	 * アイコンの箱の中の文字
	 */
	public final static Font ICON_BOX_FONT = initLabelFont(preferenceFontName_in_icon, Font.BOLD, 13);

	public final static BasicStroke STROKE_PLAIN = new BasicStroke(1);

	public final static BasicStroke STROKE_BOLD = new BasicStroke(2);
}
