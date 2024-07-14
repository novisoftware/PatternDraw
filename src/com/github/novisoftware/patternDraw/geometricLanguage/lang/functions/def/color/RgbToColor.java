package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color;

import java.awt.Color;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueColor;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class RgbToColor implements FunctionDefInterface {
	public static final String NAME = "rgb_to_color";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "rgbカラー";
	}

	@Override
	public String getDescription() {
		return "赤青緑の値により色情報を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"red", "green", "blue"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"赤(0-1)", "緑(0-1)", "青(0-1)"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.COLOR;
	}

	static double fitToRange(double min, double max, double input) {
		if (input < min) {
			return min;
		}
		if (input > max) {
			return max;
		}
		
		return input;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		Value p1 = param.get(1);
		Value p2 = param.get(2);
		if (p0 == null || p1 == null || p2 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		double r = fitToRange(0, 1, Value.getDouble(p0));
		double g = fitToRange(0, 1, Value.getDouble(p1));
		double b = fitToRange(0, 1, Value.getDouble(p2));

		Color c = new Color((float) r, (float) g, (float) b);

		return new ValueColor(c);
	}
}
