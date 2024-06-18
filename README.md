# PatternDraw
パターンによる図形描画

## これは何?
独自のビジュアルプログラミング環境上で動作する、2次元パターンジェネレータです。
2次元の幾何学模様のグラフィックスを生成します。
SVG, PNG の形式での出力を行います。
連番PNGでの出力も行います。

ごく初期のCGは、レンダリング方法が素朴で、表示装置も低い解像度でした。
両方が進化した現在でも、素朴な段階のレンダリング方法にも魅力があり、これを今の表示装置で表示したいというのが動機です。

## 前提ソフトウェア

### OS
Windowsの環境で動作確認をしています。

### Java
Javaの実行環境が必要です。
無料で使える OpenJDK の Microsoft Build がいいと思います。

https://learn.microsoft.com/ja-jp/java/openjdk/download#openjdk-21

### フォント
BIZ UDGothic をインストールしてください。

https://fonts.google.com/specimen/BIZ+UDGothic

## インストール (アンインストール)

以下のファイルをダウンロードし、適当なフォルダに保存します(アンインストールする場合は、ファイルを削除します)。

PatternDraw.jar


## 使ってみる

PatternDraw.jar のダブルクリック、または以下のコマンドラインで起動します。

java -jar PatternDraw.jar


## サンプルを動かしてみる

- 正多角形を描画する
- コッホ曲線

## スクリプトを書くには(模様の作図の指示を作成する)

## 実行する
（未執筆）

## SVGを出力する
作成した模様はSVG(Scalable Vector Graphics)形式のファイルとして出力することができます。
SVG形式のファイルは、Webブラウザにドラッグ&ドロップして表示することや、
InkScapeに読み込んでPDF(Portable Document Format)形式に変換することができます。

# 図形生成スクリプトを作る
## ダイヤグラムでのプログラミング(Script as Diagram)
字を入力するのではなく、箱を線でつないだ図形の組み合わせでプログラミングします。



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
