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
    private final static CentralProcessor cpu = hal.getProcessor();
    static long[] prevTicks = new long[CentralProcessor.TickType.values().length];
    public boolean isEnabled = true;

    public CPUChart() {
        setBorder(new FlatRoundBorder());
        chart = createChart();

        // Create a ChartPanel to display the chart
        XChartPanel<PieChart> chartPanel = new XChartPanel<>(chart);
        chartPanel.setPreferredSize(new Dimension(130, 150));

        add(chartPanel);
        updateChartData();
        // Create a Timer to update the chart every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isEnabled)
                    updateChartData();
            }
        }, 0, 5000);
    }

    private PieChart createChart() {
        PieChart chart = new PieChartBuilder().width(130).height(150).build();

        chart.getStyler().setCircular(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);

        chart.addSeries("cpu_usage", 100);
        chart.addSeries("empty", 0);
        chart.setTitle("CPU Usage");


        SystemMonitorChart.decorateChart(chart);

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
