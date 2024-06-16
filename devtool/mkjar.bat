REM jar --create --file jar/PatternDraw.jar --manifest ./META-INF/MANIFEST.MF  -C ./bin .
REM jar --create --file jar/PatternDraw.jar --main-class com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow -C ./bin .

jar cfm jar/PatternDraw.jar ./META-INF/MANIFEST.MF -C ./bin .
