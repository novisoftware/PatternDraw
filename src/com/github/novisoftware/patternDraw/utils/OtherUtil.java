package com.github.novisoftware.patternDraw.utils;

import java.util.AbstractCollection;

public class OtherUtil {

	/**
	 * abc1, abc2, abc3 ... のような連番で、ユニークな名前を生成します。
	 *
	 * @param inputName
	 * @return
	 */
	/**
	 *
	 * @param inputName inputName 連番元(例: abc3)
	 * @param nameSet 名前のセット (例: (abc1, abc2, abc3) )
	 * @return 生成した連番(例: abc4)
	 */
	static public String generateUniqueName(String inputName, AbstractCollection<String> nameSet) {
		// 衝突しない名前を設定する
		// （末尾の数字を１加算する）
		final String REG = "(.*)([0-9]+)";
	
		String baseName = inputName.replaceAll(REG, "$1");
		String number = inputName.replaceAll(REG, "$2");
		int d = 0;
		try {
			d = Integer.parseInt(number);
		} catch(Exception ex) {
		}
	
		// 名前が衝突しなくなるまでインクリメントする
		String name = "";
		while (true) {
			d++;
			name = baseName + d;
			if (! nameSet.contains(name)) {
				break;
			}
		}
	
		return name;
	}

}
