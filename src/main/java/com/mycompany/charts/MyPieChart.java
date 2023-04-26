
package com.mycompany.charts;

import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

public class MyPieChart {
    
    private List<Arc> arcList;

    public MyPieChart(double centerX, double centerY, double radius,
            Map<String, Integer> categoryMap, Map<String, Color> colorMap) {
        int total = categoryMap.values().stream()
            .reduce(0, Integer::sum);
        
        double startAngle = 0.0; // todo
        for (String category : categoryMap.keySet()) {
            double length = calculateLength(categoryMap.getOrDefault(category, 0), total, radius);
            Arc arc = new Arc(
                centerX, centerY, radius, radius, startAngle, length
            );
            arc.setType(ArcType.ROUND);
            arc.setFill(colorMap.get(category));
            
            arcList.add(arc);
            
            startAngle += (categoryMap.getOrDefault(category, 0) / total) * 360;
        }
    }

    public List<Arc> getArcList() {
        return arcList;
    }
    
    private double calculateLength(int value, int total, double radius) {
        return (total != 0) ? ((value / total) * 2 * Math.PI * radius) : 0;
    }
}
