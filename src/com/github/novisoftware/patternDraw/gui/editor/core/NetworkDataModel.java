package com.github.novisoftware.patternDraw.gui.editor.core;

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

import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsFrame;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextFrame;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.FncGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.ControllBase;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;

public class NetworkDataModel {
	/**
	 * ウィンドウタイトルの文字列の一部分として使用する。
	 */
	public String title = "";

	private String filename;
	private ArrayList<AbstractElement> elements = new ArrayList<AbstractElement>();
	protected EditDiagramPanel editPanel;

	/**
	 *  変数名の一覧(設定された変数のみを対象にする)
	 */
	public ArrayList<String> nameOfvaliables;


	/**
	 *  実行順情報: 制御用エレメントの配下のエレメント
	 */
	public HashMap<ControlElement, ArrayList<AbstractElement>> control_contains;
	//
	/**
	 * 実行順情報: ルート要素。 GUI上では「地べたに置かれた要素」が該当する。
	 */
	ArrayList<AbstractElement> rootElement;

	// elementに表示用の「単連結グループID」をつける
	public HashMap<Integer,ArrayList<AbstractGraphNodeElement>> graphGroup;
	public HashMap<ControlElement, ArrayList<AbstractElement>> controlled_head;
	public HashMap<ControlElement, ArrayList<AbstractElement>> controlled_all;

	public ArrayList<AbstractElement> getElements() {
		return elements;
	}

	public NetworkDataModel(EditDiagramPanel editPanel, String filename) {
		this.editPanel = editPanel;
		this.filename = filename;
	}

	/**
	 * abc1, abc2, abc3 ... のような連番で、ユニークな名前を生成します。
	 *
	 * @param inputName
	 * @return
	 */
	public String generateUniqueName(String inputName) {
		// 衝突しない名前を設定する
		// （末尾の数字を１加算する）
		final String REG = "(.*)([0-9]+)";

		String baseName = inputName.replaceAll(REG, "$1");
		String number = inputName.replaceAll(REG, "$2");
		int d = 0;
		try {
			d = Integer.parseInt(number);
		} catch(Exception ex) {
		}

		HashSet<String> nameSet = new HashSet<>();
		for (AbstractElement to :  this.getElements()) {
			nameSet.add(to.id);
		}

		// 名前が衝突しなくなるまでインクリメントする
		String name = "";
		while (true) {
			d++;
			name = baseName + d;
			if (! nameSet.contains(name)) {
				break;
			}
		}

		return name;
	}


