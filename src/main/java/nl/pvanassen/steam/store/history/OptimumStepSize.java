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
        stepSize = stepSize - Math.max((int) (stepSize * 0.15d), 2);
        if (stepSize < 10) {
            stepSize = 10;
        }
        success = false;
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

    void success() {
        successCount.add(stepSize);
        if (stepSize < 1000) {
            stepSize = stepSize + Math.max((int) (stepSize * 0.10d), 2);
        }
        if (stepSize > 1000) {
            stepSize = 1000;
        }
        success = true;
    }

}