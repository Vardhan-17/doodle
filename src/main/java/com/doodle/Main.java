package com.doodle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        new Main().sumOfGoodSubsequences(new int[]{1, 2, 1});
    }

    private static final long MOD = (long) 1e9 + 7;

    public int sumOfGoodSubsequences(int[] nums) {
        return (int) solve(0, nums, new HashMap<>());
    }

    private static class Pair {
        long count;
        long sum;

        public Pair(long count, long sum) {
            this.count = count;
            this.sum = sum;
        }
    }

    private long solve(int i, int[] nums, Map<Integer, Pair> map) {
        if (i == nums.length) return 0L;

        long ans = solve(i + 1, nums, map);

        var num = nums[i];
        long sum = num;
        ans = add(ans, sum);
        var pair = map.getOrDefault(num, new Pair(0L, 0L));
        pair.sum = add(pair.sum, num);
        pair.count = add(pair.count, 1);
        map.put(num, pair);

        if (map.containsKey(num - 1)) {
            var pair1 = map.get(num - 1);
            sum = add(multi(pair1.count, num), pair1.sum);

            var pair2 = map.get(num);
            pair2.count = add(pair2.count, pair1.count);
            pair2.sum = add(pair2.sum, sum);

            ans = add(ans, sum);
        }

        if (map.containsKey(num + 1)) {
            var pair1 = map.get(num + 1);
            sum = add(multi(pair1.count, num), pair1.sum);

            var pair2 = map.get(num);
            pair2.count = add(pair2.count, pair1.count);
            pair2.sum = add(pair2.sum, sum);

            ans = add(ans, sum);
        }

        return ans;
    }

    private long multi(long a, long b) {
        return (a * b) % MOD;
    }

    private long add(long a, long b) {
        return (a + b) % MOD;
    }
}
