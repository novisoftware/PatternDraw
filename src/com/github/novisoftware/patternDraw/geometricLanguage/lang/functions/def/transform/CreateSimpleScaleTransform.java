package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.Util;

public class CreateSimpleScaleTransform implements FunctionDefInterface {
	public static final String NAME = "create_simple_scale_transform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "拡大縮小変換を生成";
	}

	@Override
	public String getDescription() {
		return "拡大縮小変換を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"scale"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"拡大率"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Double scale = Value.getDouble(param.get(0));

		ValueTransform tr = ValueTransform.createScale(scale, scale);

		return tr;
	}
}
