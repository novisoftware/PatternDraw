package com.github.novisoftware.patternDraw.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P010___ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P023_____FncGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.ControllBase;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.OtherUtil;

public class NetworkDataModel {
	/**
	 * 変数
	 */
	final public HashMap<String, Value> variables;

	/**
	 * 型の妥当性検査のための変数型を記録するハッシュ表
	 */
	final public HashMap<String, ValueType> workCheckTypeVariables;

	/**
	 * ダイアログによるスクリプトのタイトル。
	 * 設定ファイルに入っている。
	 * ウィンドウタイトルの文字列の一部分として使用する。
	 */
	public String title = "";

	/**
	 * 保存先のファイル名
	 */
	private String filename;

	/**
	 * エレメントのリスト
	 */
	private ArrayList<P020___AbstractElement> elements = new ArrayList<P020___AbstractElement>();

	/**
	 * 編集用パネル(JPanel)
	 */
	protected EditDiagramPanel editPanel;

	/**
	 * パラメーター定義のリスト。
	 */
	public final ArrayList<ParameterDefine> paramDefList;

	/**
	 *  変数名の一覧(設定された変数のみを対象にする)
	 */
	public ArrayList<String> variableNameList;

	/**
	 *  実行順情報: 制御用エレメントの配下のエレメント
	 */
	public HashMap<P010___ControlElement, ArrayList<P020___AbstractElement>> control_contains;

	/**
	 * 実行順情報: ルート要素。 GUI上では「地べたに置かれた要素」が該当する。
	 * (コントロールの中に入っていないもの)
	 */
	ArrayList<P020___AbstractElement> rootElement;

	// elementに表示用の「単連結グループID」(連番)をつける
	public HashMap<Integer,ArrayList<P021____AbstractGraphNodeElement>> graphGroup;
	public HashMap<P010___ControlElement, ArrayList<P020___AbstractElement>> controlled_head;
	public HashMap<P010___ControlElement, ArrayList<P020___AbstractElement>> controlled_all;


	public ArrayList<P020___AbstractElement> getElements() {
		return elements;
	}

	public NetworkDataModel(EditDiagramPanel editPanel, String filename) {
		this.editPanel = editPanel;
		this.filename = filename;
		this.paramDefList = new ArrayList<ParameterDefine>();
		// this.paramVariables = new HashMap<String, Value>();
		this.variables = new HashMap<String, Value>();
		this.workCheckTypeVariables = new HashMap<String, ValueType>();
	}

	public void debugVariables() {
		for (String s : variables.keySet()) {
			Debug.println("variables", s + " -> " + variables.get(s) );
		}
	}

	public void resetVariables(HashMap<String, Value> initVariables) {
		this.variables.clear();
		this.variables.putAll(initVariables);
	}

	/**
	 * abc1, abc2, abc3 ... のような連番で、ユニークな名前を生成します。
	 *
	 * @param inputName 連番元(例: abc3)
	 * @return 生成した連番(例: abc4)
	 */
	public String generateUniqueName(String inputName) {
		HashSet<String> nameSet = new HashSet<>();
		for (P020___AbstractElement to :  elements) {
			nameSet.add(to.id);
		}
		return OtherUtil.generateUniqueName(inputName, nameSet);
	}

	/**
	 * 位置関係の比較
	 *
	 */
	class posComparator implements java.util.Comparator<P020___AbstractElement> {
		@Override
		public int compare(P020___AbstractElement o1, P020___AbstractElement o2) {
			// 上から下の順にソートする
			if (o1.y < o2.y) {
				return -1;
			}
			if (o1.y > o2.y) {
				return 1;
			}

			// 基本的には全く同一の高さを想定しないが、一応、同一の高さの場合の横方向の比較を行う。
			if (o1.x < o2.x) {
				return -1;
			}
			if (o1.x > o2.x) {
				return 1;
			}

			/*
			// 0 を返してはいけない。オブジェクトが同一視される
			// ★ TODO
			// 以下でいいのかどうか。 return 0 にする場面が必要かどうか検討。
			return 0;
			*/

			if (o1.hashCode() < o2.hashCode()) {
				return -1;
			}

			return 1;
		}
	}

