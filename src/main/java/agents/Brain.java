package agents;
//
//
public class Brain {
	public NeuralNetwork neural;
	
	public Brain(boolean b) {
		neural = new NeuralNetwork(b);
	}

	public void inherit(Brain brain, Brain brain2) {
		neural.inherit(brain.neural, brain2.neural);
	}
}
