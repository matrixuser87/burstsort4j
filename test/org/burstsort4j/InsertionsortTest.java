/*
 * Copyright 2008-2011 Nathan Fiedler. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package org.burstsort4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the insertion sort implementations.
 *
 * @author Nathan Fiedler
 */
public class InsertionsortTest {

    /** Maximum number of lines to test for any given file, which avoids
     * spending far too much time testing insertion sort on data sets
     * that it will never actually be called upon to sort in practice. */
    private static final int MAX_LINES = 512;

    @Test
    public void testArguments() {
        Insertionsort.sort((String[]) null);
        Insertionsort.sort(new String[0]);
        String[] arr = new String[]{"a"};
        Insertionsort.sort(arr);
        arr = new String[]{"b", "a"};
        Insertionsort.sort(arr);
        assertTrue(Tests.isSorted(arr));
        arr = new String[]{"c", "b", "a"};
        Insertionsort.sort(arr);
        assertTrue(Tests.isSorted(arr));
        // test with all empty input
        arr = new String[]{"", "", "", "", "", "", "", "", "", ""};
        Insertionsort.sort(arr);
        for (String s : arr) {
            assertEquals("", s);
        }
        // test with peculiar input
        arr = new String[]{"z", "m", "", "a", "d", "tt", "tt", "tt", "foo", "bar"};
        Insertionsort.sort(arr);
        assertTrue("peculiar input not sorted", Tests.isSorted(arr));
    }

    @Test
    public void testComparable() {
        try {
            List<String> data = Tests.loadData("dictwords", false, MAX_LINES);
            Collections.shuffle(data);
            String[] arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
            // Test with sorted list
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
            // Test with reverse sorted list
            Collections.reverse(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
            // Test with non-unique word list.
            data = Tests.loadData("hamletwords", false, MAX_LINES);
            Collections.shuffle(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
            // Test with sorted list
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
            // Test with reverse sorted list
            Collections.reverse(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
        // Test with repeated strings.
        String[] arr = new String[MAX_LINES];
        Arrays.fill(arr, "abcdefghijklmnopqrstuvwxyz");
        Insertionsort.sort(arr);
        assertTrue(Tests.isRepeated(arr, "abcdefghijklmnopqrstuvwxyz"));
        // Test with randomly generated strings.
        List<String> data = Tests.generateData(MAX_LINES, 64);
        arr = data.toArray(new String[data.size()]);
        Insertionsort.sort(arr);
        assertTrue(Tests.isSorted(arr));
    }

    @Test
    public void testStrings() {
        try {
            List<String> data = Tests.loadData("dictwords", false, MAX_LINES);
            Collections.shuffle(data);
            String[] arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
            // Test with sorted list
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
            // Test with reverse sorted list
            Collections.reverse(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
            // Test with non-unique word list.
            data = Tests.loadData("hamletwords", false, MAX_LINES);
            Collections.shuffle(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
            // Test with sorted list
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
            // Test with reverse sorted list
            Collections.reverse(data);
            arr = data.toArray(new String[data.size()]);
            Insertionsort.sort(arr, 0, arr.length, 0);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
        // Test with repeated strings.
        String[] arr = new String[MAX_LINES];
        Arrays.fill(arr, "abcdefghijklmnopqrstuvwxyz");
        Insertionsort.sort(arr, 0, arr.length, 0);
        assertTrue(Tests.isRepeated(arr, "abcdefghijklmnopqrstuvwxyz"));
        // Test with randomly generated strings.
        List<String> data = Tests.generateData(MAX_LINES, 64);
        arr = data.toArray(new String[data.size()]);
        Insertionsort.sort(arr, 0, arr.length, 0);
        assertTrue(Tests.isSorted(arr));
    }
}
