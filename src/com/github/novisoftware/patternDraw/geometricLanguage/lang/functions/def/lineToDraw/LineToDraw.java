package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.lineToDraw;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;

// line_to_draw
public class LineToDraw implements FunctionDefInterface {
	public static final String NAME = "line_to_draw";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線を描画";
	}

	@Override
	public String getDescription() {
		return "線分のリストを描画します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.LINE_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"lines"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"線分のリスト"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return null;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Line> lineList = Value.getLineList(param.get(0));

		for (Line line : lineList) {
			// Line line2 = line.translateLine(t.translateX, t.translateY);
			// t.localDrawLine(t.g, t.svgBuff, t.s, line2);

			String strokeColor = t.currentStrokeColor;
			String strokeWidth = t.currentStrokeWidth;
			boolean isFill = false;
			String fillColor = null;

			// String strokeColor, String strokeWidth, boolean isFill,
			// String fillColor

			Path path = new Path(line, strokeColor, strokeWidth, isFill, fillColor);

			if (t != null) {
				t.primitiveList.add(path);
			}
		}

		return null;
	}
}
