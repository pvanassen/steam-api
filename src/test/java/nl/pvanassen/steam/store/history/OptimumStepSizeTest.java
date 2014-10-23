package nl.pvanassen.steam.store.history;

import static org.junit.Assert.*;

import org.junit.Test;

public class OptimumStepSizeTest {

    @Test
    public void test() {
        OptimumStepSize optimumStepSize = new OptimumStepSize();
        optimumStepSize.error();
        assertEquals(500, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(250, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(125, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(135, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(145, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(155, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(77, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(87, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(97, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(107, optimumStepSize.getStepSize());
    }

}
