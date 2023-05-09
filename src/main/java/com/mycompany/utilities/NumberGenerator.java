
package com.mycompany.utilities;

public final class NumberGenerator {
    
    private static int negative = -1;
    
    public static int generateNextNegativeNumber() {
        return --negative;
    }
}
