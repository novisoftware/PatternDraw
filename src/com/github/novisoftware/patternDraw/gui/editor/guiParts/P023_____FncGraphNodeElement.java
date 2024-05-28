package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.Rpn;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;


public class P023_____FncGraphNodeElement extends P021____AbstractGraphNodeElement {
	static InputStreamReader isr = new InputStreamReader(System.in);
	static BufferedReader bufferedReader = new BufferedReader(isr);

	final String functionName;
	final FunctionDefInterface function;

	public P023_____FncGraphNodeElement(EditDiagramPanel EditPanel, String functionName, FunctionDefInterface f) {
		super(EditPanel);
		this.functionName = functionName;
		this.function = f;
		buildParameterList(this.function);
	}

	public P023_____FncGraphNodeElement(EditDiagramPanel EditPanel, String s) throws LangSpecException {
		super(EditPanel);

		String a[] = FileReadUtil.tokenizeToArray(s);
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = unescape(a[5]);
		this.setKindString(unescape(a[6]));
		this.functionName = unescape(a[7]);
		this.function = FunctionUtil.getFunctionDef(this.functionName);
		buildParameterList(this.function);
	}

	public String str() {
		System.out.println("this.valueType: " + this.getValueType());

		return String.format(
				"FNC_ELEMENT: %d %d %d %d %s %s %s",
				x,
				y,
				w,
				h,
				escape(id),
				escape(getKindString()),
				escape(functionName));
	}

	public void buildParameterList(FunctionDefInterface f) {
		connectors = new ArrayList<>();

		String[] connNames = f.getParameterNames();
		ValueType[] connValueTypes = f.getParameterTypes();
		String[] connValueDescs = f.getParameterDescs();

		for (int i = 0; i < connNames.length; i++) {
			connectors.add(new P010___ConnectTerminal(this, connNames[i], connValueTypes[i], connValueDescs[i], i,
					connNames.length));
		}

		this.paramMapInfo = new HashMap<String,String>();
		this.paramMapObj = new HashMap<String,P021____AbstractGraphNodeElement>();
	}

	@Override
	public Value.ValueType getValueType() {
		return function.getReturnType();
	}

	@Override
	public void evaluateExactly() throws CaliculateException {
		InstructionRenderer target = OutputGraphicsWindow.getRenderer();
		ArrayList<Value> args = new ArrayList<Value>();

		for (String paraName : this.function.getParameterNames()) {
			P021____AbstractGraphNodeElement pObj = this.paramMapObj.get(paraName);
			if (pObj == null) {
				System.out.println("param not satisfied.");
				return;
			}

			args.add(this.paramMapObj.get(paraName).workValue);
		}
		// TODO 副作用先のオブジェクトを持たせる
		System.out.println("呼び出し元");
		this.workValue = this.function.exec(args, target);
	}

	@Override
	public P023_____FncGraphNodeElement getCopy() {
		P023_____FncGraphNodeElement ret;
		try {
			ret = new P023_____FncGraphNodeElement(this.editPanel, this.str());
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
		return this.function.getDisplayName();
	}

	@Override
	public boolean isComment() {
		return false;
	}

	@Override
	public String getDescription() {
		return this.function.getDescription();
	}
}