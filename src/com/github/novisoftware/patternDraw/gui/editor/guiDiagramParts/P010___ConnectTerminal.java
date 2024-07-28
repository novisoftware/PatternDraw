package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Color;
import java.awt.Graphics2D;

import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.RenderingUtil.WidthCache;
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
	private P020___AbstractElement node;
	private int index;
	private int nIndex;

	private WidthCache tipsWidthCache = new WidthCache();

	/**
	 * 入力された型が妥当な場合 true
	 * 入力された型が妥当でない場合 false
	 * を設定する。
	 * 初期値はtrue。エラーメッセージの代入と同時にfalseにする。
	 */
	public boolean isTypeChekResultValid = true;
	public String[] typeChekErrorMessage = null;

	public P010___ConnectTerminal(
			P020___AbstractElement abstractElement,
			String paraName,
			ValueType valueType,
			String paraDescription,
			int index, int indexNum) {
		this.node = abstractElement;
		this.valueType = valueType;
		this.paraName = paraName;
		this.paraDescription = paraDescription;
		this.index = index;
		this.nIndex = indexNum;
	}

	public P020___AbstractElement getNode() {
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
		return node.getXc() + RENDER_WIDTH / 2;
	}


	public int getTopY() {
		return node.getYc() + (node.getHc() - Y_INTERVAL * nIndex) / 2 + index * Y_INTERVAL +
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
		if ( node.getXc() <= x && x <= node.getXc() + RENDER_WIDTH
			&& this.getTopY() <= y && y <= this.getTopY() + RENDER_HEIGHT) {
			return true;
		}

		return false;
	}

	public void paint(Graphics2D g2, int phase, boolean isDisplayConnectName) {
		if (phase == 1) {
			g2.setFont(GuiPreference.CONNECTOR_TEXT_FONT);
			g2.setColor(GuiPreference.CONNECTOR_FILL_COLOR);
			g2.fillOval(node.getXc(), this.getTopY(), RENDER_WIDTH, RENDER_HEIGHT);
			if (isDisplayConnectName) {
				g2.setColor(Color.BLACK);
				g2.drawString(paraName, node.getXc() + RENDER_WIDTH   - strUtil.strWidth(paraName, g2),
						this.getTopY() - 2);
			}
		}
		if (phase == 2) {
			if (this.isOnMouse()) {

				String srcValueType = "";
				P020___AbstractElement src = node.paramMapObj.get(this.getParaName());
				if (src != null &&
						src.actualValueTypeResult != null &&
						!(ValueType.UNDEF.equals(src.actualValueTypeResult))) {
					ValueType valueType = src.actualValueTypeResult;

					srcValueType = Value.valueTypeToDescString(valueType);
				}

				int drawX = node.getXc();
				int drawY = node.getYc() + node.getHc();

				String aceptableType = "受け付ける型: " + Value.valueTypeToDescString(this.valueType);
				String actualType  = "入力された型: " + srcValueType;

				String[] desc;
				if (isDisplayConnectName) {
					String[] _d= {
							// 注釈情報があれば注釈情報をつける
							paraDescription.length() == 0 ? paraName : paraName + " (" + paraDescription + ")",
							aceptableType,
							actualType
					};
					desc = _d;
				}
				else {
					String[] _d= {
							aceptableType,
							actualType
					};
					desc = _d;
				}

				RenderingUtil.drawTipsWindow(g2, this.tipsWidthCache, drawX, drawY, desc);
			}
		}

	}
}
