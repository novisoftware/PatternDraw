package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class DoTransformLine implements FunctionDefInterface {
	public static final String NAME = "do_transform_line";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "変換を実行(線)";
	}

	@Override
	public String getDescription() {
		return "線の並びに対して変換を実行します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.LINE_LIST, ValueType.TRANSFORM};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"lines", "transform"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"線の並び", "変換"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		if (!(param.get(1) instanceof ValueTransform)) {
			throw new CaliculateException("型が違います");
		}
		ArrayList<Line> src = Value.getLineList(param.get(0));
		ValueTransform transform = (ValueTransform)param.get(1);

		ArrayList<Line> dst = new ArrayList<Line>();
		int n = src.size();
		for (int i = 0; i < n; i++) {
			Line line = src.get(i);
			dst.add(new Line(
					transform.transform(line.from),
					transform.transform(line.to)));
		}

		return new ValueLineList(dst);
	}
}
