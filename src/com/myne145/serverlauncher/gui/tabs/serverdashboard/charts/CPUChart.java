package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.Timer;
import java.util.TimerTask;

public class CPUChart extends JPanel {
    private final PieChart chart;
    public boolean isEnabled = true;
    private final static OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final XChartPanel<PieChart> chartPanel;

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        isEnabled = aFlag;
        chartPanel.setVisible(aFlag);
    }

    public CPUChart() {
        setBorder(new FlatRoundBorder());
        chart = createChart();

        // Create a ChartPanel to display the chart

        chartPanel = new XChartPanel<>(chart);
        chartPanel.setPreferredSize(new Dimension(130, 150));
        for(MouseListener mouseListener : chartPanel.getMouseListeners())
            chartPanel.removeMouseListener(mouseListener);

        chart.getStyler().setSumFormat("100%%");
        add(chartPanel);
//        updateChartData();
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


        ChartDecorator.decorateChart(chart);

        return chart;
    }


    public void updateChartData() {
        int cpuLoad = (int) Math.ceil(systemMXBean.getCpuLoad() * 100);
//        if (cpuLoad != 0) {
            chart.updatePieSeries("cpu_usage", cpuLoad);
            chart.getStyler().setSumFormat(((int) cpuLoad + "%%"));
            chart.updatePieSeries("empty", 100 - cpuLoad);
            repaint();  // Repaint the panel to update the chart
//        }
    }
}