	/**
	 * 実行順をきめる等を行う。
	 *
	 *
	 */
	public void
	analyze(
//			ArrayList<Element> returnList, HashMap<Element,HashMap<String,Integer>> name2index
			) {
		// 上から下の順にソートした Element
		TreeSet<P020___AbstractElement> positionSortedElements = new TreeSet<>(new posComparator());
		for (P020___AbstractElement e : elements) {
			if (! e.isComment()) {
				positionSortedElements.add(e);
			}
		}


		// 変数名の一覧(設定された変数のみを対象にする)
		variableNameList = new ArrayList<>();

		// 変数名の一覧を作成する
		for (P020___AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof P022_____RpnGraphNodeElement) {
				if (((P022_____RpnGraphNodeElement)elementIcon).getKindId().equals(KindId.VARIABLE_SET)) {
				// if (((RpnGraphNodeElement)elementIcon).getKindString().equals("変数を設定")) {
					String rep = ((P022_____RpnGraphNodeElement)elementIcon).getRepresentExpression();
					if (! variableNameList.contains(rep)) {
						variableNameList.add(rep);
					}
				}
			}
		}

		for (String varName: variableNameList) {
			Debug.println("Evaluate", "var name: " + varName);
		}

		//////////////////////////////////////////////////////////
		// グラフ構造を解析して、評価順をきめる

		// 幅優先探索での追加順
		ArrayList<P021____AbstractGraphNodeElement> addOrder = new ArrayList<>();

