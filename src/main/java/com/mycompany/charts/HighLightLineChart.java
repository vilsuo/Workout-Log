
package com.mycompany.charts;

import javafx.beans.NamedArg;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;

public class HighLightLineChart<X, Y> extends LineChart<X, Y> {
    
    public HighLightLineChart(@NamedArg("xAxis") Axis<X> axis, @NamedArg("yAxis") Axis<Y> axis1) {
        super(axis, axis1);
    }
    
    @Override
    protected void seriesAdded(XYChart.Series<X, Y> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        
        Node seriesNode = series.getNode();
        if (seriesNode != null && seriesNode instanceof Path){
            Path seriesPath = (Path) seriesNode;
            
            seriesPath.setOnMouseEntered(
                event -> {
                    Color strokeColor = (Color) seriesPath.getStroke();
                    double strokeWidth = seriesPath.getStrokeWidth();
                    updatePath(seriesPath, strokeColor.darker(), 3 / 2.0 * strokeWidth, true);
                }
            );
            
            seriesPath.setOnMouseExited(
                event -> {
                    Color strokeColor = (Color) seriesPath.getStroke();
                    double strokeWidth = seriesPath.getStrokeWidth();
                    updatePath(seriesPath, strokeColor.brighter(), 2 / 3.0 * strokeWidth, false);
                }
            );
        }
    }
    
    private void updatePath(Path seriesPath, Paint strokeColor, double strokeWidth, boolean toFront){
        seriesPath.setStroke(strokeColor);
        seriesPath.setStrokeWidth(strokeWidth);
        if (toFront){
            seriesPath.toFront();
        }
    } 
}
