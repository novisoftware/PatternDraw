package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.IconImage;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;

public abstract class AbstractGraphNodeElement extends AbstractElement {
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
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)
	 */
	public HashMap<String, String> paramMapInfo;

	/**
	 * 外部パラメタ関連 (コネクタと他の箱を結ぶ線で表現される)
	 */
	public HashMap<String, AbstractGraphNodeElement> paramMapObj;

	/**
	 * コネクタ(端子)のオブジェクト
	 */
	public ArrayList<ConnectTerminal> connectors;

	/**
	 * コンストラクタ
	 *
	 * @param editPanel
	 */
	public AbstractGraphNodeElement(EditDiagramPanel editPanel) {
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

	abstract String getRepresentExpression();

	/**
	 * 計算する(workValueに計算結果が格納された状態にする)。
	 */
	public abstract void evaluate();

	/**
	 * 描画用メソッドは段階に分けて呼び出されます(引数: phase)。
	 */
	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		AbstractGraphNodeElement e = this;

		// 結線
		if (phase == 0) {
			// 「端子」と結線されるよう表現する
			g2.setStroke(Preference.STROKE_BOLD);
			for (ConnectTerminal connector : this.connectors) {
				AbstractGraphNodeElement src = e.paramMapObj.get(connector.getParaName());
				if (src != null) {
					ValueType valueType = src.getValueType();
					// RPNノードで、変数の値の場合は、変数・パラメタ定義から ValueType を取得する
					// 取得できたら上書きする
					if (src instanceof RpnGraphNodeElement) {
						RpnGraphNodeElement r = (RpnGraphNodeElement)src;
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
					g2.drawLine(connector.getCenterX(), connector.getCenterY(), src.getCenterX(), src.getCenterY());
				}
			}

			if (this.groupHead != null) {
				g2.setFont(Preference.GROUP_ID_FONT);
				g2.setColor(Color.GRAY);
				g2.drawString("" + this.groupHead, e.x - 30, e.y + 40);
			}
		}

		// 箱
		if (phase == 1) {
			g2.setFont(Preference.ICON_BOX_FONT);
			g2.setColor(Color.GRAY);
			String boxTitle = e.getKindString();
			if (e.getKindId() == KindId.CONSTANT) {
				// 整数とか浮動小数点とかの区別を表示する
				boxTitle += " - " + Value.valueTypeToDescString(this.getValueType());
			}
			g2.drawString(boxTitle, e.x + 30, e.y - 9);

			g2.setColor(Preference.color);
			if ( // t.getKindString().equals("入力")
				 e.getKindId() == KindId.INPUT
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRect(e.x + 12, e.y, e.w, e.h);
				BufferedImage image = GuiUtil.getImage(IconImage.EDIT, this);
				g2.drawImage(image, e.x + 20, e.y + 5, null);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getOutputType(), e.x + 50, e.y + e.h * 2 / 3 + 5 );
			}
			else if ( e.getKindId() == KindId.DISPLAY // t.getKindString().equals("表示")
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = GuiUtil.getImage(IconImage.DISPLAY, this);
				g2.drawImage(image, e.x + 40, e.y, null);
			}
			else if ( e.getKindId() == KindId.CONSTANT // t.getKindString().equals("定数")
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getRepresentExpression()  //  t.getRpnString().replaceAll(";.*", "")
								, e.x + 30, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_SET
					// t.getKindString().equals("変数を設定")
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_SET, this);
				g2.drawImage(image, e.x + 20, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 50, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_REFER
					// t.getKindString().equals("変数を参照")
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_REFER, this);
				g2.drawImage(image, e.x + 20, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 50, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.OPERATOR
					// t.getKindString().equals("演算子")
					) {
				g2.setColor(Preference.ICON_BACKGROUND_COLOR);
				g2.fillOval(e.x + 16, e.y, e.w, e.h);

				String printMark = e.getRepresentExpression();
				g2.setColor(Color.BLACK);
				g2.drawString(printMark, e.x + 30, e.y + e.h / 2 + 10);
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

			// 端子
			g2.setStroke(Preference.STROKE_PLAIN);
			for (ConnectTerminal connector : connectors) {
				connector.paint(g2, phase);
			}

			if (this.editPanel.isVisibleDebugInfo) {
				g2.setFont(Preference.ICON_BOX_FONT);
				g2.setColor(Color.RED);
				g2.drawString("" + e.id  /* e.getDebugIdString() */ + "  " + e.getValueType(), e.x + 30, e.y + 9);
			}
		}

	}

	@Override
	public IconGuiInterface getTouchedObject(int x, int y) {
		for (ConnectTerminal connector : connectors) {
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
