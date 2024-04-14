package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;
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
		workDescription = "";
		workKindName = "";
		workTypeName = "";
		workControlTypeName = "";
		workDispName = "";
		workFncName = "";
		workRPN = "";
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
	public ArrayList<ElementFactory> generateMenuList(EditPanel editPanel) {
		ArrayList<ElementFactory> list = new ArrayList<>();

		// ファイルから読み込む
		BufferedReader reader;

		try {
			int lineNumber = 0;
			String path = "/resource/elementGenerateMenuDef.txt";
			InputStream is = getClass().getResourceAsStream(path);
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			// String input = "../partsElement.txt";
			// reader = new BufferedReader(new FileReader(new File(filename)));
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
						parts.description = workDescription;
						parts.kindName = workKindName;
						parts.controlType = workControlTypeName;
						parts.valueType = Value.str2valueType.get(workTypeName);
						parts.dispName = workDispName;
						parts.rpn = workRPN;
						parts.width = workWidth;
						parts.height = workHeight;

						list.add(parts);
						resetWorkValues();
					} else if (defType.equals(DefType.TYPE_FNCDEF)) {
						ElementFactory parts = new ElementFactory(editPanel, DefType.TYPE_FNCDEF);
						parts.description = workDescription;
						parts.kindName = workKindName;
						parts.valueType = Value.str2valueType.get(workTypeName);
						parts.dispName = workDispName;
						parts.rpn = workRPN;
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
		}

		return list;
	}

}
