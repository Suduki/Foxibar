package gpu;

import java.util.ArrayList;
import java.util.List;

import gpu.Renderer;

public abstract class RenderNode {
	
	public RenderNode() {
		mChildren = new ArrayList<RenderNode>();
	}
	
	public void enter(Renderer renderer) {
	}
	
	public void leave(Renderer renderer) {
	}
	
	public RenderNode addChild(RenderNode node) {
		mChildren.add(node);
		return node;
	}
	
	public List<RenderNode> children() {
		return mChildren;
	}
	
	private List<RenderNode> mChildren;	
}
