package com.github.novisoftware.patternDraw.core.control;

public class Looper implements ControllBase {
	// 単純なループ(添え字なし固定回数ループ)
	public int loopCounter;
	int loopFrom;
	int loopTo;
	int skip = 1;

	public Looper(int loopFrom, int loopTo) {
		this.loopFrom = loopFrom;
		this.loopTo = loopTo;

		this.loopCounter = loopFrom;
	}

	/**
	 * 今回ループ実行があるか?
	 *
	 */
	public boolean hasNext() {
		// 注:
		// C や Java では 「 ループインデックス < 最大 」でループ処理が行われることが多い。
		// ループカウンタの最大値を INT_MAX にできないため。
		// 特に気にする必要がないため、「 ループインデックス ≦ 最大値 」の条件でループする。
		if (loopCounter <= this.loopTo) {
			return true;
		}

		return false;
	}

	/**
	 * 次状態に遷移
	 *
	 */
	public void nextState() {
		loopCounter += skip;
	}


	public String getDebugString() {
		return String.format("添字なし固定回数ループ, from %d to %d", loopFrom, loopTo);
	}
}
