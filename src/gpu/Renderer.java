package gpu;

public class Renderer {
	public Renderer() {
	}
	
	public void render(RenderNode root) {
		visitNode(root);
	}
	
	private void visitNode(RenderNode node) {
		node.enter(this);
		
		for (RenderNode child : node.children()) {
			visitNode(child);
		}
		
		node.leave(this);
	}
}
