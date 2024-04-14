package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.editor.util.IconImage;

public abstract class AbstractGraphNodeElement extends AbstractElement {
	/**
	 * グループの先頭の場合 グループID(整数)。
	 * グループの先頭でない場合はNULL。
	 */
	public Integer groupHead = null;

	public AbstractGraphNodeElement(EditPanel editPanel) {
		super(editPanel);
	}

	/**
	 * 計算結果
	 */
	public Value workValue;

	public ArrayList<String> optStr() {
		ArrayList<String> ret = new ArrayList<>();

		for (String s: paramMapInfo.keySet()) {
			ret.add(String.format("REF: %s %s %s", escape(id), s, paramMapInfo.get(s)));
		}
		return ret;
	}

	static HashMap<String, Value> variables = new  HashMap<String, Value>();
	static void resetVariables() {
		variables = new  HashMap<String, Value>();
	}
	public static void debugVariables() {
		for (String s : variables.keySet()) {
			Debug.println("variables", s + " -> " + variables.get(s) );
		}
	}

	// 外部パラメタパラメタ関連
	public HashMap<String,String> paramMapInfo;
	public HashMap<String,AbstractGraphNodeElement> paramMapObj;

	public ArrayList<GraphConnector> connectors;

	/**
	 * 構造化されているか（外部パラメタを持つか）。
	 *
	 * @return
	 */
	public boolean hasParameter() {
		return connectors.size() != 0;
	}


//	final Color colorBorder = new Color( 0.5f, 0.5f, 0.5f );
	final Color colorBorder = new Color( 0.3f, 0.3f, 0.3f );

	final Color color = new Color( 1f, 0.8f, 0.8f );
	final Color color2 = new Color( 1f, 0.9f, 0.9f );
	final Color grayColor = new Color( 0.9f, 0.9f, 0.9f );


	Font groupIdFont = new Font("Meiryo UI", Font.BOLD, 40);

	Font font1 = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	// Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
	Font font = new Font("Meiryo UI", Font.BOLD, 13);

	BasicStroke strokePlain = new BasicStroke(1);
	BasicStroke strokeBold = new BasicStroke(2);

	abstract String getRepresentExpression();

	/**
	 *
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
			g2.setStroke(strokeBold);
			for (GraphConnector connector : this.connectors) {
				AbstractGraphNodeElement src = e.paramMapObj.get(connector.getParaName());
				if (src != null) {
					if (Value.isAcceptable(src.getValueType(), connector.valueType) ) {
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
				g2.setFont(this.groupIdFont);
				g2.setColor(Color.GRAY);
				g2.drawString("" + this.groupHead, e.x - 30, e.y + 40);
			}
		}

		// 箱
		if (phase == 1) {
			g2.setFont(font);
			g2.setColor(Color.GRAY);
			String boxTitle = e.getKindString();
			if (e.getKindId() == KindId.CONSTANT) {
				// 整数とか浮動小数点とかの区別を表示する
				boxTitle += " - " + Value.valueTypeToDescString(this.getValueType());
			}
			g2.drawString(boxTitle, e.x + 30, e.y - 9);

			g2.setColor(color);
			if ( // t.getKindString().equals("入力")
				 e.getKindId() == KindId.INPUT
					) {
				g2.setColor(this.grayColor);
				g2.fillRect(e.x + 12, e.y, e.w, e.h);
				BufferedImage image = Common.getImage(IconImage.EDIT, this);
				g2.drawImage(image, e.x + 20, e.y + 5, null);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getOutputType(), e.x + 50, e.y + e.h * 2 / 3 + 5 );
			}
			else if ( e.getKindId() == KindId.DISPLAY // t.getKindString().equals("表示")
					) {
				g2.setColor(this.grayColor);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = Common.getImage(IconImage.DISPLAY, this);
				g2.drawImage(image, e.x + 40, e.y, null);
			}
			else if ( e.getKindId() == KindId.CONSTANT // t.getKindString().equals("定数")
					) {
				g2.setColor(this.grayColor);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getRepresentExpression()  //  t.getRpnString().replaceAll(";.*", "")
								, e.x + 30, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_SET
					// t.getKindString().equals("変数を設定")
					) {
				g2.setColor(this.grayColor);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = Common.getImage(IconImage.VAR_SET, this);
				g2.drawImage(image, e.x + 20, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 50, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_REFER
					// t.getKindString().equals("変数を参照")
					) {
				g2.setColor(this.grayColor);
				g2.fillRect(e.x + 16, e.y, e.w, e.h);
				BufferedImage image = Common.getImage(IconImage.VAR_REFER, this);
				g2.drawImage(image, e.x + 20, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 50, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.OPERATOR
					// t.getKindString().equals("演算子")
					) {
				g2.setColor(this.grayColor);
				g2.fillOval(e.x + 16, e.y, e.w, e.h);

				String printMark = e.getRepresentExpression();
				g2.setColor(Color.BLACK);
				g2.drawString(printMark, e.x + 30, e.y + e.h / 2 + 10);
			}
			else {
				// なにか追加した場合の暫定動作用

				int r = e.h * 2 / 3;

				g2.setStroke(strokeBold);
				g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, r, r);
				g2.setColor(colorBorder);
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
			g2.setStroke(strokePlain);
			for (GraphConnector connector : connectors) {
				connector.paint(g2, phase);
			}

			if (this.editPanel.isVisibleDebugInfo) {
				g2.setFont(font);
				g2.setColor(Color.RED);
				g2.drawString("" + e.id  /* e.getDebugIdString() */ + "  " + e.getValueType(), e.x + 30, e.y + 9);
			}
		}

	}


	@Override
	public IconGuiInterface getTouchedObject(int x, int y) {
		for (GraphConnector connector : connectors) {
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
