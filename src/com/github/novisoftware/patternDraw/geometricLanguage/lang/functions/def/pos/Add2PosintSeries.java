package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class Add2PosintSeries implements FunctionDefInterface {
	public static final String NAME = "add_2_point_series";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "2系列の点を加算";
	}

	@Override
	public String getDescription() {
		return "2系列の点の座標を加算します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions1", "positions2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"系列1", "系列2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));

		ArrayList<Pos> ret = new ArrayList<Pos>();
		int n = posList1.size();
		int m = posList2.size();
		int loopLen = n > m ? n : m;
		for (int i = 0; i < loopLen; i++) {
			Pos a = posList1.get(i % n);
			Pos b = posList2.get(i % n);

			ret.add(new Pos(a.getX()+b.getX(), a.getY() + b.getY()));
		}

		return new ValuePosList(ret);
	}
}
