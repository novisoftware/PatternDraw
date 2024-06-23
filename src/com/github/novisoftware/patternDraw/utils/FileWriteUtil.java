package com.github.novisoftware.patternDraw.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FileWriteUtil {
	public static final String LF = "\n";
	public static final String UTF8 = "UTF-8";

	public static void fileOutput(File file, ArrayList<String> buff) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		OutputStreamWriter br;
		try {
			br = new OutputStreamWriter(bos, UTF8);
			for (String line : buff) {
				br.write(line);
				br.write(LF);
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println("UTF-8 に対する UnsupportedEncodingException");
			System.exit(1);
		}
	}

	public static File replaceSuffix(File file, String newSuffix) {
		String newFileName = file.getAbsolutePath().replaceAll("[.]...$", "." + newSuffix);
		if (!newFileName.endsWith(newSuffix)) {
			// replaceAll() では置き換わらなかった場合。
			// (元々拡張子が付いていないなど)
			newFileName = newFileName + newSuffix;
		}
		return new File(newFileName);
	}

	/**
	 * 拡張子の点検。 ファイル選択ダイアログで png, svg 等の拡張子を付け忘れた場合のため。
	 *
	 * @param file
	 * @param newSuffix
	 * @return
	 */
	public static File checkSuffix(File file, String newSuffix) {
		if (file.getAbsolutePath().toLowerCase().endsWith(newSuffix.toLowerCase())) {
			return file;
		}

		return new File(file.getAbsolutePath() + "." + newSuffix);
	}

}
