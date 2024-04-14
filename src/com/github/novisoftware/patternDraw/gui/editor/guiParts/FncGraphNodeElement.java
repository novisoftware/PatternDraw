package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.gui.editor.core.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;


public class FncGraphNodeElement extends AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	final String functionName;
	final FunctionDefInterface function;

	public String str() {
		return String.format(
				"FNC_ELEMENT: %d %d %d %d %s %s %s %s",
				x,
				y,
				w,
				h,
				escape(id),
				escape(getKindString()),
				escape(Value.valueType2str.get(this.valueType)),
				escape(functionName));
	}

	public FncGraphNodeElement(EditPanel EditPanel, String functionName, FunctionDefInterface f) {
		super(EditPanel);
		this.functionName = functionName;
		this.function = f;
		this.valueType = f.getReturnType();
		buildParameterList(this.function);
	}

	public FncGraphNodeElement(EditPanel EditPanel, String s) throws LangSpecException {
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
		this.functionName = unescape(a[8]);
		this.function = FunctionUtil.getFunctionDef(this.functionName);
		buildParameterList(this.function);
	}

	public void buildParameterList(FunctionDefInterface f) {
		connectors = new ArrayList<>();

		String[] connNames = f.getParameterNames();
		ValueType[] connValueTypes = f.getParameterTypes();
		String[] connValueDescs = f.getParameterDescs();

		for (int i = 0; i < connNames.length; i++) {
			connectors.add(new GraphConnector(this, connNames[i], connValueTypes[i], connValueDescs[i], i));
		}

		this.paramMapInfo = new HashMap<String,String>();
		this.paramMapObj = new HashMap<String,AbstractGraphNodeElement>();
		this.paramSatisfied = false;
	}

	@Override
	public void evaluate() {
		if (this.paramSatisfied) {
			ArrayList<Value> args = new ArrayList<Value>();

			for (String paraName : this.function.getParameterNames()) {
				args.add(this.paramMapObj.get(paraName).workValue);
			}
			// TODO 副作用先のオブジェクトを持たせる
			try {
				this.workValue = this.function.exec(args, null);
			} catch (InvaliScriptException e) {
				// TODO もう少し適切なエラーハンドリング
				e.printStackTrace();
			}
		}

		// this.workValue = this.getRpn().doCaliculate(this, variables);
	}

	@Override
	public FncGraphNodeElement getCopy() {
		FncGraphNodeElement ret;
		try {
			ret = new FncGraphNodeElement(this.editPanel, this.str());
			return ret;
		} catch (LangSpecException e) {
			// コピー元がコンストラクタでのエラーチェックを通過しているため、発生しない。
			// コーディング上はエラー表示とnullの返却を残しておく
			e.printStackTrace();
			return null;
		}
	}

	@Override
	String getRepresentExpression() {
		return this.function.getDescription();
	}
}