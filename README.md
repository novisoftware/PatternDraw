# プログラミング環境「らららプログラミング」

## 概要
「らららプログラミング」は、2次元の幾何学模様のグラフィックスを生成するために作成したビジュアルプログラミング環境です。

作成した模様は以下の形式のファイルとして出力することができます。
- SVG(Scalable Vector Graphics)形式
- PNG(Portable Network Graphics)形式
- 連番PNG (複数のPNGファイルの出力)

SVG形式のファイルは、Webブラウザにドラッグ&ドロップして表示することや、InkScapeに読み込んでPDF(Portable Document Format)形式に変換することができます。

## 開発動機
ごく初期のCGは、レンダリング方法が素朴で、表示装置も低い解像度でした。
両方が進化した現在でも、素朴な段階のレンダリング方法にも魅力があり、これを今の表示装置で表示したいというのが動機です。

## 導入方法
Windows PC と PCにソフトウェアをインストールできる権限が必要です。

### 前提ソフトウェア

#### OS
Windows 10環境で動作確認をしています。
メモリ8GBの環境で確認しています(メモリ4GBでの動作確認はしていません)。

Ubuntu 等でも動かせるようになるかもしれませんが、動作確認をしていません。

#### Java
Javaの実行環境が必要です。
無料で使える OpenJDK の Microsoft Build がいいと思います。

https://learn.microsoft.com/ja-jp/java/openjdk/download#openjdk-21

#### フォント
BIZ UDGothic をインストールしてください。

https://fonts.google.com/specimen/BIZ+UDGothic

### インストール (アンインストール)

以下のファイルをダウンロードし、適当なフォルダに保存します(アンインストールする場合は、ファイルを削除します)。

PatternDraw.jar


## あると良いソフトウェア

### Inkscape
出力したSVGファイルをInkscapeで読み込むことで、さらに編集したり、印刷用のPDFファイルを出力することができます。

https://inkscape.org/ja/

### ffmpeg
出力した連番PNGファイルを元に、動画gifファイルを作成することができます。
例えば以下のようなコマンドラインです。

```
ffmpeg -f image2 -r 12 -i image%5d.png -r 12 -an -filter_complex "[0:v] split [a][b];[a] palettegen [p];[b][p] paletteuse"  -f gif output.gif
```

## 使用方法

### 起動

Windows の場合はエクスプローラーから PatternDraw.jar をダブルクリック、または以下のコマンドラインで起動します。

```
java -jar PatternDraw.jar
```

### サンプルを動かしてみる

- 正多角形を描画する
- コッホ曲線

## スクリプトを書くには(模様の作図の指示を作成する)

字を入力するのではなく、箱を線でつないだ図形の組み合わせでプログラミングします。
(細かな説明は[ここ](./doc/specification.md) を参照してください)

### アイコンを配置する

左クリックでメニューが表示されます。
メニューから配置したいアイコンを選びます。

### 線で結ぶ

端子をドラッグすると線が伸び、アイコンとアイコンを結ぶことができます。



# ライセンス
## アイコン
© copyright interactivemania 2010-2011
Default Icon by interactivemania is licensed under a Creative Commons Attribution-No Derivative Works 3.0

本ソフトウェアは、アイコンとして interactivemania による defaultIcon ( http://www.defaulticon.com/ )を使用しています。
このアイコン部分のライセンスは Creative Commons Attribution-No Derivative Works 3.0 です。

## アイコン以外
© copyright NOVISOFTWARE (japan) 2024

アイコン以外の部分のライセンスは Apache License 2.0 です。
