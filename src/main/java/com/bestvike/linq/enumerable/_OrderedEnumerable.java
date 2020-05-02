package com.bestvike.linq.enumerable;

import com.bestvike.collections.generic.Comparer;
import com.bestvike.collections.generic.ICollection;
import com.bestvike.function.Func1;
import com.bestvike.function.Predicate1;
import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.IEnumerator;
import com.bestvike.linq.IOrderedEnumerable;
import com.bestvike.linq.exception.ExceptionArgument;
import com.bestvike.linq.exception.ThrowHelper;
import com.bestvike.linq.util.ArrayUtils;
import com.bestvike.linq.util.ListUtils;
import com.bestvike.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 许崇雷 on 2018-05-07.
 */
abstract class AbstractOrderedEnumerable<TElement> implements IOrderedEnumerable<TElement>, IPartition<TElement> {
    IEnumerable<TElement> source;

    private Integer[] sortedMap(Buffer<TElement> buffer) {
        return this.getEnumerableSorter().sort(buffer.items, buffer.count);
    }

    private Integer[] sortedMap(Buffer<TElement> buffer, int minIdx, int maxIdx) {
        return this.getEnumerableSorter().sort(buffer.items, buffer.count, minIdx, maxIdx);
    }

    @Override
    public IEnumerator<TElement> enumerator() {
        return new OrderedEnumerableEnumerator();
    }

    public IEnumerator<TElement> enumerator(int minIdx, int maxIdx) {
        return new OrderedEnumerableRangeEnumerator(minIdx, maxIdx);
    }

    private AbstractEnumerableSorter<TElement> getEnumerableSorter() {
        return this.getEnumerableSorter(null);
    }

    protected abstract AbstractEnumerableSorter<TElement> getEnumerableSorter(AbstractEnumerableSorter<TElement> next);

    private AbstractCachingComparer<TElement> getComparer() {
        return this.getComparer(null);
    }

    protected abstract AbstractCachingComparer<TElement> getComparer(AbstractCachingComparer<TElement> childComparer);

    @Override
    public <TKey> IOrderedEnumerable<TElement> createOrderedEnumerable(Func1<TElement, TKey> keySelector, Comparator<TKey> comparer, boolean descending) {
        return new OrderedEnumerable<>(this.source, keySelector, comparer, descending, this);
    }

    public TElement _tryGetFirst(Predicate1<TElement> predicate, out<Boolean> found) {
        AbstractCachingComparer<TElement> comparer = this.getComparer();
        try (IEnumerator<TElement> e = this.source.enumerator()) {
            TElement value;
            do {
                if (!e.moveNext()) {
                    found.value = false;
                    return null;
                }
                value = e.current();
            } while (!predicate.apply(value));

            comparer.setElement(value);
            while (e.moveNext()) {
                TElement x = e.current();
                if (predicate.apply(x) && comparer.compare(x, true) < 0)
                    value = x;
            }

            found.value = true;
            return value;
        }
    }

    public TElement _tryGetLast(Predicate1<TElement> predicate, out<Boolean> found) {
        AbstractCachingComparer<TElement> comparer = this.getComparer();
        try (IEnumerator<TElement> e = this.source.enumerator()) {
            TElement value;
            do {
                if (!e.moveNext()) {
                    found.value = false;
                    return null;
                }
                value = e.current();
            } while (!predicate.apply(value));

            comparer.setElement(value);
            while (e.moveNext()) {
                TElement x = e.current();
                if (predicate.apply(x) && comparer.compare(x, false) >= 0)
                    value = x;
            }

            found.value = true;
            return value;
        }
    }

    @Override
    public TElement[] _toArray(Class<TElement> clazz) {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (count == 0)
            return buffer.toArray(clazz);

        TElement[] array = ArrayUtils.newInstance(clazz, count);
        Integer[] map = this.sortedMap(buffer);
        for (int i = 0; i != array.length; i++)
            //noinspection unchecked
            array[i] = (TElement) buffer.items[map[i]];
        return array;
    }

    @Override
    public Object[] _toArray() {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (count == 0)
            return buffer.items;

        Object[] array = new Object[count];
        Integer[] map = this.sortedMap(buffer);
        for (int i = 0; i != array.length; i++)
            array[i] = buffer.items[map[i]];
        return array;
    }

    @Override
    public List<TElement> _toList() {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        List<TElement> list = new ArrayList<>(count);
        if (count > 0) {
            Integer[] map = this.sortedMap(buffer);
            for (int i = 0; i != count; i++)
                //noinspection unchecked
                list.add((TElement) buffer.items[map[i]]);
        }
        return list;
    }

