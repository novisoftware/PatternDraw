package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;

// pos_clipping
public class PosClipping implements FunctionDefInterface {
	public static final String NAME = "pos_clipping";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線分の交点";
	}

	@Override
	public String getDescription() {
		return "2系列の線分が交わる点のあつまりを抽出します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"pos1", "pos2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"クリッピング内側", "クリッピング外側"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	private List<Pos> crossPoints(Line line1, List<Line> lineList2) {
		ArrayList<Pos> crossPointList = new ArrayList<Pos>();
		for (Line line2 : lineList2) {
			Pos p = line1.crossPoint(line2);
			if (p != null) {
				crossPointList.add(p);
			}
		}
		
		// TODO:
		// 出発点からの距離によるソート。
		
		// 出発点を持つ Comparator を new して、与えられた座標を比較する。

		return crossPointList;
	}
	
	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		Value p1 = param.get(1);
		if (p0 == null || p1 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));

		// クリッピング対象が 要素数 0 だった場合
		// クリッピング領域が 3角形に満たなかった場合
		if (posList1.size() == 0 || posList2.size() < 3) {
			return param.get(0);
		}

		
		// 内側の点が、「外側の点をつないで作られた多角形に含まれるか」を検査する。
		// スキャンは
		// <ul>
		// <li> 水平に行う
		// <li> 奇数偶数判定をする
		// </ul>

		// 一番左の位置の初期値( 適当で良い )
		double min = 0;
		// 一番左の位置を探す。
		for (Pos p : posList1) {
			if (p.getX() < min) {
				min = p.getX();
			}
		}
		for (Pos p : posList2) {
			if (p.getX() < min) {
				min = p.getX();
			}
		}
		// 適当にもう少し左にずらす
		min -= 1;

		// クリッピング領域の線分
		ArrayList<Line> lineList2 = new ArrayList<Line>();
		int m = posList2.size();
		for (int i = 0 ; i < m ; i++) {
			lineList2.add(new Line(posList1.get(i), posList1.get((i + 1) % m)));
		}

		// 内側にいるか?
		boolean isIn;

		// 「完全に外側の適当な点」から。
		Line line00 = new Line(new Pos(min, posList1.get(0).getY()), posList1.get(0));
		if (crossPoints(line00, lineList2).size() % 2 != 0) {
			// 交差回数が奇数なら内側
			isIn = true;
		} else {
			// 交差回数が奇数なら外側
			isIn = false;
		}

		ArrayList<Pos> workPosList = new ArrayList<Pos>();

		// クリッピング判定
		int n = posList1.size();
		for (int i = 0 ; i < n ; i++) {
			if (isIn) {
				newPosList.add(posList1.get(i));
			}
			Line line1 = new Line(posList1.get(i), posList1.get((i + 1) % n));
			for (Pos p : crossPoints(line1, lineList2)) {
				isIn = !isIn;
				newPosList.add(p);
				// 注: 内側にめり込む場合の考慮が必要。
			}
		}

		return new ValuePosList(newPosList);
	}
}
