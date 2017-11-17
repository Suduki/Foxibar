//package agents;
//
//import java.util.ArrayList;
//
//import agents.Brain.Neuron.Link;
//import constants.Constants;
//
//public class Brain {
//	public Neuron[][] layers;
//	
//	
//	public Brain(int[] layerSizes) {
//		layers = new Neuron[layerSizes.length][];
//		for (int i = 0; 0 < layerSizes.length; ++i) {
//			layers[i] = new Neuron[layerSizes[i]];
//			for (int neuron = 0; neuron < layerSizes[i]; ++neuron) {
//				layers[i][neuron] = new Neuron();
//			}
//		}
//		for (int i = 0; 0 < layerSizes.length - 1; ++i) {
//			for (int leftNeuron = 0; 0 < layerSizes[i]; ++leftNeuron) {
//				for (int rightNeuron = 0; 0 < layerSizes[i+1]; ++rightNeuron) {
//					// Add link from left layer to neuron in right layer
//					layers[i][leftNeuron].addLink(layers[i][rightNeuron]);
//				}
//			}
//		}
//	}
//	
//	public void inherit(Brain ancestor) {
//		for (int i = 0; 0 < ancestor.layers.length; ++i) {
//			for (int neuron = 0; 0 < ancestor.layers[i].length; ++neuron) {
//				for (Link link : layers[i][neuron].links) {
//					link.weight = ancestor.layers[i][neuron].links
//				}
//			}
//		}
//	}
//	
//	///////////////////////  N  E  U  R  O  N  ////////////////////////
//	public class Neuron {
//		public ArrayList<Link> links;
//		public float value;
//		public Neuron() {
//			links = new ArrayList<>();
//		}
//		public void addLink(Neuron child) {
//			links.add(new Link(child));
//		}
//		
//		/////////////////////////  L   I   N   K  //////////////////////////////
//		public class Link {
//			public float weight;
//			public Neuron child;
//			
//			public Link(Neuron child) {
//				this.child = child;
//			}
//			
//			public void communicate() {
//				child.value += weight * value;
//			}
//		}
//	}
//}
