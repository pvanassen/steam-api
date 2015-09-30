package nl.pvanassen.steam.store.history;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OptimumStepSizeTest {

    @Test
    public void test() {
        OptimumStepSize optimumStepSize = new OptimumStepSize();
        optimumStepSize.error();
        assertEquals(850, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(723, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(615, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(676, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(743, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(817, optimumStepSize.getStepSize());
        optimumStepSize.error();
        assertEquals(695, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(764, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(840, optimumStepSize.getStepSize());
        optimumStepSize.success();
        assertEquals(924, optimumStepSize.getStepSize());
    }

}
