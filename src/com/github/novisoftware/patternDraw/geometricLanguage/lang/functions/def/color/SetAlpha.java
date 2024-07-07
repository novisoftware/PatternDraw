package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color;

import java.awt.Color;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueColor;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class SetAlpha implements FunctionDefInterface {
	public static final String NAME = "set_alpha";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "透明度を設定";
	}

	@Override
	public String getDescription() {
		return "色情報に透明度を設定します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.COLOR};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"alpha", "color"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"不透明度(0が透過。1が不透明)", "色"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.COLOR;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		Value p1 = param.get(1);
		if (p0 == null || p1 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		double alpha = Value.getDouble(p0);
		int iAlpha = (int)Math.round(0xFF * alpha);
		if (iAlpha < 0) {
			iAlpha = 0;
		}
		if (iAlpha > 0xFF) {
			iAlpha = 0xFF;
		}
		ValueColor c = ((ValueColor)p1);
		int ic = c.getInternal().getRGB();
		int r = (ic >> 16) & 0xFF;
		int g = (ic >>  8) & 0xFF;
		int b = (ic >>  0) & 0xFF;
		// int iNewColor = c.getInternal().getRGB()  + (iAlpha << 24);

		Color c2 = new Color(r, g, b, iAlpha);

		return new ValueColor(c2);
	}
}
