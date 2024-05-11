package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.core.CaliculateException;
import com.github.novisoftware.patternDraw.gui.editor.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.gui.editor.core.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;

/**
 * ノードに閉じたミクロなRPN式(逆ポーランド記法の式)で計算をします。
 *
 */
public class P022_____RpnGraphNodeElement extends P021____AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	public P022_____RpnGraphNodeElement(EditDiagramPanel editPanel) {
		super(editPanel);
	}

	public P022_____RpnGraphNodeElement(EditDiagramPanel editPanel, String s) {
		super(editPanel);

		String a[] = FileReadUtil.tokenizeToArray(s);
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = a[5];
		this.setKindString(a[6]);
		String strOutputType = a[7];
		this.valueType = Value.str2valueType.get(strOutputType);

		System.out.println("rpn string = " + a[8]);

		this.setRpnString(a[8]);
		buildParameterList(this.getRpnString());
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
		this.rpn = new Rpn(rpnString, this.editPanel.networkDataModel);
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

	boolean isVarRef = false;
	String VarName = null;

	/**
	 * 要素が値を持つ場合の、値の型。
	 */
	@Override
	public Value.ValueType getValueType() {
		return this.valueType;
	}

	public void buildParameterList(String s0) {
		ArrayList<String> a = Rpn.s2a(s0);
		connectors = new ArrayList<>();

		ArrayList<String> workParameters = new ArrayList<String>();
		for (String s : a) {
			String paraName = Rpn.getParamName(s);
			if (paraName != null) {
				workParameters.add(s);
			}
		}

		int index = 0;
		int n = workParameters.size();
		for (String s : workParameters) {
			String paraName = Rpn.getParamName(s);
			if (paraName != null) {
				String paraType = Rpn.getParamType(s);
				Value.ValueType valueType = null;
				if (paraType != null) {
					valueType = Value.str2valueType.get(paraType);
				}

				connectors.add(new P010___ConnectTerminal(this, paraName, valueType, "", index, n));
				index ++;
			}
		}

		this.paramMapInfo = new HashMap<String,String>();
		this.paramMapObj = new HashMap<String,P021____AbstractGraphNodeElement>();
	}

	@Override
	public void evaluate() throws CaliculateException {
		try {
			this.workValue = this.getRpn().doCaliculate(this, this.editPanel.networkDataModel.variables);
			this.isError = false;
			this.errorMessage = null;
		} catch (CaliculateException e) {
			this.workValue = null;
			this.isError = true;
			this.errorMessage = e.getMessage();
			throw e;
		}
	}

	@Override
	public P022_____RpnGraphNodeElement getCopy() {
		P022_____RpnGraphNodeElement ret = new P022_____RpnGraphNodeElement(this.editPanel, this.str());

		return ret;
	}

	@Override
	public boolean isComment() {
		return this.rpn.isComment();
	}
}