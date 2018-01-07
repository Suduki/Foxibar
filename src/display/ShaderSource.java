package display;

import utils.FileSlurper;

public class ShaderSource {
	
	public static String simpleVertex = FileSlurper.slurp("shaders/simple.vert");
	public static String simpleFragment = FileSlurper.slurp("shaders/simple.frag");
}
