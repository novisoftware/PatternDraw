# プログラミング環境「らららプログラミング」

## あると良いソフトウェア

他のソフトウェアを使い、「らららプログラミング」の出力をさらに加工することができます。
ここでは、出力データをさらに加工できるソフトウェアを記載します。

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

---------------------------------------
[README.mdに戻る](../README.md)