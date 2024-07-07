package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;


// pos_to_fill
public class PosToFill implements FunctionDefInterface {
	public static final String NAME = "pos_to_fill";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "ポリゴンを塗りつぶす";
	}

	@Override
	public String getDescription() {
		return "点の集まりを辿ってポリゴンを塗りつぶします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"points"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点のリスト"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return null;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> src = Value.getPosList(param.get(0));
		ArrayList<Pos> posList = new ArrayList<Pos>();
		posList.addAll(src);

		String strokeColor = t.currentStrokeColor;
		double strokeWidth = Double.parseDouble(t.currentStrokeWidth);
		boolean isFill = true;
		String fillColor = strokeColor;

		Path path = new Path(posList, strokeColor, strokeWidth, isFill, fillColor);

		if (t != null) {
			t.primitiveList.add(path);
		}

		return null;
	}
}
