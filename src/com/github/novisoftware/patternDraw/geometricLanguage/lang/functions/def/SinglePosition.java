package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;

// line_to_draw
public class SinglePosition implements FunctionDefInterface {
	// 注: 逆ポーランド記法板スクリプトでの rt_pos に対応
	public static final String NAME = "single_position";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "座標を生成";
	}

	@Override
	public String getDescription() {
		return "長さと角度を元に座標を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.FLOAT, ValueType.FLOAT};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"r", "angle"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"原点からの距離", "角度"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer _t) throws CaliculateException {
		double r = Value.getDouble(param.get(0));
		double angle = Value.getDouble(param.get(1));
		double theta = Util.angleToRadian(angle);

		// 時計回り
		double x = r * Math.sin(theta);
		double y = -r * Math.cos(theta);
		ArrayList<Pos> posList = new ArrayList<Pos>();
		posList.add(new Pos(x, y));

		return new ValuePosList(posList);
	}
}
