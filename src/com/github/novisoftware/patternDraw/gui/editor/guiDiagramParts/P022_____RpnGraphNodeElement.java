package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.Rpn;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;

/**
 * ノードに閉じたミクロなRPN式(逆ポーランド記法の式)で計算をします。
 *
 */
public class P022_____RpnGraphNodeElement extends P021____AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);
	String description = "";

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
		this.setRpnString(a[8]);

		// 開発中データとしては Description なしが存在するため
		if (a.length >= 10) {
			this.setDescription(a[9]);
		}
	}

	public String str() {
		return String.format("RPN_ELEMENT: %d %d %d %d %s %s %s %s %s",
								x, y, w, h, escape(id),
								escape(getKindString()),
								escape(Value.valueType2str.get(valueType)),
								escape(getRpnString()),
								escape(this.getDescription())
								);
	}

	/**
	 * 要素のRPN式。RPNは逆ポーランド記法。
	 */
	private Rpn rpn;

	public void setRpnString(String rpnString) {
		this.rpn = new Rpn(rpnString, this.editPanel.networkDataModel);
		P022_____RpnGraphNodeElement.buildParameterList2(this, this.getRpnString());
	}

	public void setRpn(Rpn r) {
		this.rpn = r;
		P022_____RpnGraphNodeElement.buildParameterList2(this, this.getRpnString());
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

	static public void buildParameterList2(P020___AbstractElement element, String s0) {
		ArrayList<String> a = Rpn.s2a(s0);
		element.connectors = new ArrayList<>();

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

				element.connectors.add(new P010___ConnectTerminal(element, paraName, valueType, "", index, n));
				index ++;
			}
		}

		/*
		if (element.paramMapInfo == null) {
			element.paramMapInfo = new HashMap<String,String>();
			element.paramMapObj = new HashMap<String, P021____AbstractGraphNodeElement>();
		}
		*/

		// RPNを書き換えた場合も呼び出される。
		// RPNの解析によって得られるパラメーターの一覧が、前と同じであることは仮定せずに、
		// 内容の引き継ぎを行う。
		HashMap<String, String> old_paramMapInfo = element.paramMapInfo;
		HashMap<String, P021____AbstractGraphNodeElement> old_paramMapObj = element.paramMapObj;

		element.paramMapInfo = new HashMap<String,String>();
		element.paramMapObj = new HashMap<String, P021____AbstractGraphNodeElement>();

		if (old_paramMapInfo != null && old_paramMapObj != null) {
			for (P010___ConnectTerminal c : element.connectors) {
				String k = c.getParaName();
				if (old_paramMapInfo.containsKey(k)) {
					element.paramMapInfo.put(k, old_paramMapInfo.get(k));
				}
				if (old_paramMapObj.containsKey(k)) {
					element.paramMapObj.put(k, old_paramMapObj.get(k));
				}
			}
		}
	}

	@Override
	public void evaluateValue() throws CaliculateException {
		try {
			this.workValue = this.getRpn().doCaliculate(this, this.editPanel.networkDataModel.variables);
		} catch (InterruptedException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INTERRUPTED);
		} catch (OutOfMemoryError e) {
			throw new CaliculateException(CaliculateException.MESSAGE_OUT_OF_MEMORY);
		}

	}

	/**
	 * どのような型になるのかを判定する
	 *
	 * @return
	 */
	public ValueType evaluateValueType() {
		return this.getRpn().evaluateValueType(this,
				this.editPanel.networkDataModel.variables,
				this.editPanel.networkDataModel.paramDefList,
				this.editPanel.networkDataModel.workCheckTypeVariables
				);
	}

	@Override
	public P022_____RpnGraphNodeElement getCopy() {
		P022_____RpnGraphNodeElement ret = new P022_____RpnGraphNodeElement(this.editPanel, this.str());

		return ret;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isComment() {
		return this.rpn.isComment();
	}
}