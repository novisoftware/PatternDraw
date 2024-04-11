package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;



/**
 * 端子の部分。 GUI用の部品オブジェクト。
 * このファイルは描画のみ実装する。
 * GraphNodeElement でデータ構造を実装している。
 *
 * @author user
 *
 */
public class GraphConnector implements IconGuiInterface {
	private String paraName;
	private String paraDescription;
	public Value.ValueType valueType;
	private AbstractGraphNodeElement node;
	private int index;

	static final int Y_INTERVAL = 24;
	static final int RENDER_HEIGHT= 10;
	static final int RENDER_WIDTH= 10;

	static private Common.StringWidthUtil strUtil = new Common.StringWidthUtil();

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


	/**
	 * 中心のY座標を取得します。
	 *
	 * @return 中心のY座標
	 */
	@Override
	public int getCenterY() {
		return node.y + index*Y_INTERVAL + RENDER_HEIGHT / 2;
	}

	public boolean isTouched(int x, int y) {
		if ( node.x < x && x < node.x + RENDER_WIDTH
			&& node.y + index * Y_INTERVAL  < y && y < node.y + index * Y_INTERVAL + RENDER_HEIGHT) {
			return true;
		}

		return false;
	}

	public void paint(Graphics2D g2, int phase) {
		if (phase == 1) {
			g2.setFont(node.font1);
			g2.setColor(node.color2);
			g2.fillRect(node.x, node.y + index * Y_INTERVAL, RENDER_WIDTH, RENDER_HEIGHT);
			g2.setColor(Color.BLACK);
			g2.drawString(paraName, node.x + RENDER_WIDTH   - strUtil.strWidth(paraName, g2), node.y + index * Y_INTERVAL - 2);
			g2.drawRect(node.x, node.y +index * Y_INTERVAL, RENDER_WIDTH, RENDER_HEIGHT);
		}
	}

	public GraphConnector(AbstractGraphNodeElement abstractGraphNodeElement,
			String paraName,
			ValueType valueType,
			String paraDescription,
			int index) {
		this.node = abstractGraphNodeElement;
		this.valueType = valueType;
		this.paraName = paraName;
		this.paraDescription = paraDescription;
		this.index = index;
	}
}
