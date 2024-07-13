package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.Comparator;
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
		return "点のクリッピング";
	}

	@Override
	public String getDescription() {
		return "点の並びをクリッピングします。";
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

	public static class Dim2distanceComparator implements Comparator<Pos> {
		Pos zeroPoint;
		
		Dim2distanceComparator(Pos zeroPoint) {
			this.zeroPoint = zeroPoint;
		}

		@Override
		public int compare(Pos p1, Pos p2) {
			double d1 = p1.distance(this.zeroPoint);
			double d2 = p2.distance(this.zeroPoint);

			if (d1 == d2) {
				return 0;
			}

			return d1 < d2 ? -1 : 1;
		}
	}

	public static class CrossInfoComparator implements Comparator<CrossInfo> {
		Dim2distanceComparator dim2distanceComparator;
		
		CrossInfoComparator(Pos zeroPoint) {
			this.dim2distanceComparator = new Dim2distanceComparator(zeroPoint);
		}

		@Override
		public int compare(CrossInfo c1, CrossInfo c2) {
			return this.dim2distanceComparator.compare(c1.pos, c2.pos);
		}
	
	}
	
	
	public static class CrossInfo {
		Pos pos;
		Line line;
	}

	private static List<Pos> crossPoints(Line line1, List<Line> lineList2) {
		ArrayList<Pos> crossPointList = new ArrayList<Pos>();
		for (Line line2 : lineList2) {
			Pos p = line1.crossPoint(line2);
			if (p != null) {
				crossPointList.add(p);
			}
		}

		// 出発点からの距離によるソート。
		
		// 出発点を持つ Comparator を new して、与えられた座標を比較する。
		Dim2distanceComparator c = new Dim2distanceComparator(new Pos(line1.x0, line1.y0)) ;
		crossPointList.sort(c);

		return crossPointList;
	}
	
	public static ArrayList<Pos> clip(ArrayList<Pos> posList1, ArrayList<Pos> posList2) {
		// クリッピング対象が 要素数 0 だった場合
		// クリッピング領域が 3角形に満たなかった場合
		if (posList1.size() == 0 || posList2.size() < 3) {
			return posList1;
		}

		
		// 内側の点が、「外側の点をつないで作られた多角形に含まれるか」を検査する。
		// スキャンは
		// <ul>
		// <li> 水平に行う
		// <li> 奇数偶数判定をする
		// </ul>

		// 一番左の位置の初期値
		// 確実に両方の外側であるような位置を、適当で良いので探す
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
			lineList2.add(new Line(posList2.get(i), posList2.get((i + 1) % m)));
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

		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		ArrayList<Pos> workPosList = new ArrayList<Pos>();


		System.out.println("##############################");
		// クリッピング判定
		int n = posList1.size();
		for (int i = 0 ; i < n ; i++) {
			if (isIn) {
				newPosList.add(posList1.get(i));
			}
			Line line1 = new Line(posList1.get(i), posList1.get((i + 1) % n));
			List<Pos> cpList = crossPoints(line1, lineList2);
			System.out.println("line from pos#" + i + "  cp num = " + cpList.size());
			for (Pos p : cpList) {
				if (isIn) {
					isIn = !isIn;
				}
				newPosList.add(p);
				// TODO: 内側にめり込む場合の考慮が必要。
			}
		}

		// 最後まで交点がなかった場合:
		if (newPosList.isEmpty()) {
			// クリッピング用の領域として与えられた領域そのものを返す
			return posList2;
		}
		
		return newPosList;
	}
	
	
	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		Value p1 = param.get(1);
		if (p0 == null || p1 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));
		ArrayList<Pos> newPosList = clip(posList1, posList2);

		return new ValuePosList(newPosList);
		
	}
}
