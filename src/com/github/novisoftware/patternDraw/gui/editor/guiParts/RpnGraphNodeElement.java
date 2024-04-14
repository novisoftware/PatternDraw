package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.core.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;

/**
 * ノードに閉じたミクロなRPN式(逆ポーランド記法の式)で計算をします。
 *
 */
public class RpnGraphNodeElement extends AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	public RpnGraphNodeElement(EditPanel editPanel) {
		super(editPanel);
	}

	public String str() {
		return String.format("RPN_ELEMENT: %d %d %d %d %s %s %s %s",
								x, y, w, h, escape(id),
								escape(getKindString()),
								escape(Value.valueType2str.get(valueType)),
								escape(getRpnString()));
	}

	/**
	 * 要素のRPN式。RPNは逆ポーランド記法。
	 */
	private Rpn rpn;

	public void setRpnString (String rpnString) {
		this.rpn = new Rpn(rpnString);
	}

	public Rpn getRpn() {
		return this.rpn;
	}

	public String getRpnString() {
		return this.rpn.getFormula();
	}

	public String getRepresentExpression() {
		return this.rpn.getDisplayString();
	}

	/**
	 * 要素が値を持つ場合の、値の型。
	 */
	protected Value.ValueType valueType;

	/**
	 * 要素が値を持つ場合の、値の型。
	 */
	public void setValueType(Value.ValueType valueType) {
		this.valueType = valueType;
	}

	/**
	 * 要素が値を持つ場合の、値の型。
	 */
	@Override
	public Value.ValueType getValueType() {
		return this.valueType;
	}

	public RpnGraphNodeElement(EditPanel EditPanel, String s) {
		super(EditPanel);

		String a[] =s.split(" ");
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = unescape(a[5]);
		this.setKindString(unescape(a[6]));
		String strOutputType = unescape(a[7]);
		this.valueType = Value.str2valueType.get(strOutputType);
		this.setRpnString(unescape(a[8]));
		buildParameterList(this.getRpnString());
	}

	public void buildParameterList(String s0) {
		ArrayList<String> a = Rpn.s2a(s0);
		connectors = new ArrayList<>();

		int index = 0;
		for (String s : a) {
			String paraName = Rpn.getParamName(s);
			if (paraName != null) {
				String paraType = Rpn.getParamType(s);
				Value.ValueType valueType = null;
				if (paraType != null) {
					valueType = Value.str2valueType.get(paraType);
				}

				connectors.add(new GraphConnector(this, paraName, valueType, "", index));
				index ++;
			}
		}

		this.paramMapInfo = new HashMap<String,String>();
		this.paramMapObj = new HashMap<String,AbstractGraphNodeElement>();
		this.paramSatisfied = false;
	}

	@Override
	public void evaluate() {
		this.workValue = this.getRpn().doCaliculate(this, variables);
	}

	@Override
	public RpnGraphNodeElement getCopy() {
		RpnGraphNodeElement ret = new RpnGraphNodeElement(this.editPanel, this.str());

		return ret;
	}
}