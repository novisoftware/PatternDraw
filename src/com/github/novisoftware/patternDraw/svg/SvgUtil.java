package com.github.novisoftware.patternDraw.svg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.renderer.Renderer;

public class SvgUtil {
	private static final String LF = "\n";

	public static void fileOutput(File file, ArrayList<String> buff) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			OutputStreamWriter br = new OutputStreamWriter(bos);
			for (String line : buff) {
				br.write(line);
				br.write(LF);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void outSvg(SvgInstruction s, String filename, Renderer renderer) {
		SvgUtil.outSvg(s, new File(filename), renderer);
	}

	public static void outSvg(SvgInstruction s, File file, Renderer renderer) {
		ArrayList<String> out = new ArrayList<String>();

		out.add("<?xml version=\"1.0\"?>");
		out.add("<svg xmlns=\"http://www.w3.org/2000/svg\">");

		renderer.render(null, out, s);

		out.add("</svg>");

		fileOutput(file, out);
	}
}