    @Override
    public int _getCount(boolean onlyIfCheap) {
        if (this.source instanceof IIListProvider) {
            IIListProvider<TElement> listProv = (IIListProvider<TElement>) this.source;
            return listProv._getCount(onlyIfCheap);
        }
        return !onlyIfCheap || this.source instanceof ICollection ? this.source.count() : -1;
    }

    public TElement[] _toArray(Class<TElement> clazz, int minIdx, int maxIdx) {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (count <= minIdx)
            return ArrayUtils.empty(clazz);

        if (count <= maxIdx)
            maxIdx = count - 1;

        if (minIdx == maxIdx)
            return ArrayUtils.singleton(clazz, this.getEnumerableSorter().elementAt(buffer.items, count, minIdx));

        Integer[] map = this.sortedMap(buffer, minIdx, maxIdx);
        TElement[] array = ArrayUtils.newInstance(clazz, maxIdx - minIdx + 1);
        int idx = 0;
        while (minIdx <= maxIdx) {
            //noinspection unchecked
            array[idx] = (TElement) buffer.items[map[minIdx]];
            ++idx;
            ++minIdx;
        }

        return array;
    }

    public Object[] _toArray(int minIdx, int maxIdx) {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (count <= minIdx)
            return ArrayUtils.empty();

        if (count <= maxIdx)
            maxIdx = count - 1;

        if (minIdx == maxIdx)
            return ArrayUtils.singleton(this.getEnumerableSorter().elementAt(buffer.items, count, minIdx));

        Integer[] map = this.sortedMap(buffer, minIdx, maxIdx);
        Object[] array = new Object[maxIdx - minIdx + 1];
        int idx = 0;
        while (minIdx <= maxIdx) {
            array[idx] = buffer.items[map[minIdx]];
            ++idx;
            ++minIdx;
        }

        return array;
    }

    public List<TElement> _toList(int minIdx, int maxIdx) {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (count <= minIdx)
            return ListUtils.empty();

        if (count <= maxIdx)
            maxIdx = count - 1;

        if (minIdx == maxIdx)
            return ListUtils.singleton(this.getEnumerableSorter().elementAt(buffer.items, count, minIdx));

        Integer[] map = this.sortedMap(buffer, minIdx, maxIdx);
        List<TElement> list = new ArrayList<>(maxIdx - minIdx + 1);
        while (minIdx <= maxIdx) {
            //noinspection unchecked
            list.add((TElement) buffer.items[map[minIdx]]);
            ++minIdx;
        }

        return list;
    }

    public int _getCount(int minIdx, int maxIdx, boolean onlyIfCheap) {
        int count = this._getCount(onlyIfCheap);
        if (count <= 0)
            return count;

        if (count <= minIdx)
            return 0;

        return (count <= maxIdx ? count : maxIdx + 1) - minIdx;
    }

    @Override
    public IPartition<TElement> _skip(int count) {
        return new OrderedPartition<>(this, count, Integer.MAX_VALUE);
    }

    @Override
    public IPartition<TElement> _take(int count) {
        return new OrderedPartition<>(this, 0, count - 1);
    }

    @Override
    public TElement _tryGetElementAt(int index, out<Boolean> found) {
        if (index == 0)
            return this._tryGetFirst(found);

        if (index > 0) {
            Buffer<TElement> buffer = new Buffer<>(this.source);
            int count = buffer.count;
            if (index < count) {
                found.value = true;
                return this.getEnumerableSorter().elementAt(buffer.items, count, index);
            }
        }

        found.value = false;
        return null;
    }

    @Override
    public TElement _tryGetFirst(out<Boolean> found) {
        AbstractCachingComparer<TElement> comparer = this.getComparer();
        try (IEnumerator<TElement> e = this.source.enumerator()) {
            if (!e.moveNext()) {
                found.value = false;
                return null;
            }

            TElement value = e.current();
            comparer.setElement(value);
            while (e.moveNext()) {
                TElement x = e.current();
                if (comparer.compare(x, true) < 0)
                    value = x;
            }

            found.value = true;
            return value;
        }
    }

    @Override
    public TElement _tryGetLast(out<Boolean> found) {
        try (IEnumerator<TElement> e = this.source.enumerator()) {
            if (!e.moveNext()) {
                found.value = false;
                return null;
            }

            AbstractCachingComparer<TElement> comparer = this.getComparer();
            TElement value = e.current();
            comparer.setElement(value);
            while (e.moveNext()) {
                TElement current = e.current();
                if (comparer.compare(current, false) >= 0)
                    value = current;
            }

            found.value = true;
            return value;
        }
    }

