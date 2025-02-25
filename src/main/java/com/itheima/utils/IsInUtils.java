package com.itheima.utils;

import java.util.List;

public class IsInUtils {
    public static boolean isIn(Integer nowItem,List<Integer> postList) {
        for (Integer item : postList) {
            if (item == nowItem) {
                return true;
            }
        }
        return false;
    }
}
