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
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.Util;

public class SeriesOnCircle2  implements FunctionDefInterface {
	public static final String NAME = "series_on_circle2";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "座標の系列2";
	}

	@Override
	public String getDescription() {
		return "円周上に並んだ座標の系列を生成します。\\n周回できます";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.INTEGER, ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "m", "r", "angle"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"分割数", "周回数", "半径", "開始角度"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer _t) throws CaliculateException {
		int n = Value.getInteger(param.get(0)).intValue();
		int m = Value.getInteger(param.get(1)).intValue();
		double r = Value.getDouble(param.get(2));
		double angle = Value.getDouble(param.get(3));
		double theta = Util.angleToRadian(angle);

		ArrayList<Pos> posList = new ArrayList<Pos>();
		for (int i = 0; i < n; i++) {
			// 時計回り
			double x = r * Math.sin(2 * Math.PI * i * m / n + theta);
			double y = -r * Math.cos(2 * Math.PI * i * m / n + theta);
			posList.add(new Pos(x, y));
		}

		return new ValuePosList(posList);
	}
}
