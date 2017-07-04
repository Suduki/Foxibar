//package org.jfree.chart.demo;
//
///*
// * SliderDemo1.java
// * ---------------
// * A demo that uses a SlidingXYDataset that provides a window of the
// * underlying dataset
// * 
// * This example uses XYSeries
// *  
// */
//
//import java.awt.BorderLayout;
//import javax.swing.JPanel;
//import javax.swing.JSlider;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//
//import org.jfree.chart.axis.ValueAxis;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.chart.util.ApplicationFrame;
//import org.jfree.chart.util.RefineryUtilities;
//
//public class SliderDemo1 extends ApplicationFrame {
//
//  /** The Constant COUNT. */
//  static final int COUNT = 20;
//
//  /** The Constant WINDOW. */
//  public static final int WINDOW = 5;
//
//  /** The Constant FIRST. */
//  public static final int FIRST = 0;
//
//  /**
//   * The Class DemoPanel.
//   */
//  private static class DemoPanel extends JPanel
//  implements ChangeListener {
//
//    //private TimeSeries series;
//
//    /** The chart panel. */
//    private ChartPanel chartPanel;
//
//    /** The chart. */
//    private JFreeChart chart;
//
//    /** The slider. */
//    private JSlider slider;
//
//    /** The dataset. */
//    private SlidingXYDataset dataset;
//
//    /**
//     * Creates a new demo panel.
//     */
//    public DemoPanel() {
//      super(new BorderLayout());
//      this.chart = createChart();
//      this.chartPanel = new ChartPanel(this.chart);
//      this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
//      add(this.chartPanel);
//      JPanel dashboard = new JPanel(new BorderLayout());
//      this.slider = new JSlider(0, COUNT - WINDOW - 1, 0);
//      slider.setPaintLabels(true);
//      slider.setPaintTicks(true);
//      slider.setMajorTickSpacing(WINDOW);
//      this.slider.addChangeListener(this);
//      dashboard.add(this.slider);
//      add(dashboard, BorderLayout.SOUTH);
//    }
//
//    /**
//     * Creates the demo chart.
//     * 
//     * @return The chart.
//     */
//    private JFreeChart createChart() {
//      XYDataset dataset2 = createDataset2();
//      JFreeChart chart2 = ChartFactory.createXYLineChart("Sliding demo1",
//          "xAxisLabel", "yAxisLabel",
//          dataset2, PlotOrientation.VERTICAL, true, true, false);
//      XYPlot plot = chart2.getXYPlot();
//      ValueAxis xaxis =  plot.getDomainAxis();
//      xaxis.setAutoRange(true);
//      return chart2;
//    }
//
//
//
//    /**
//     * Creates a sample dataset.
//     * 
//     * @return a sample dataset.
//     */
//    private XYDataset createDataset2() {
//
//      final XYSeries series1 = new XYSeries("First");
//      series1.add(1.0, 5.0);
//      series1.add(2.0, 7.0);
//      series1.add(3.0, 5.0);
//      series1.add(4.0, 7.0);
//      series1.add(5.0, 5.0);
//      series1.add(6.0, 7.0);
//      series1.add(7.0, 5.0);
//      series1.add(8.0, 7.0);
//      series1.add(9.0, 5.0);
//      series1.add(10.0, 7.0);
//      series1.add(11.0, 5.0);
//      series1.add(12.0, 7.0);
//      series1.add(13.0, 5.0);
//      series1.add(14.0, 7.0);
//      series1.add(15.0, 5.0);
//      series1.add(16.0, 7.0);
//      series1.add(17.0, 5.0);
//      series1.add(18.0, 7.0);
//      series1.add(19.0, 5.0);
//
//      final XYSeries series2 = new XYSeries("Second");
//      series2.add(1.0, 6.0);
//      series2.add(2.0, 7.0);
//      series2.add(3.0, 5.5);
//
//      final XYSeries series3 = new XYSeries("Third");
//
//      series3.add(3.0, 4.0);
//      series3.add(4.0, 3.0);
//      series3.add(5.0, 4.0);
//      series3.add(6.0, 3.0);
//      series3.add(7.0, 4.0);
//      series3.add(8.0, 3.0);
//      series3.add(9.0, 4.0);
//      series3.add(10.0, 3.0);
//      series3.add(11.0, 4.0);
//      series3.add(12.0, 3.0);
//
//
//      final XYSeriesCollection dc = new XYSeriesCollection();
//      dc.addSeries(series1);
//      //dc.addSeries(series2);
//      dc.addSeries(series3);
//      this.dataset = new SlidingXYDataset(dc, FIRST, WINDOW);
//      return dataset; 
//    }
//
//
//    /**
//     * Handles a state change event.
//     * 
//     * @param event  the event.
//     */
//    public void stateChanged(ChangeEvent event) {
//      int value = this.slider.getValue();
//      this.dataset.setFirstItemIndex(value);
//    }
//  }
//
//  /**
//   * A demonstration application showing how to control a crosshair using an
//   * external UI component.
//   * 
//   * @param title  the frame title.
//   */
//  public SliderDemo1(String title) {
//    super(title);
//    setContentPane(new DemoPanel());
//  }
//
//  /**
//   * Creates a panel for the demo (used by SuperDemo.java).
//   * 
//   * @return A panel.
//   */
//  public static JPanel createDemoPanel() {
//    return new DemoPanel();
//  }
//
//  /**
//   * Starting point for the demonstration application.
//   * 
//   * @param args  ignored.
//   */
//  public static void main(String[] args) {
//    SliderDemo1 demo = new SliderDemo1("SliderDemo Demo");
//    demo.pack();
//    RefineryUtilities.centerFrameOnScreen(demo);
//    demo.setVisible(true);
//
//  }
//
//}