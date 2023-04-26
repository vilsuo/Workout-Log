
package com.mycompany.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class HoverBarChart<X, Y> extends BarChart<X, Y> {
    
    private final Text textNode;
    
    public HoverBarChart(@NamedArg("xAxis") Axis xAxis, @NamedArg("yAxis") Axis yAxis) {
        super(xAxis, yAxis);
        textNode = new Text();
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
            
            dataPoint.getNode().setOnMouseEntered(event -> select(dataPoint));
            dataPoint.getNode().setOnMouseExited(event -> deselect(dataPoint));
        }
    }
    
    private void select(XYChart.Data<X, Y> dataPoint) {
        textNode.setText(dataPoint.getYValue().toString());
        
        Paint p = ((StackPane) dataPoint.getNode()).getBackground()
            .getFills().get(0).getFill();
        
        textNode.setFill(p);
        
        double textNodeHalfWay = textNode.maxWidth(USE_PREF_SIZE) / 2;
        Bounds barBoundsInParent = dataPoint.getNode().getBoundsInParent();
        
        textNode.relocate(
            barBoundsInParent.getCenterX() - textNodeHalfWay,
            barBoundsInParent.getMinY() - 20
        );
        
        getPlotChildren().add(textNode);
        dataPoint.getNode().setCursor(Cursor.HAND);
    }
    
    private void deselect(XYChart.Data<X, Y> dataPoint) {
        getPlotChildren().remove(textNode);
        dataPoint.getNode().setCursor(Cursor.DEFAULT);
    }
}
