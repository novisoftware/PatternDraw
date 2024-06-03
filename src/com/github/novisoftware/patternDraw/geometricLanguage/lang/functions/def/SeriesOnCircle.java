package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueFloat;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;

public class SeriesOnCircle  implements FunctionDefInterface {
	public static final String NAME = "series_on_circle";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "座標の系列";
	}

	@Override
	public String getDescription() {
		return "円周上に並んだ座標の系列を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "r", "angle"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"分割数", "半径", "角度"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer _t) throws CaliculateException {
		int n = Value.getInteger(param.get(0)).intValue();
		double r = Value.getDouble(param.get(1));
		double angle = Value.getDouble(param.get(2));
		double theta = Util.angleToRadian(angle);

		ArrayList<Pos> posList = new ArrayList<Pos>();
		for (int i = 0; i < n; i++) {
			// 時計回り
			double x = r * Math.sin(2 * Math.PI * i / n + theta);
			double y = -r * Math.cos(2 * Math.PI * i / n + theta);
			posList.add(new Pos(x, y));
		}

		return new ValuePosList(posList);
	}
}