	/**
	 * 位置関係の比較
	 *
	 */
	class posComparator implements java.util.Comparator<AbstractElement> {
		@Override
		public int compare(AbstractElement o1, AbstractElement o2) {
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
			// ★ TODO
			// 0 を返してはいけない。オブジェクトが同一視される
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
	evaluate(
//			ArrayList<Element> returnList, HashMap<Element,HashMap<String,Integer>> name2index
			) {
		// 上から下の順にソートした Element
		TreeSet<AbstractElement> positionSortedElements = new TreeSet<>(new posComparator());
		positionSortedElements.addAll(elements);

		// 変数名の一覧(設定された変数のみを対象にする)
		nameOfvaliables = new ArrayList<>();

		// 変数名の一覧を作成する
		for (AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof RpnGraphNodeElement) {
				if (((RpnGraphNodeElement)elementIcon).getKindId().equals(KindId.VARIABLE_SET)) {
				// if (((RpnGraphNodeElement)elementIcon).getKindString().equals("変数を設定")) {
					String rep = ((RpnGraphNodeElement)elementIcon).getRepresentExpression();
					if (! nameOfvaliables.contains(rep)) {
						nameOfvaliables.add(rep);
					}
				}
			}
		}

		for (String varName: nameOfvaliables) {
			Debug.println("Evaluate", "var name: " + varName);
		}

		//////////////////////////////////////////////////////////
		// グラフ構造を解析して、評価順をきめる

		// 幅優先探索での追加順
		ArrayList<AbstractGraphNodeElement> addOrder = new ArrayList<>();

		// 他の要素を参照していない要素
		// 「計算済の値」の集合の初期値
		HashMap<AbstractGraphNodeElement,Integer> addSet0 = new HashMap<>();
		int workCounter = 0;
		for (AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof AbstractGraphNodeElement) {
				AbstractGraphNodeElement referFrom = ((AbstractGraphNodeElement)elementIcon);
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
		ArrayList<Set<AbstractGraphNodeElement>> addSet = new ArrayList<>();
		addSet.add(addSet0.keySet());

		HashMap<AbstractGraphNodeElement,Integer> foundSet = new HashMap<>();
		foundSet.putAll(addSet0);

		// グループの合流。数字をマージする
		HashMap<Integer,Integer> mergeInfo_old = new HashMap<>();
		ArrayList<Integer> mergeFrom = new ArrayList<Integer>();
		ArrayList<Integer> mergeTo = new ArrayList<Integer>();

		while (true) {
			HashMap<AbstractGraphNodeElement,Integer> nextAddSet = new HashMap<>();

			for (AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof AbstractGraphNodeElement) {
					AbstractGraphNodeElement element = ((AbstractGraphNodeElement)elementIcon);
					// 入力を持つノードを対象に処理をする
					if (foundSet.containsKey(element)) {
						continue;
					}
					boolean isOk = true;
					int num = -1;
					for (AbstractElement e_ : element.paramMapObj.values()) {
						if (e_ instanceof AbstractGraphNodeElement) {
							// 入力ノード
							AbstractGraphNodeElement e = (AbstractGraphNodeElement)e_;

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
		HashMap<AbstractGraphNodeElement,Integer> groupIdBuild = new HashMap<>();
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
		for (AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = foundSet.get(element);
			groupIdBuild.put(element,groupIdWork);
		}
		int n = mergeFrom.size();
		for (int i = 0; i < n; i++) {
			int from = mergeFrom.get(i);
			int to = mergeTo.get(i);

			for (AbstractGraphNodeElement element: foundSet.keySet()) {
				int t = groupIdBuild.get(element);
				if (t == from) {
					groupIdBuild.put(element, to);
				}
			}
		}
		// デバッグ確認用
		for (AbstractGraphNodeElement element: foundSet.keySet()) {
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

			for (AbstractGraphNodeElement element: groupIdBuild.keySet()) {
				if (groupIdBuild.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		/////////////////////////////////////////////
		// 単連結グループIDの番号を詰める
		TreeSet<Integer> groupIdSet = new TreeSet<>();
		for (AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupIdSet.add(groupIdBuild.get(element));
		}

		HashMap<Integer,Integer> groupIdTrim = new HashMap<>();
		int inc = 1;
		for (int oldId : groupIdSet) {
			groupIdTrim.put(oldId, inc);
			inc++;
		}

		// 切り詰めた番号を割り振る
		HashMap<AbstractGraphNodeElement,Integer> groupId2 = new HashMap<>();
		for (AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupId2.put(element, groupIdTrim.get(groupIdBuild.get(element)));
		}

		// 詰めた番号のセット
		TreeSet<Integer> groupIdSet2 = new TreeSet<>();
		groupIdSet2.addAll(groupId2.values());

		// デバッグ確認用
		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ(連番整列)");
		for (int i : groupIdSet2) {
			Debug.println("evaluate", "------------------------------ id: " + i);

			for (AbstractGraphNodeElement element: addOrder) {
				if (groupId2.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		// elementに表示用の「単連結グループID」をつける
		graphGroup = new HashMap<>();
		for (AbstractGraphNodeElement element : addOrder) {
			int id = groupId2.get(element);
			if (!graphGroup.containsKey(id)) {
				element.groupHead = id;
				ArrayList<AbstractGraphNodeElement> list = new ArrayList<>();
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

		HashMap<AbstractElement,ControlElement> elementToControl = new HashMap<>();

		// controlオブジェクトと、制御対象になるオブジェクトの一式
		controlled_head = new HashMap<>();

		// group と control の関係をスキャンする
		//
		for (int groupId : graphGroup.keySet()) {
			AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);

			for (AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof ControlElement) {
					ControlElement control = ((ControlElement)elementIcon);

					// TODO
					// 複数のcontrolに含まれる場合がある
					if (control.includes(headElement)) {
						elementToControl.put(headElement, control);

						if (! controlled_head.containsKey(control)) {
							ArrayList<AbstractElement> list = new ArrayList<>();
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
		HashSet<AbstractGraphNodeElement> headElementSet = new HashSet<AbstractGraphNodeElement>();
		for (int groupId = 1; groupId <= graphGroup.size() ; groupId++) {
			AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);
			headElementSet.add(headElement);
		}

		// ControlとControlの包含関係を作る。
		// Control と Control の関係をスキャンする
		HashMap<ControlElement,ArrayList<ControlElement>> controlContains = new HashMap<ControlElement,ArrayList<ControlElement>>();

		HashSet<AbstractElement> nonRoot = new HashSet<AbstractElement>();

		for (AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof ControlElement)) {
				continue;
			}
			ControlElement c0 = ((ControlElement)ei0);
			for (AbstractElement ei1 : positionSortedElements) {
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
				if (!(ei1 instanceof ControlElement)) {
					continue;
				}
				ControlElement c1 = (ControlElement)ei1;

				if (c0.includes(c1)) {
					// 包含関係表現用オブジェクトに追加
					ArrayList<ControlElement> a = controlContains.get(c0);
					if (a == null) {
						a = new ArrayList<ControlElement>();
						controlContains.put(c0, a);
					}
					a.add(c1);
					nonRoot.add(c1);
				}
			}
		}

		// elementがどのcontrolにぶら下がるかを調べる:
		// デバッグ用情報のみ事前出力 0
		for (AbstractElement ei1 : positionSortedElements) {
//			ControlBlock c1 = (ControlBlock)ei1;

			if (ei1 != null) {
				Debug.println("evaluete", "pre0: " + ei1.id);
				Debug.println("evaluete", "pre0: " + ei1.str());
			}
		}



		// elementがどのcontrolにぶら下がるかを調べる:
		// デバッグ用情報のみ事前出力 0
		for (AbstractElement ei1 : this.getElements()) {
//			ControlBlock c1 = (ControlBlock)ei1;

			if (ei1 != null) {
				Debug.println("evaluete", "pre1: " + ei1.id);
				Debug.println("evaluete", "pre1: " + ei1.str());
			}
		}


		// デバッグ用情報のみ事前出力 1
		for (AbstractElement ei1 : positionSortedElements) {
			if (!(ei1 instanceof ControlElement)) {
				continue;
			}
			ControlElement c1 = (ControlElement)ei1;

			Debug.println("evaluete", "pre: contol block --- " + c1.id);
		}

		// elementがどのcontrolにぶら下がるかを調べる
		HashMap<AbstractElement, ControlElement> controlElementIn = new HashMap<AbstractElement,ControlElement>();
		for (AbstractElement ei0 : positionSortedElements) {
			if (!( headElementSet.contains(ei0) || ei0 instanceof ControlElement)) {
				continue;
			}

			Debug.println("evaluate", "包含関係のチェック対象: " + ei0.getDebugIdString());

			for (AbstractElement ei1 : positionSortedElements) {
				if (ei0 == ei1) {
					continue;
				}
				if (!(ei1 instanceof ControlElement)) {
					continue;
				}
				ControlElement c1 = (ControlElement)ei1;

				if (c1.includes(ei0)) {
					ControlElement c = controlElementIn.get(ei0);
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
		ArrayList<AbstractElement> workList = new ArrayList<AbstractElement>();
		for (AbstractElement ei0 : positionSortedElements) {
			if (headElementSet.contains(ei0)) {
				workList.add(ei0);
			}
			else if (ei0 instanceof ControlElement) {
				workList.add(ei0);
			}
		}

		// 実行順情報: 地べたに置かれた要素
		this.rootElement = new ArrayList<AbstractElement>();
		for (AbstractElement ei0 : workList) {
			if (controlElementIn.get(ei0) == null) {
				rootElement.add(ei0);
			}
		}

		// 実行順情報: 制御用エレメントの配下のエレメント
		this.control_contains = new HashMap<ControlElement, ArrayList<AbstractElement>>();
		for (AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof ControlElement)) {
				continue;
			}
			ControlElement c0 = (ControlElement)ei0;

			for (AbstractElement ei1 : positionSortedElements) {
				if ( controlElementIn.get(ei1) == c0) {
					ArrayList<AbstractElement> a = control_contains.get(c0);
					if (a == null) {
						a = new ArrayList<AbstractElement>();
						control_contains.put(c0, a);
					}
					a.add(ei1);
				}
			}
		}
	}

	Value evaluateOneGraph(AbstractGraphNodeElement headElement) {
		ArrayList<AbstractGraphNodeElement> eList = graphGroup.get(headElement.groupHead);
		Debug.println("evaluate", "GRPAH GROUP ---  " + headElement.groupHead + "   items: " + eList.size());
		for(AbstractGraphNodeElement element : eList) {
			Debug.println("evaluate", "begin: " + element.id);
			/*
			element.evaluate();
			*/
			if (element instanceof RpnGraphNodeElement) {
				((RpnGraphNodeElement)element).evaluate();
				Debug.println("rpn evaluate is called");
			} else if (element instanceof FncGraphNodeElement) {
				Debug.println("fnc evaluate is called");
				((FncGraphNodeElement)element).evaluate(OutputGraphicsFrame.getRenderer());
				OutputGraphicsFrame.getInstance().repaint();
			}
			Debug.println("evaluate", "end: " + element.id);
		}

		AbstractGraphNodeElement e = eList.get(eList.size() - 1);
		return e.workValue;
	}

	void evaluateControl(ControlElement control) {
		Debug.println("evaluate", "------------------------ CONTROL"  + "  " + control.id);
		if (control.getControlType().equals("REPEAT")) {
			ControllBase c = control.init();
			Debug.println("evaluate", "------------------------ REPEAT"  + "  " + c.getDebugString());
			while( c.hasNext() ) {
				ArrayList<AbstractElement> eList = control_contains.get(control);
				for(AbstractElement e0 : eList) {
					if (e0 instanceof RpnGraphNodeElement) {
						RpnGraphNodeElement element = ((RpnGraphNodeElement) e0);
						evaluateOneGraph(element);
					}
					else if (e0 instanceof ControlElement) {
						evaluateControl((ControlElement)e0);
					}
				}

				c.nextState();
			}
		}
		else if(control.getControlType().equals("IF")) {
			Value lastValue = null;
			{
				ArrayList<AbstractElement> eList = control_contains.get(control);
				for(AbstractElement e0 : eList) {
					if (e0 instanceof RpnGraphNodeElement) {
						RpnGraphNodeElement element = ((RpnGraphNodeElement) e0);
						lastValue = evaluateOneGraph(element);
					}
					else if (e0 instanceof ControlElement) {
						Debug.println("RUN", "INVALID STRUCTURE. 妥当でない構造。");
					}
				}
			}
			Debug.println("RUN", "last value is " + lastValue + (lastValue != null ? ("  " + lastValue.toDebugString()):""));


			ControlElement thenBlock = null;
			ControlElement elseBlock = null;
			for(ControlElement c : control.controllerGroup) {
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
					ArrayList<AbstractElement> eList = control_contains.get(thenBlock);
					for(AbstractElement e0 : eList) {
						if (e0 instanceof RpnGraphNodeElement) {
							RpnGraphNodeElement element = ((RpnGraphNodeElement) e0);
							evaluateOneGraph(element);
						}
						else if (e0 instanceof ControlElement) {
							evaluateControl((ControlElement)e0);
						}
					}
				}
			}
			if (elseBlock != null) {
				if (lastValue != null && lastValue.valueType == Value.ValueType.BOOLEAN  && lastValue.sameTo(false)) {
					Debug.println("RUN", "ELSE節");

					ArrayList<AbstractElement> eList = control_contains.get(elseBlock);
					for(AbstractElement e0 : eList) {
						if (e0 instanceof RpnGraphNodeElement) {
							RpnGraphNodeElement element = ((RpnGraphNodeElement) e0);
							evaluateOneGraph(element);
						}
						else if (e0 instanceof ControlElement) {
							evaluateControl((ControlElement)e0);
						}
					}
				}
			}
		}

	}

	public void runProgram() {
		OutputTextFrame.clear();
		OutputGraphicsFrame.reset();
		Debug.println("evaluate", "control_contains: " + control_contains.keySet().size());


		//////////////////////////////////////////////////////////
		// ここから実行
		Debug.println("evaluate", "--------------------------------------------- 「実行」★");


		for (AbstractElement elementIcon : this.rootElement) {
			if (elementIcon instanceof AbstractGraphNodeElement) {
				evaluateOneGraph((AbstractGraphNodeElement)elementIcon);
				/*
				ArrayList<GraphNodeElement> eList = graphGroup.get(((GraphNodeElement)elementIcon).groupHead);
				for(GraphNodeElement element : eList) {
					element.evaluate();

					Debug.println("evaluate", "done: " + element.id);
				}
				*/
			}
			else if(elementIcon instanceof ControlElement) {
				evaluateControl((ControlElement)elementIcon);
			}
		}

		OutputGraphicsFrame.refresh();
	}

	public void load() {
		// ファイルから読み込む
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader( new File(filename) ));
			ArrayList<String> refInfo = new ArrayList<>();
			HashMap<String, ControlElement> controllerMap = new HashMap<String, ControlElement>();

			while( true ) {
				String line = reader.readLine();
				if( line==null ) {
					break;
				}
				if (line.startsWith("TITLE:")) {
					this.title = line.substring("TITLE:".length());
				} else if (line.startsWith("RPN_ELEMENT:")) {
					this.getElements().add(new RpnGraphNodeElement(this.editPanel, line));
				} else if (line.startsWith("FNC_ELEMENT:")) {
					this.getElements().add(new FncGraphNodeElement(this.editPanel, line));
				} else if (line.startsWith("CONTROL:")) {
					ControlElement c = new ControlElement(this.editPanel, line);
					this.getElements().add(c);

					controllerMap.put(c.id, c);
				} else if (line.startsWith("REF:")) {
					refInfo.add(line);
				} else if (line.startsWith("CONTROL_GROUP:")) {
					HashSet<ControlElement> controllerGroup = new HashSet<ControlElement>();
					for (String id : line.substring("CONTROL_GROUP:".length()).split(" ")) {
						if (id.equals("")) {
							continue;
						}
						// String t_ = t.replaceAll(" ", "");
						ControlElement c = controllerMap.get(id);
						Debug.println("LOAD", "Control_Group   id='" + id + "'  obj=" + c);
						controllerGroup.add(c);
					}
					for (ControlElement c : controllerGroup) {
						Debug.println("LOAD", "c = " + (c));

						c.controllerGroup = controllerGroup;
					}
				} else {
					System.err.println("UNKNOWN LINE: " + line);
				}
			}
			reader.close();

			System.out.println("make s2t. getElements size = " + getElements().size());
			HashMap<String, AbstractGraphNodeElement> s2t = new HashMap<>();
			for( AbstractElement t : getElements()) {
				if (t instanceof AbstractGraphNodeElement) {
					s2t.put(t.id, (AbstractGraphNodeElement)t);
					System.out.println("  name = " + t.id);
				}
			}
			System.out.println();
			for(String line : refInfo) {
				String[] a = line.split(" ");
				String name = a[1];
				String parameterName = a[2];
				String targetName = a[3];
				AbstractGraphNodeElement t = s2t.get(name);
				AbstractGraphNodeElement targetObj = s2t.get(targetName);
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

	public void save() {
		// ファイルに書き込む
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter( new File(filename) ));
			writer.write("");
			writer.write("TITLE:" + this.title + "\n");
			for (AbstractElement n : getElements()) {
				writer.write( n.str() + '\n' );
			}
			for (AbstractElement n : getElements()) {
				for (String s : n.optStr()) {
					writer.write( s + '\n' );
				}
			}
			for (AbstractElement n : getElements()) {
				if (n instanceof ControlElement) {
					String s = ((ControlElement)n).contollerGroup_str();
					if (s != null) {
						writer.write( s + '\n' );
					}
				}
			}

			writer.close();
		} catch (IOException e) {
			System.err.println("途中でエラーが発生しました。" + e.toString());
		}
	}
}
