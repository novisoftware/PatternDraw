package com.github.novisoftware.patternDraw.gui.editor.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.InterfaceScalar;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.TypeUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.TypeUtil.TwoValues;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueAbstractScalar;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueBoolean;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueFloat;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueNumeric;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueString;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;


public class Rpn {
	private String formula;
	private ArrayList<String> array;
	private String displayString;
	private String comment;
	private final NetworkDataModel networkDataModel;

	public Rpn(String formula, NetworkDataModel networkDataModel) {
		this.formula = formula;
		this.array = RpnUtil.s2a(formula);
		this.networkDataModel = networkDataModel;
		this.makeDisplayString();
	}

	/**
	 * 表示等に使用する文字列を生成します。
	 *
	 */
	public void makeDisplayString() {
		Stack<String> stack = new Stack<>();

		for(String op : this.array) {
			if (op.startsWith("'")) {
				stack.push( op.substring(1));
			}
			else if (op.equals(":set-variable")) {
			}
			else if (op.equals(":recall-variable")) {
			}
			else if (op.equals(":as-numeric")) {
			}
			else {
				stack.push(op.replaceAll(";.*", ""));
			}
		}

		String value = stack.pop();
		this.displayString = RpnUtil.getRepresent(value);
		this.comment = RpnUtil.getComment(value);
	}



	public ValueType getValueType(HashMap<String, Value> variables, ArrayList<ParameterDefine> params) {
		Stack<String> stack = new Stack<>();
		Stack<String> stringStack = new Stack<>();

		String varName = null;
		String opStr = null;

		for(String op : this.array) {
			if (op.startsWith("'")) {
				stack.push( op.substring(1));
			}
			else if (op.equals(":set-variable")) {
				opStr = op;
				varName = stack.pop();

				return ValueType.NONE;
			}
			else if (op.equals(":recall-variable")) {
				opStr = op;
				varName = stack.pop();
				break;
			}
			else if (op.equals(":as-numeric")) {
				return ValueType.NONE;
			}
			else {
				stack.push(op.replaceAll(";.*", ""));
			}
		}

		if (varName == null) {
			return ValueType.NONE;
		}

		Value var = variables.get(varName);
		if (var != null) {
			return var.valueType;
		}
		for (ParameterDefine p : params) {
			if (p.name.equals(varName)) {
				return p.valueType;
			}
		}

		return ValueType.NONE;
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
	 * @return represent
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


	public Value doCaliculate(P022_____RpnGraphNodeElement ele, HashMap<String, Value> variables) {
		Stack<Value> stack = new Stack<>();
		Stack<String> stringStack = new Stack<>();

		// ArrayList<String> array = Rpn.s2a(this.getRpnString());
		for (String s : array) {
			Debug.println("RPN DETAIL", "op is ***" + s + "***");


			/**
			 * 端子についているパラメーターを取得
			 */
			String paraName = getParamName(s);
			if (paraName != null) {
				P021____AbstractGraphNodeElement src = ele.paramMapObj.get(paraName);
				stack.push(src.workValue);
				continue;
			}

			if (s.equals(":print")) {
				OutputTextWindow.println(stack.pop().toString());
			}
			else if (s.equals(":input:integer")) {
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
			else if (s.equals("-") || s.equals("/") || s.equals("%") || s.equals("+") || s.equals("*")) {
				Value b0 = stack.pop();
				Value a0 = stack.pop();

				// TODO:
				// キャストエラーの処理が必要。

				if (a0 instanceof ValueAbstractScalar && b0 instanceof ValueAbstractScalar) {
					TwoValues work = TypeUtil.upCast(a0, b0);
					ValueAbstractScalar a = (ValueAbstractScalar)work.a;
					ValueAbstractScalar b = (ValueAbstractScalar)work.b;
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
				}

			}
			else if (s.equals(">") || s.equals(">=") || s.equals("<") || s.equals("<=") || s.equals("==") || s.equals("!=")) {
				Value b0 = stack.pop();
				Value a0 = stack.pop();

				// TODO:
				// キャストエラーの処理が必要。

				if (a0 instanceof ValueAbstractScalar && b0 instanceof ValueAbstractScalar) {
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
				}
			}
			else if (s.equals(":and")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(a_.getInternal() && b_.getInternal()));
				}
				else {
					Debug.println("RPN", ":and INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.equals(":or")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(a_.getInternal() || b_.getInternal()));
				}
				else {
					Debug.println("RPN", ":or INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.equals(":xor")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(a_.getInternal() ^ b_.getInternal()));
				}
				else {
					Debug.println("RPN", ":xor INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.equals(":not")) {
				Value a = stack.pop();

				if (a instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;

					stack.push(new ValueBoolean(!a_.getInternal()));
				}
				else {
					Debug.println("RPN", ":not INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
				}
			}
			else if (s.equals(":nand")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(!(a_.getInternal() && b_.getInternal())));
				}
				else {
					Debug.println("RPN", ":and INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.equals(":nor")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(!(a_.getInternal() || b_.getInternal())));
				}
				else {
					Debug.println("RPN", ":or INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.equals(":xnor")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueBoolean && b instanceof ValueBoolean) {
					ValueBoolean a_ = (ValueBoolean)a;
					ValueBoolean b_ = (ValueBoolean)b;

					stack.push(new ValueBoolean(!(a_.getInternal() ^ b_.getInternal())));
				}
				else {
					Debug.println("RPN", ":xor INVALID");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
			}
			else if (s.startsWith("'")) {
				stringStack.push( RpnUtil.getRepresent(s.substring(1)) );
			}
			else {
				try {
					stack.push( new ValueString(RpnUtil.getRepresent(s)) );
				} catch(Exception e) {
					System.err.println("Value is " + s);
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
}
