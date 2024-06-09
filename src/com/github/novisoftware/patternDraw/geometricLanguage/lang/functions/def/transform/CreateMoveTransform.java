package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class CreateMoveTransform implements FunctionDefInterface {
	public static final String NAME = "create_move_transform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "移動変換を生成";
	}

	@Override
	public String getDescription() {
		return "移動変換を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"pos"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点(1個)"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.TRANSFORM;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));
		if (posList.size() == 0) {
			throw new CaliculateException("点がありません");
		}

		Pos refPos = posList.get(0);

		return ValueTransform.createMove(refPos.getX(), refPos.getY());
	}
}
