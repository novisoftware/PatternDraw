package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.exception.EvaluateException;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.RenderingUtil.WidthCache;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.IconImage;
import com.github.novisoftware.patternDraw.utils.GuiPreference;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.GuiUtil.StringRectUtil;

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
	 * @return 説明のテキスト
	 */
	abstract public String getDescription();

	
	
	static private GuiUtil.StringRectUtil strUtil = new GuiUtil.StringRectUtil();

	WidthCache tipsWidthCache = new WidthCache();

	/**
	 * コンストラクタ
	 *
	 * @param editPanel
	 */
	public P021____AbstractGraphNodeElement(EditDiagramPanel editPanel) {
		super(editPanel);
		
		this.boxOffsetX = 12;
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
	public void evaluate() throws EvaluateException {
		try {
			evaluateValue();
			this.isError = false;
			this.errorMessage = null;
		} catch (CaliculateException e) {
			this.workValue = null;
			this.isError = true;
			this.errorMessage = e.getMessage();
			throw e;
		}
	}

	public int getXc() {
		return x;
	}
	public int getYc() {
		return y;
	}
	public int getWc() {
		return w;
	}
	public int getHc() {
		return h;
	}

	public abstract void evaluateValue() throws EvaluateException;

	static StringRectUtil str2rect = new StringRectUtil();
	static StringRectUtil str2rect_forComment = new StringRectUtil();

	private String[] descriptionCache = null;

	private String[] getDescriptionAsList() {
		if (descriptionCache==null) {
			String work = this.getDescription();

			this.descriptionCache = work.split("\\\\n");
		}

		return descriptionCache;
	}

	/**
	 * 数字の部分をドラッグすると、単連結グラフが移動する。
	 * この処理するためのオブジェクト。
	 */
	P102___NumberPicker numberPicker;

	/**
	 * 描画用メソッドは段階に分けて呼び出されます(引数: phase)。
	 */
	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		int arcWidth = 10;
		int arcHeight = 10;

		P021____AbstractGraphNodeElement e = this;
		// 「演算子」の場合は、引数名を表示しない。
		boolean isDisplayConnectName = 
				// 「演算子」以外の場合は、引数名を表示する。
				(e.getKindId() != KindId.OPERATOR)
				||
				(e.getKindId() == KindId.OPERATOR && e.getRepresentExpression().equals("if"))
				;

		// 結線
		if (phase == 0) {
			this.paintConnectedLine(g2);

			// 単連結グラフごとに、実行順を数字で表示する
			if (this.groupHead != null) {
				g2.setFont(GuiPreference.GROUP_ID_FONT);
				g2.setColor(Color.GRAY);

				//
				String s = "" + this.groupHead;
				Rectangle rect = strUtil.str2rect(s, g2);
				int x = e.x - rect.width;
				int y = e.y + 30;
				g2.drawString(s, x, y);

				this.numberPicker = new P102___NumberPicker(this, this.editPanel, this.groupHead, x, y, rect);
			}
			else {
				this.numberPicker = null;
			}

		}

		// 箱
		else if (phase == 1) {
			g2.setFont(GuiPreference.ICON_BOX_FONT);
			if (! (e.getKindId() == KindId.COMMENT
					|| e.getKindId() == KindId.OPERATOR
					)) {
				// コメントや、演算子の上に、わざわざ「コメント」「演算子」と表示しない。
				
				g2.setColor(Color.GRAY);
				String boxTitle = e.getKindString();
				if (e.getKindId() == KindId.CONSTANT) {
					// 整数とか浮動小数点とかの区別を表示する
					boxTitle += " - " + Value.valueTypeToDescString(this.getValueType());
				}
				else if (e.getKindId() == KindId.DISPLAY) {
					// どのような動作か書く。
					// 「表示」という文言はわざわざ出さない
					// boxTitle += " - " + this.getDescription();
					// boxTitle = this.getDescription();
					boxTitle = this.getRepresentExpression();
				}
				g2.drawString(boxTitle, e.x + 30, e.y - 9);
			}

			g2.setColor(GuiPreference.color);
			if (e.getKindId() == KindId.INPUT) {
				// インタラクティブな入力
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, arcWidth, arcHeight);

				BufferedImage image = GuiUtil.getImage(IconImage.EDIT, this);
				g2.drawImage(image, e.x + 20, e.y + 5, null);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getOutputType(), e.x + 50, e.y + e.h * 2 / 3 + 5 );
			}
			else if (e.getKindId() == KindId.DISPLAY) {
				// 表示
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);

				BufferedImage image = GuiUtil.getImage(IconImage.DISPLAY, this);
				g2.drawImage(image, e.x + 40, e.y, null);
			}
			else if (e.getKindId() == KindId.CONSTANT ) {
				// 定数
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);

				g2.setColor(Color.BLACK);
				g2.drawString(e.getRepresentExpression()  //  t.getRpnString().replaceAll(";.*", "")
								, e.x + 30, e.y + e.h / 2 + 10);
			}
			else if (e.getKindId() == KindId.COMMENT) {
				// コメント
				// コメントの場合は width は無視する
				String s = e.getRepresentExpression();
				g2.setFont(GuiPreference.COMMENT_FONT);
				Rectangle r = str2rect_forComment.str2rect(s, g2);
				int sWidth = r.width < 16 ? 16 : r.width;
				
				// コメントの幅は、表示タイミングで上書きする。
				e.w = sWidth + 38 + 12;

				if (this.isOnMouse()) {
					g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
					g2.fillRoundRect(e.x + 12, e.y, /* e.w */ sWidth + 38 , e.h, arcWidth, arcHeight);
				}

				g2.setColor(GuiPreference.COMMENT_BACKGROUND_COLOR);
				g2.fillRect(e.x + 16, e.y + e.h -3, /* e.w */ sWidth + 28  , 3);

				g2.setColor(Color.BLACK);
				g2.drawString(s, e.x + 30, e.y + e.h / 2 + 10);

			}
			else if ( e.getKindId() == KindId.VARIABLE_SET ) {
				// 変数を設定
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_SET, this);
				g2.drawImage(image, e.x + 26, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 60, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.VARIABLE_REFER ) {
				// 変数を参照
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, arcWidth, arcHeight);
				BufferedImage image = GuiUtil.getImage(IconImage.VAR_REFER, this);
				g2.drawImage(image, e.x + 26, e.y, null);

				g2.setColor(Color.BLACK);
