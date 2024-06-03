package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import org.knowm.xchart.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class CPUChart extends BasicChart {


    public CPUChart() {
        super("CPU_CHART");
    }

    @Override
    public PieChart createChart() {
        PieChart chart = super.createChart();
        chart.addSeries("cpu_usage", 100);
        chart.addSeries("empty", 0);
        chart.setTitle("CPU usage");
        return chart;
    }

    @Override
    protected void updateChartData() {
//        int cpuLoad = (int) Math.ceil(systemMXBean.getCpuLoad() * 100);
        getChart().updatePieSeries("cpu_usage", cpuLoad);
        getChart().getStyler().setSumFormat(cpuLoad + "%%");
        getChart().updatePieSeries("empty", 100 - cpuLoad);
        repaint();
    }
}
