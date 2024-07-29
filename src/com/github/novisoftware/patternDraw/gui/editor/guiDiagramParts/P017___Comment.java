package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.github.novisoftware.patternDraw.core.exception.LangSpecException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;
import com.github.novisoftware.patternDraw.utils.GuiPreference;
import com.github.novisoftware.patternDraw.utils.GuiUtil.StringRectUtil;


public class P017___Comment extends P015__AbstractIcon2 {
	static StringRectUtil str2rect_forComment = new StringRectUtil();

	public static final String[] TEXT_SIZE_LIST = {"大", "中", "小"};
	public static final String[] TEXT_SIZE_VALUES = {"big", "medium", "small"};
	public static final Font[] FONT_VALUES = {
			GuiPreference.COMMENT_FONT_BIG,
			GuiPreference.COMMENT_FONT,
			GuiPreference.COMMENT_FONT_SMALL
	};
	private static final int DEFAULT_SIZE_INDEX = 2;

	private String commentString;
	private int fontSizeIndex;

	public P017___Comment(EditDiagramPanel EditPanel) {
		super(EditPanel);
		this.fontSizeIndex = DEFAULT_SIZE_INDEX;
	}

	public P017___Comment(EditDiagramPanel EditPanel, String s) throws LangSpecException {
		super(EditPanel);

		String a[] = FileReadUtil.tokenizeToArray(s);
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = unescape(a[5]);
		this.setFontSize(unescape(a[6]));
		// attribute
		this.setCommentString(unescape(a[8]));
	}

	@Override
	public String str() {
		// System.out.println("this.valueType: " + this.getValueType());
		return String.format(
				/* REMARK */
				"COMMENT: %d %d %d %d %s %s - %s",
				x,
				y,
				w,
				h,
				escape(id),
				escape(this.getFontSize()),
				escape(this.getCommentString()));
	}

	@Override
	public P015__AbstractIcon2 getCopy() {
		P015__AbstractIcon2 ret;
		try {
			ret = new P017___Comment(this.editPanel, this.str());
			return ret;
		} catch (LangSpecException e) {
			// コピー元がコンストラクタでのエラーチェックを通過しているため、発生しない。
			// コーディング上はエラー表示とnullの返却を残しておく
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isComment() {
		return true;
	}

	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		int arcWidth = 10;
		int arcHeight = 10;

		P017___Comment e = this;

		if (phase == 1) {
			g2.setFont(GuiPreference.ICON_BOX_FONT);

			g2.setColor(GuiPreference.color);
			// コメントの場合は width を無視し、
			// コメントの幅を、表示タイミングで上書きする。
			g2.setFont(FONT_VALUES[fontSizeIndex]);
			Rectangle r = str2rect_forComment.str2rectWithKind(this.getFontSize(),
					getCommentString(), g2);
			int sWidth = r.width < 16 ? 16 : r.width;

			e.w = sWidth + 38 + 12;

			if (this.isOnMouse()) {
				g2.setColor(GuiPreference.ICON_BACKGROUND_COLOR);
				g2.fillRoundRect(e.x + 12, e.y, /* e.w */ sWidth + 38, e.h, arcWidth, arcHeight);
			}

			g2.setColor(GuiPreference.COMMENT_BACKGROUND_COLOR);
			g2.fillRect(e.x + 16, e.y + e.h - 3, /* e.w */ sWidth + 28, 3);

			g2.setColor(Color.BLACK);
			g2.drawString(getCommentString(), e.x + 30, e.y + e.h / 2 + 10);
		}
	}

	/**
	 * @return the commentString
	 */
	public String getCommentString() {
		return commentString;
	}

	/**
	 * @param commentString the commentString to set
	 */
	public void setCommentString(String commentString) {
		this.commentString = commentString;
	}

	/**
	 * @return the fontSize
	 */
	public String getFontSize() {
		return TEXT_SIZE_VALUES[fontSizeIndex];
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(String fontSize) {
		for (int i=0; i < TEXT_SIZE_VALUES.length ; i++ ) {
			String s = TEXT_SIZE_VALUES[i];
			if (s.equals(fontSize)) {
				this.fontSizeIndex = i;
				return;
			}
		}
		// こないはず
		this.fontSizeIndex = 1;
	}
}