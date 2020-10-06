package com.bestvike.tuple;

import com.bestvike.collections.IStructuralComparable;
import com.bestvike.collections.IStructuralEquatable;
import com.bestvike.collections.generic.Comparer;
import com.bestvike.collections.generic.EqualityComparer;
import com.bestvike.collections.generic.IEqualityComparer;
import com.bestvike.linq.exception.ExceptionArgument;
import com.bestvike.linq.exception.ThrowHelper;

import java.util.Comparator;

/**
 * Created by 许崇雷 on 2017-07-23.
 */
@SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "rawtypes"})
public final class Tuple6<T1, T2, T3, T4, T5, T6> implements IStructuralEquatable, IStructuralComparable, Comparable, ITupleInternal, ITuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;
    private final T4 item4;
    private final T5 item5;
    private final T6 item6;

    public Tuple6(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
    }

    public T1 getItem1() {
        return this.item1;
    }

    public T2 getItem2() {
        return this.item2;
    }

    public T3 getItem3() {
        return this.item3;
    }

    public T4 getItem4() {
        return this.item4;
    }

    public T5 getItem5() {
        return this.item5;
    }

    public T6 getItem6() {
        return this.item6;
    }

    @Override
    public int size() {
        return 6;
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0:
                return this.item1;
            case 1:
                return this.item2;
            case 2:
                return this.item3;
            case 3:
                return this.item4;
            case 4:
                return this.item5;
            case 5:
                return this.item6;
            default:
                ThrowHelper.throwIndexOutOfRangeException();
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.equals(obj, EqualityComparer.Default());
    }

    @Override
    public boolean equals(Object other, IEqualityComparer comparer) {
        Tuple6 objTuple;
        //noinspection unchecked
        return other instanceof Tuple6
                && comparer.equals(this.item1, (objTuple = (Tuple6) other).item1)
                && comparer.equals(this.item2, objTuple.item2)
                && comparer.equals(this.item3, objTuple.item3)
                && comparer.equals(this.item4, objTuple.item4)
                && comparer.equals(this.item5, objTuple.item5)
                && comparer.equals(this.item6, objTuple.item6);
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo(obj, Comparer.Default());
    }

    @Override
    public int compareTo(Object other, Comparator comparer) {
        if (other == null)
            return 1;
        if (!(other instanceof Tuple6))
            ThrowHelper.throwTupleIncorrectTypeException(this.getClass(), ExceptionArgument.other);
        Tuple6 objTuple = (Tuple6) other;
        //noinspection unchecked
        int c = comparer.compare(this.item1, objTuple.item1);
        if (c != 0)
            return c;
        //noinspection unchecked
        c = comparer.compare(this.item2, objTuple.item2);
        if (c != 0)
            return c;
        //noinspection unchecked
        c = comparer.compare(this.item3, objTuple.item3);
        if (c != 0)
            return c;
        //noinspection unchecked
        c = comparer.compare(this.item4, objTuple.item4);
        if (c != 0)
            return c;
        //noinspection unchecked
        c = comparer.compare(this.item5, objTuple.item5);
        if (c != 0)
            return c;
        //noinspection unchecked
        return comparer.compare(this.item6, objTuple.item6);
    }

    @Override
    public int hashCode() {
        return this.hashCode(EqualityComparer.Default());
    }

    @Override
    public int hashCode(IEqualityComparer comparer) {
        //noinspection unchecked
        return Tuple.combineHashCodes(comparer.hashCode(this.item1),
                comparer.hashCode(this.item2),
                comparer.hashCode(this.item3),
                comparer.hashCode(this.item4),
                comparer.hashCode(this.item5),
                comparer.hashCode(this.item6));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        return this.toString(sb);
    }

    @Override
    public String toString(StringBuilder sb) {
        sb.append(this.item1);
        sb.append(", ");
        sb.append(this.item2);
        sb.append(", ");
        sb.append(this.item3);
        sb.append(", ");
        sb.append(this.item4);
        sb.append(", ");
        sb.append(this.item5);
        sb.append(", ");
        sb.append(this.item6);
        sb.append(')');
        return sb.toString();
    }
}