    public TElement _tryGetLast(int minIdx, int maxIdx, out<Boolean> found) {
        Buffer<TElement> buffer = new Buffer<>(this.source);
        int count = buffer.count;
        if (minIdx >= count) {
            found.value = false;
            return null;
        }

        found.value = true;
        return maxIdx < count - 1 ? this.getEnumerableSorter().elementAt(buffer.items, count, maxIdx) : this._last(buffer);
    }

    private TElement _last(Buffer<TElement> buffer) {
        AbstractCachingComparer<TElement> comparer = this.getComparer();
        Object[] items = buffer.items;
        int count = buffer.count;
        //noinspection unchecked
        TElement value = (TElement) items[0];
        comparer.setElement(value);
        for (int i = 1; i != count; ++i) {
            //noinspection unchecked
            TElement x = (TElement) items[i];
            if (comparer.compare(x, false) >= 0)
                value = x;
        }

        return value;
    }


    private class OrderedEnumerableEnumerator extends AbstractEnumerator<TElement> {
        private Buffer<TElement> buffer;
        private Integer[] map;
        private int index;

        @Override
        public boolean moveNext() {
            switch (this.state) {
                case 0:
                    this.buffer = new Buffer<>(AbstractOrderedEnumerable.this.source);
                    if (this.buffer.count <= 0) {
                        this.close();
                        return false;
                    }
                    this.map = AbstractOrderedEnumerable.this.sortedMap(this.buffer);
                    this.index = -1;
                    this.state = 1;
                case 1:
                    this.index++;
                    if (this.index < this.buffer.count) {
                        //noinspection unchecked
                        this.current = (TElement) this.buffer.items[this.map[this.index]];
                        return true;
                    }
                    this.close();
                    return false;
                default:
                    return false;
            }
        }

        @Override
        public void close() {
            this.buffer = null;
            this.map = null;
            super.close();
        }
    }

    private class OrderedEnumerableRangeEnumerator extends AbstractEnumerator<TElement> {
        private int minIdx;
        private int maxIdx;
        private Buffer<TElement> buffer;
        private Integer[] map;

        private OrderedEnumerableRangeEnumerator(int minIdx, int maxIdx) {
            this.minIdx = minIdx;
            this.maxIdx = maxIdx;
        }

        @Override
        public boolean moveNext() {
            switch (this.state) {
                case 0:
                    this.buffer = new Buffer<>(AbstractOrderedEnumerable.this.source);
                    int count = this.buffer.count;
                    if (count <= this.minIdx) {
                        this.close();
                        return false;
                    }
                    if (count <= this.maxIdx)
                        this.maxIdx = count - 1;
                    if (this.minIdx == this.maxIdx) {
                        this.current = AbstractOrderedEnumerable.this.getEnumerableSorter().elementAt(this.buffer.items, count, this.minIdx);
                        this.state = 2;
                        return true;
                    }
                    this.map = AbstractOrderedEnumerable.this.sortedMap(this.buffer, this.minIdx, this.maxIdx);
                    this.state = 1;
                case 1:
                    if (this.minIdx <= this.maxIdx) {
                        //noinspection unchecked
                        this.current = (TElement) this.buffer.items[this.map[this.minIdx]];
                        ++this.minIdx;
                        return true;
                    }
                    this.map = null;
                    this.close();
                    return false;
                case 2:
                    this.close();
                    return false;
                default:
                    return false;
            }
        }

        @Override
        public void close() {
            this.buffer = null;
            this.map = null;
            super.close();
        }
    }
}


final class OrderedEnumerable<TElement, TKey> extends AbstractOrderedEnumerable<TElement> {
    private final AbstractOrderedEnumerable<TElement> parent;
    private final Func1<TElement, TKey> keySelector;
    private final Comparator<TKey> comparer;
    private final boolean descending;

    OrderedEnumerable(IEnumerable<TElement> source, Func1<TElement, TKey> keySelector, Comparator<TKey> comparer, boolean descending, AbstractOrderedEnumerable<TElement> parent) {
        if (source == null)
            ThrowHelper.throwArgumentNullException(ExceptionArgument.source);
        if (keySelector == null)
            ThrowHelper.throwArgumentNullException(ExceptionArgument.keySelector);

        this.source = source;
        this.parent = parent;
        this.keySelector = keySelector;
        this.comparer = comparer == null ? Comparer.Default() : comparer;
        this.descending = descending;
    }

