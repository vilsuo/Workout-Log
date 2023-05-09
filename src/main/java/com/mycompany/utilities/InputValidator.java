
package com.mycompany.utilities;

public final class InputValidator {
    
    public static boolean isNonNegativeInteger(String str) {
        String regex = "[0-9]+";
        return str.matches(regex);
    }
    
    public static boolean isNonNegativeDouble(String str) {
        String[] arr = str.split("\\.", -1);
        
        switch (arr.length) {
            case 1:
                return isNonNegativeInteger(arr[0]);
                
            case 2:
                // case str = ".xyz"
                if (arr[0].equals("")) {
                    return isNonNegativeInteger(arr[1]);
                }
                
                // case str = "xyz."
                if (arr[1].equals("")) {
                    return isNonNegativeInteger(arr[0]);
                }
                
                // case str = "xyz.vbn"
                if (!isNonNegativeInteger(arr[0])) {
                    return false;
                }
                
                if (!isNonNegativeInteger(arr[1])) {
                    return false;
                }
                
                return true;
                
            default:
                return false;
        }
    }
}
