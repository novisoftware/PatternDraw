package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometryLanguage.primitives.Path;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValueLineList;

// line_to_draw
public class LineToDraw implements FunctionDefInterface {
	public static final String NAME = "line_to_draw";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "線分のリストを描画します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.POS_LIST};
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
	public Value exec(List<Value> param, InstructionRenderer t) throws InvaliScriptException {
		// TODO 副作用先は、どのように持たせるのが良いか？
		ArrayList<Line> a = ((ValueLineList)(param.get(1))).getInternal();
		for (Line line : a) {
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
				t.pathList.add(path);
			}
		}
		return null;
	}
}
