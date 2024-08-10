package com.github.novisoftware.patternDraw.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.novisoftware.patternDraw.core.exception.BreakSignal;
import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.exception.ContinueSignal;
import com.github.novisoftware.patternDraw.core.exception.EvaluateException;
import com.github.novisoftware.patternDraw.core.exception.LangSpecException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.TypeUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.TypeUtil.TwoValues;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueBoolean;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueString;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueAbstractScalar;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueFloat;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueInteger;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.scalar.ValueNumeric;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;


public class Rpn {
	private String formula;
	private ArrayList<String> array;
	private String displayString;
	/**
	 * このRPNのコメント。
	 */
	private String comment;

	private final NetworkDataModel networkDataModel;

	public Rpn(String formula, NetworkDataModel networkDataModel) {
		this.formula = formula;
		this.array = RpnUtil.s2a(formula);
		this.networkDataModel = networkDataModel;
		this.makeDisplayString();
	}

	public Rpn(ArrayList<String> array, NetworkDataModel networkDataModel) {
		this.formula = RpnUtil.a2s(array);
		this.array = array;
		this.networkDataModel = networkDataModel;
		this.makeDisplayString();
	}

	public Rpn convSetToRecallVariable() {
		ArrayList<String> newArray = new ArrayList<String>();
		for (String s : this.array) {
			if (s.startsWith("'")) {
				newArray.add(s.replaceAll(";.*", ""));
			}
			if (s.equals(":set-variable")) {
				newArray.add(":recall-variable");
			}
			else {
				// newArray.add(s);
			}
		}

		return new Rpn(newArray, this.networkDataModel);
	}

	/**
	 * 表示等に使用する文字列を生成します。
	 *
	 */
	private void makeDisplayString() {
		Stack<String> stack = new Stack<>();

		for(String op : this.getExpandedArray()) {
			if (op.startsWith(":")) {
				FunctionDefInterface f = FunctionUtil.getFunctionDef_internal(op.substring(1));
				if (f != null) {
					stack.push(':' + f.getDisplayName());
					break;
				}
			}
			
			if (op.startsWith("'")) {
				stack.push( op.substring(1));
			}
			else if (op.equals(":set-variable")) {
			}
			else if (op.equals(":recall-variable")) {
			}
			else if (op.equals(":as-numeric")) {
			}
			else if (op.equals(":as-boolean")) {
			}
			else if (op.equals(":as-display-string")) {
				// この ":as-display-string" があったら、スタックトップが表示用文字列で確定
				break;
			}
			else {
				stack.push(op.replaceAll(";.*", ""));
			}
		}

		String value = stack.pop();
		String display = value.replaceAll("^:", "");
		this.displayString = RpnUtil.getRepresent(display);
		this.comment = RpnUtil.getComment(value);
	}

	/**
	 * @param formula セットする formula
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @return formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 *
	 * @return array
	 */
	public ArrayList<String> getArray() {
		return array;
	}

	public ArrayList<String> getExpandedArray() {
		return RpnMacroUtil.expandMacro(this.array);
	}

	public boolean hasMacro() {
		return RpnMacroUtil.hasMacro(this.array);
	}

	public void setRepeatN(int n) {
		this.array = RpnMacroUtil.setRepeatN(this.array, n);
		this.formula = RpnUtil.a2s(array);
	}

	public int getRepeatN() {
		return RpnMacroUtil.getRepeatN(this.array);
	}

