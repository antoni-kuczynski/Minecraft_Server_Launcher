package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.utils.Colors;
import com.sun.management.OperatingSystemMXBean;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

public class BasicChart extends JPanel {
    private final PieChart chart;
    private final static OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private boolean isEnabled = true;

    protected static int cpuLoad = 0;
    protected final static double TOTAL_MEMORY_GB = (double) systemMXBean.getTotalMemorySize() / 1073741824;
    protected static double usedMemoryGB = 0;
    protected static double usedMemoryPercentage = 0;


    public static void startResourceMonitoringTimer() {
        Timer timer = new Timer("TEST");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cpuLoad = (int) Math.ceil(systemMXBean.getCpuLoad() * 100);
                usedMemoryGB = TOTAL_MEMORY_GB - (double) systemMXBean.getFreeMemorySize() / 1073741824;
                usedMemoryPercentage = usedMemoryGB / TOTAL_MEMORY_GB * 100;
            }
        }, 0, 5000);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        isEnabled = aFlag;
    }

    protected BasicChart(String threadName) {
        setBorder(new FlatRoundBorder());
        chart = createChart();

        XChartPanel<PieChart> chartPanel = new XChartPanel<>(chart);
        chartPanel.setPreferredSize(new Dimension(130, 150));
        for(MouseListener mouseListener : chartPanel.getMouseListeners())
            chartPanel.removeMouseListener(mouseListener);

        add(chartPanel);

        Timer timer = new Timer(threadName);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isEnabled)
                    updateChartData();
            }
        }, 0, 5000);

        chart.getStyler().setSumFormat("100%%");
    }

    protected void updateChartData() {

    }

    protected PieChart createChart() {
        Dimension dimension = new Dimension(130, 150);
        if(SystemInfo.isLinux) {
            dimension.width *= Window.getDisplayScale();
            dimension.height *= Window.getDisplayScale();
        }
        PieChart chart = new PieChartBuilder().width(dimension.width).height(dimension.height).build();

        chart.getStyler().setCircular(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);

        decorateChart(chart);

        return chart;
    }

    private static void decorateChart(PieChart chart) {
        chart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
        chart.getStyler().setSeriesColors(new Color[]{Colors.ACCENT_COLOR, Colors.TABBEDPANE_BACKGROUND_COLOR});
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(Colors.BACKGROUND_PRIMARY_COLOR);
        chart.getStyler().setPlotBackgroundColor(Colors.BACKGROUND_PRIMARY_COLOR);
        chart.getStyler().setPlotBorderColor(Colors.BACKGROUND_PRIMARY_COLOR);
        chart.getStyler().setLabelsVisible(false);

        chart.getStyler().setChartFontColor(Colors.TEXT_COLOR);

        chart.getStyler().setLabelType(PieStyler.LabelType.NameAndPercentage);


        chart.getStyler().setLabelsDistance(.82);
        chart.getStyler().setPlotContentSize(.9);
        chart.getStyler().setSumVisible(true);
        if(SystemInfo.isLinux) {
            chart.getStyler().setSumFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (12 * Window.getDisplayScale())));
            return;
        }
        chart.getStyler().setSumFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    }

    protected PieChart getChart() {
        return chart;
    }
}
