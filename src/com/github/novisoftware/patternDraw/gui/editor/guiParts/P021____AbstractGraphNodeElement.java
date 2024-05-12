package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.core.CaliculateException;
import com.github.novisoftware.patternDraw.gui.editor.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.IconImage;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.GuiUtil.StringRectUtil;
import com.github.novisoftware.patternDraw.utils.GuiUtil.StringWidthUtil;
import com.github.novisoftware.patternDraw.utils.Preference;

public abstract class P021____AbstractGraphNodeElement extends P020___AbstractElement {
	/**
	 * 単連結グループの先頭の場合 グループID(整数)。
	 * 単連結グループの先頭でない場合はNULL。
	 */
	public Integer groupHead = null;

	/**
	 * 計算結果
	 */
	public Value workValue;

	/**
	 * 計算にエラーがあったか
	 */
	public boolean isError;

	/**
	 * 計算のエラー内容
	 */
	public String errorMessage;

	/**
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)
	 */
	public HashMap<String, String> paramMapInfo;

	/**
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)
	 */
	public HashMap<String, P021____AbstractGraphNodeElement> paramMapObj;

	/**
	 * コネクタ(端子)のオブジェクト
	 */
	public ArrayList<P010___ConnectTerminal> connectors;

	static private GuiUtil.StringWidthUtil strUtil = new GuiUtil.StringWidthUtil();

	/**
	 * コンストラクタ
	 *
	 * @param editPanel
	 */
	public P021____AbstractGraphNodeElement(EditDiagramPanel editPanel) {
		super(editPanel);
	}

	public ArrayList<String> optStr() {
		ArrayList<String> ret = new ArrayList<>();

		for (String s: paramMapInfo.keySet()) {
			ret.add(String.format("REF: %s %s %s", escape(id), escape(s), escape(paramMapInfo.get(s))));
		}
		return ret;
	}

	/**
	 * 構造化されているか（外部パラメタを持つか）。
	 *
	 * @return
	 */
	public boolean hasParameter() {
		return connectors.size() != 0;
	}

	/**
	 * アイコン等での表示用の文字列を取得する。
	 * @return アイコン等での表示用の文字列(ユーザーに見せるための文字列)
	 */
	abstract String getRepresentExpression();

	/**
	 * 計算する(workValueに計算結果が格納された状態にする)。
	 * @throws CaliculateException
	 */
	public abstract void evaluate() throws CaliculateException;


	static StringRectUtil str2rect = new StringRectUtil();

	/**
	 * 描画用メソッドは段階に分けて呼び出されます(引数: phase)。
	 */
	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		P021____AbstractGraphNodeElement e = this;

