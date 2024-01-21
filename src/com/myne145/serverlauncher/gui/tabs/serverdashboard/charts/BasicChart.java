package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.myne145.serverlauncher.utils.Colors;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

class BasicChart extends JPanel {
    private final PieChart chart;
    private boolean isEnabled = true;

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
        PieChart chart = new PieChartBuilder().width(130).height(150).build();

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
        chart.getStyler().setSumFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        chart.getStyler().setLabelsDistance(.82);
        chart.getStyler().setPlotContentSize(.9);
        chart.getStyler().setSumVisible(true);
    }

    protected PieChart getChart() {
        return chart;
    }
}
