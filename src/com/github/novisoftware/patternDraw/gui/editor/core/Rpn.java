package com.github.novisoftware.patternDraw.gui.editor.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueBoolean;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueNumeric;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueString;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputFrame;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;


public class Rpn {
	private String formula;
	private ArrayList<String> array;
	private String displayString;
	private String comment;

	public Rpn(String formula) {
		this.formula = formula;
		this.array = RpnUtil.s2a(formula);
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
		String a1[] = s.split(" +");
		ArrayList<String> a2 = new ArrayList<String>();
		for(String s1 : a1) {
			a2.add(s1);
		}
		return a2;
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


	public Value doCaliculate(RpnGraphNodeElement ele, HashMap<String, Value> variables) {
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
				AbstractGraphNodeElement src = ele.paramMapObj.get(paraName);
				stack.push(src.workValue);
				continue;
			}

			if (s.equals(":print")) {
				OutputFrame.println(stack.pop().toString());
//				System.out.println(stack.pop().internal.toString());
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

				RpnGraphNodeElement.debugVariables();
			}
			else if (s.equals(":recall-variable")) {
				String name = stringStack.pop();
				Value v =variables.get(name);
				stack.push(v);

				RpnGraphNodeElement.debugVariables();
			}
			else if (s.equals(":as-numeric")) {
				stack.push(new ValueNumeric(stack.pop().toString()));
			}
			else if (s.equals("-")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					stack.push( new ValueNumeric( a_.getInternal().subtract(b_.getInternal())));
				}
			}
			else if (s.equals("/")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					stack.push( new ValueNumeric( a_.getInternal().divide(b_.getInternal())));
				}
//				stack.push( new Value( stack.pop().getInternal().divide(stack.pop().getInternal())));
			}
			else if (s.equals("%")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					stack.push(new ValueNumeric(new BigDecimal( a_.getInternal().toBigInteger().mod(b_.getInternal().toBigInteger()))));

					Debug.println("RPN", "mod(VALID)");
					Debug.println("RPN", "a: " + a_.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b_.toDebugString() + "  " + b);
					Debug.println("RPN:", "result: " + stack.peek().toDebugString());
				}
				else {
					Debug.println("RPN", "mod(INVALID)");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
			}
			else if (s.equals("+")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					stack.push( new ValueNumeric( a_.getInternal().add(b_.getInternal())));


					Debug.println("RPN", "plus");
				}
				else {
					Debug.println("RPN", "plus(INVALID)");
					Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
					Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				}
//				stack.push( new Value( stack.pop().getInternal().add(stack.pop().getInternal())));
			}
			else if (s.equals("*")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					stack.push( new ValueNumeric( a_.getInternal().multiply(b_.getInternal())));
				}

//				stack.push( new Value( stack.pop().getInternal().multiply(stack.pop().getInternal()) ) );
			}
			else if (s.equals(">")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) > 0) {
						stack.push( new ValueBoolean(true));
					}
					else {
						stack.push( new ValueBoolean(false));
					}
				}
			}
			else if (s.equals(">=")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) >= 0) {
						stack.push( new ValueBoolean(true));
					}
					else {
						stack.push( new ValueBoolean(false));
					}
				}
			}
			else if (s.equals("<")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) < 0) {
						stack.push( new ValueBoolean(true));
					}
					else {
						stack.push( new ValueBoolean(false));
					}
				}
			}
			else if (s.equals("<=")) {
				Value b = stack.pop();
				Value a = stack.pop();

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) <= 0) {
						stack.push( new ValueBoolean(true));
					}
					else {
						stack.push( new ValueBoolean(false));
					}
				}
			}
			else if (s.equals("==")) {
				Value b = stack.pop();
				Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);
				Value a = stack.pop();
				Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) == 0) {
						stack.push(new ValueBoolean(true));
					}
					else {
						stack.push(new ValueBoolean(false));
					}

					Debug.println("RPN", "eq");
				}
				else {
					Debug.println("RPN", "eq<INVALID>");
				}
			}
			else if (s.equals("!=")) {
				Value b = stack.pop();
				Value a = stack.pop();

				Debug.println("RPN", "a: " + a.toDebugString() + "  " + a);
				Debug.println("RPN", "b: " + b.toDebugString() + "  " + b);

				if (a instanceof ValueNumeric && b instanceof ValueNumeric) {
					ValueNumeric a_ = (ValueNumeric)a;
					ValueNumeric b_ = (ValueNumeric)b;

					if (a_.getInternal().compareTo(b_.getInternal()) != 0) {
						stack.push( new ValueBoolean(true));
					}
					else {
						stack.push( new ValueBoolean(false));
					}

					Debug.println("RPN", "neq");
				}
				else {
					Debug.println("RPN", "neq<INVALID>");
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
//				stack.push( new Value( new BigDecimal( stack.pop().getInternal().toBigInteger().mod(stack.pop().getInternal().toBigInteger()))));
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
