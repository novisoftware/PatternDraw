package com.github.novisoftware.patternDraw.geometricLanguage.entity;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;

public class PosUtil {
	// このクラスはインスタンス化しない
	private PosUtil() {
	}
	
	/**
	 * 並びを逆順にする。
	 * 
	 * @param posList
	 * @return 並びを逆順にしたリスト。
	 */
	public static ArrayList<Pos> reverse(ArrayList<Pos> posList) {
		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		for (int i = posList.size() - 1; i >=0 ; i++ ) {
			newPosList.add(posList.get(i));
		}
		
		return newPosList;
	}

	/**
	 * テクスチャとしてあてはめる
	 * 
	 * @param basePosList ベース側
	 * @param patternPosList パターン側
	 * @return
	 * @throws CaliculateException 
	 */
	public static ArrayList<Pos> applyAsTexture(ArrayList<Pos> basePosList, ArrayList<Pos> patternPosList) throws CaliculateException {
		if (basePosList.size() < 2 || patternPosList.size() < 2) {
			// 点が1個しかない場合はエラー。
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_VALUE, "点が2つ以上必要です。");
		} else {
			// 始点と終点が完全に同じ場所の場合はエラー。
			Pos start = patternPosList.get(0);
			Pos end = patternPosList.get(patternPosList.size() - 1);
			if (start.isSamePosStrict(end)) {
				throw new CaliculateException(CaliculateException.MESSAGE_INVALID_VALUE,
						"パターンの開始と終了の位置が同じです");
			}
		}

		ArrayList<Pos> result = new ArrayList<Pos>();

		ArrayList<Pos> texturePosList = Pos.move_to_origin(patternPosList);


		Pos textureStartPos = texturePosList.get(0);
		Pos textureEndPos = texturePosList.get(texturePosList.size()-1);
		double textureTh = textureEndPos.atan(textureStartPos);
		double textureLength = textureEndPos.distance(textureStartPos);

		int n = basePosList.size();
		for (int i = 0; i < n ; i++) {
			Pos startPos1 = basePosList.get(i);
			Pos endPos1 = basePosList.get((i+1)%n);

			double baseTh = endPos1.atan(startPos1);
			double baseLength = endPos1.distance(startPos1);

			double ratio = baseLength / textureLength;

			ArrayList<Pos> work1 = Pos.rotate(texturePosList, - baseTh + textureTh);
			ArrayList<Pos> work2 = Pos.scale(work1, ratio);
			ArrayList<Pos> work3 = Pos.move(work2, startPos1);
			// ArrayList<Pos> work3 = work2;

			result.addAll(work3);
		}

		return result;
	}
}
