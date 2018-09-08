package agents;

import java.io.Serializable;

public class Brain implements Serializable {
	private static final long serialVersionUID = 1L;
	public NeuralNetwork neural;
	
	public Brain(boolean b) {
		neural = new NeuralNetwork(b);
	}

	public void inherit(Brain brain) {
		neural.inherit(brain.neural);
	}
}
