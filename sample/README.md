# サンプルプログラム(図形描画編)

ここでは図形描画用のサンプルプログラムについて説明をしていきます。

## 正多角形を描画する

- 01_draw_regular_polygon.txt では、正多角形を線分で描画します。
- 02_fill_regular_polygon.txt では、正多角形を塗りつぶします。

##### プログラム(線分で描画)

<kbd><img src="doc_image/caputure_of---01_draw_regular_polygon.png" /></kbd>

##### プログラム(塗りつぶし)

<kbd><img src="doc_image/caputure_of---02_fill_regular_polygon.png" /></kbd>

##### プログラム作成のための操作方法

「ダイヤグラムを編集」ウィンドウで左クリックすると表示される
「部品を追加」メニューから
「点の集まり - 円周上に並んだ点」を選ぶことで、
「座標の系列」アイコンを配置することができます。

この処理は円周上に並べられた点を作成するので、線分として描画すると正多角形の出来上がりです。

<kbd><img src="doc_image/manipulation_001.png" /></kbd>

「ダイヤグラムを編集」ウィンドウで左クリックすると表示される
「部品を追加」メニューから
「点の集まり - 円周上に並んだ点」を選ぶことで、
「座標の系列」アイコンを配置することができます。

この処理は円周上に並べられた点を作成するので、線分として描画すると正多角形の出来上がりです。


また、閉じた多角形を描画する場合、「部品を追加」メニューから「出力 - 線の集まりをつないで描画(閉じる)」を選びます。

<kbd><img src="doc_image/manipulation_002.png" /></kbd>

##### 生成される画像

以下は 01_draw_regular_polygon.txt により生成した線分での多角形の描画です。

<img width="300px" src="image_sample/01_draw_regular_polygon_inkscape.svg" />

以下は 02_fill_regular_polygon.txt により生成した多角形の塗りつぶしです。

<img width="300px" src="image_sample/02_fill_regular_polygon_inkscape.svg" />

## 星形を描画する

以下の2通りを作例として格納しています。
- 03_star_example1.txt
- 04_star_example2.txt

03_star_example1.txt では、多角形の系列の順番を入れ替えることで星形を描画しています。

04_star_example2.txt では、外側の頂点の系列と内側の頂点の系列をそれぞれ作成し、混ぜ合わせてひとつの系列にすることで星形を描画しています。


##### 生成される画像

03_star_example1.txt による作図例を 1 枚 、および、04_star_example2.txt による作図例を 2 枚示します。

<img width="300px" src="image_sample/03_star_example1_inkscape.svg" />
<img width="300px" src="image_sample/04_star_example2_inkscape.svg" />
<img width="300px" src="image_sample/04_star_example2_b_inkscape.svg" />


## コッホ曲線(コッホ雪片)

イニシエーターとジェネレーターの組み合わせによりフラクタル図形を生成する例です。





