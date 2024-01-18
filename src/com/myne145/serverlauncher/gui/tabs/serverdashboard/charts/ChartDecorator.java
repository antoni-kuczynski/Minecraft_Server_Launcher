package com.myne145.serverlauncher.gui.tabs.serverdashboard.charts;

import com.myne145.serverlauncher.utils.Colors;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.style.PieStyler;

import java.awt.*;

public class ChartDecorator {
    public static void decorateChart(PieChart chart) {
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
}
