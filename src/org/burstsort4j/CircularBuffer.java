/*
 * Copyright (C) 2009  Nathan Fiedler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id$
 */

package org.burstsort4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A simple circular buffer of fixed length which has an empty and full
 * state. When full, the buffer will not accept any new entries.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a buffer concurrently, and at least one of
 * the threads modifies the buffer structurally, it must be synchronized
 * externally.</p>
 *
 * @param  <E>  type of elements in the buffer.
 * @author Nathan Fiedler
 */
public class CircularBuffer<E> implements Collection, Queue {
    /** The circular buffer. */
    private final Object[] buffer;
    /** Lower limit within the buffer, equal to the first usable position. */
    private final int lower;
    /** Upper limit within the buffer, just past the last usable position. */
    private final int upper;
    /** Offset of the first entry in the buffer. */
    private int start;
    /** Offset of the last entry in the buffer. */
    private int end;
    /** Number of elements in the buffer. Used to distinquish between
     * the full and empty case. */
    private int count;

    /**
     * Constructs a new instance of CircularBuffer with the given capacity.
     *
     * @param  capacity  maximum number of valid elements to contain.
     */
    public CircularBuffer(int capacity) {
        buffer = new Object[capacity];
        lower = 0;
        upper = capacity;
    }

    /**
     * Constructs a new instance of CircularBuffer with the given data.
     * The given array data will be copied to a new array to prevent
     * accidental modification of the buffer.
     *
     * @param  initial  data to be stored in buffer initially.
     */
    public CircularBuffer(E[] initial) {
        this(initial, true);
    }

    /**
     * Constructs a new instance of CircularBuffer with the given data.
     * All entries in the array are assumed to be valid data such that
     * the buffer count will be equal to the length of the given array.
     *
     * @param  initial  data to be stored in buffer initially.
     * @param  copy     if true, data will be copied to a new array,
     *                  otherwise the given array will be used as-is.
     */
    public CircularBuffer(E[] initial, boolean copy) {
        this(initial, 0, initial.length, copy);
    }

    /**
     * Constructs a new instance of CircularBuffer with the given data.
     * The entries in the array from the offset up to <tt>offset + count</tt>
     * are assumed to be valid data such that the buffer count will be equal
     * to the count argument.
     *
     * @param  initial  data to be stored in buffer initially.
     * @param  offset   first offset within array to be used.
     * @param  count    number of elements from offset to be used.
     * @param  copy     if true, data will be copied to a new array,
     *                  otherwise the given array will be used as-is.
     */
    public CircularBuffer(E[] initial, int offset, int count, boolean copy) {
        if (copy) {
            buffer = new Object[count];
            System.arraycopy(initial, offset, buffer, 0, count);
        } else {
            buffer = initial;
        }
        this.count = count;
        lower = offset;
        upper = offset + count;
        start = lower;
        end = lower;
    }

    @Override
    public boolean add(Object o) {
        if (count == upper - lower) {
            throw new IllegalStateException("buffer is full");
        }
        count++;
        buffer[end] = o;
        end++;
        if (end == upper) {
            end = lower;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection c) {
        Iterator i = c.iterator();
        while (i.hasNext()) {
            add(i.next());
        }
        return true;
    }

    /**
     * Returns the total number of elements this buffer can hold (the same
     * value passed to the constructor).
     *
     * @return  buffer capacity.
     */
    public int capacity() {
        return upper - lower;
    }

    @Override
    public void clear() {
        start = lower;
        end = lower;
        count = 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Drains the contents of the circular buffer into the given sink
     * in an efficient manner (using System.arraycopy). Copies the
     * remaining elements of this circular buffer to the destination,
     * leaving this buffer empty.
     *
     * @param  sink  destination for buffer contents.
     */
    public void drain(CircularBuffer sink) {
        if (count == 0) {
            throw new IllegalStateException("buffer is empty");
        }
        move(sink, count);
    }

    /**
     * Drains the contents of the circular buffer into the given output
     * array in an efficient manner (using System.arraycopy). Copies the
     * remaining elements of this circular buffer to the destination,
     * leaving this buffer empty.
     *
     * @param  sink    destination for buffer contents.
     * @param  offset  position in output to which elements are copied.
     */
    public void drain(E[] sink, int offset) {
        if (count == 0) {
            throw new IllegalStateException("buffer is empty");
        }
        if (sink.length - offset < count) {
            throw new IllegalArgumentException("destination too small");
        }
        if (end <= start) {
            // Buffer wraps around, must make two calls to arraycopy().
            int leading = upper - start;
            System.arraycopy(buffer, start, sink, offset, leading);
            System.arraycopy(buffer, lower, sink, offset + leading, end - lower);
        } else {
            // Buffer is in one contiguous region.
            System.arraycopy(buffer, start, sink, offset, end - start);
        }
        start = lower;
        end = lower;
        count = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object element() {
        if (count == 0) {
            throw new NoSuchElementException("buffer is empty");
        }
        return (E) buffer[start];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Indicates if the buffer is full.
     *
     * @return  true if full, false otherwise.
     */
    public boolean isFull() {
        return count == (upper - lower);
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean offer(Object o) {
        if (count == upper - lower) {
            return false;
        }
        count++;
        buffer[end] = o;
        end++;
        if (end == upper) {
            end = lower;
        }
        return true;
    }

    /**
     * Moves the given number of elements from this circular buffer into
     * the sink buffer in an efficient manner (using System.arraycopy).
     * This is equivalent to repeatedly removing elements from this buffer
     * and adding them to the sink.
     *
     * @param  sink  destination for buffer contents.
     * @param  n     number of elements to be moved.
     */
    @SuppressWarnings("unchecked")
    public void move(CircularBuffer sink, int n) {
        if (count < n) {
            throw new IllegalStateException("source has too few items");
        }
        if (sink.upper - sink.lower - sink.count < n) {
            throw new IllegalArgumentException("sink has insufficient space");
        }

        int tocopy = n;
        while (tocopy > 0) {
            int desired = Math.min(tocopy, Math.max(end - start, upper - start));
            int willfit = sink.start <= sink.end ? sink.upper - sink.end : sink.start - sink.end;
            int copied = Math.min(desired, willfit);
            System.arraycopy(buffer, start, sink.buffer, sink.end, copied);
            sink.end += copied;
            if (sink.end >= sink.upper) {
                sink.end -= (sink.upper - sink.lower);
            }
            start += copied;
            if (start >= upper) {
                start -= (upper - lower);
            }
            tocopy -= copied;
        }
        sink.count += n;
        count -= n;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        return count == 0 ? null : (E) buffer[start];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (count == 0) {
            return null;
        }
        count--;
        Object o = buffer[start];
        start++;
        if (start == upper) {
            start = lower;
        }
        return (E) o;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove() {
        if (count == 0) {
            throw new NoSuchElementException("buffer is empty");
        }
        count--;
        Object o = buffer[start];
        start++;
        if (start == upper) {
            start = lower;
        }
        return (E) o;
    }

    /**
     * Returns the number of empty spaces within this buffer (i.e. how many
     * times {@link #add(Object)} can be called before the buffer is full).
     *
     * @return  number of empty spaces in buffer.
     */
    public int remaining() {
        return upper - lower - count;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported on circular buffer.");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Not supported on circular buffer.");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Not supported on circular buffer.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Use drain() instead.");
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("Use drain() instead.");
    }

    @Override
    public int size() {
        return count;
    }
}
