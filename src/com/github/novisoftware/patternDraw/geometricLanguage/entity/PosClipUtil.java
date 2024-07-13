package com.github.novisoftware.patternDraw.geometricLanguage.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class PosClipUtil {
	// このクラスはインスタンス化しない
	private PosClipUtil() {
	}
	
	static private final double MY_EPSILON = 1e-08;
	
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
		Line line1;
		Line line2;
		
		CrossInfo(Pos pos, Line line1, Line line2) {
			this.pos = pos;
			this.line1 = line1;
			this.line2 = line2;
		}
	}

	private static List<CrossInfo> crossPoints(Line line1, List<Line> lineList2) {
		ArrayList<CrossInfo> crossInfoList = new ArrayList<CrossInfo>();
		for (Line line2 : lineList2) {
			Pos p = line1.crossPoint(line2);
			if (p != null) {
				crossInfoList.add(new CrossInfo(p, line1, line2));
			}
		}

		// 出発点からの距離によるソート。
		
		// 出発点を持つ Comparator を new して、与えられた座標を比較する。
		CrossInfoComparator c = new CrossInfoComparator(line1.from) ;
		crossInfoList.sort(c);

		int n = crossInfoList.size();
		for (int i = n - 1; i > 0; i--) {
			// 線に対して1回交わるが、つなぎ目の部分で2回カウントされてしまった場合、1回にする。
			// ( 系列を辿り、連続しているかを見たほうが良いのかもしれない)
			if (crossInfoList.get(i).pos.distance(crossInfoList.get(i - 1).pos) < MY_EPSILON) {
				crossInfoList.remove(i);
			}
		}
		
		return crossInfoList;
	}

	/**
	 * 与えられた座標リストの外側になるような X 位置を計算する。
	 * 
	 * 
	 * @param posList1
	 * @param posList2
	 * @return
	 */
	public static double calcOuterX(ArrayList<Pos> posList1, ArrayList<Pos> posList2) {
		// 一番左の位置の初期値
		// 確実に両方の外側であるような位置を、適当で良いので探す
		double outerX = 0;
		// 一番左の位置を探す。
		for (Pos p : posList1) {
			if (p.getX() < outerX) {
				outerX = p.getX();
			}
		}
		for (Pos p : posList2) {
			if (p.getX() < outerX) {
				outerX = p.getX();
			}
		}
		// 適当にもう少し左にずらす
		outerX -= 1;
		
		return outerX;
	}

	public static boolean isIn(double outerX, Pos pos, ArrayList<Line> lineList) {
		// 「完全に外側の適当な点」から。
		Line line00 = new Line(new Pos(outerX, pos.getY()), pos);
		if (crossPoints(line00, lineList).size() % 2 != 0) {
			// 交差回数が奇数なら内側
			return true;
		} else {
			// 交差回数が奇数なら外側
			return false;
		}
	}

	public static ArrayList<Pos> retrieve(
			Pos from,
			Pos to,
			ArrayList<PosInfo> piList
			) {
		ArrayList<Pos> workPosList = new ArrayList<Pos>();
		int n = piList.size();
		if (n == 0) {
			return workPosList;
		}

		int nFrom = -1;
		int nTo = -1;
		for (int i = 0; i < n; i++) {
			PosInfo pi = piList.get(i);
			if (pi.kind == Kind.cross) {
				Pos p = pi.pos;
				if (p == from) {
					nFrom = i;
				}
				if (p == to) {
					nTo = i;
				}
			}
		}

		if (nFrom == -1 || nTo == -1) {
			return workPosList;
		}
		
		if ((n + nTo - nFrom)%n < (n + nFrom - nTo)%n) {
			int nn = (n + nTo - nFrom) % n;
			for (int i = 1; i < nn; i++) {
				workPosList.add(piList.get((nFrom + i) % n).pos);
			}
		} else {
			int nn = (n + nFrom - nTo) % n;
			for (int i = 1; i < nn; i++) {
				workPosList.add(piList.get((n + nFrom - i) % n).pos);
			}
		}

		
		return workPosList;
	}
	
	static public enum Kind {
		cross,
		edge
	}

	static public class PosInfo {
		Pos pos;
		Kind kind;

		PosInfo(Pos pos, Kind kind) {
			this.pos = pos;
			this.kind = kind;
		}
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
		int n = posList1.size();
		int m = posList2.size();

		// クリッピング対象の線分
		ArrayList<Line> lineList1 = new ArrayList<Line>();
		for (int i = 0 ; i < posList1.size() ; i++) {
			Line line1 = new Line(posList1.get(i), posList1.get((i + 1) % n));
			lineList1.add(line1);
		}

		// クリッピング領域の線分
		ArrayList<Line> lineList2 = new ArrayList<Line>();
		for (int i = 0 ; i < m ; i++) {
			lineList2.add(new Line(posList2.get(i), posList2.get((i + 1) % m)));
		}

		double outerX = calcOuterX(posList2, posList2);

		ArrayList<Pos> newPosList = new ArrayList<Pos>();

		ArrayList<CrossInfo> allCrossInfo = new ArrayList<CrossInfo>();
		HashMap<Line, List<CrossInfo>> lineToCrossInfo = new HashMap<Line, List<CrossInfo>>();
		for (int i = 0 ; i < n ; i++) {
			Line line1 = lineList1.get(i);
			List<CrossInfo> cpList1 = crossPoints(line1, lineList2);
			lineToCrossInfo.put(line1, cpList1);
			allCrossInfo.addAll(cpList1);
		}

		// クリッピング領域の、交点、頂点のリスト
		ArrayList<PosInfo> posSeries2 = new ArrayList<PosInfo>();
		
		HashSet<Pos> allCrossPos = new HashSet<Pos>();
		HashMap<Pos, Line> posToLine2 = new HashMap<Pos, Line>();
		HashMap<Line, ArrayList<CrossInfo>> line2ToCrossInfo = new HashMap<Line, ArrayList<CrossInfo>>();
		for (Line line2 : lineList2) {
			line2ToCrossInfo.put(line2, new ArrayList<CrossInfo>());
		}
		for (CrossInfo ci : allCrossInfo) {
			line2ToCrossInfo.get(ci.line2).add(ci);
			posToLine2.put(ci.pos, ci.line2);
			allCrossPos.add(ci.pos);
		}
		
		int mm = posList2.size();
		for (int i=0 ; i<mm; i++) {
			Line line2 = lineList2.get(i);
			CrossInfoComparator c = new CrossInfoComparator(line2.from) ;
			ArrayList<CrossInfo> ciList = line2ToCrossInfo.get(line2);
			ciList.sort(c);

			boolean isIn2 = isIn(outerX, posList2.get(i), lineList1);
			if (isIn2) {
				posSeries2.add(new PosInfo(line2.from, Kind.edge));
			}
			for (CrossInfo ci : ciList) {
				posSeries2.add(new PosInfo(ci.pos, Kind.cross));
				
				// 奇偶判定で行けそうに思えるが、点の部分を厳密に判定するのは大変。
				// 都度判定する
				/*
				isIn2 = !isIn2;
				*/
			}
		}
		
		
		
		System.out.println("##############################");
		
		Pos firstCrossPos = null;
		Pos lastCrossPos = null;
		
		// クリッピング判定
		for (int i = 0 ; i < n ; i++) {
			// 内側にいるか?
			boolean isIn = isIn(outerX, posList1.get(i), lineList2);
			if (isIn) {
				newPosList.add(posList1.get(i));
			}
			
			Line line1 = lineList1.get(i);
			/*
			List<CrossInfo> cpList = crossPoints(line1, lineList2);
			*/
			List<CrossInfo> cpList = lineToCrossInfo.get(line1);
			System.out.println("line from pos#" + i + "  cp num = " + cpList.size());
			for (CrossInfo ci : cpList) {
				/*
				if (isIn) {
					isIn = !isIn;
				}
				*/
				Pos p = ci.pos;
				
				if (lastCrossPos != null) {
					// 内側にめり込む場合の考慮
					List<Pos> pList = 
							retrieve(
									lastCrossPos,
									p,
									posSeries2);
					newPosList.addAll(pList);
				}
				
				newPosList.add(p);
				lastCrossPos = p;
				if (firstCrossPos == null) {
					firstCrossPos = p;
				}
			}
		}

		List<Pos> pList = 
				retrieve(
						lastCrossPos,
						firstCrossPos,
						posSeries2);
		newPosList.addAll(pList);
		

		
		
		// 最後まで交点がなかった場合:
		if (newPosList.isEmpty()) {
			// クリッピング用の領域として与えられた領域そのものを返す
			return posList2;
		}
		
		return newPosList;
	}


	/**
	 * 線分のリストを、点のリストで与えられるポリゴンでクリッピングする
	 * 
	 * @param lineList1
	 * @param posList2
	 * @param isMask false: クリッピング。 true: マスキング
	 * @return
	 */
	public static ArrayList<Line> lineClip(ArrayList<Line> lineList1, ArrayList<Pos> posList2, boolean isMask) {
		// クリッピング領域が 3角形に満たなかった場合
		if (posList2.size() < 3) {
			return new ArrayList<Line>();
		}

		
		// 内側の点が、「外側の点をつないで作られた多角形に含まれるか」を検査する。
		// スキャンは
		// <ul>
		// <li> 水平に行う
		// <li> 奇数偶数判定をする
		// </ul>
		int m = posList2.size();

		// クリッピング領域の線分
		ArrayList<Line> lineList2 = new ArrayList<Line>();
		for (int i = 0 ; i < m ; i++) {
			lineList2.add(new Line(posList2.get(i), posList2.get((i + 1) % m)));
		}

		ArrayList<Line> newLineList = new ArrayList<Line>();
		double outerX = calcOuterX(posList2, posList2);

		for (Line line: lineList1) {
			boolean isIn = isIn(outerX, line.from, lineList2);
			List<CrossInfo> cpList = crossPoints(line, lineList2);
			if (cpList.size() == 0) {
				// 交点がなかった場合、元々の線分そのものか、線分なしになる
				if (isIn ^ isMask) {
					newLineList.add(line);
				}
			}
			else {
				// 交点ごとに線分を作成していく
				List<Pos> posList = new ArrayList<Pos>();
				if (isIn ^ isMask) {
					posList.add(line.from);
				}
				for (CrossInfo c : cpList) {
					posList.add(c.pos);
				}
				if (posList.size() % 2 != 0) {
					posList.add(line.to);
				}
				int n = posList.size();
				for (int i = 0; i < n; i+=2) {
					newLineList.add(new Line(posList.get(i), posList.get(i+1)));
				}
			}
		}
		
		return newLineList;
	}

}
