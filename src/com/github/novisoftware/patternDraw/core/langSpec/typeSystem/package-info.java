/**
 * 型の体系を定義しています。
 *
 * 値の型。
 * 単精度浮動小数点や、精度に限定のある整数型は使用しない。
 * 大は小を兼ねるというか、この処理系は遅いので、単精度を使っても速くなる期待がないため。
 *
 * <ul>
 * <li> STRING     文字列
 * <li> BOOLEAN    真偽値
 * <li> SCALAR     ( FLOAT, NUMERIC, INTEGER )のいずれかを表す抽象型。
 * <ul>
 * <li> INTEGER    多倍長整数
 * <li> FLOAT      倍精度浮動小数点(注: 単精度をサポートしない)
 * <li> NUMERIC    多倍長小数
 * </ul>
 * <li> POS_LIST   座標のリスト
 * <li> LINE_LIST  線分のリスト
 * </ul>
 *
 * ほかに以下。
 * <ul>
 * <li> ANY	       何でもよい
 * <li> NONE       値が存在しない
 * </ul>
 *
 */
package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;