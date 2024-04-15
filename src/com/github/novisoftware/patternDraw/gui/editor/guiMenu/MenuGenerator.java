package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ElementFactory.DefType;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;

public class MenuGenerator {
	private String workDescription;
	private String workKindName;
	private String workTypeName;
	private String workControlTypeName;
	private String workDispName;
	private String workFncName;
	private String workRPN;
	private int workWidth;
	private int workHeight;
	private DefType defType;
	private ArrayList<String> workList;

	void resetWorkValues() {
		workDescription = null;
		workKindName = null;
		workTypeName = null;
		workControlTypeName = null;
		workDispName = null;
		workFncName = null;
		workRPN = null;
		workWidth = 120;
		workHeight = 60;
		defType = DefType.TYPE_UNDEF;
		workList = new ArrayList<>();
	}

	public MenuGenerator() {
		this.resetWorkValues();
	}

	/**
	 * テキストファイルの記載内容から ElementFactory のリストを作成する。
	 *
	 * @param editPanel
	 * @param filename
	 * @return
	 */
	public ArrayList<ElementFactory> generateMenuList(EditDiagramPanel editPanel) {
		ArrayList<ElementFactory> list = new ArrayList<>();

		try {
			// リソースからファイルから読み込む
			String path = "/resource/elementGenerateMenuDef.txt";
			InputStream is = getClass().getResourceAsStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			// lineNumberは、記載誤りがあった場合に、それが何行目かをデバッグ出力するために使う。
			int lineNumber = 0;

			while (true) {
				lineNumber++;
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = Common.trim(line);

				if (line.startsWith("#")) {
					continue;
				}

				if (line.startsWith("DESCRIPTION:")) {
					workDescription = Common.trim(line.substring("DESCRIPTION:".length()));
				} else if (line.startsWith("KIND:")) {
					workKindName = Common.trim(line.substring("KIND:".length()));
				} else if (line.startsWith("TYPE:")) {
					workTypeName = Common.trim(line.substring("TYPE:".length()));
				} else if (line.startsWith("CONTROL_TYPE:")) {
					workControlTypeName = Common.trim(line.substring("CONTROL_TYPE:".length()));
				} else if (line.startsWith("DISPNAME:")) {
					workDispName = Common.trim(line.substring("DISPNAME:".length()));
				} else if (line.startsWith("WIDTH:")) {
					workWidth = Integer.parseInt(Common.trim(line.substring("WIDTH:".length())));
				} else if (line.startsWith("HEIGHT:")) {
					workHeight = Integer.parseInt(Common.trim(line.substring("HEIGHT:".length())));
				} else if (line.startsWith("FUNCTION:")) {
					workFncName = Common.trim(line.substring("FUNCTION:".length()));
					defType = DefType.TYPE_FNCDEF;
				} else if (line.startsWith("-->RPN")) {
					workRPN = Common.concatStrings(workList);

					defType = DefType.TYPE_RPNDEF;
				} else if (line.startsWith("---->DEFINE")) {
					if (defType.equals(DefType.TYPE_RPNDEF)) {
						ElementFactory parts = new ElementFactory(editPanel, DefType.TYPE_RPNDEF);
						parts.dispName = workDispName;
						parts.description = workDescription;
						parts.kindName = workKindName;
						parts.controlType = workControlTypeName;
						parts.valueType = Value.str2valueType.get(workTypeName);
						if (parts.valueType == null) {
							System.err.println("設定誤り: " + parts.dispName);
						}
						parts.rpn = workRPN;
						parts.width = workWidth;
						parts.height = workHeight;

						list.add(parts);
						resetWorkValues();
					} else if (defType.equals(DefType.TYPE_FNCDEF)) {
						FunctionDefInterface tmpFunc = FunctionUtil.getFunctionDef(workFncName);

						ElementFactory parts = new ElementFactory(editPanel, DefType.TYPE_FNCDEF);
						parts.dispName = workDispName != null ? workDispName : tmpFunc.getName();
						parts.description = workDescription != null ? workDescription : tmpFunc.getDescription();
						parts.valueType = tmpFunc.getReturnType();
						parts.kindName = workKindName;
						parts.rpn = "";
						parts.fncName = workFncName;
						parts.width = workWidth;
						parts.height = workHeight;

						list.add(parts);
						resetWorkValues();
					} else {
						System.err.println(String.format("メニュー定義誤り(行: %d)", lineNumber));
						System.exit(1);
					}
					workWidth = 120;
					workHeight = 60;
					defType = DefType.TYPE_UNDEF;
				} else {
					String s = Common.trim(line);
					if (!s.equals("")) {
						workList.add(Common.trim(line));
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("途中でエラーが発生しました。" + e.toString());
		} catch (LangSpecException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return list;
	}

}
