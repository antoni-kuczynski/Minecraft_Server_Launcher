package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import org.knowm.xchart.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class RAMChart extends BasicChart {
    private final static OperatingSystemMXBean operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final static double TOTAL_MEMORY_GB = (double) operatingSystem.getTotalMemorySize() / 1073741824;

    public RAMChart() {
        super("RAM_CHART");
    }

    @Override
    public PieChart createChart() {
        PieChart chart = super.createChart();
        chart.addSeries("ram_usage", 0);
        chart.addSeries("empty", 100);
        chart.setTitle("RAM usage");
        return chart;
    }

    @Override
    protected void updateChartData() {
        double usedMemoryGB = TOTAL_MEMORY_GB - (double) operatingSystem.getFreeMemorySize() / 1073741824;
        double usedMemoryPercentage = usedMemoryGB / TOTAL_MEMORY_GB * 100;

        getChart().updatePieSeries("ram_usage", usedMemoryPercentage);
        getChart().getStyler().setSumFormat(Math.round(usedMemoryGB * 10.0) / 10.0 +
                " / " + (int) Math.ceil(TOTAL_MEMORY_GB) + "GiB");
        getChart().updatePieSeries("empty", 100 - usedMemoryPercentage);
        repaint();
    }
}
