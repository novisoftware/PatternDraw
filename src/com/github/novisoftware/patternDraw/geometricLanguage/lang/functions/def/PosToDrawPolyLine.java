package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;


// line_to_draw
public class PosToDrawPolyLine implements FunctionDefInterface {
	public static final String NAME = "draw_polyline";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線分として描画";
	}

	@Override
	public String getDescription() {
		return "点の集まりを線で辿って描画します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"points"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点のリスト"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return null;
	}

	protected void overWrap(ArrayList<Pos> posList, ArrayList<Pos> src) {
		// オーバーラップする場合、ここに処理を記載。
		// (差分プログラミング用の場所)
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> src = Value.getPosList(param.get(0));
		ArrayList<Pos> posList = new ArrayList<Pos>();
		posList.addAll(src);
		this.overWrap(posList, src);

		String strokeColor = t.currentStrokeColor;
		double strokeWidth = Double.parseDouble(t.currentStrokeWidth);
		boolean isFill = false;
		String fillColor = null;

		Path path = new Path(posList, strokeColor, strokeWidth, isFill, fillColor);

		if (t != null) {
			t.primitiveList.add(path);
		}

		return null;
	}
}
