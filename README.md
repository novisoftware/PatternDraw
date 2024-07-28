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

### 前提とする環境・ソフトウェア

#### OS

Windows 10環境で動作確認をしています。
メモリ8GBの環境で確認しています(メモリ4GBでの動作確認はしていません)。

Ubuntu 等でも動かせるようになるかもしれませんが、動作確認をしていません。

#### 画面の解像度

1920×1080 (FHD) 以上の解像度を想定しています。

#### Java

Javaの実行環境が必要です。
無料で使える OpenJDK の Microsoft Build がいいと思います。

https://learn.microsoft.com/ja-jp/java/openjdk/download#openjdk-21

#### フォント
BIZ UDGothic をインストールしてください。

https://fonts.google.com/specimen/BIZ+UDGothic

### インストール (アンインストール)
以下のファイルをダウンロードし、適当なフォルダに保存します(アンインストールする場合は、ファイルを削除します)。

lalala.jar


## あると良いソフトウェア

他のソフトウェアを使い、「らららプログラミング」の出力をさらに加工することができます。

### Inkscape
Inkscapeはオープンソースで開発されているベクター画像編集ソフトウェアです。
出力したSVGファイルをInkscapeで読み込むことで、さらに編集したり、印刷用のPDFファイルを出力することができます。

https://inkscape.org/ja/

### FFmpeg
FFmpegは動画を変換することができるフリーソフトウェアです。

https://ffmpeg.org/

FFmpegを使用することにより、出力した連番PNGファイルを元に動画を作成することができます。
例えば以下のようなコマンドラインでアニメーションGIFを作成することができます。

```
ffmpeg -f image2 -r 12 -i image%5d.png -r 12 -an -filter_complex "[0:v] split [a][b];[a] palettegen [p];[b][p] paletteuse"  -f gif output.gif
```

## 使用方法

サンプルプログラムを動かしてみます。

プログラミングする方法は [ここ](./doc/specification.md) を参照してください。

### 起動

Windows の場合はエクスプローラーから lalala.jar をダブルクリック、または以下のコマンドラインで起動します。

```
java -jar lalala.jar
```

![起動直後の画面](./doc/image/edit_window_blank.png "キャプチャ(起動直後の画面)")

### サンプルを動かしてみる

サンプルフォルダ [sample](./sample/) のファイルをダウンロードし、エディタにドラッグ&ドロップします。
以下はサンプル 01_draw_regular_polygon.txt を読み込んだ状態です。

![サンプルを読み込んだ画面](./doc/image/edit_window_draw_polygon.png "キャプチャ(サンプルを読み込んだ画面)")

### 実行する

プルダウンメニューから実行を選びます。

![「実行」メニュー](./doc/image/excecute_button.png "キャプチャ(「実行」メニュー)")

実行すると以下のようなウィンドウが表示されます。

![実行時のウィンドウ](./doc/image/widow_in_running.png "キャプチャ(実行時のウィンドウ)")

- 「パラメーターの指定」では、パラーメーターの値を変更します。
- 「グラフィックの出力」では、グラフィックの描画結果が表示されます。
- 「テキストの出力」では、テキストの出力があれば表示されます。

パラメーターを変えると、グラフィックの出力に反映されます。

また、「グラフィックの出力」のメニューから画像ファイルを保存できます。

![「画像を出力」メニュー](./doc/image/graphics_save_menu.png "キャプチャ(「画像を出力」メニュー)")

### 終了

編集ウィンドウの終了メニュー、または、閉じる(×) アイコンで、「らららプログラミング」が終了します。

![「終了」メニュー](./doc/image/exit_menu.png "キャプチャ(「終了」メニュー)")

# セキュリティ関連

セキュリティ関連の覚え書きです。

## Java について

[Java](https://ja.wikipedia.org/wiki/Java) は、かつて米国のサン・マイクロシステムズ社によって開発されたプログラミング言語・実行環境です。
Javaの環境自体のセキュリティは、Java実行環境の配布元によって担保されます。

## 「らららプログラミング」について

「らららプログラミング」について、セキュリティに関連しそうな事柄を以下にメモします。

- インストーラーを持ちません
- 実行プログラムは、Windowsレジストリへのアクセスを行いません
- 権限昇格関係のAPIを使用しません
- 「らららプログラミング」は、ユーザー操作による入出力時にファイルへのアクセスを行います。
    - プログラムが記述されたファイルの読み取り
    - 画像ファイルの出力
- 現時点で、ダイヤグラムでプログラミングされたプログラミング言語の中から、以下を呼び出すことはできません(機能を作成してません)。
    - ファイルの入出力(ストレージへのアクセス)
    - ネットワークへのアクセス

# ライセンス
## アイコン
© copyright interactivemania 2010-2011
Default Icon by interactivemania is licensed under a Creative Commons Attribution-No Derivative Works 3.0

本ソフトウェアは、アイコンとして interactivemania による defaultIcon ( http://www.defaulticon.com/ )を使用しています。
このアイコン部分のライセンスは Creative Commons Attribution-No Derivative Works 3.0 です。

## アイコン以外

© copyright NOVISOFTWARE (japan) 2024

アイコン以外の部分のライセンスは Apache License 2.0 です。

## 生成された画像

生成された画像の著作権は、(著作権が主張できるようなものであれば)生成した人のものです。

(本ソフトウェアや、本ソフトウェアの作者は、ユーザーが生成した画像について何の権利も主張しません)

