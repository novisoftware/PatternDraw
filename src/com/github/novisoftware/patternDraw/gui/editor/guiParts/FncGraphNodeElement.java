package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDef;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.util.Rpn;


public class FncGraphNodeElement extends AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	public String str() {
		return String.format(
				"FNC_ELEMENT: %d %d %d %d %s %s %s %s",
				x,
				y,
				w,
				h,
				escape(id),
				escape(getKindString()),
				escape(outputType),
				escape(functionName));
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
		this.outputType = unescape(a[7]);
		this.valueType = Value.str2valueType.get(this.outputType);
		this.functionName = unescape(a[8]);
		this.function = FunctionUtil.getFunctionDef(this.functionName);
		buildParameterList(this.function);
	}

	final String functionName;
	final FunctionDef function;


	public void buildParameterList(FunctionDef f) {
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