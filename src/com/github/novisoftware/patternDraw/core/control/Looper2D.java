package com.github.novisoftware.patternDraw.core.control;

import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueTransform;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueNumeric;

/**
 * 2次元ループ
 */
public class Looper2D implements ControllBase {
	int xLoopCounter;
	int yLoopCounter;
	final int xN;
	final int yN;
	// 変数セット
	final HashMap<String, Value> variables;
	// インデックスループの場合の代入先の変数名
	final String varName_pos;
	final String varName_Xn;
	final String varName_Yn;

	public Looper2D(HashMap<String, Value> variables,
			String varName_pos,
			String varName_Xn, 
			String varName_Yn,
			int xN, int yN) {
		this.variables = variables;
		this.varName_pos = varName_pos;
		this.varName_Xn = varName_Xn;
		this.varName_Yn = varName_Yn;
		this.xN = xN;
		this.yN = yN;

		init();
	}

	private void init() {
		this.xLoopCounter = 0;
		this.yLoopCounter = 0;
	}
	
	/**
	 * 今回ループ実行があるか?
	 *
	 */
	public boolean hasNext() {
		// 注:
		// X軸, Y軸 それぞれに対し 0, 1, 2, ... (N-1) のループをする。
		// Xを先に動かし(内側)、次に Yを動かす。
		if (this.yLoopCounter >= this.yN) {
			return false;
		}

		// インデックスループの場合は、変数名を設定する
		this.variables.put(varName_Xn, new ValueNumeric("" + xLoopCounter));
		this.variables.put(varName_Yn, new ValueNumeric("" + yLoopCounter));

		this.variables.put(varName_pos,
				ValueTransform.createMove(
						(double) this.xLoopCounter,
						(double) this.yLoopCounter));

		return true;
	}

	/**
	 * 次状態に遷移
	 *
	 */
	public void nextState() {
		this.xLoopCounter ++;
		if (this.xLoopCounter >= this.xN) {
			this.xLoopCounter = 0;
			this.yLoopCounter ++;
		}
	}

	public String getDebugString() {
		return String.format("2次元充填ループ, xN=%d yN=%d", this.xN, this.yN);
	}
}