    @Override
    protected AbstractEnumerableSorter<TElement> getEnumerableSorter(AbstractEnumerableSorter<TElement> next) {
        AbstractEnumerableSorter<TElement> sorter = new EnumerableSorter<>(this.keySelector, this.comparer, this.descending, next);
        if (this.parent != null)
            sorter = this.parent.getEnumerableSorter(sorter);
        return sorter;
    }

    @Override
    protected AbstractCachingComparer<TElement> getComparer(AbstractCachingComparer<TElement> childComparer) {
        AbstractCachingComparer<TElement> cmp = childComparer == null
                ? new CachingComparer<>(this.keySelector, this.comparer, this.descending)
                : new CachingComparerWithChild<>(this.keySelector, this.comparer, this.descending, childComparer);
        return this.parent != null ? this.parent.getComparer(cmp) : cmp;
    }
}


abstract class AbstractCachingComparer<TElement> {
    abstract int compare(TElement element, boolean cacheLower);

    abstract void setElement(TElement element);
}


class CachingComparer<TElement, TKey> extends AbstractCachingComparer<TElement> {
    protected final Func1<TElement, TKey> keySelector;
    protected final Comparator<TKey> comparer;
    protected final boolean descending;
    protected TKey lastKey;

    CachingComparer(Func1<TElement, TKey> keySelector, Comparator<TKey> comparer, boolean descending) {
        this.keySelector = keySelector;
        this.comparer = comparer;
        this.descending = descending;
    }

    @Override
    int compare(TElement element, boolean cacheLower) {
        TKey newKey = this.keySelector.apply(element);
        int cmp = this.descending ? this.comparer.compare(this.lastKey, newKey) : this.comparer.compare(newKey, this.lastKey);
        if (cacheLower == cmp < 0)
            this.lastKey = newKey;
        return cmp;
    }

    @Override
    void setElement(TElement element) {
        this.lastKey = this.keySelector.apply(element);
    }
}


final class CachingComparerWithChild<TElement, TKey> extends CachingComparer<TElement, TKey> {
    private final AbstractCachingComparer<TElement> child;

    CachingComparerWithChild(Func1<TElement, TKey> keySelector, Comparator<TKey> comparer, boolean descending, AbstractCachingComparer<TElement> child) {
        super(keySelector, comparer, descending);
        this.child = child;
    }

    @Override
    protected int compare(TElement element, boolean cacheLower) {
        TKey newKey = this.keySelector.apply(element);
        int cmp = this.descending ? this.comparer.compare(this.lastKey, newKey) : this.comparer.compare(newKey, this.lastKey);
        if (cmp == 0)
            return this.child.compare(element, cacheLower);
        if (cacheLower == cmp < 0) {
            this.lastKey = newKey;
            this.child.setElement(element);
        }
        return cmp;
    }

    @Override
    protected void setElement(TElement element) {
        super.setElement(element);
        this.child.setElement(element);
    }
}


@SuppressWarnings("SameParameterValue")
abstract class AbstractEnumerableSorter<TElement> {
    protected abstract void computeKeys(Object[] elements, int count);

    protected abstract int compareAnyKeys(int index1, int index2);

    private Integer[] computeMap(Object[] elements, int count) {
        this.computeKeys(elements, count);
        Integer[] map = new Integer[count];
        for (int i = 0; i < map.length; i++)
            map[i] = i;
        return map;
    }

    protected Integer[] sort(Object[] elements, int count) {
        Integer[] map = this.computeMap(elements, count);
        this.quickSort(map, 0, count - 1);
        return map;
    }

    protected Integer[] sort(Object[] elements, int count, int minIdx, int maxIdx) {
        Integer[] map = this.computeMap(elements, count);
        this.partialQuickSort(map, 0, count - 1, minIdx, maxIdx);
        return map;
    }

    protected TElement elementAt(Object[] elements, int count, int idx) {
        Integer[] map = this.computeMap(elements, count);
        //noinspection unchecked
        return (TElement) (idx == 0 ? elements[this.min(map, count)] : elements[this.quickSelect(map, count - 1, idx)]);
    }

    protected abstract void quickSort(Integer[] map, int left, int right);

    // Sorts the k elements between minIdx and maxIdx without sorting all elements
    // Time complexity: O(n + k log k) best and average case. O(n^2) worse case.
    protected abstract void partialQuickSort(Integer[] map, int left, int right, int minIdx, int maxIdx);

