package com.myne145.serverlauncher.Gui.Charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import org.knowm.xchart.*;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class RAMChart extends JPanel {
    private final PieChart chart;
    SystemInfo systemInfo = new SystemInfo();
    GlobalMemory memory = systemInfo.getHardware().getMemory();

    long totalMemoryMegabytes = memory.getTotal() / 1048576; //convert to mb
    long availableMemoryMegabytes = memory.getAvailable() / 1048576;
    long usedMemoryMegabytes = totalMemoryMegabytes - availableMemoryMegabytes;

    public RAMChart() {
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

        chart.addSeries("ram_usage", 0);
        chart.addSeries("empty", 100);
        chart.setTitle("RAM Usage");


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
        double usedMemoryPercentage = (double) usedMemoryMegabytes / totalMemoryMegabytes * 100;

        chart.updatePieSeries("ram_usage", usedMemoryPercentage);
        chart.getStyler().setSumFormat(usedMemoryMegabytes + " / " + totalMemoryMegabytes);
//        chart.updatePieSeries("empty", totalMemoryMegabytes - usedMemoryMegabytes);
        repaint();  // Repaint the panel to update the chart

        System.out.printf("Total Memory: %d bytes%n", totalMemoryMegabytes);
        System.out.printf("Used Memory: %d bytes%n", usedMemoryMegabytes);
        System.out.printf("Used Memory Percentage: %.2f%%%n", usedMemoryPercentage);
    }
}
