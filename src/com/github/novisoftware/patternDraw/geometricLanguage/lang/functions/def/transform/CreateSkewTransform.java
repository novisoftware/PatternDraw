package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.Util;

public class CreateSkewTransform implements FunctionDefInterface {
	public static final String NAME = "create_skew_transform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "傾ける変換を生成";
	}

	@Override
	public String getDescription() {
		return "傾ける変換(スキュー)を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"angle", "skew_angle"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"基準にする軸の向き", "傾ける角度"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Double angle = Value.getDouble(param.get(0));
		Double skew_angle = Value.getDouble(param.get(1));
		double th = Util.angleToRadian(angle);
		double th_skew = Util.angleToRadian(skew_angle);

		ValueTransform t0 = ValueTransform.createRotate(th);
		ValueTransform t1 = ValueTransform.createSkew(th_skew);
		ValueTransform t2 = ValueTransform.createRotate(-th);

		return t0.multiply(t1).multiply(t2);
	}
}
