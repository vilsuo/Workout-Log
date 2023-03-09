package com.mycompany.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {
    
    private final String[] integers = {"0", "01", "1", "007", "71", "431", "1000", "4444"};
    
    private final String[] pureDecimals = {"1.0", "0.00", "00.07", "1.9", "57.5", "132.1", "1601.91022"};
    
    private final String[] decimalEdgeCases = {".9", ".0", ".729", "1.", "600.", "2023."};
    
    private final String[] invalidInputs = {"", " ", ".", "-0", "-116", "-1.5", " 1", " 17.5", "1,0", "h", "18i7"};
    
    @Test
    void nonNegativeIntegerTest() {
        for (int i = 0; i < integers.length; ++i) {
            assertTrue(InputValidator.isNonNegativeInteger(integers[i]));
        }
        
        for (int i = 0; i < invalidInputs.length; ++i) {
            assertFalse(InputValidator.isNonNegativeInteger(invalidInputs[i]));
        }
    }
    
    @Test
    void nonNegativeDoubleTest() {
        for (int i = 0; i < pureDecimals.length; ++i) {
            assertTrue(InputValidator.isNonNegativeDouble(pureDecimals[i]));
        }
        
        for (int i = 0; i < decimalEdgeCases.length; ++i) {
            assertTrue(InputValidator.isNonNegativeDouble(decimalEdgeCases[i]));
        }
        
        for (int i = 0; i < invalidInputs.length; ++i) {
            assertFalse(InputValidator.isNonNegativeDouble(invalidInputs[i]));
        }
    }
    
    @Test
    void nonNegativeIntegersAreNonNegativeDecimals() {
        for (int i = 0; i < integers.length; ++i) {
            assertSame(
                InputValidator.isNonNegativeInteger(integers[i]),
                InputValidator.isNonNegativeDouble(integers[i])
            );
        }
    }
    
    @Test
    void pureNonNegativeDecimalsAreNotNonNegativeIntegerValues() {
        for (int i = 0; i < pureDecimals.length; ++i) {
            assertNotSame(
                InputValidator.isNonNegativeDouble(pureDecimals[i]),
                InputValidator.isNonNegativeInteger(pureDecimals[i])
            );
        }
    }
    
}
