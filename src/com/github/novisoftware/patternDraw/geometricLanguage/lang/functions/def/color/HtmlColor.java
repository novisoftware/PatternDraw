package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color;

import java.awt.Color;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueColor;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class HtmlColor implements FunctionDefInterface {
	public static final String NAME = "html_color";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "文字列カラー";
	}

	@Override
	public String getDescription() {
		return "#000000から#FFFFFFの文字列により色情報を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.STRING};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"color_string"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"#000000 - #FFFFFF の形式の文字列"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.COLOR;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		if (p0 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		String text = p0.toString().toLowerCase();
		if (! text.matches("#[a-f0-9]{6}")) {
			throw new CaliculateException("文字列の指定に誤りがあります(#000000 - #FFFFFF のパターンでない)。");
		}

		Color c = new Color(Integer.parseInt(text.substring(1), 16));

		return new ValueColor(c);
	}
}
