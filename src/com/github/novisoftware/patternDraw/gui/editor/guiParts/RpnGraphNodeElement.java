package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.util.Rpn;


public class RpnGraphNodeElement extends AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	public RpnGraphNodeElement(EditPanel editPanel) {
		super(editPanel);
	}

	public String str() {
		return String.format("RPN_ELEMENT: %d %d %d %d %s %s %s %s", x, y, w, h, escape(id), escape(getKindString()), escape(outputType), escape(getRpnString()));
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
		this.outputType = unescape(a[7]);
		this.valueType = Value.str2valueType.get(this.outputType);
		this.setRpnString(unescape(a[8]));
		buildParameterList(this.getRpnString());
	}

	public void buildParameterList(String s0) {
		ArrayList<String> a = Rpn.s2a(s0);
//		params = new ArrayList<>();
		connectors = new ArrayList<>();

		int index = 0;
		for (String s : a) {
			String paraName = Rpn.getParamName(s);
			if (paraName != null) {
//				params.add(paraName);
				String paraType = Rpn.getParamType(s);
				Value.ValueType valueType = null;
				if (paraType != null) {
					valueType = Value.str2valueType.get(paraType);
				}

				connectors.add(new GraphConnector(this, paraName, valueType, index));
				index ++;
				// System.out.println("param add: " + paraName);
			}
		}

		this.paramMapInfo = new HashMap<String,String>();
		this.paramMapObj = new HashMap<String,RpnGraphNodeElement>();
		this.paramSatisfied = false;
	}

	public void evaluate() {
		this.workValue = this.getRpn().doCaliculate(this, variables);
	}

	@Override
	public RpnGraphNodeElement getCopy() {
		RpnGraphNodeElement ret = new RpnGraphNodeElement(this.editPanel, this.str());

		return ret;
	}
}