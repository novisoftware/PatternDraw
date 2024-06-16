package com.github.novisoftware.patternDraw.svg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.utils.FileWriteUtil;

public class SvgUtil {
	public static void outSvg(SvgInstruction s, String filename, Renderer renderer) throws IOException {
		SvgUtil.outSvg(s, new File(filename), renderer);
	}

	public static void outSvg(SvgInstruction s, File file, Renderer renderer) throws IOException {
		ArrayList<String> out = new ArrayList<String>();

		out.add("<?xml version=\"1.0\"?>");
		out.add("<svg xmlns=\"http://www.w3.org/2000/svg\">");

		renderer.render(null, out, s);

		out.add("</svg>");

		FileWriteUtil.fileOutput(file, out);
	}
}
