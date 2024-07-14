package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class DoTransformPos implements FunctionDefInterface {
	public static final String NAME = "do_transform_pos";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "変換を実行(点)";
	}

	@Override
	public String getDescription() {
		return "点の並びに対して変換を実行します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.TRANSFORM};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"points", "transform"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び", "変換"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		if (!(param.get(1) instanceof ValueTransform)) {
			throw new CaliculateException("型が違います");
		}
		ArrayList<Pos> src = Value.getPosList(param.get(0));
		ValueTransform transform = (ValueTransform)param.get(1);

		ArrayList<Pos> dst = new ArrayList<Pos>();
		int n = src.size();
		for (int i = 0; i < n; i++) {
			dst.add(transform.transform(src.get(i)));
		}

		return new ValuePosList(dst);
	}
}