	/**
	 *
	 * @return array
	 */
	public ArrayList<String> getArrayCopy() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.addAll(array);
		return ret;
	}

	/**
	 * 作成してある表示用文字列を返却する
	 *
	 * @return 表示用文字列
	 */
	public String getDisplayString() {
		return displayString;
	}

	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}


	public static ArrayList<String> s2a(String s) {
		return FileReadUtil.tokenize(s);
	}


	public static String getParamName(String s) {
		if (s.startsWith("<")) {
			return (s.replaceAll("[<>]","").replaceAll(":.*",""));
		}
		return null;
	}

	public static String getParamType(String s) {
		if (s.startsWith("<")) {
			String work = s.replaceAll("[<>]","");

			String[] a = work.split(":");
			if (a.length > 1) {
				return a[1];
			}
		}
		return null;
	}


	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	private ValueType lastOpToValueType(String op) {
		if (op.equals(":print")) {
			return ValueType.NONE;
		} else if (op.equals(":input:integer")) {
			return ValueType.INTEGER;
		} else if (op.equals(":set-variable")) {
			return ValueType.NONE;
		} else if (op.equals(":recall-variable")) {
			return ValueType.UNDEF;
		} else if (op.equals(":as-display-string")) {
			return ValueType.STRING;
		} else if (op.equals(":as-numeric")) {
			return ValueType.SCALAR;
		} else if (op.equals(":pi")) {
			return ValueType.FLOAT;
		} else if (op.equals(":e")) {
			return ValueType.FLOAT;
		} else if (op.equals(":as-boolean")) {
			return ValueType.BOOLEAN;
		} else if (op.equals(":if")) {
			return ValueType.NONE;
		} else if (op.equals("-") || op.equals("/") || op.equals("%") || op.equals("+") || op.equals("*")) {
			return ValueType.SCALAR;
		} else if (op.equals("^")) {
			return ValueType.SCALAR;
		} else if (op.equals(":C") || op.equals(":P")) {
			return ValueType.INTEGER;
		} else if (op.equals("!")) {
			return ValueType.INTEGER;
		} else if (op.equals(">") || op.equals(">=") || op.equals("<") || op.equals("<=") || op.equals("==")
				|| op.equals("!=")) {
			return ValueType.BOOLEAN;
		} else if (op.equals(":continue") || op.equals(":break")) {
			return ValueType.NONE;
		} else if (op.equals(":and") || op.equals(":or") || op.equals(":xor")
				|| op.equals(":nand") || op.equals(":nor") || op.equals(":xnor")) {
			return ValueType.BOOLEAN;
		} else if (op.equals(":not")) {
			return ValueType.BOOLEAN;
		} else if (op.equals(":join")) {
			return ValueType.STRING;
		}

		return null;
	}

	/**
	 * 結果の型を求める
	 * 
	 * @param ele
	 * @param variables
	 * @param paramDefList
	 * @param workCheckTypeVariables
	 * @param networkDataModel2
	 * @return
	 */
	public ValueType evaluateValueType(P022_____RpnGraphNodeElement ele,
			HashMap<String, Value> variables,
			ArrayList<ParameterDefine> paramDefList,
			HashMap<String, ValueType> workCheckTypeVariables,
			NetworkDataModel networkDataModel2
			
			) {
		Stack<ValueType> stack = new Stack<>();
		Stack<String> stringStack = new Stack<>();

		Debug.println("");
		Debug.println("::CHECK RPN::   " + this.getExpandedArray());

		ArrayList<String> inputArray = this.getExpandedArray();
		
		// ArrayList<String> array = Rpn.s2a(this.getRpnString());
		for (String s : inputArray) {
			/**
			 * 端子についているパラメーターを取得
			 */
			String paraName = getParamName(s);
			if (paraName != null) {
				// ワードにパラメーター名がある。
				// 端子を作ったりするときに使う。
				// Debug.println("IN LOOP paraName = " + paraName);

				P021____AbstractGraphNodeElement src = ele.paramMapObj.get(paraName);
				if (src == null) {
					// 端子にパラメーターが設定されていない。
					// (該当ノードを計算エラーにして、計算を打ち切らせるような場合)
					// 端子がないとしても、ある程度は限定できるはずなので、それを考慮する。
					String lastOp = inputArray.get(inputArray.size() - 1);
					return lastOpToValueType(lastOp);
				}

				// Debug.println("para valueType = " + src.actualValueTypeResult );

				stack.push(src.actualValueTypeResult);
				continue;
			}

			if (s.equals(":print")) {
				return ValueType.NONE;
			}
			else if (s.equals(":input:integer")) {
				return ValueType.INTEGER;
			}
			else if (s.equals(":set-variable")) {
				String name = stringStack.pop();
				ValueType valueType = stack.pop();
				Debug.println(":set-variable " + name + "  valueType = " + valueType);

				workCheckTypeVariables.put(name, valueType);
				return ValueType.NONE;
			}
			else if (s.equals(":recall-variable")) {
				String name = stringStack.pop();
				Debug.println(":recall-variable " + name + "");

				
				// コントロールで定義している変数かを調べる
				HashMap<String, ValueType> wkControl = networkDataModel2.checkVariableType(ele);
				ValueType valueType = wkControl.get(name);
				if (valueType == null) {
					// 見つからなければ継続してグローバルな定義を検索
					valueType = workCheckTypeVariables.get(name);
				}
				if (valueType != null) {
					Debug.println("    変数名 - ValueType のハッシュ表の中に存在: " + valueType);
					stack.push(valueType);
				}
				else {
					// 初期パラメーターを参照する場合
					boolean isDone = false;
					for (ParameterDefine def : paramDefList) {
						if (def.name.equals(name)) {
							isDone = true;
							stack.push(def.valueType);

							Debug.println("変数 " + name + " を参照(型は " + def.valueType + ")");
						}
					}

					if (! isDone) {
						Debug.println("    ハッシュ表の中にも、初期パラメーターの中にもない");
						// TODO 見つからないのは、この演算子のエラー。
						// エラーを記録するコードを入れる。

						stack.push(ValueType.UNDEF);
					}
				}
			}
			else if (s.equals(":as-display-string")) {
				// ":as-display-string" は、表示用の文字列を指定する。
				// 計算の際はオペランドを捨て、使用しない。
				stack.pop();
			}
			else if (s.equals(":as-numeric")) {
				if (ele.getValueType().equals(Value.ValueType.INTEGER)) {
					stack.push(ValueType.INTEGER);
				}
				else if (ele.getValueType().equals(Value.ValueType.FLOAT)) {
					stack.push(ValueType.FLOAT);
				}
				else if (ele.getValueType().equals(Value.ValueType.NUMERIC )) {
					stack.push(ValueType.NUMERIC);
				}
			}
			else if (s.equals(":pi")) {
				// 型が値なしとなってしまうための対処。
				// TODO これはその場しのぎの応急対処
				// stack.push(ValueType.FLOAT);
				return ValueType.FLOAT;
			}
			else if (s.equals(":e")) {
				// 型が値なしとなってしまうための対処。
				// TODO これはその場しのぎの応急対処
				// stack.push(ValueType.FLOAT);
				return ValueType.FLOAT;
			}
			else if (s.equals(":as-boolean")) {
				stack.push(ValueType.BOOLEAN);
			}
			else if (s.equals(":if")) {
				ValueType b0 = stack.pop();
				ValueType a0 = stack.pop();
				stack.pop();

				stack.push(TypeUtil.upCastValueType(a0, b0));
			}
			else if (s.equals("-") || s.equals("/") || s.equals("%") || s.equals("+") || s.equals("*")) {
				ValueType b0 = stack.pop();
				ValueType a0 = stack.pop();
				stack.push(TypeUtil.upCastValueType(a0, b0));
			}
			else if (s.equals("^")) {
				ValueType b0 = stack.pop();
				ValueType a0 = stack.pop();
				stack.push(TypeUtil.upCastValueType(a0, b0));
			}
			else if (s.equals(":C") || s.equals(":P")) {
				stack.pop();
				stack.pop();
				stack.push(ValueType.INTEGER);
			}
			else if (s.equals("!")) {
				stack.pop();
				stack.push(ValueType.INTEGER);
			}
			else if (s.equals(">") || s.equals(">=") || s.equals("<") || s.equals("<=") || s.equals("==") || s.equals("!=")) {
				stack.pop();
				stack.pop();
				stack.push(ValueType.BOOLEAN);
			}
			else if (s.equals(":continue") || s.equals(":break")) {
				return ValueType.NONE;
			}
			else if (s.equals(":and") || s.equals(":or") || s.equals(":xor")
					|| s.equals(":nand") || s.equals(":nor") || s.equals(":xnor")) {
				stack.pop();
				stack.pop();
				stack.push(ValueType.BOOLEAN);
			}
			else if (s.equals(":not")) {
				stack.pop();
				stack.push(ValueType.BOOLEAN);
			}
			else if (s.equals(":join")) {
				/*
				stack.pop();
				stack.pop();
				stack.push(ValueType.STRING);
				*/
				return ValueType.STRING;
			}
			else if (s.startsWith(":")) {
				FunctionDefInterface f;
				try {
					f = FunctionUtil.getFunctionDef(s.substring(1));
					return f.getReturnType();
				} catch (LangSpecException e) {
					System.err.println("解決不能な rpn 参照 " + s);
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			
			else if (s.startsWith("'")) {
				stringStack.push( RpnUtil.getRepresent(s.substring(1)) );
			}
			else {
				stack.push(ValueType.STRING);
			}
		}

		if (stack.size() > 0) {
			Debug.println("RPN DETAIL", "result " + stack.peek());
		}
		else {
			Debug.println("RPN DETAIL", "result stack empty.");
		}

		if (stack.size() > 0) {
			return stack.pop();
		}

		return null;
	}

	/**
	 * 計算して値を求める
	 * 
	 * @param ele
	 * @param variables
	 * @return
	 * @throws EvaluateException
	 * @throws InterruptedException
	 */
	public Value doCaliculate(P022_____RpnGraphNodeElement ele, HashMap<String, Value> variables)
		throws EvaluateException, InterruptedException {
		Stack<Value> stack = new Stack<>();
		Stack<String> stringStack = new Stack<>();

		// ArrayList<String> array = Rpn.s2a(this.getRpnString());
		for (String s : RpnMacroUtil.expandMacro(this.array)) {
			// Thread に interrupt が入ったら終了させる作りにする
			if (Thread.currentThread().isInterrupted()) {
				throw new CaliculateException(CaliculateException.MESSAGE_INTERRUPTED);
			}

			Debug.println("RPN DETAIL", "op is ***" + s + "***");
			Debug.println("STACK DEBUG 1/2 (Value stack)", "");
			for (Value x : stack) {
				Debug.print("  ("  + x.toString()  + " : " + x.valueType + ")");
			}
			Debug.println();
			Debug.println("STACK DEBUG 1/2 (string stack)", "");
			for (String x : stringStack) {
				Debug.print("  ("  + x  + ")");
			}
			Debug.println();


			/**
			 * 端子についているパラメーターを取得
			 */
			String paraName = getParamName(s);
			if (paraName != null) {
				P021____AbstractGraphNodeElement src = ele.paramMapObj.get(paraName);
				if (src == null) {
					// 端子にパラメーターが設定されていない。
					// 該当ノードを計算エラーにして、計算を打ち切らせたいので
					// CaliculateExceptionをthrowする
					throw new CaliculateException(CaliculateException.MESSAGE_NOT_ENOUGH_INPUT);
				}
				stack.push(src.workValue);
				continue;
			}

			if (s.equals(":print")) {
				OutputTextInterface outputTextInterface = OutputTextWindow.getInstance();
				outputTextInterface.println(stack.pop().toString());
			}
			else if (s.equals(":join")) {
				String s2 = stack.pop().toString();
				String s1 = stack.pop().toString();

				stack.push(new ValueString(s1 + s2));
			}
			else if (s.equals(":input:integer")) {
				// インタラクティブな入力（試作レベル）
				// TODO:
				// 消すか、書き直すかを検討する。
				// (これは要らないような気もする)
				while (true) {
					System.out.print("整数を入力してください: ");
					String inputString = null;
					try {
						inputString = bufferedReader.readLine();

						Value inputValue;
						try {
							inputValue = new ValueNumeric(inputString);
							stack.push(inputValue);
							break;
						} catch( Exception ex ) {
							System.out.println("読み取ることができません。" + ex.toString());
						}
					} catch (IOException ex) {
						System.out.println("入力の読み取りでエラーが発生しました。" + ex.toString());
						break;
					}
				}
			}
			else if (s.equals(":set-variable")) {
				String name = stringStack.pop();
				Value v = stack.pop();
				variables.put(name,v);
				stack.push(v);

				this.networkDataModel.debugVariables();
			}
			else if (s.equals(":recall-variable")) {
				String name = stringStack.pop();
				Value v = variables.get(name);
				stack.push(v);

				this.networkDataModel.debugVariables();
			}
			else if (s.equals(":as-display-string")) {
				// ":as-display-string" は、表示用の文字列を指定する。
				// 計算の際はオペランドを捨て、使用しない。
				stack.pop();
			}
			else if (s.equals(":as-numeric")) {
				if (ele.getValueType().equals(Value.ValueType.INTEGER)) {
					stack.push(new ValueInteger(stack.pop().toString()));
				}
				else if (ele.getValueType().equals(Value.ValueType.FLOAT)) {
					stack.push(new ValueFloat(stack.pop().toString()));
				}
				else if (ele.getValueType().equals(Value.ValueType.NUMERIC )) {
					stack.push(new ValueNumeric(stack.pop().toString()));
				}
			}
			else if (s.equals(":pi")) {
				stack.push(new ValueFloat(Math.PI));
			}
			else if (s.equals(":e")) {
				stack.push(new ValueFloat(Math.E));
			}
			else if (s.equals(":as-boolean")) {
				stack.push(new ValueBoolean(stack.pop().toString()));
			}
			else if (s.equals(":if")) {
				Value b0 = stack.pop();
				Value a0 = stack.pop();
				Value cond = stack.pop();

				if (!(cond instanceof ValueBoolean)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				if (((ValueBoolean)cond).getInternal()) {
					stack.push(a0);
				} else {
					stack.push(b0);
				}
			}
			else if (s.equals(":continue")) {
				Value cond = stack.pop();
				if (!(cond instanceof ValueBoolean)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				if (((ValueBoolean)cond).getInternal()) {
					throw new ContinueSignal("条件成立によるcontinue", ele);
				}

				return null;
			}
			else if (s.equals(":break")) {
				Value cond = stack.pop();
				if (!(cond instanceof ValueBoolean)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				if (((ValueBoolean)cond).getInternal()) {
					throw new BreakSignal("条件成立によるcontinue", ele);
				}
				
				return null;
			}
			else if (s.equals(":P")) {
				// 順列組合せ
				// https://ja.wikipedia.org/wiki/%E9%A0%86%E5%88%97
				
				Value k0 = stack.pop();
				Value n0 = stack.pop();
				if (!(k0 instanceof ValueInteger)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				if (!(n0 instanceof ValueInteger)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				// n!
				BigInteger n_f = ValueInteger.fractional(Value.getInteger(n0).intValue());
				// (n-k)!
				BigInteger n_k_f = ValueInteger.fractional(
						Value.getInteger(n0).subtract(Value.getInteger(k0)).intValue() );
				BigInteger p = n_f.divide(n_k_f);

				stack.push(new ValueInteger(p));
			}
			else if (s.equals(":C")) {
				// 組合せ
				// https://ja.wikipedia.org/wiki/%E7%B5%84%E5%90%88%E3%81%9B_(%E6%95%B0%E5%AD%A6)

				Value k0 = stack.pop();
				Value n0 = stack.pop();
				if (!(k0 instanceof ValueInteger)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				if (!(n0 instanceof ValueInteger)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				// n!
				BigInteger n_f = ValueInteger.fractional(Value.getInteger(n0).intValue());
				// (n-k)!
				BigInteger n_k_f = ValueInteger.fractional(
						Value.getInteger(n0).subtract(Value.getInteger(k0)).intValue() );
				BigInteger k_f = ValueInteger.fractional(
						Value.getInteger(k0).intValue() );
				BigInteger c = n_f.divide(n_k_f).divide(k_f);

				stack.push(new ValueInteger(c));
			}
			else if (s.equals("!")) {
				Value a0 = stack.pop();
				if (!(a0 instanceof ValueInteger)) {
					// キャストできない型。
					throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
				}
				int a = Value.getInteger(a0).intValue();
				BigInteger work = BigInteger.ONE;
				for (int m = 2; m <= a; m++) {
					work = work.multiply(new BigInteger("" + m));

					// 単発でも処理に時間がかかる可能性があるため、
					// Thread に interrupt が入ったら終了させる作りにする
					if (Thread.currentThread().isInterrupted()) {
						throw new CaliculateException(CaliculateException.MESSAGE_INTERRUPTED);
					}
				}
				stack.push(new ValueInteger(work));
			}
			else if (s.equals("-") || s.equals("/") || s.equals("%") || s.equals("+") || s.equals("*")
					 || s.equals("^")
					) {
				try {
					Value b0 = stack.pop();
					Value a0 = stack.pop();

					try {
						if (!(a0 instanceof ValueAbstractScalar && b0 instanceof ValueAbstractScalar)) {
							// キャストできない型。
							throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
						}
						TwoValues work = TypeUtil.upCast(a0, b0);
						ValueAbstractScalar a = (ValueAbstractScalar)work.a;
						ValueAbstractScalar b = (ValueAbstractScalar)work.b;

						if (s.equals("/") || s.equals("%")) {
							if (b.isZero()) {
								throw new CaliculateException(CaliculateException.MESSAGE_ZERO_DIV);
							}
						}

						if (s.equals("-")) {
							stack.push(a.sub(b));
						}
						else if (s.equals("/")) {
							stack.push(a.div(b));
						}
						else if (s.equals("%")) {
							stack.push(a.mod(b));
						}
						else if (s.equals("+")) {
							stack.push(a.add(b));
						}
						else if (s.equals("*")) {
							stack.push(a.mul(b));
						}
						else if (s.equals("^")) {
							stack.push(a.pow(b));
						}
					} catch(CaliculateException e) {
						throw e;
					} catch(Exception ee) {
						ee.printStackTrace();
						throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
					}
				} catch(CaliculateException e) {
					throw e;
				} catch(Exception e) {
					throw new CaliculateException(CaliculateException.MESSAGE_NOT_ENOUGH_INPUT);
				}
			}
			else if (s.equals(">") || s.equals(">=") || s.equals("<") || s.equals("<=") || s.equals("==") || s.equals("!=")) {

				try {
					Value b0 = stack.pop();
					Value a0 = stack.pop();

					try {
						if (!(a0 instanceof ValueAbstractScalar && b0 instanceof ValueAbstractScalar)) {
							// キャストできない型。
							throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
						}
						TwoValues work = TypeUtil.upCast(a0, b0);
						ValueAbstractScalar a = (ValueAbstractScalar)work.a;
						ValueAbstractScalar b = (ValueAbstractScalar)work.b;

						if (s.equals(">")) {
							stack.push( new ValueBoolean(a.compareInternal(b) > 0 ));
						}
						else if (s.equals(">=")) {
							stack.push( new ValueBoolean(a.compareInternal(b) >= 0 ));
						}
						else if (s.equals("<")) {
							stack.push( new ValueBoolean(a.compareInternal(b) < 0 ));
						}
						else if (s.equals("<=")) {
							stack.push( new ValueBoolean(a.compareInternal(b) <= 0 ));
						}
						else if (s.equals("==")) {
							stack.push( new ValueBoolean(a.compareInternal(b) == 0 ));
						}
						else if (s.equals("!=")) {
							stack.push( new ValueBoolean(a.compareInternal(b) != 0 ));
						}
					} catch(Exception ee) {
						throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
					}
				} catch(Exception e) {
					throw new CaliculateException(CaliculateException.MESSAGE_NOT_ENOUGH_INPUT);
				}
			}
			else if (s.equals(":and") || s.equals(":or") || s.equals(":xor")
					|| s.equals(":nand") || s.equals(":nor") || s.equals(":xnor")) {
				try {
					Value b = stack.pop();
					Value a = stack.pop();
					try {
						if (!(a instanceof ValueBoolean && b instanceof ValueBoolean)) {
							// 型の誤り。
							throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
						}
						ValueBoolean a_ = (ValueBoolean)a;
						ValueBoolean b_ = (ValueBoolean)b;

						if (s.equals(":and")) {
							stack.push(new ValueBoolean(a_.getInternal() && b_.getInternal()));
						} else if (s.equals(":nand")) {
							stack.push(new ValueBoolean(!(a_.getInternal() && b_.getInternal())));
						} else if (s.equals(":or")) {
							stack.push(new ValueBoolean(a_.getInternal() || b_.getInternal()));
						} else if (s.equals(":xor")) {
							stack.push(new ValueBoolean(a_.getInternal() ^ b_.getInternal()));
						} else if (s.equals(":nor")) {
							stack.push(new ValueBoolean(!(a_.getInternal() || b_.getInternal())));
						} else if (s.equals(":xnor")) {
							stack.push(new ValueBoolean(!(a_.getInternal() ^ b_.getInternal())));
						}
					} catch (Exception ee) {
						throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
					}
				} catch (Exception e) {
					throw new CaliculateException(CaliculateException.MESSAGE_NOT_ENOUGH_INPUT);
				}
			}
			else if (s.equals(":not")) {
				try {
					Value a = stack.pop();
					try {
						if (!(a instanceof ValueBoolean)) {
							// 型の誤り。
							throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
						}
						ValueBoolean a_ = (ValueBoolean)a;
						stack.push(new ValueBoolean(!a_.getInternal()));
					} catch (Exception ee) {
						throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
					}
				} catch (Exception e) {
					throw new CaliculateException(CaliculateException.MESSAGE_NOT_ENOUGH_INPUT);
				}
			}
			else if (s.startsWith(":")) {
				try {
					FunctionDefInterface f = FunctionUtil.getFunctionDef(s.substring(1));
					Value[] v = new Value[f.getParameterTypes().length];
					for (int i = 0; i < v.length; i++) {
						v[v.length - 1 - i] = stack.pop();
					}
					ArrayList<Value> vArray = new ArrayList<Value>();
					for (Value v_ : v) {
						vArray.add(v_);
					}
					// TODO
					// 注:
					// 現時点では出力系の関数は呼び出す実装が無いので
					// InstructionRender に null を指定している。
					// もし今後、出力系の関数をrpn 経由で呼び出す場合は InstructionRender を取ってくるような
					// 実装を追加する必要あり。
					f.exec(stack, null);
				} catch (LangSpecException e) {
					// 使用されていた関数が、定義されないものだった場合。
					// (通常のユーザーオペレーションでは再現不能な状況のはず)
					throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
				}
			}
			else if (s.endsWith(";コメント")) {
				stack.push( new ValueString(RpnUtil.getRepresent(s)) );
			}
			else if (s.startsWith("'")) {
				stringStack.push( RpnUtil.getRepresent(s.substring(1)) );
			}
			else {
				try {
					stack.push( new ValueString(RpnUtil.getRepresent(s)) );
				} catch(Exception e) {
					Debug.println("Value is " + s);
					throw e;
				}
			}
		}

		if (stack.size() > 0) {
			Debug.println("RPN DETAIL", "result " + stack.peek());
		}
		else {
			Debug.println("RPN DETAIL", "result stack empty.");
		}

		if (stack.size() > 0) {
			return stack.pop();
		}

		return null;
	}

	/**
	 * 変数名やパラメタ名が変更になったことを通知するインタフェース
	 * 
	 * @param before 変更前
	 * @param after 変更後
	 */
	public void notifyVarNameChange(String before, String after) {
		ArrayList<String> newArray = new ArrayList<String>();
		boolean isChanged = false;
		
		for (String s : array) {
			if (s.startsWith("'")) {
				String r = RpnUtil.getRepresent(s.substring(1));
				if (r.equals(before)) {
					isChanged = true;
					newArray.add("'" + after + ";" + RpnUtil.getComment(s));
				}else {
					newArray.add(s);
				}
			}
			else {
				newArray.add(s);
			}
		}
		
		if (isChanged) {
			// System.out.println("before: " + RpnUtil.a2s(this.array));
			// System.out.println("after: " + RpnUtil.a2s(newArray));
			
			this.array = newArray;
			this.formula = RpnUtil.a2s(newArray);
			this.makeDisplayString();
		}
	}
}
