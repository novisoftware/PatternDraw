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
	
	private static final double MY_EPSILON = 1e-08;
	
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
		final Pos pos;
		final Line line1;
		final Line line2;
		
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
	 * 与えられた座標リストの外側になるような X 位置を計算する(左側)。
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


	/**
	 * 与えられた座標リストの外側になるような X 位置を計算する(右側)。
	 * 
	 * 
	 * @param posList1
	 * @param posList2
	 * @return
	 */
	public static double calcOuterX2(ArrayList<Pos> posList1, ArrayList<Pos> posList2) {
		// 一番左の位置の初期値
		// 確実に両方の外側であるような位置を、適当で良いので探す
		double outerX = 0;
		// 一番左の位置を探す。
		for (Pos p : posList1) {
			if (p.getX() > outerX) {
				outerX = p.getX();
			}
		}
		for (Pos p : posList2) {
			if (p.getX() > outerX) {
				outerX = p.getX();
			}
		}
		// 適当にもう少し左にずらす
		outerX += 1;
		
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
	
	/**
	 * 2座標の組み合わせ
	 */
	static class TwoPos {
		Pos pos1;
		Pos pos2;
		
		TwoPos(Pos pos1, Pos pos2) {
			this.pos1 = pos1;
			this.pos2 = pos2;
		}
		
		@Override
		public int hashCode() {
			return pos1.hashCode() * 3 + pos2.hashCode() * 7;
		}
		
		@Override
		public boolean equals(Object o) {
			TwoPos p = (TwoPos)o;
			return this.pos1 == p.pos1 && this.pos2 == p.pos1;
		}
	}

	/**
	 * 点のリストで与えられたポリゴンを、点のリストで与えられるポリゴンでクリッピングする
	 * 
	 * @param posList1
	 * @param posList2
	 * @return
	 */
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

		// 「どちらの多角形から見ても、必ず外側になるようなX座標」を求めておく(左側)。
		double outerX = calcOuterX(posList2, posList2);

		// 「どちらの多角形から見ても、必ず外側になるようなX座標」を求めておく(右側)。
		double outerX2 = calcOuterX2(posList2, posList2);

		
		// 「クリッピング領域とクリッピング領域の交点」をリストアップする
		ArrayList<CrossInfo> allCrossInfo = new ArrayList<CrossInfo>();
		HashMap<Line, List<CrossInfo>> lineToCrossInfo = new HashMap<Line, List<CrossInfo>>();
		for (int i = 0 ; i < n ; i++) {
			Line line1 = lineList1.get(i);
			List<CrossInfo> cpList1 = crossPoints(line1, lineList2);
			lineToCrossInfo.put(line1, cpList1);
			allCrossInfo.addAll(cpList1);
		}

		// line2 の系列を起点とした交点の情報を作る
		// (作成済のallCrossInfoから構築する)
		HashMap<Line, ArrayList<CrossInfo>> work = new HashMap<Line, ArrayList<CrossInfo>>();
		for (int i = 0 ; i < m ; i++) {
			work.put(lineList2.get(i), new ArrayList<CrossInfo>());
		}
		for (CrossInfo crossInfo : allCrossInfo) {
			Line line2 = crossInfo.line2;
			ArrayList<CrossInfo> crossInfoList = work.get(line2);
			crossInfoList.add(crossInfo);
		}
		for (int i = 0 ; i < m ; i++) {
			Line line2 = lineList2.get(i);
			ArrayList<CrossInfo> crossInfoList = work.get(line2);
			
			// 出発点を持つ Comparator を new して、与えられた座標を比較するコンパレーター
			CrossInfoComparator c = new CrossInfoComparator(line2.from) ;
			// 出発点に近い順にソート
			crossInfoList.sort(c);
		}
		
		for (Line k : work.keySet()) {
			lineToCrossInfo.put(k, work.get(k));
		}

		// クリッピング領域の、交点、頂点のリスト
		// → クリッピング領域とクリッピング領域の交点のリスト
		// 以下を作成する。
		// ・クリッピング対象とクリッピング領域の交点
		// ・クリッピング対象の頂点
		// ・クリッピング領域の頂点
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

		// 内側の点なのかを判定する1
		for (int i=0 ; i<m; i++) {
			Line line2 = lineList2.get(i);
			CrossInfoComparator c = new CrossInfoComparator(line2.from) ;
			ArrayList<CrossInfo> ciList = line2ToCrossInfo.get(line2);
			ciList.sort(c);

			boolean isIn2 = isIn(outerX, posList2.get(i), lineList1)
					&& 
					// 左側からだけだと判定しきれないので、右側からも判定する
					isIn(outerX2, posList2.get(i), lineList1);
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

		// 内側の点なのかを判定する2
		for (int i=0 ; i<n; i++) {
			Line line1 = lineList1.get(i);
			/*
			CrossInfoComparator c = new CrossInfoComparator(line1.from) ;
			ArrayList<CrossInfo> ciList = line2ToCrossInfo.get(line1);
			ciList.sort(c);
			*/

			boolean isIn = isIn(outerX, posList1.get(i), lineList2)
					&& isIn(outerX2, posList1.get(i), lineList2);
			if (isIn) {
				posSeries2.add(new PosInfo(line1.from, Kind.edge));
			}
		}
		
		
		System.out.println("seize of posSeries2 = " + posSeries2.size());

		// System.out.println("##############################");
		
		// 線分の連結情報
		HashSet<TwoPos> lineInfo = new HashSet<TwoPos>();
		HashSet<Pos> posSet = new HashSet<Pos>();

		boolean isIn = false;
		boolean isIn2 = false;
		for (int i = 0 ; i < n ; i++) {
			// 始点が領域の内側にいるか?
			if (i == 0) {
				isIn = isIn(outerX, posList1.get(i), lineList2);
			} else {
				isIn = isIn2;
			}
			// 終点が領域の内側にいるか?
			isIn2 = isIn(outerX, posList1.get((i+1)%n), lineList2);

			boolean flag = isIn;
			Line line1 = lineList1.get(i);
			List<CrossInfo> cpList = lineToCrossInfo.get(line1);
			Pos pos0 = posList1.get(i);
			for (CrossInfo ci : cpList) {
				Pos pos1 = ci.pos;
				if (flag) {
					lineInfo.add(new TwoPos(pos0, pos1));
					lineInfo.add(new TwoPos(pos1, pos0));
					posSet.add(pos0);
					posSet.add(pos1);
				}
				pos0 = pos1;
				flag = !flag;
			}

			if (isIn2) {
				Pos pos1 = posList1.get((i+1)%n);
				lineInfo.add(new TwoPos(pos0, pos1));
				lineInfo.add(new TwoPos(pos1, pos0));
				posSet.add(pos0);
				posSet.add(pos1);
			}
		}

		for (int i = 0 ; i < m ; i++) {
			// 始点が領域の内側にいるか?
			if (i == 0) {
				isIn = isIn(outerX, posList2.get(i), lineList1);
			} else {
				isIn = isIn2;
			}
			// 終点が領域の内側にいるか?
			isIn2 = isIn(outerX, posList2.get((i+1)%m), lineList1);

			boolean flag = isIn;
			Line line2 = lineList2.get(i);
			List<CrossInfo> cpList = lineToCrossInfo.get(line2);
			Pos pos0 = posList2.get(i);
			for (CrossInfo ci : cpList) {
				Pos pos1 = ci.pos;
				if (flag) {
					lineInfo.add(new TwoPos(pos0, pos1));
					lineInfo.add(new TwoPos(pos1, pos0));
					posSet.add(pos0);
					posSet.add(pos1);
				}
				pos0 = pos1;
				flag = !flag;
			}

			if (isIn2) {
				Pos pos1 = posList2.get((i+1)%m);
				lineInfo.add(new TwoPos(pos0, pos1));
				lineInfo.add(new TwoPos(pos1, pos0));
				posSet.add(pos0);
				posSet.add(pos1);
			}
		}

		HashSet<TwoPos> addedLineSet = new HashSet<TwoPos>();
		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		HashSet<Pos> newPosSet = new HashSet<Pos>();

		if (lineInfo.isEmpty()) {
			return newPosList;
		}

		
		Pos head = null;
		for (Pos p: posSet) {
			head = p;
			break;
		}
		newPosList.add(head);
		newPosSet.add(head);

		System.out.println("lineInfo size = " + lineInfo.size());
		for (TwoPos twoPos : lineInfo) {
			Pos p0 = twoPos.pos1;
			System.out.println("pos1 of two pos ... (" + p0.getX() + " , " + p0.getY() + ")");
			Pos p2 = twoPos.pos2;
			System.out.println("pos2 of two pos ... (" + p2.getX() + " , " + p2.getY() + ")");
		}

		
		System.out.println("posSeries2 size = " + posSeries2.size());
		for (PosInfo posInfo: posSeries2) {
			Pos p = posInfo.pos;
			System.out.println("pos ... (" + p.getX() + " , " + p.getY() + ")");
		}
		
		while (true) {
			boolean changed = false;
			for (PosInfo posInfo: posSeries2) {
				Pos p = posInfo.pos;
				if (head == p) {
					continue;
				}
				/*
				if (addedLineSet.contains(new TwoPos(head, p))) {
					continue;
				}
				*/
				boolean alreadyAdded = false;
				for (TwoPos twoPos : addedLineSet) {
					if (twoPos.pos1.isSamePosStrict(head) && twoPos.pos2.isSamePosStrict(p)) {
						alreadyAdded = true;
						break;
					}
				}
				if (alreadyAdded) {
					continue;
				}

				boolean find = false;
				for (TwoPos twoPos : lineInfo) {
					if (twoPos.pos1.isSamePosStrict(head) && twoPos.pos2.isSamePosStrict(p)) {
						find = true;
						break;
					}
				}
				if (find) {
					newPosList.add(p);
					newPosSet.add(p);
					addedLineSet.add(new TwoPos(head, p));
					addedLineSet.add(new TwoPos(p, head));
					head = p;
					changed = true;
					break;
				}
				
				/*
				if (lineInfo.contains(new TwoPos(head, p))) {
					newPosList.add(p);
					newPosSet.add(p);
					addedLineSet.add(new TwoPos(head, p));
					addedLineSet.add(new TwoPos(p, head));
					head = p;
					changed = true;
				}*/
			}

			if (!changed) {
				break;
			}
		}
		
		System.out.println("newPosList size = " + newPosList.size());
		
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
			// 判定対象の線分の開始点がクリッピング領域に含まれるか
			boolean isIn = isIn(outerX, line.from, lineList2);
			// 判定対象の線分の終了点がクリッピング領域に含まれるか
			boolean isIn2 = isIn(outerX, line.to, lineList2);

			List<CrossInfo> cpList = crossPoints(line, lineList2);
			if ((cpList.size() % 2 == 0 ) != (isIn == isIn2)) {
				// 矛盾あり
				// 点を微小に移動させ再判定する。
				// 判定結果が異なるなら線分を作成しても良いかもしれないが、
				// 微小であるため、もし線分のデータを作っても描画に反映されない。
				// (見えない線分は作っても仕方がないので作らない)
				if (line.length2() != 0) {
					boolean a = isIn(outerX, line.from.mix(line.to, 0.001), lineList2);
					if (a != isIn) {
						// TODO: 結局たんなる代入と同じだが。
						isIn = a;
					}
					boolean b = isIn(outerX, line.to.mix(line.from, 0.001), lineList2);
					if (b != isIn2) {
						isIn = b;
					}
					// さらに矛盾が残る可能性があるが追求しない。
				}
			} else {
				// 矛盾なし
			}
			
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
