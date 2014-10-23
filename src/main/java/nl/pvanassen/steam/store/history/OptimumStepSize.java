package nl.pvanassen.steam.store.history;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

class OptimumStepSize {
    private int stepSize = 1000;
    private Multiset<Integer> successCount = HashMultiset.create();
    private Multiset<Integer> errorCount = HashMultiset.create();
    private boolean success;
    
    void error() {
        errorCount.add(stepSize);
        stepSize = stepSize / 2;
        success = false;
    }
    
    void success() {
        successCount.add(stepSize);
        stepSize = stepSize + 10;
        success = true;
    }
    
    int getStepSize() {
        if (success) {
            return stepSize;
        }
        Multiset<Integer> highestSuccessFirst = Multisets.copyHighestCountFirst(successCount);
        Multiset<Integer> highestErrorFirst = Multisets.copyHighestCountFirst(errorCount);
        int highestError = 0;
        if (highestErrorFirst.iterator().hasNext()) {
            highestError = highestErrorFirst.iterator().next();
        }
        for (Integer stepSize : highestSuccessFirst) {
            if (stepSize.intValue() == highestError) {
                continue;
            }
            stepSize = stepSize.intValue();
            break;
        }
        return stepSize;
    }
    
}