//				g2.drawString(t.getRpnString().replaceAll("^.*'", "").replaceAll(" .*", ""), t.x + 50, t.y + t.h / 2 + 10);
				g2.drawString(e.getRepresentExpression(), e.x + 60, e.y + e.h / 2 + 10);
			}
			else if ( e.getKindId() == KindId.OPERATOR) {
				// 演算子
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillOval(e.x + 16, e.y, e.w, e.h);

				String printMark = e.getRepresentExpression();
				g2.setColor(Color.BLACK);
				Rectangle rect = str2rect.str2rect(printMark, g2);

				g2.drawString(printMark, e.x  + 16 + (e.w - rect.width)/2, e.y + (e.h + rect.height)/ 2 - 4);
			} else if (e.getKindId() == KindId.PROCESSING) {
				// 処理
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 16, e.y, e.w, e.h, 16, 16);

				String printMark = e.getRepresentExpression();
				g2.setColor(Color.BLACK);
				Rectangle rect = str2rect.str2rect(printMark, g2);

				g2.drawString(printMark, e.x  + 16 + (e.w - rect.width)/2, e.y + (e.h + rect.height)/ 2 - 4);
			}
			else {
				// なにか追加した場合の暫定動作用

				int r = e.h * 2 / 3;

				g2.setStroke(GuiPreference.STROKE_BOLD);
				g2.fillRoundRect(e.x + 12, e.y, e.w, e.h, r, r);
				g2.setColor(GuiPreference.ICON_BORDER_COLOR);
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
			g2.setStroke(GuiPreference.STROKE_PLAIN);
			for (P010___ConnectTerminal connector : connectors) {
				connector.paint(g2, phase, isDisplayConnectName);
			}

			if (this.editPanel.isVisibleDebugInfo) {
				g2.setFont(GuiPreference.ICON_BOX_FONT);
				g2.setColor(Color.RED);
				g2.drawString("" + e.id  /* e.getDebugIdString() */ + "  " + e.getValueType(), e.x + 30, e.y + 9);
			}
		}

		// 説明類
		else if (phase == 2) {
			if (e.getKindId() == KindId.PROCESSING
					|| e.getKindId() == KindId.OPERATOR
					|| e.getKindId() == KindId.DISPLAY) {
				if (this.isOnMouse() && (! this.isHandled())
						// && this instanceof P023_____FncGraphNodeElement
						) {
					int drawX = e.x;
					int drawY = e.y + e.h;
					drawX += 40;

					String[] desc = this.getDescriptionAsList();
					if (desc == null) {
						// 動作確認用。
						String[] wk = {"NULL値"};
						desc = wk;
					}

					ArrayList<String> aWork = new ArrayList<String>();
					for (String s : desc) {
						aWork.add(s);
					}

					String vDesc = this.getValueTypeDescString();
					if (vDesc.length() > 0) {
						aWork.add("型: " + vDesc);
					}

					String[] desc2 = new String[aWork.size()];
					for (int i = 0 ; i < desc2.length; i++) {
						desc2[i] = aWork.get(i);
					}

					RenderingUtil.drawTipsWindow(g2, this.tipsWidthCache, drawX, drawY, desc2);
				}
			}

			if (e.getKindId() == KindId.VARIABLE_SET
					|| e.getKindId() == KindId.VARIABLE_REFER) {
					if (this.isOnMouse() && (! this.isHandled())
							) {
					int drawX = e.x;
					int drawY = e.y + e.h;
					drawX += 80;

					String[] desc = {};

					if (e.getKindId() == KindId.VARIABLE_SET) {
						String [] d = {
								"変数を設定します。"
						};
						desc = d;
					} else if (e.getKindId() == KindId.VARIABLE_REFER) {
						String valueTypeStr = null;
						if (this.actualValueTypeResult != null
								&& !(ValueType.UNDEF.equals(this.actualValueTypeResult))) {
							ValueType valueType = this.actualValueTypeResult;

							valueTypeStr = Value.valueTypeToDescString(valueType);
							String [] d = {
									"変数を参照します。",
									"型: " + valueTypeStr
							};
							desc = d;
						}
						else {
							String [] d = {
									"変数を参照します。"
							};
							desc = d;
						}
					}

					RenderingUtil.drawTipsWindow(g2, this.tipsWidthCache, drawX, drawY, desc);
				}
			}

			// コネクタ用の端子
			g2.setStroke(GuiPreference.STROKE_PLAIN);
			for (P010___ConnectTerminal connector : connectors) {
				connector.paint(g2, phase, isDisplayConnectName);
			}

			// エラー表示
			if (this.isError) {
				g2.setFont(GuiPreference.ICON_BOX_FONT);
				BufferedImage image = GuiUtil.getImage2(IconImage.MESSAGE, this);
				g2.drawImage(image, e.x + 40, e.y - 60, null);
				g2.setColor(Color.BLACK);
				g2.drawString("計算エラー", e.x + 50, e.y - 35);
				g2.drawString(this.errorMessage, e.x + 50, e.y - 18);
			}
		}
	}

	@Override
	public P001_IconGuiInterface getTouchedObject(EditDiagramPanel editDiagramPanel, int x, int y) {
		for (P010___ConnectTerminal connector : connectors) {
			if (connector.isTouched(x, y)) {
				editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return connector;
			}
		}

		if (this.numberPicker != null) {
			if (this.numberPicker.isHit(x, y)) {
				editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				return this.numberPicker;
			}
		}

		if (this.getKindId() == KindId.CONSTANT
				|| this.getKindId() == KindId.VARIABLE_REFER
				|| this.getKindId() == KindId.VARIABLE_SET
				) {
			if (this.isOnRightEdge(x) && this.isOnHeight(y)) {
				editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				this.dragMode = DragMode.RESIZE_X;
				return this;
			}
		}

		if (this.isOnWidth(x) && this.isOnHeight(y)) {
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			this.dragMode = DragMode.MOVE;
			return this;
		}

		return null;
	}
	
	@Override
	public void dragged(int moveX, int moveY) {
		if (this.dragMode == DragMode.MOVE) {
			this.x += moveX;
			this.y += moveY;
		}
		else if (this.dragMode == DragMode.RESIZE_X) {
			if (this.dragMode == DragMode.RESIZE_X) {
				this.w += moveX;
			}

			// サイズを制限する
			if (this.w < GuiPreference.NodeElementLimit.SIZE_MIN_WIDTH) {
				this.w = GuiPreference.NodeElementLimit.SIZE_MIN_WIDTH;
			}
			if (this.w > GuiPreference.NodeElementLimit.SIZE_MAX_WIDTH) {
				this.w = GuiPreference.NodeElementLimit.SIZE_MAX_WIDTH;
			}
		}
	}
}
