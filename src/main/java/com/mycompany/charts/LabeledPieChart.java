
package com.mycompany.charts;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ListChangeListener;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;

public class LabeledPieChart extends PieChart {

    private final Map<Data, Text> labels = new HashMap<>();
    private double total = 0.0;

    public LabeledPieChart () {
        super();
        this.getData().addListener(
            (ListChangeListener.Change<? extends Data> change) -> {
                addLabels();
            }
        );
    }
    
    public final double getTotal() {
        return total;
    }

    @Override
    protected void layoutChartChildren(double top, double left,
            double contentWidth, double contentHeight) {
        
        super.layoutChartChildren(top, left, contentWidth, contentHeight);

        double centerX = contentWidth / 2 + left;
        double centerY = contentHeight / 2 + top;

        layoutLabels(centerX, centerY);
    }

    private void addLabels() {
        for (Text label : labels.values()) {
            this.getChartChildren().remove(label);
        }
        labels.clear();
        
        total = 0.0;
        for (Data d : this.getData()) {
            total += d.getPieValue();
        }
        
        for (Data vData : getData()) {
            final Text dataText;
            final double yValue = vData.getPieValue();
            double ratio = (total != 0) ? (yValue / total) : 1.0;
            dataText = new Text(new DecimalFormat("#0.0%").format(ratio));
            labels.put(vData, dataText);
            this.getChartChildren().add(dataText);
        }
    }

    private void layoutLabels(double centerX, double centerY) {
        double scale = (total != 0) ? 360 / total : 0;

        for (Map.Entry<Data, Text> entry : labels.entrySet()) {
            Data vData = entry.getKey();
            Text vText = entry.getValue();

            Region vNode = (Region) vData.getNode();
            Arc arc = (Arc) vNode.getShape();

            double start = arc.getStartAngle();

            double size = (isClockwise())
                ? (-scale * Math.abs(vData.getPieValue()))
                : (scale * Math.abs(vData.getPieValue()));
            
            final double angle = normalizeAngle(start + (size / 2));
            final double sproutX = calcX(angle, 0.7 * arc.getRadiusX(), centerX);
            final double sproutY = calcY(angle, 0.7 * arc.getRadiusY(), centerY);

            vText.relocate(
                sproutX - vText.getBoundsInLocal().getWidth() / 2,
                sproutY - vText.getBoundsInLocal().getHeight() / 2
            );
        }
    }

    private static double normalizeAngle(double angle) {
        double a = angle % 360;
        if (a <= -180) {
            a += 360;
        }
        if (a > 180) {
            a -= 360;
        }
        return a;
    }

    private static double calcX(double angle, double radius, double centerX) {
        return (double) (centerX + radius * Math.cos(Math.toRadians(-angle)));
    }

    private static double calcY(double angle, double radius, double centerY) {
        return (double) (centerY + radius * Math.sin(Math.toRadians(-angle)));
    }
}