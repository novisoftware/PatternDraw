package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueFloat;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.LineWidthSetterPNG;
import com.github.novisoftware.patternDraw.utils.Debug;

// set_stroke_width_png
public class SetLineWidthPNG implements FunctionDefInterface {
	public static final String NAME = "set_stroke_width_png";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "太さを設定(PNG)";
	}

	@Override
	public String getDescription() {
		return "線分の太さを設定します(PNG)。";
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
		return null;
	}
}
