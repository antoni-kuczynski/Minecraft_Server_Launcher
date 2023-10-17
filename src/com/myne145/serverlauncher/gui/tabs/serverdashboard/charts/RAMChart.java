package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class RAMChart extends JPanel {
    private final PieChart chart;
    private final SystemInfo systemInfo = new SystemInfo();
    private final GlobalMemory memory = systemInfo.getHardware().getMemory();
    private final double TOTAL_MEMORY_GB = (double) memory.getTotal() / 1073741824;
    public boolean isEnabled = true;

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        isEnabled = aFlag;
    }

    public RAMChart() {
        setBorder(new FlatRoundBorder());
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


        chart.addSeries("ram_usage", 0);
        chart.addSeries("empty", 100);
        chart.setTitle("RAM Usage");

        ChartDecorator.decorateChart(chart);
        return chart;
    }

    private void updateChartData() {
        double availableMemoryGB = (double) memory.getAvailable() / 1073741824;
        double usedMemoryGB = TOTAL_MEMORY_GB - availableMemoryGB;
        double usedMemoryPercentage = usedMemoryGB / TOTAL_MEMORY_GB * 100;

        chart.updatePieSeries("ram_usage", usedMemoryPercentage);
        chart.getStyler().setSumFormat(String.valueOf(usedMemoryGB).split("\\.")[0] + "." +  String.valueOf(usedMemoryGB).split("\\.")[1].charAt(0) +
                " / " + (int) Math.ceil(TOTAL_MEMORY_GB) + "GiB");
        chart.updatePieSeries("empty", 100 - usedMemoryPercentage);
        repaint();  // Repaint the panel to update the chart
    }
}
