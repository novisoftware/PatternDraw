package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.LineWidthSetterSVG;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueFloat;

// set_stroke_width_svg
public class SetLineWidthSVG implements FunctionDefInterface {
	public static final String NAME = "set_stroke_width_svg";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "線分の太さを設定します(SVG)。";
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
	public Value exec(List<Value> param, InstructionRenderer t) {
		double width = ((ValueFloat)(param.get(0))).getInternal();

		t.currentStrokeWidth = "" + width;
		if (t.s != null) {
			t.s.setStrokeWidth(width);
		}

		LineWidthSetterSVG s = new LineWidthSetterSVG(width);
		t.primitiveList.add(s);

		return null;
	}
}