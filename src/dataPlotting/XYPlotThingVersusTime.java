

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import agents.Animal;
import constants.Constants;
import main.Main;
import world.World;

@SuppressWarnings("serial")
public class XYPlotThingVersusTime extends ApplicationFrame {

	public static final XYPlotThingVersusTime myInstance = new XYPlotThingVersusTime("Plot versus time");

	public static XYSeries grassSeries = new XYSeries("grass");
	public static XYSeries bloodlingSeries = new XYSeries("bloodling");
	public static XYSeries grasslerSeries = new XYSeries("grassler");

	public XYPlotThingVersusTime(String title) {
		super(title);
	}

	public static void plotStuff() {
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(grasslerSeries);
		data.addSeries(bloodlingSeries);
		data.addSeries(grassSeries);
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"POPULATION",
				"TIME", 
				"SIZE", 
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
	
	private final int width = 100;
	private int index = 0;
	public void step() {
		grasslerSeries.add(index*Main.plottingNumber, Animal.numGrasslers);
		bloodlingSeries.add(index*Main.plottingNumber, Animal.numBloodlings);
		grassSeries.add(index*Main.plottingNumber, World.grass.getTotalHeight()/Constants.TILES_PER_ANIMAL);
		index++;
		if (index > width) {
			bloodlingSeries.remove(0); //TODO: These cause stack traces, better to reduce the viewport
			grasslerSeries.remove(0);
			grassSeries.remove(0);
		}
	}
}
