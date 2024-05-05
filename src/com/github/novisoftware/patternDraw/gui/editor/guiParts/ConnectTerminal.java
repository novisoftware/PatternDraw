package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;



/**
 * 端子の部分。 GUI用の部品オブジェクト。
 * このファイルは描画のみ実装する。
 * GraphNodeElement でデータ構造を実装している。
 *
 * @author user
 *
 */
public class ConnectTerminal implements IconGuiInterface {
	static final int Y_INTERVAL = 24;
	static final int RENDER_HEIGHT= 10;
	static final int RENDER_WIDTH= 10;

	static private GuiUtil.StringWidthUtil strUtil = new GuiUtil.StringWidthUtil();

	private String paraName;
	private String paraDescription;
	public Value.ValueType valueType;
	private AbstractGraphNodeElement node;
	private int index;
	private int nIndex;

	public ConnectTerminal(AbstractGraphNodeElement abstractGraphNodeElement,
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

	public AbstractGraphNodeElement getNode() {
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
		if ( node.x < x && x < node.x + RENDER_WIDTH
			&& this.getTopY() < y && y < this.getTopY() + RENDER_HEIGHT) {
			return true;
		}

		return false;
	}

	public void paint(Graphics2D g2, int phase) {
		if (phase == 1) {
			g2.setFont(Preference.CONNECTOR_TEXT_FONT);
			g2.setColor(Preference.CONNECTOR_FILL_COLOR);
			g2.fillOval(node.x, this.getTopY(), RENDER_WIDTH, RENDER_HEIGHT);
			g2.setColor(Color.BLACK);
			g2.drawString(paraName, node.x + RENDER_WIDTH   - strUtil.strWidth(paraName, g2),
					this.getTopY() - 2);
		}
		if (phase == 1) {
			if (this.isOnMouse) {
				g2.setFont(Preference.LABEL_FONT);
				g2.setColor(Preference.TIPS_TEXT_COLOR);

				g2.drawString(
						"" + paraName,
						node.x,
						this.node.y + this.node.h + 10 + 30
						);
				g2.drawString(
						"" + this.paraDescription + " (" + this.valueType + ")",
						node.x,
						this.node.y + this.node.h + 10 + 55
						);
			}
		}

	}

	boolean isOnMouse = false;

	@Override
	public void setOnMouse(boolean b) {
		this.isOnMouse = b;
	}
}
