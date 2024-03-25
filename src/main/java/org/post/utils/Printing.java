package org.post.utils;

import java.util.List;

public class Printing {
    public static <T> void print(List<T> items) {
        for (T item : items) {
            System.out.println(item);
        }
    }


}



