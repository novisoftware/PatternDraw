package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.LineWidthSetterPNG;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.LineWidthSetterSVG;

// set_stroke_width_png
public class SetLineWidth implements FunctionDefInterface {
	public static final String NAME = "set_stroke_width";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線の太さを設定";
	}

	@Override
	public String getDescription() {
		return "線分の太さを設定します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"strokeWidth"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"線分の太さ"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return null;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		double width = Value.getDouble(param.get(0));

		t.currentStrokeWidth = "" + width;
		/*
		t.currentStrokeWidth = "" + width;
		if (t.s != null) {
			t.s.setStrokeWidth(width);
		}
		*/
		LineWidthSetterPNG p = new LineWidthSetterPNG(width);
		t.primitiveList.add(p);

		LineWidthSetterSVG s = new LineWidthSetterSVG(width);
		t.primitiveList.add(s);

		return null;
	}
}
