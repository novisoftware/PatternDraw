package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.utils.GuiPreference;



/**
 * 端子の部分。 GUI用の部品オブジェクト。
 * このファイルは描画のみ実装する。
 * GraphNodeElement でデータ構造を実装している。
 *
 * @author user
 *
 */
public class P010___ConnectTerminal extends P002__AbstractIcon {
	static final int Y_INTERVAL = 24;
	static final int RENDER_HEIGHT= 10;
	static final int RENDER_WIDTH= 10;

	static private GuiUtil.StringWidthUtil strUtil = new GuiUtil.StringWidthUtil();

	private String paraName;
	private String paraDescription;
	public Value.ValueType valueType;
	private P021____AbstractGraphNodeElement node;
	private int index;
	private int nIndex;

	/**
	 * 入力された型が妥当な場合 true
	 * 入力された型が妥当でない場合 false
	 * を設定する。
	 * 初期値はtrue。エラーメッセージの代入と同時にfalseにする。
	 */
	public boolean isTypeChekResultValid = true;
	public String typeChekErrorMessage = null;

	public P010___ConnectTerminal(
			P021____AbstractGraphNodeElement abstractGraphNodeElement,
			String paraName,
			ValueType valueType,
			String paraDescription,
			int index, int indexNum) {
		this.node = abstractGraphNodeElement;
		this.valueType = valueType;
		this.paraName = paraName;
		this.paraDescription = paraDescription;
		this.index = index;
		this.nIndex = indexNum;
	}

	public P021____AbstractGraphNodeElement getNode() {
		return this.node;
	}

	public String getParaName() {
		return this.paraName;
	}

	/**
	 * 中心のX座標を取得します。
	 *
	 * @return 中心のX座標
	 */
	@Override
	public int getCenterX() {
		return node.x + RENDER_WIDTH / 2;
	}


	public int getTopY() {
		return node.y + (node.h - Y_INTERVAL * nIndex) / 2 + index * Y_INTERVAL +
				/* 注: Y_INTERVAL / 5 は、微調整で追加 */ + Y_INTERVAL / 5;
	}

	/**
	 * 中心のY座標を取得します。
	 *
	 * @return 中心のY座標
	 */
	@Override
	public int getCenterY() {
		return this.getTopY() + RENDER_HEIGHT / 2;
	}

	public boolean isTouched(int x, int y) {
		if ( node.x <= x && x <= node.x + RENDER_WIDTH
			&& this.getTopY() <= y && y <= this.getTopY() + RENDER_HEIGHT) {
			return true;
		}

		return false;
	}

	public void paint(Graphics2D g2, int phase) {
		if (phase == 1) {
			g2.setFont(GuiPreference.CONNECTOR_TEXT_FONT);
			g2.setColor(GuiPreference.CONNECTOR_FILL_COLOR);
			g2.fillOval(node.x, this.getTopY(), RENDER_WIDTH, RENDER_HEIGHT);
			g2.setColor(Color.BLACK);
			g2.drawString(paraName, node.x + RENDER_WIDTH   - strUtil.strWidth(paraName, g2),
					this.getTopY() - 2);
		}
		if (phase == 2) {
			if (this.isOnMouse()) {
				int arcWidth = 10;
				int arcHeight = 10;
				int width = 200;
				int height = 80;
				int drawX = node.x;
				int drawY = node.y + node.h;

				g2.setColor(GuiPreference.TIPS_WINDOW_BACKGROUND_COLOR);
				g2.setStroke(GuiPreference.TIPS_WINDOW_FRAME_STROKE);
				g2.fillRoundRect(
						drawX - 15,
						drawY + 5,
						width,
						height,
						arcWidth, arcHeight);
				g2.setColor(GuiPreference.TIPS_WINDOW_FRAME_COLOR);
				g2.drawRoundRect(
						drawX - 15,
						drawY + 5,
						width,
						height,
						arcWidth, arcHeight);

				g2.setFont(GuiPreference.TIPS_FONT);
				g2.setColor(GuiPreference.TIPS_TEXT_COLOR);

				g2.drawString(
						"" + paraName,
						node.x,
						this.node.y + this.node.h + 10 + 30
						);
				String s2 = String.format("%s (%s)",
						this.paraDescription,
						Value.valueTypeToDescString(this.valueType));
				g2.drawString(
						s2,
						node.x,
						this.node.y + this.node.h + 10 + 55
						);
			}
		}

	}
}