    // Finds the element that would be at idx if the collection was sorted.
    // Time complexity: O(n) best and average case. O(n^2) worse case.
    protected abstract int quickSelect(Integer[] map, int right, int idx);

    protected abstract int min(Integer[] map, int count);
}


final class EnumerableSorter<TElement, TKey> extends AbstractEnumerableSorter<TElement> {
    private final Func1<TElement, TKey> keySelector;
    private final Comparator<TKey> comparer;
    private final boolean descending;
    private final AbstractEnumerableSorter<TElement> next;
    private Object[] keys;

    EnumerableSorter(Func1<TElement, TKey> keySelector, Comparator<TKey> comparer, boolean descending, AbstractEnumerableSorter<TElement> next) {
        this.keySelector = keySelector;
        this.comparer = comparer;
        this.descending = descending;
        this.next = next;
    }

    @Override
    protected void computeKeys(Object[] elements, int count) {
        this.keys = new Object[count];
        for (int i = 0; i < count; i++)
            //noinspection unchecked
            this.keys[i] = this.keySelector.apply((TElement) elements[i]);
        if (this.next == null)
            return;
        this.next.computeKeys(elements, count);
    }

    @Override
    protected int compareAnyKeys(int index1, int index2) {
        assert this.keys != null;

        //noinspection unchecked
        int c = this.comparer.compare((TKey) this.keys[index1], (TKey) this.keys[index2]);
        if (c == 0) {
            if (this.next == null)
                return index1 - index2; // ensure stability of sort
            return this.next.compareAnyKeys(index1, index2);
        }

        // -c will result in a negative value for int.MinValue (-int.MinValue == int.MinValue).
        // Flipping keys earlier is more likely to trigger something strange in a comparer,
        // particularly as it comes to the sort being stable.
        return this.descending != (c > 0) ? 1 : -1;
    }

    private int compareKeys(int index1, int index2) {
        return index1 == index2 ? 0 : this.compareAnyKeys(index1, index2);
    }

    @Override
    protected void quickSort(Integer[] keys, int lo, int hi) {
        Arrays.sort(keys, lo, hi - lo + 1, Comparer.create(this::compareAnyKeys));
    }

    // Sorts the k elements between minIdx and maxIdx without sorting all elements
    // Time complexity: O(n + k log k) best and average case. O(n^2) worse case.
    @Override
    protected void partialQuickSort(Integer[] map, int left, int right, int minIdx, int maxIdx) {
        do {
            int i = left;
            int j = right;
            int x = map[i + ((j - i) >> 1)];
            do {
                while (i < map.length && this.compareKeys(x, map[i]) > 0)
                    i++;

                while (j >= 0 && this.compareKeys(x, map[j]) < 0)
                    j--;

                if (i > j)
                    break;

                if (i < j) {
                    int temp = map[i];
                    map[i] = map[j];
                    map[j] = temp;
                }

                i++;
                j--;
            } while (i <= j);

            if (minIdx >= i)
                left = i + 1;
            else if (maxIdx <= j)
                right = j - 1;

            if (j - left <= right - i) {
                if (left < j)
                    this.partialQuickSort(map, left, j, minIdx, maxIdx);

                left = i;
            } else {
                if (i < right)
                    this.partialQuickSort(map, i, right, minIdx, maxIdx);

                right = j;
            }
        } while (left < right);
    }

    // Finds the element that would be at idx if the collection was sorted.
    // Time complexity: O(n) best and average case. O(n^2) worse case.
    @Override
    protected int quickSelect(Integer[] map, int right, int idx) {
        int left = 0;
        do {
            int i = left;
            int j = right;
            int x = map[i + ((j - i) >> 1)];
            do {
                while (i < map.length && this.compareKeys(x, map[i]) > 0)
                    i++;

                while (j >= 0 && this.compareKeys(x, map[j]) < 0)
                    j--;

                if (i > j)
                    break;

                if (i < j) {
                    int temp = map[i];
                    map[i] = map[j];
                    map[j] = temp;
                }

                i++;
                j--;
            } while (i <= j);

            if (i <= idx)
                left = i + 1;
            else
                right = j - 1;

            if (j - left <= right - i) {
                if (left < j)
                    right = j;

                left = i;
            } else {
                if (i < right)
                    left = i;

                right = j;
            }
        } while (left < right);

        return map[idx];
    }

    @Override
    protected int min(Integer[] map, int count) {
        int index = 0;
        for (int i = 1; i < count; i++) {
            if (this.compareKeys(map[i], map[index]) < 0)
                index = i;
        }
        return map[index];
    }
}
