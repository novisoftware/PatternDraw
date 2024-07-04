package com.github.novisoftware.patternDraw.core.control;

import java.math.BigDecimal;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueNumeric;

public class Looper implements ControllBase {
	// 単純なループ(添え字なし固定回数ループ)
	public BigDecimal loopCounter;
	BigDecimal loopFrom;
	BigDecimal loopTo;
	BigDecimal skip;
	// 変数セット
	final HashMap<String, Value> variables;
	// インデックスループの場合の代入先の変数名
	final String varName;

	public Looper(
			BigDecimal loopFrom,
			BigDecimal loopTo,
			BigDecimal skip
			) {
		this.variables = null;
		this.varName = null;
		this.loopFrom = loopFrom;
		this.loopTo = loopTo;
		this.skip = skip;

		this.loopCounter = loopFrom;
	}

	public Looper(HashMap<String, Value> variables, String varName, BigDecimal loopFrom, BigDecimal loopTo, BigDecimal skip) {
		this.variables = variables;
		this.varName = varName;
		this.loopFrom = loopFrom;
		this.loopTo = loopTo;
		this.skip = skip;

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
		if (loopCounter.compareTo(this.loopTo) <= 0) {
			if (this.variables != null) {
				// インデックスループの場合は、変数名を設定する
				this.variables.put(varName, new ValueNumeric(loopCounter));
			}
			
			return true;
		}

		return false;
	}

	/**
	 * 次状態に遷移
	 *
	 */
	public void nextState() {
		loopCounter =loopCounter.add(skip);
	}

	public String getDebugString() {
		return String.format("添字なし固定回数ループ, from %s to %s", loopFrom.toString(), loopTo.toString());
	}
}