		// 結線
		if (phase == 0) {
			// 「端子」と結線されるよう表現する
			g2.setStroke(Preference.STROKE_BOLD);
			for (P010___ConnectTerminal connector : this.connectors) {
				P021____AbstractGraphNodeElement src = e.paramMapObj.get(connector.getParaName());
				if (src != null) {
					ValueType valueType = src.getValueType();
					// RPNノードで、変数の値の場合は、変数・パラメタ定義から ValueType を取得する
					// 取得できたら上書きする
					if (src instanceof P022_____RpnGraphNodeElement) {
						P022_____RpnGraphNodeElement r = (P022_____RpnGraphNodeElement)src;
						ArrayList<ParameterDefine> params = this.editPanel.networkDataModel.paramDefList;
						ValueType work = r.getRpn().getValueType(this.editPanel.networkDataModel.variables, params);
						if (! work.equals(ValueType.NONE)) {
							valueType = work;
						}
					}

					if (Value.isAcceptable(valueType, connector.valueType) ) {
						g2.setColor(Color.GRAY);
					}
					else {
						g2.setColor(Color.RED);

						Debug.println("PAINT", "invalid type src:" + src.getValueType()  + " receive:" + connector.valueType);
					}
					// 線分描画
					// 直線
					// g2.drawLine(connector.getCenterX(), connector.getCenterY(), src.getCenterX(), src.getCenterY());

					// double x0 = src.getCenterX() ;
					double x0 = src.getConnectOutputX();
					double y0 = src.getCenterY();
					double x2 = connector.getCenterX();
					double y2 = connector.getCenterY();

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

			// 単連結グラフごとに、実行順を数字で表示する
			if (this.groupHead != null) {
				g2.setFont(Preference.GROUP_ID_FONT);
				g2.setColor(Color.GRAY);

				int strWidth = strUtil.strWidth("" + this.groupHead, g2);
				g2.drawString("" + this.groupHead, e.x - strWidth, e.y + 30);
			}

		}

		int arcWidth = 10;
		int arcHeight = 10;

		// 箱
		if (phase == 1) {
			g2.setFont(Preference.ICON_BOX_FONT);
			if (! (e.getKindId() == KindId.COMMENT)) {
				g2.setColor(Color.GRAY);
				String boxTitle = e.getKindString();
				if (e.getKindId() == KindId.CONSTANT) {
					// 整数とか浮動小数点とかの区別を表示する
					boxTitle += " - " + Value.valueTypeToDescString(this.getValueType());
				}
				g2.drawString(boxTitle, e.x + 30, e.y - 9);
			}

			g2.setColor(Preference.color);
			if (e.getKindId() == KindId.INPUT) {
				// インタラクティブな入力
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, arcWidth, arcHeight);

				BufferedImage image = GuiUtil.getImage(IconImage.EDIT, this);
				g2.drawImage(image, e.x + 20, e.y + 5, null);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getOutputType(), e.x + 50, e.y + e.h * 2 / 3 + 5 );
			}
			else if (e.getKindId() == KindId.DISPLAY) {
				// 表示
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);

				BufferedImage image = GuiUtil.getImage(IconImage.DISPLAY, this);
				g2.drawImage(image, e.x + 40, e.y, null);
			}
			else if (e.getKindId() == KindId.CONSTANT ) {
				// 定数
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getRepresentExpression()  //  t.getRpnString().replaceAll(";.*", "")
								, e.x + 30, e.y + e.h / 2 + 10);
			}
			else if (e.getKindId() == KindId.COMMENT) {
				// コメント
				if (this.isOnMouse()) {
					g2.setColor(Preference.ICON_BACKGROUND_COLOR);
					g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, arcWidth, arcHeight);
				}

				g2.setColor(Preference.COMMENT_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y + e.h -3, e.w, 3);

				g2.setColor(Color.BLACK);
				g2.setFont(Preference.COMMENT_FONT);
				g2.drawString(e.getRepresentExpression()  //  t.getRpnString().replaceAll(";.*", "")
								, e.x + 30, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_SET ) {
				// 変数を設定
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_SET, this);
				g2.drawImage(image, e.x + 26, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 60, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_REFER ) {
				// 変数を参照
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_REFER, this);
				g2.drawImage(image, e.x + 26, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 60, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.OPERATOR) {
				// 演算子
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillOval(e.x + 16, e.y, e.w, e.h);

				String printMark = e.getRepresentExpression();
				g2.setColor(Color.BLACK);
				Rectangle rect = str2rect.str2rect(printMark, g2);

				g2.drawString(printMark, e.x  + 16 + (e.w - rect.width)/2, e.y + (e.h + rect.height)/ 2 - 4);


			}
			else {
				// なにか追加した場合の暫定動作用

				int r = e.h * 2 / 3;

				g2.setStroke(Preference.STROKE_BOLD);
				g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, r, r);
				g2.setColor(Preference.ICON_BORDER_COLOR);
//				g2.drawOval(t.x, t.y, t.w, t.h);
				g2.drawRoundRect(e.x + 12, e.y, e.w, e.h, r, r);

				g2.setColor(Color.BLACK);
				if (this.getKindString().equals("暫定動作（開発追加）")) {
					g2.drawString(e.id, e.x + 20, e.y + e.h / 2 - 10);
					g2.drawString(e.getRepresentExpression(), e.x + 30, e.y + e.h / 2 + 10);
				}
				else {
					g2.drawString(e.id, e.x + 18, e.y + e.h / 2);
				}
			}

			// コネクタ用の端子
			g2.setStroke(Preference.STROKE_PLAIN);
			for (P010___ConnectTerminal connector : connectors) {
				connector.paint(g2, phase);
			}

			if (this.editPanel.isVisibleDebugInfo) {
				g2.setFont(Preference.ICON_BOX_FONT);
				g2.setColor(Color.RED);
				g2.drawString("" + e.id  /* e.getDebugIdString() */ + "  " + e.getValueType(), e.x + 30, e.y + 9);
			}

			if (this.isError) {
				g2.setFont(Preference.ICON_BOX_FONT);
				BufferedImage image = GuiUtil.getImage2(IconImage.MESSAGE, this);
				g2.drawImage(image, e.x + 40, e.y - 60, null);
				g2.setColor(Color.BLACK);
				g2.drawString("計算エラー", e.x + 50, e.y - 35);
				g2.drawString(this.errorMessage, e.x + 50, e.y - 18);
			}
		}
	}

	@Override
	public P001_IconGuiInterface getTouchedObject(int x, int y) {
		for (P010___ConnectTerminal connector : connectors) {
			if (connector.isTouched(x, y)) {
				return connector;
			}
		}

		if (
				1.0f * (this.x + this.w/2 - x)*(this.x + this.w/2 - x) * this.h*this.h
				+ 1.0f * (this.y + this.h/2  -y)*(this.y + this.h/2 - y) * this.w*this.w
				<
				1.0f * this.h*this.h*this.w*this.w/4
				) {
			return this;
		}


		return null;
	}

}
