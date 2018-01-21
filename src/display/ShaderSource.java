package display;

import utils.FileSlurper;

public class ShaderSource {
	
	public static String solidGroundVertex = FileSlurper.slurp("shaders/solidGround.vert");
	public static String solidGroundFragment = FileSlurper.slurp("shaders/solidGround.frag");
	public static String waterVertex = FileSlurper.slurp("shaders/water.vert");
	public static String waterFragment = FileSlurper.slurp("shaders/water.frag");	
	public static String simVertex= FileSlurper.slurp("shaders/simFullScreenQuad.vert");
	public static String simFluxUpdateFragment = FileSlurper.slurp("shaders/simFluxUpdate.frag");
	public static String simWaterUpdateFragment = FileSlurper.slurp("shaders/simWaterUpdate.frag");
}
