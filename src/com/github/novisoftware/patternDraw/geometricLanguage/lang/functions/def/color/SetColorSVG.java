package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color;

import java.awt.Color;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueColor;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.ColorSetterPNG;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.ColorSetterSVG;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.LineWidthSetterSVG;

// set_stroke_width_png
public class SetColorSVG implements FunctionDefInterface {
	public static final String NAME = "set_color_svg";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "色を設定(SVG)";
	}

	@Override
	public String getDescription() {
		return "色を設定します(SVG)。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.COLOR};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"color"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"色"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return null;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		if (p0 == null) {
			throw new CaliculateException("パラメーターが設定されていません。");
		}
		if (!(p0 instanceof ValueColor)) {
			throw new CaliculateException("型に誤りがあります。");
		}

		Color c = ((ValueColor)p0).getInternal();

		ColorSetterSVG s = new ColorSetterSVG(c);
		t.primitiveList.add(s);

		// t.currentStrokeWidth = "" + width;
		return null;
	}
}
