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
public final class TupleN<T1, T2, T3, T4, T5, T6, T7, TRest> implements IStructuralEquatable, IStructuralComparable, Comparable, ITupleInternal, ITuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;
    private final T4 item4;
    private final T5 item5;
    private final T6 item6;
    private final T7 item7;
    private final TRest rest;

    public TupleN(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6, T7 item7, TRest rest) {
        if (!(rest instanceof ITupleInternal))
            ThrowHelper.throwTupleLastArgumentNotATupleException();
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
        this.item7 = item7;
        this.rest = rest;
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

    public T7 getItem7() {
        return this.item7;
    }

    public TRest getRest() {
        return this.rest;
    }

    @Override
    public int size() {
        return 7 + ((ITupleInternal) this.rest).size();
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
            case 6:
                return this.item7;
            default:
                return ((ITupleInternal) this.rest).get(index - 7);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.equals(obj, EqualityComparer.Default());
    }

    @Override
    public boolean equals(Object other, IEqualityComparer comparer) {
        TupleN objTuple;
        //noinspection unchecked
        return other instanceof TupleN
                && comparer.equals(this.item1, (objTuple = (TupleN) other).item1)
                && comparer.equals(this.item2, objTuple.item2)
                && comparer.equals(this.item3, objTuple.item3)
                && comparer.equals(this.item4, objTuple.item4)
                && comparer.equals(this.item5, objTuple.item5)
                && comparer.equals(this.item6, objTuple.item6)
                && comparer.equals(this.item7, objTuple.item7)
                && comparer.equals(this.rest, objTuple.rest);
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo(obj, Comparer.Default());
    }

    @Override
    public int compareTo(Object other, Comparator comparer) {
        if (other == null)
            return 1;
        if (!(other instanceof TupleN))
            ThrowHelper.throwTupleIncorrectTypeException(this.getClass(), ExceptionArgument.other);
        TupleN objTuple = (TupleN) other;
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
        c = comparer.compare(this.item6, objTuple.item6);
        if (c != 0)
            return c;
        //noinspection unchecked
        c = comparer.compare(this.item7, objTuple.item7);
        if (c != 0)
            return c;
        //noinspection unchecked
        return comparer.compare(this.rest, objTuple.rest);
    }

    @Override
    public int hashCode() {
        return this.hashCode(EqualityComparer.Default());
    }

    @Override
    public int hashCode(IEqualityComparer comparer) {
        ITupleInternal t = (ITupleInternal) this.rest;
        if (t.size() >= 8)
            return t.hashCode(comparer);
        int k = 8 - t.size();
        switch (k) {
            case 1:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item7), t.hashCode(comparer));
            case 2:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            case 3:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item5), comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            case 4:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item4), comparer.hashCode(this.item5), comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            case 5:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item3), comparer.hashCode(this.item4), comparer.hashCode(this.item5), comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            case 6:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item2), comparer.hashCode(this.item3), comparer.hashCode(this.item4), comparer.hashCode(this.item5), comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            case 7:
                //noinspection unchecked
                return Tuple.combineHashCodes(comparer.hashCode(this.item1), comparer.hashCode(this.item2), comparer.hashCode(this.item3), comparer.hashCode(this.item4), comparer.hashCode(this.item5), comparer.hashCode(this.item6), comparer.hashCode(this.item7), t.hashCode(comparer));
            default:
                System.out.println("Missed all cases for computing Tuple hash code");
                return -1;
        }
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
        sb.append(", ");
        sb.append(this.item7);
        sb.append(", ");
        return ((ITupleInternal) this.rest).toString(sb);
    }
}
