package dataPlotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import agents.Animal;
import agents.NeuralFactors;
import agents.NeuralNetwork;
import constants.Constants;
import constants.RenderState;
import main.Main;
import world.World;

@SuppressWarnings("serial")
public class XYPlotThingVersusTime2 extends ApplicationFrame {

	public static final XYPlotThingVersusTime2 myInstance = new XYPlotThingVersusTime2("Plot versus time");

	public static final int NUM_STUFF = 2;
	
	public static XYSeries[] h = new XYSeries[NUM_STUFF];

	public XYPlotThingVersusTime2(String title) {
		super(title);
	}

	public static void plotStuff() {
		final XYSeriesCollection data = new XYSeriesCollection();
		
		for (int i = 0; i < NUM_STUFF; ++i) {
			h[i] = new XYSeries("h"+i);
			data.addSeries(h[i]);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"XY Series Demo",
				"X", 
				"Y", 
				data,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
				);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		myInstance.setContentPane(chartPanel);
		myInstance.pack();
		RefineryUtilities.centerFrameOnScreen(myInstance);
		myInstance.setVisible(true);
		
	}
	
	private final int width = 1000;
	private int index = 0;
	public void step() {
		NeuralNetwork brain;
		if (RenderState.FOLLOW_BLOODLING) {
			if (Constants.SpeciesId.BEST_BLOODLING_ID == -1) {
				return;
			}
			brain = Animal.pool[Constants.SpeciesId.BEST_BLOODLING_ID].neuralNetwork;
		}
		else {
			if (Constants.SpeciesId.BEST_GRASSLER_ID == -1) {
				return;
			}
			brain = Animal.pool[Constants.SpeciesId.BEST_GRASSLER_ID].neuralNetwork;
		}
		
		for (int i = 0; i < NUM_STUFF; ++i) {
			h[i].add(index, brain.z[0][i]);
		}
		
		index++;
		if (index > width) {
			for (int i = 0; i < NUM_STUFF; ++i) {
				h[i].remove(0); //TODO: These cause stack traces, better to reduce the viewport
			}
		}
	}
}
