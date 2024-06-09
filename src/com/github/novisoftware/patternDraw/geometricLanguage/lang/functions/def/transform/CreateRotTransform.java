package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.Util;

public class CreateRotTransform implements FunctionDefInterface {
	public static final String NAME = "create_rot_transform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "回転変換を生成";
	}

	@Override
	public String getDescription() {
		return "回転変換を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"angle"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Double angle = Value.getDouble(param.get(0));

		if (angle == null) {
			angle = 0.0;
		}
		double th = Util.angleToRadian(angle);
		return ValueTransform.createRotate(th);
	}
}
