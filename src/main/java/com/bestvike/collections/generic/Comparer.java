package com.bestvike.collections.generic;

import com.bestvike.CultureInfo;
import com.bestvike.IComparison;
import com.bestvike.linq.exception.ExceptionArgument;
import com.bestvike.linq.exception.ThrowHelper;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by 许崇雷 on 2017-07-18.
 */
public final class Comparer<T> implements Comparator<T> {
    private static final Comparer<?> DEFAULT = new Comparer<>(null);
    private static final Comparer<?> DEFAULT_INVARIANT = new Comparer<>(CultureInfo.getInvariantCulture());

    private final Collator collator;

    private Comparer(Collator collator) {
        this.collator = collator;
    }

    public static <T> Comparator<T> Default() {
        //noinspection unchecked
        return (Comparator<T>) DEFAULT;
    }

    public static <T> Comparator<T> DefaultInvariant() {
        //noinspection unchecked
        return (Comparator<T>) DEFAULT_INVARIANT;
    }

    public static <T> Comparator<T> create(Collator collator) {
        if (collator == null)
            ThrowHelper.throwArgumentNullException(ExceptionArgument.collator);
        return new Comparer<>(collator);
    }

    public static <T> Comparator<T> create(IComparison<? super T> comparison) {
        if (comparison == null)
            ThrowHelper.throwArgumentNullException(ExceptionArgument.comparison);
        return new ComparisonComparer<>(comparison);
    }

    @Override
    public int compare(T x, T y) {
        if (x == y)
            return 0;
        if (x == null)
            return -1;
        if (y == null)
            return 1;
        if (x instanceof String && y instanceof String)
            return (this.collator == null ? CultureInfo.getCurrentCulture() : this.collator).compare((String) x, (String) y);
        if (x instanceof Comparable)
            //noinspection unchecked
            return ((Comparable<T>) x).compareTo(y);
        if (y instanceof Comparable)
            //noinspection unchecked
            return -((Comparable<T>) y).compareTo(x);
        ThrowHelper.throwImplementComparableException();
        return 0;
    }
}
