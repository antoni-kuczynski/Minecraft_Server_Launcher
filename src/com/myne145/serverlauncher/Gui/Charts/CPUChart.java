package com.myne145.serverlauncher.Gui.Charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class CPUChart extends JPanel {
    private final PieChart chart;
    private static final SystemInfo si = new SystemInfo();
    private static final HardwareAbstractionLayer hal = si.getHardware();
    private static final CentralProcessor cpu = hal.getProcessor();
    static long[] prevTicks = new long[CentralProcessor.TickType.values().length];

    public CPUChart() {
        setBorder(new FlatRoundBorder());
        setForeground(Color.GREEN);
        chart = createChart();

        // Create a ChartPanel to display the chart
        XChartPanel<PieChart> chartPanel = new XChartPanel<>(chart);
        chartPanel.setPreferredSize(new Dimension(130, 150));

        add(chartPanel);

        // Create a Timer to update the chart every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateChartData();
            }
        }, 0, 1000);
    }

    private PieChart createChart() {
        PieChart chart = new PieChartBuilder().width(130).height(150).build();

        chart.getStyler().setCircular(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);

        chart.addSeries("cpu_usage", 0);
        chart.addSeries("empty", 100);
        chart.setTitle("CPU Usage");


        chart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
        chart.getStyler().setSeriesColors(new Color[]{Color.RED, new Color(51, 51, 52)});
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(new Color(56, 56, 56));
        chart.getStyler().setPlotBackgroundColor(new Color(56, 56, 56));
        chart.getStyler().setPlotBorderColor(new Color(56, 56, 56));
        chart.getStyler().setLabelsVisible(false);

        chart.getStyler().setChartFontColor(new Color(204, 204, 204));

        chart.getStyler().setLabelType(PieStyler.LabelType.NameAndPercentage);

        chart.getStyler().setLabelsDistance(.82);
        chart.getStyler().setPlotContentSize(.9);
        chart.getStyler().setSumVisible(true);

        return chart;
    }

    private void updateChartData() {
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = cpu.getSystemCpuLoadTicks();
        if (cpuLoad != 0) {
            chart.updatePieSeries("cpu_usage", cpuLoad);
            chart.getStyler().setSumFormat((int) cpuLoad + "%%");
            chart.updatePieSeries("empty", 100 - cpuLoad);
            repaint();  // Repaint the panel to update the chart
        }
    }
}
