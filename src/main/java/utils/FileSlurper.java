package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileSlurper {
	public static String slurp(File file) {
		Scanner scanner;
		String string = "";
		try {
			scanner = new Scanner(file);
			string = scanner.useDelimiter("\\A").next();
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return string;
	}
	public static String slurp(String filename)	{
		return slurp(new File(filename));
	}
}