		// 他の要素を参照していない要素
		// 「計算済の値」の集合の初期値
		HashMap<P021____AbstractGraphNodeElement,Integer> addSet0 = new HashMap<>();
		int workCounter = 0;
		for (P020___AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof P021____AbstractGraphNodeElement) {
				P021____AbstractGraphNodeElement referFrom = ((P021____AbstractGraphNodeElement)elementIcon);
				if (referFrom.paramMapObj.size() == 0) {
					// 単連結のグラフを区別したいので、異なる番号を振っておく
					addSet0.put(referFrom, workCounter);
					workCounter++;

					// 付随的に使用するデータ構造の組み立て(追加順)
					addOrder.add(referFrom);
				}
			}
		}

		// 単連結グラフに分解

		// 幅優先探索で、計算済の値の段階数を増やしていく
		ArrayList<Set<P021____AbstractGraphNodeElement>> addSet = new ArrayList<>();
		addSet.add(addSet0.keySet());

		HashMap<P021____AbstractGraphNodeElement,Integer> foundSet = new HashMap<>();
		foundSet.putAll(addSet0);

		// グループの合流。数字をマージする
		HashMap<Integer,Integer> mergeInfo_old = new HashMap<>();
		ArrayList<Integer> mergeFrom = new ArrayList<Integer>();
		ArrayList<Integer> mergeTo = new ArrayList<Integer>();

		while (true) {
			HashMap<P021____AbstractGraphNodeElement,Integer> nextAddSet = new HashMap<>();

			for (P020___AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof P021____AbstractGraphNodeElement) {
					P021____AbstractGraphNodeElement element = ((P021____AbstractGraphNodeElement)elementIcon);
					// 入力を持つノードを対象に処理をする
					if (foundSet.containsKey(element)) {
						continue;
					}
					boolean isOk = true;
					int num = -1;
					for (P020___AbstractElement e_ : element.paramMapObj.values()) {
						if (e_ instanceof P021____AbstractGraphNodeElement) {
							// 入力ノード
							P021____AbstractGraphNodeElement e = (P021____AbstractGraphNodeElement)e_;

							if (!foundSet.containsKey(e)) {
								// 入力ノードが、発見済みの集合に含まれていない場合は後回し
								isOk = false;
								break;
							}
							else {
								// 入力ノードが、発見済みの集合に含まれている場合のみ
								int m = foundSet.get(e);
								if (num == -1) {
									// TODO これは常にNOPになるはず
									num = m;
								}
								if (m < num) {
									mergeFrom.add(num);
									mergeTo.add(m);

									if (! mergeInfo_old.containsKey(num) || mergeInfo_old.get(num) > m) {
										mergeInfo_old.put(num, m);
										Debug.println("evaluate.mergeInfo", "" + num + " -> " + m);
									}
									num = m;
								}
								else if (num < m) {
									mergeFrom.add(m);
									mergeTo.add(num);

									mergeInfo_old.put(m, num);
									Debug.println("evaluate.mergeInfo", "" + m + " -> " + num);
								}
							}
						} else {
							isOk = false;
							break;
						}
					}
					if (isOk) {
						nextAddSet.put(element, num);

						// 付随的に使用するデータ構造の組み立て(追加順)
						addOrder.add(element);
					}
				}
			}

			if (nextAddSet.isEmpty()) {
				break;
			}
			foundSet.putAll(nextAddSet);
		}

		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ 作業状態");
		HashMap<P021____AbstractGraphNodeElement,Integer> groupIdBuild = new HashMap<>();
		// デバッグ確認用
		/*
		for (AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = foundSet.get(element);
			if ( mergeInfo_old.containsKey(groupIdWork)) {
				groupIdWork = mergeInfo_old.get(groupIdWork);
			}
			groupIdBuild.put(element,groupIdWork);
			Debug.println("evaluate",  "ID: " + element.id + "  groupId(raw):" + foundSet.get(element) + "  groupId:" + groupIdWork);
		}
		*/

		// 番号の付け替え
		for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = foundSet.get(element);
			groupIdBuild.put(element,groupIdWork);
		}
		int n = mergeFrom.size();
		for (int i = 0; i < n; i++) {
			int from = mergeFrom.get(i);
			int to = mergeTo.get(i);

			for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
				int t = groupIdBuild.get(element);
				if (t == from) {
					groupIdBuild.put(element, to);
				}
			}
		}
		// デバッグ確認用
		for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = groupIdBuild.get(element);
			Debug.println("evaluate",  "ID: " + element.id + "  groupId(raw):" + foundSet.get(element) + "  groupId:" + groupIdWork);
		}


		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ(連番整列前)");
		// デバッグ確認用
		for (int i = 0;  i < addSet0.size(); i++) {
			if (!groupIdBuild.values().contains(i)) {
				continue;
			}
			Debug.println("evaluate", "------------------------------ id: " + i);

			for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
				if (groupIdBuild.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		/////////////////////////////////////////////
		// 単連結グループIDの番号を詰める
		TreeSet<Integer> groupIdSet = new TreeSet<>();
		for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupIdSet.add(groupIdBuild.get(element));
		}

		HashMap<Integer,Integer> groupIdTrim = new HashMap<>();
		int inc = 1;
		for (int oldId : groupIdSet) {
			groupIdTrim.put(oldId, inc);
			inc++;
		}

		// 切り詰めた番号を割り振る
		HashMap<P021____AbstractGraphNodeElement,Integer> groupId2 = new HashMap<>();
		for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupId2.put(element, groupIdTrim.get(groupIdBuild.get(element)));
		}

		// 詰めた番号のセット
		TreeSet<Integer> groupIdSet2 = new TreeSet<>();
		groupIdSet2.addAll(groupId2.values());

		// デバッグ確認用
		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ(連番整列)");
		for (int i : groupIdSet2) {
			Debug.println("evaluate", "------------------------------ id: " + i);

			for (P021____AbstractGraphNodeElement element: addOrder) {
				if (groupId2.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		// elementに表示用の「単連結グループID」をつける
		graphGroup = new HashMap<>();
		for (P021____AbstractGraphNodeElement element : addOrder) {
			int id = groupId2.get(element);
			if (!graphGroup.containsKey(id)) {
				element.groupHead = id;
				ArrayList<P021____AbstractGraphNodeElement> list = new ArrayList<>();
				list.add(element);
				graphGroup.put(id, list);
			}
			else {
				element.groupHead = null;
				graphGroup.get(id).add(element);
			}
		}

		Debug.println("evaluate", "-");
		Debug.println("evaluate", "-");
		Debug.println("evaluate", "-");

		HashMap<P020___AbstractElement,P010___ControlElement> elementToControl = new HashMap<>();

		// controlオブジェクトと、制御対象になるオブジェクトの一式
		controlled_head = new HashMap<>();

		// group と control の関係をスキャンする
		//
		for (int groupId : graphGroup.keySet()) {
			P021____AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);

			for (P020___AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof P010___ControlElement) {
					P010___ControlElement control = ((P010___ControlElement)elementIcon);

					// TODO
					// 複数のcontrolに含まれる場合がある
					if (control.includes(headElement)) {
						elementToControl.put(headElement, control);

						if (! controlled_head.containsKey(control)) {
							ArrayList<P020___AbstractElement> list = new ArrayList<>();
							list.add(headElement);
							controlled_head.put(control, list);
						}
						else {
							controlled_head.get(control).add(headElement);
						}
					}
				}
			}

			// TODO
			// Control の中に Control が入れ子になっている場合に
			// それを拾い上げてデータ構造を組み立てる
		}

		// 実行順(計算順)を作成

		// 単連結グラフ先頭ノード
		HashSet<P021____AbstractGraphNodeElement> headElementSet = new HashSet<P021____AbstractGraphNodeElement>();
		for (int groupId = 1; groupId <= graphGroup.size() ; groupId++) {
			P021____AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);
			headElementSet.add(headElement);
		}

		// ControlとControlの包含関係を作る。
		// Control と Control の関係をスキャンする
		HashMap<P010___ControlElement,ArrayList<P010___ControlElement>> controlContains = new HashMap<P010___ControlElement,ArrayList<P010___ControlElement>>();

		HashSet<P020___AbstractElement> nonRoot = new HashSet<P020___AbstractElement>();

		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof P010___ControlElement)) {
				continue;
			}
			P010___ControlElement c0 = ((P010___ControlElement)ei0);
			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (ei0 == ei1) {
					continue;
				}
				/*
				if (nonRoot.contains(ei1)) {
					continue;
				}
				if (! headElementSet.contains(ei1)) {
					continue;
				}
				*/
				if (!(ei1 instanceof P010___ControlElement)) {
					continue;
				}
				P010___ControlElement c1 = (P010___ControlElement)ei1;

				if (c0.includes(c1)) {
					// 包含関係表現用オブジェクトに追加
					ArrayList<P010___ControlElement> a = controlContains.get(c0);
					if (a == null) {
						a = new ArrayList<P010___ControlElement>();
						controlContains.put(c0, a);
					}
					a.add(c1);
					nonRoot.add(c1);
				}
			}
		}

		if (Debug.enable) {
			// elementがどのcontrolにぶら下がるかを調べる:
			// デバッグ用情報のみ事前出力 0
			for (P020___AbstractElement ei1 : positionSortedElements) {
	//			ControlBlock c1 = (ControlBlock)ei1;

				if (ei1 != null) {
					Debug.println("evaluete", "pre0: " + ei1.id);
					Debug.println("evaluete", "pre0: " + ei1.str());
				}
			}



			// elementがどのcontrolにぶら下がるかを調べる:
			// デバッグ用情報のみ事前出力 0
			for (P020___AbstractElement ei1 : this.getElements()) {
	//			ControlBlock c1 = (ControlBlock)ei1;

				if (ei1 != null) {
					Debug.println("evaluete", "pre1: " + ei1.id);
					Debug.println("evaluete", "pre1: " + ei1.str());
				}
			}


			// デバッグ用情報のみ事前出力 1
			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (!(ei1 instanceof P010___ControlElement)) {
					continue;
				}
				P010___ControlElement c1 = (P010___ControlElement)ei1;

				Debug.println("evaluete", "pre: contol block --- " + c1.id);
			}
		}

		// elementがどのcontrolにぶら下がるかを調べる
		HashMap<P020___AbstractElement, P010___ControlElement> controlElementIn = new HashMap<P020___AbstractElement,P010___ControlElement>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!( headElementSet.contains(ei0) || ei0 instanceof P010___ControlElement)) {
				continue;
			}

			Debug.println("evaluate", "包含関係のチェック対象: " + ei0.getDebugIdString());

			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (ei0 == ei1) {
					continue;
				}
				if (!(ei1 instanceof P010___ControlElement)) {
					continue;
				}
				P010___ControlElement c1 = (P010___ControlElement)ei1;

				if (c1.includes(ei0)) {
					P010___ControlElement c = controlElementIn.get(ei0);
					if (c == null) {
						controlElementIn.put(ei0, c1);
						Debug.println("evaluate", "包含関係 " + ei0.getDebugIdString() + " is in " + c1.getDebugIdString());
					}
					else {
						if (c.includes(c1)) {
							controlElementIn.put(ei0, c1);

							Debug.println("evaluate", "包含関係 " + ei0.getDebugIdString() + " is in " + c1.getDebugIdString());
						}
						else if (c1.includes(c)) {
							// 敢えて実行する必要がない
							/*
							controlElementIn.put(ei0, c);
							*/
						}
						else {
							//
							Debug.println("evaluate", "交差するcontrol。 invalid。");
						}
					}
				}
			}
		}

		// 実行順構造

		// 作業用のリスト。このリストの内容を、実行順構造に追加していく。
		ArrayList<P020___AbstractElement> workList = new ArrayList<P020___AbstractElement>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (headElementSet.contains(ei0)) {
				workList.add(ei0);
			}
			else if (ei0 instanceof P010___ControlElement) {
				workList.add(ei0);
			}
		}

		// 実行順情報: 地べたに置かれた要素
		this.rootElement = new ArrayList<P020___AbstractElement>();
		for (P020___AbstractElement ei0 : workList) {
			if (controlElementIn.get(ei0) == null) {
				rootElement.add(ei0);
			}
		}

		// 実行順情報: 制御用エレメントの配下のエレメント
		this.control_contains = new HashMap<P010___ControlElement, ArrayList<P020___AbstractElement>>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof P010___ControlElement)) {
				continue;
			}
			P010___ControlElement c0 = (P010___ControlElement)ei0;

			for (P020___AbstractElement ei1 : positionSortedElements) {
				if ( controlElementIn.get(ei1) == c0) {
					ArrayList<P020___AbstractElement> a = control_contains.get(c0);
					if (a == null) {
						a = new ArrayList<P020___AbstractElement>();
						control_contains.put(c0, a);
					}
					a.add(ei1);
				}
			}
		}

		// 延長でconnector の型チェックを呼び出す。
		this.typeCheck();
	}

	Value evaluateOneGraph(P021____AbstractGraphNodeElement headElement) throws CaliculateException {
		ArrayList<P021____AbstractGraphNodeElement> eList = graphGroup.get(headElement.groupHead);
		Debug.println("evaluate", "GRPAH GROUP ---  " + headElement.groupHead + "   items: " + eList.size());
		for(P021____AbstractGraphNodeElement element : eList) {
			Debug.println("evaluate", "begin: " + element.id);
			element.evaluate();
			Debug.println("evaluate", "end: " + element.id);
		}

		P021____AbstractGraphNodeElement e = eList.get(eList.size() - 1);
		return e.workValue;
	}

	void evaluateControl(P010___ControlElement control) throws CaliculateException {
		Debug.println("evaluate", "------------------------ CONTROL"  + "  " + control.id);
		if (control.getControlType().equals("REPEAT")) {
			ControllBase c = control.init();
			Debug.println("evaluate", "------------------------ REPEAT"  + "  " + c.getDebugString());
			while( c.hasNext() ) {
				ArrayList<P020___AbstractElement> eList = control_contains.get(control);
				for(P020___AbstractElement e0 : eList) {
					if (e0 instanceof P022_____RpnGraphNodeElement) {
						P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
						evaluateOneGraph(element);
					}
					else if (e0 instanceof P010___ControlElement) {
						evaluateControl((P010___ControlElement)e0);
					}
				}

				c.nextState();
			}
		}
		else if(control.getControlType().equals("IF")) {
			Value lastValue = null;
			{
				ArrayList<P020___AbstractElement> eList = control_contains.get(control);
				for(P020___AbstractElement e0 : eList) {
					if (e0 instanceof P022_____RpnGraphNodeElement) {
						P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
						lastValue = evaluateOneGraph(element);
					}
					else if (e0 instanceof P010___ControlElement) {
						Debug.println("RUN", "INVALID STRUCTURE. 妥当でない構造。");
					}
				}
			}
			Debug.println("RUN", "last value is " + lastValue + (lastValue != null ? ("  " + lastValue.toDebugString()):""));


			P010___ControlElement thenBlock = null;
			P010___ControlElement elseBlock = null;
			for(P010___ControlElement c : control.controllerGroup) {
				if (c.getControlType().equals("THEN")) {
					thenBlock = c;
				}
				else if (c.getControlType().equals("ELSE")) {
					elseBlock = c;
				}
			}

			if (thenBlock != null) {
				if (lastValue != null && lastValue.valueType == Value.ValueType.BOOLEAN  && lastValue.sameTo(true)) {
					Debug.println("RUN", "THEN節");
					ArrayList<P020___AbstractElement> eList = control_contains.get(thenBlock);
					for(P020___AbstractElement e0 : eList) {
						if (e0 instanceof P022_____RpnGraphNodeElement) {
							P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
							evaluateOneGraph(element);
						}
						else if (e0 instanceof P010___ControlElement) {
							evaluateControl((P010___ControlElement)e0);
						}
					}
				}
			}
			if (elseBlock != null) {
				if (lastValue != null && lastValue.valueType == Value.ValueType.BOOLEAN  && lastValue.sameTo(false)) {
					Debug.println("RUN", "ELSE節");

					ArrayList<P020___AbstractElement> eList = control_contains.get(elseBlock);
					for(P020___AbstractElement e0 : eList) {
						if (e0 instanceof P022_____RpnGraphNodeElement) {
							P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
							evaluateOneGraph(element);
						}
						else if (e0 instanceof P010___ControlElement) {
							evaluateControl((P010___ControlElement)e0);
						}
					}
				}
			}
		}

	}

	public void runProgram() {
		OutputTextWindow.clear();
		OutputGraphicsWindow.reset();
		Debug.println("evaluate", "control_contains: " + control_contains.keySet().size());


		//////////////////////////////////////////////////////////
		// ここから実行
		Debug.println("evaluate", "--------------------------------------------- 「実行」★");


		try {
			for (P020___AbstractElement elementIcon : this.rootElement) {
				if (elementIcon instanceof P021____AbstractGraphNodeElement) {
					evaluateOneGraph((P021____AbstractGraphNodeElement)elementIcon);
					/*
					ArrayList<GraphNodeElement> eList = graphGroup.get(((GraphNodeElement)elementIcon).groupHead);
					for(GraphNodeElement element : eList) {
						element.evaluate();

						Debug.println("evaluate", "done: " + element.id);
					}
					*/
				}
				else if(elementIcon instanceof P010___ControlElement) {
					evaluateControl((P010___ControlElement)elementIcon);
				}
			}
		} catch (CaliculateException e) {
			// 特に処理不要
		}

		OutputGraphicsWindow.refresh();
	}


	void typeCheckForOneGraph(P021____AbstractGraphNodeElement headElement) {
		Debug.println("typeCheckForOneGraph called");

		ArrayList<P021____AbstractGraphNodeElement> eList = graphGroup.get(headElement.groupHead);
		for(P021____AbstractGraphNodeElement element : eList) {
			element.typeCheck();
		}
	}

	/**
	 * 各ノードへの入力の妥当性を検査する。
	 * (各ノードの入力型がおかしいかどうかを見ておきたい)
	 *
	 * おかしかったかどうか。
	 * 型は何になるか。
	 * を見る。
	 *
	 * 実際に計算しても良いが、
	 * 実際の計算だとゼロ除算等の実行時のエラーが発生しうるので、型の検査のみする。
	 *
	 */
	public void typeCheck() {
		Debug.println("静的な型チェック");

		for (P020___AbstractElement elementIcon : this.rootElement) {
			if (elementIcon instanceof P021____AbstractGraphNodeElement) {
				typeCheckForOneGraph((P021____AbstractGraphNodeElement)elementIcon);
			}
			else if(elementIcon instanceof P010___ControlElement) {
				// evaluateControl((P010___ControlElement)elementIcon);
			}
		}
	}

	public void load() {
		// ファイルから読み込む
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader( new File(filename) ));
			ArrayList<String> refInfo = new ArrayList<>();
			HashMap<String, P010___ControlElement> controllerMap = new HashMap<String, P010___ControlElement>();

			while( true ) {
				String line = reader.readLine();
				if( line==null ) {
					break;
				}
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				if (line.startsWith("TITLE:")) {
					this.title = line.substring("TITLE:".length());
				} else if (line.startsWith("PARAMETER:")) {
					this.paramDefList.add(ParameterDefine.getParameterDefineToEdit(line));
				} else if (line.startsWith("RPN_ELEMENT:")) {
					this.getElements().add(new P022_____RpnGraphNodeElement(this.editPanel, line));
				} else if (line.startsWith("FNC_ELEMENT:")) {
					this.getElements().add(new P023_____FncGraphNodeElement(this.editPanel, line));
				} else if (line.startsWith("CONTROL:")) {
					P010___ControlElement c = new P010___ControlElement(this.editPanel, line);
					this.getElements().add(c);

					controllerMap.put(c.id, c);
				} else if (line.startsWith("REF:")) {
					refInfo.add(line);
				} else if (line.startsWith("CONTROL_GROUP:")) {
					HashSet<P010___ControlElement> controllerGroup = new HashSet<P010___ControlElement>();
					for (String id : line.substring("CONTROL_GROUP:".length()).split(" ")) {
						if (id.equals("")) {
							continue;
						}
						// String t_ = t.replaceAll(" ", "");
						P010___ControlElement c = controllerMap.get(id);
						Debug.println("LOAD", "Control_Group   id='" + id + "'  obj=" + c);
						controllerGroup.add(c);
					}
					for (P010___ControlElement c : controllerGroup) {
						Debug.println("LOAD", "c = " + (c));

						c.controllerGroup = controllerGroup;
					}
				} else {
					System.err.println("UNKNOWN LINE: " + line);
				}
			}
			reader.close();

			System.out.println("make s2t. getElements size = " + getElements().size());
			HashMap<String, P021____AbstractGraphNodeElement> s2t = new HashMap<>();
			for( P020___AbstractElement t : getElements()) {
				if (t instanceof P021____AbstractGraphNodeElement) {
					s2t.put(t.id, (P021____AbstractGraphNodeElement)t);
					System.out.println("  name = " + t.id);
				}
			}
			System.out.println();
			for(String line : refInfo) {
				String[] a = line.split(" ");
				String name = a[1];
				String parameterName = a[2];
				String targetName = a[3];
				P021____AbstractGraphNodeElement t = s2t.get(name);
				P021____AbstractGraphNodeElement targetObj = s2t.get(targetName);
				System.out.println("LINE = " + line);
				System.out.println("  name = " + name);
				System.out.println("  paramMapInfo = " + t.paramMapInfo);
				t.paramMapInfo.put(parameterName, targetName);
				t.paramMapObj.put(parameterName, targetObj);
			}
		} catch (FileNotFoundException e) {
			System.err.println("新規にファイルを作成します。" + filename);
		} catch (IOException e) {
			System.err.println("途中でエラーが発生しました。処理を継続します。" + e.toString());
		} catch (LangSpecException e) {
			System.err.println("途中でエラーが発生しました。該当ノードは作成せず、処理を継続します。" + e.toString());
		}
	}

	public void save() throws IOException {
		// ファイルに書き込む
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter( new File(filename) ));
		writer.write("");
		writer.write("TITLE:" + this.title + "\n");
		writer.write("\n");
		for (ParameterDefine p : paramDefList) {
			writer.write( p.str() + '\n' );
		}
		writer.write("\n");
		for (P020___AbstractElement n : getElements()) {
			writer.write( n.str() + '\n' );
		}
		for (P020___AbstractElement n : getElements()) {
			for (String s : n.optStr()) {
				writer.write( s + '\n' );
			}
		}
		for (P020___AbstractElement n : getElements()) {
			if (n instanceof P010___ControlElement) {
				String s = ((P010___ControlElement)n).contollerGroup_str();
				if (s != null) {
					writer.write( s + '\n' );
				}
			}
		}

		writer.close();
	}
}
