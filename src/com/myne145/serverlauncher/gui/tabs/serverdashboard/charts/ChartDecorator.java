package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.style.PieStyler;

import java.awt.*;

public class ChartDecorator {
    public static void decorateChart(PieChart chart) {
        chart.getStyler().setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Donut);
        chart.getStyler().setSeriesColors(new Color[]{new Color(42, 82, 133), new Color(51, 51, 52)});
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(new Color(56, 56, 56));
        chart.getStyler().setPlotBackgroundColor(new Color(56, 56, 56));
        chart.getStyler().setPlotBorderColor(new Color(56, 56, 56));
        chart.getStyler().setLabelsVisible(false);

        chart.getStyler().setChartFontColor(new Color(204, 204, 204));

        chart.getStyler().setLabelType(PieStyler.LabelType.NameAndPercentage);
        chart.getStyler().setSumFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        chart.getStyler().setLabelsDistance(.82);
        chart.getStyler().setPlotContentSize(.9);
        chart.getStyler().setSumVisible(true);
    }
}
