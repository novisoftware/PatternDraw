package com.github.novisoftware.patternDraw.gui.editor.parts.controlSub;

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
		if (loopCounter < this.loopTo) {
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
