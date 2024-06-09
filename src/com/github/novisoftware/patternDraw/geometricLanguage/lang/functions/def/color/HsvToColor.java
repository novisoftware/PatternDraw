package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueColor;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class HsvToColor implements FunctionDefInterface {
	public static final String NAME = "hsv_to_color";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "hsvカラー";
	}

	@Override
	public String getDescription() {
		return "色相・彩度・明度を基準に色情報を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"hue", "saturation", "value"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"色相(0-360)", "彩度(0-1)", "明度(0-1)"};
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
		Value p2 = param.get(2);
		if (p0 == null || p1 == null || p2 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		double hue = Value.getDouble(p0);
		double saturation = Value.getDouble(p1);
		double value = Value.getDouble(p2);

		// 注: java.awt.Color.getHSBColor の仕様より、
		// hueは任意の値で良い。
		hue /= 360;

		if (saturation < 0) {
			saturation = 0.0;
		}
		if (saturation > 1) {
			saturation = 1.0;
		}
		if (value < 0) {
			value = 0.0;
		}
		if (value > 1) {
			value = 1.0;
		}

		Color c = Color.getHSBColor(
				(float)hue,
				(float)saturation,
				(float)value);

		return new ValueColor(c);
	}
}
