# PatternDraw
パターンによる図形描画

## これは何?
- 2次元パターンジェネレータです。
- 幾何学模様を生成します。
- 例えば正多角形の図形のSVGが欲しいときなど。

ごく初期のCGは、レンダリング方法が素朴で、表示装置の解像度も低く、今は両方が進化した。
素朴な段階のレンダリング方法にも魅力があり、これを今の表示装置で表示したいというのが動機です。

# 使ってみよう

- 正多角形を描画する


## 模様の作図の指示を作成する
（未執筆）

## 実行する
（未執筆）

## SVGを出力する
作成した模様はSVG(Scalable Vector Graphics)形式のファイルとして出力することができます。
SVG形式のファイルは、Webブラウザにドラッグ&ドロップして表示することや、
InkScapeに読み込んでPDF(Portable Document Format)形式に変換することができます。

# プログラムを作る
## 図形言語(Script as Diagram)
字を入力するのではなく、フローチャートのような図形の組み合わせでプログラミングします。
DSLの場合、
- 文法を理解したり、覚えたりすることのコスト
- 書いて違ってて実行時にエラーになり、モチベーションが削がれてしまう
が普通の汎用的なプログラム言語より大きいと思います。
(DSLは沢山の言語があるため)

## データの型

- 数値
    - 整数(多倍長整数のみ)
    - 浮動小数点(倍精度のみ)
    - 任意精度実数
- 文字列
- ブール値

固定精度の整数はサポートしません。
もともと基本的には固定精度の整数の取り扱いのみが存在し、多倍長精度は特殊なものです。
固定精度整数と多倍長精度が両方使える処理系(システム)では、多倍長精度が必要ない場合は
固定精度整数を良く、そのメリットは計算速度やメモリ領域のオーバーヘッドです。
ダイアグラムによるプログラムでは元々オーバーヘッドがあるため、
固定精度整数／多倍長精度の速度の違いを差を気にしても仕方がありません。

## パラメーター
プログラムを実行するときのパラメーター設定を定義することができます。
パラメーターは、定数値(または変数の初期値)です。


# ワード一覧
##

# ライセンス
## アイコン
© copyright interactivemania 2010-2011
Default Icon by interactivemania is licensed under a Creative Commons Attribution-No Derivative Works 3.0

本ソフトウェアは、アイコンとして interactivemania による defaultIcon ( http://www.defaulticon.com/ )を使用しています。
このアイコン部分のライセンスは Creative Commons Attribution-No Derivative Works 3.0 です。

## アイコン以外
© copyright NOVISOFTWARE (japan) 2024
それ以外の部分のライセンスは Apache License 2.0 です。
