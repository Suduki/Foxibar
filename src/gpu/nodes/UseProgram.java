package gpu.nodes;

import gpu.Program;
import gpu.RenderNode;

public class UseProgram extends RenderNode {
	
	private Program mProgram = null;
	
	public UseProgram(Program program) {
		mProgram = program;
	}
	
	void enter() {
		mProgram.bind();
	}
}
