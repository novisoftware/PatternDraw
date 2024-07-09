package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class PosSortByAngle implements FunctionDefInterface {
	public static final String NAME = "pos_sort";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "角度でソート";
	}

	@Override
	public String getDescription() {
		return "点の並びを角度でソートします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	/**
	 * Posの並びを角度でソートするためのもの
	 */
	public static class AngleComparator implements Comparator<Pos> {
		@Override
		public int compare(Pos p1, Pos p2) {
			// 中心点 0, 0 は最小値として扱う
			if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
				return 0;
			}
			if (p1.getX() == 0 && p1.getY() == 0) {
				return -1;
			}
			if (p2.getX() == 0 && p2.getY() == 0) {
				return 1;
			}

			double t1 = Math.atan2(-p1.getY(), p1.getX());
			double t2 = Math.atan2(-p2.getY(), p2.getX());
			
			if (t1 == t2) {
				return 0;
			}
			
			return t1 < t2 ? -1 : 1;
		}
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));

		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		newPosList.addAll(posList);
		newPosList.sort(new AngleComparator());

		return new ValuePosList(newPosList);
	}
}
