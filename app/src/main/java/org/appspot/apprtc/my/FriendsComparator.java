package org.appspot.apprtc.my;

import java.util.Comparator;

public class FriendsComparator implements Comparator<Friends> {
    @Override
    public int compare(Friends first, Friends second) {
        double firstValue = Double.parseDouble(first.getTitle());
        double secondValue = Double.parseDouble(second.getTitle());

        // Order by descending//오름
        if (firstValue < secondValue) {
            return -1;
        } else if (firstValue > secondValue) {
            return 1;
        } else {
            return 0;
        }
    }
}
