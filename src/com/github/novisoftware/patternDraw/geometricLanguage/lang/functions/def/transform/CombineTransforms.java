package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class CombineTransforms implements FunctionDefInterface {
	public static final String NAME = "combine_transforms";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "変換を結合";
	}

	@Override
	public String getDescription() {
		return "2つの変換を結合します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.TRANSFORM, ValueType.TRANSFORM};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"transform1", "transform2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"変換", "変換2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		if (!(param.get(0) instanceof ValueTransform)) {
			throw new CaliculateException("型が違います");
		}
		if (!(param.get(1) instanceof ValueTransform)) {
			throw new CaliculateException("型が違います");
		}
		ValueTransform transform1 = (ValueTransform)param.get(0);
		ValueTransform transform2 = (ValueTransform)param.get(1);

		return transform1.multiply(transform2);
	}
}
