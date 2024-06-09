package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.Util;

public class CreateScaleTransform implements FunctionDefInterface {
	public static final String NAME = "create_scale_transform";

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
		String[] ret = {"angle", "scale1", "scale2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"軸の向き", "拡大率1", "拡大率2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Double angle = Value.getDouble(param.get(0));
		Double scale1 = Value.getDouble(param.get(1));
		Double scale2 = Value.getDouble(param.get(2));
		double th = Util.angleToRadian(angle);

		ValueTransform t0 = ValueTransform.createRotate(th);
		ValueTransform t1 = ValueTransform.createScale(scale1, scale2);
		ValueTransform t2 = ValueTransform.createRotate(-th);

		return t0.multiply(t1).multiply(t2);
	}
}
