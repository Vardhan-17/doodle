package com.doodle.services;

import com.doodle.enums.WordMatchingStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WordMatchingService {

    public WordMatchingStatus match(String s1, String s2) {
        if (Objects.isNull(s1) || Objects.isNull(s2)) {
            return WordMatchingStatus.MISMATCH;
        }

        if (s1.length() != s2.length()) {
            return WordMatchingStatus.MISMATCH;
        }

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int mismatch = 0;

        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) mismatch++;
        }

        if (mismatch == 1) return WordMatchingStatus.CLOSE_MATCH;

        if (mismatch > 1) return WordMatchingStatus.MISMATCH;

        return WordMatchingStatus.MATCH;
    }
}
