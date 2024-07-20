/**
 * クラス階層は以下。
 * (LangSpecExceptionはこれらとは別に、単発で定義している例外)
 * 
 * <ul>
 * <li> 《EvaluateException抽象クラス》
 * <ul>
 * <li> CaliculateException 計算にエラーが発生した場合に発生させる
 * <li> 《ControlSignal抽象クラス》
 * <ul>
 * <li> BreakSignal ループのbreakで発生させる
 * <li> ContinueSignal ループのcontinueで発生させる
 * </ul>
 * </ul>
 * </ul>
 */
package com.github.novisoftware.patternDraw.core.exception;