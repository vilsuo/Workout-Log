
package com.mycompany.charts;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

public class HoverBarChart<X, Y> extends BarChart<X, Y> {
    
    private Node bar = null;
    private Text textNode = null;
    
    public HoverBarChart(Axis xAxis, Axis yAxis) {
        super(xAxis, yAxis);
    }
    
    @Override
    protected void seriesAdded(XYChart.Series<X, Y> series, int seriesIndex) {
        super.seriesAdded(series, seriesIndex);
        
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<X, Y> dataPoint = series.getData().get(i);
            
            /*
            series.nodeProperty().addListener(
                (obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        System.out.println("in series node listener");
                    }
                }
            );
            */
            
            dataPoint.getNode().setOnMouseEntered(event -> {
                String text = series.getName() + " (" + dataPoint.getYValue() + ")";
                textNode = new Text(text);
                
                bar = dataPoint.getNode();
                textNode.relocate(
                    bar.getBoundsInParent().getMinX(),
                    bar.getBoundsInParent().getMinY() - 30
                );
                
                getPlotChildren().add(textNode);
                
                dataPoint.getNode().setCursor(Cursor.HAND);
            });

            
            dataPoint.getNode().setOnMouseExited(event -> {
                getPlotChildren().remove(textNode);
                
                dataPoint.getNode().setCursor(Cursor.DEFAULT);
            });
        }
    }
    
    // Adjust text of bars, position them on top
    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        
        if (bar != null && textNode != null) {
            
            textNode.relocate(
                bar.getBoundsInParent().getMinX(),
                bar.getBoundsInParent().getMinY() - 30
            );
        }
    }
}
