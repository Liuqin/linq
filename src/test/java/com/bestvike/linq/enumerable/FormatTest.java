package com.bestvike.linq.enumerable;

import com.bestvike.TestCase;
import com.bestvike.ValueType;
import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.Linq;
import com.bestvike.linq.exception.ArgumentNullException;
import com.bestvike.linq.util.ArgsList;
import com.bestvike.linq.util.FormatTypeStyle;
import com.bestvike.linq.util.Formatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by 许崇雷 on 2019-06-20.
 */
class FormatTest extends TestCase {
    private final Formatter formatter;

    {
        TimeZone.setDefault(TIME_ZONE);
        this.formatter = new Formatter();
        this.formatter.setNullString("NULL");
        this.formatter.setStringQuotes("\"");
        this.formatter.setDecimalWithScale(false);
        this.formatter.setDecimalScale(0);
        this.formatter.setDecimalRounding(RoundingMode.CEILING);
        this.formatter.setArrayTypeStyle(FormatTypeStyle.SimpleName);
        this.formatter.setArrayPrefix("[ ");
        this.formatter.setArraySuffix(" ]");
        this.formatter.setArrayEmpty("[ ]");
        this.formatter.setArrayValueSeparator(",");
        this.formatter.setMapTypeStyle(null);
        this.formatter.setMapPrefix("( ");
        this.formatter.setMapSuffix(" )");
        this.formatter.setMapEmpty("( )");
        this.formatter.setMapEntrySeparator(",");
        this.formatter.setMapKeyValueSeparator(":");
        this.formatter.setObjectTypeStyle(FormatTypeStyle.FullName);
        this.formatter.setObjectPrefix("( ");
        this.formatter.setObjectSuffix(" )");
        this.formatter.setObjectEmpty("( )");
        this.formatter.setObjectFieldSeparator(",");
        this.formatter.setObjectFieldValueSeparator(":");
    }

    private static IEnumerable<Object[]> Format_Int_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.repeat(42, 1), "[42]");
        argsList.add(Linq.range(1, 10).toArray(), "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        argsList.add(Linq.of(new int[]{-1, -10, 10, 200, 1000}), "[-1, -10, 10, 200, 1000]");
        argsList.add(Linq.of(new int[]{3000, 100, 200, 1000}), "[3000, 100, 200, 1000]");
        argsList.add(Linq.of(new int[]{3000, 100, 200, 1000}).concat(Linq.repeat(Integer.MIN_VALUE, 1)), "[3000, 100, 200, 1000, -2147483648]");

        argsList.add(Linq.repeat(20, 1), "[20]");
        argsList.add(Linq.repeat(-2, 5), "[-2, -2, -2, -2, -2]");
        argsList.add(Linq.range(1, 10).toArray(), "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        argsList.add(Linq.of(new int[]{6, 9, 10, 7, 8}), "[6, 9, 10, 7, 8]");
        argsList.add(Linq.of(new int[]{6, 9, 10, 0, -5}), "[6, 9, 10, 0, -5]");
        argsList.add(Linq.of(new int[]{6, 0, 9, 0, 10, 0}), "[6, 0, 9, 0, 10, 0]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_Long_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.repeat(42L, 1), "[42]");
        argsList.add(Linq.range(1, 10).select(i -> (long) i).toArray(), "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        argsList.add(Linq.of(new long[]{-1, -10, 10, 200, 1000}), "[-1, -10, 10, 200, 1000]");
        argsList.add(Linq.of(new long[]{3000, 100, 200, 1000}), "[3000, 100, 200, 1000]");
        argsList.add(Linq.of(new long[]{3000, 100, 200, 1000}).concat(Linq.repeat(Long.MIN_VALUE, 1)), "[3000, 100, 200, 1000, -9223372036854775808]");

        argsList.add(Linq.repeat(Integer.MAX_VALUE + 10L, 1), "[2147483657]");
        argsList.add(Linq.repeat(500L, 5), "[500, 500, 500, 500, 500]");
        argsList.add(Linq.of(new long[]{-250, 49, 130, 47, 28}), "[-250, 49, 130, 47, 28]");
        argsList.add(Linq.of(new long[]{6, 9, 10, 0, -Integer.MAX_VALUE - 50L}), "[6, 9, 10, 0, -2147483697]");
        argsList.add(Linq.of(new long[]{6, -5, 9, -5, 10, -5}), "[6, -5, 9, -5, 10, -5]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_Float_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.repeat(42f, 1), "[42.0]");
        argsList.add(Linq.range(1, 10).select(i -> (float) i).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]");
        argsList.add(Linq.of(new float[]{-1, -10, 10, 200, 1000}), "[-1.0, -10.0, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new float[]{3000, 100, 200, 1000}), "[3000.0, 100.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new float[]{3000, 100, 200, 1000}).concat(Linq.repeat(Float.MIN_VALUE, 1)), "[3000.0, 100.0, 200.0, 1000.0, 1.4E-45]");

        argsList.add(Linq.repeat(5.5f, 1), "[5.5]");
        argsList.add(Linq.repeat(Float.NaN, 5), "[NaN, NaN, NaN, NaN, NaN]");
        argsList.add(Linq.of(new float[]{-2.5f, 4.9f, 130f, 4.7f, 28f}), "[-2.5, 4.9, 130.0, 4.7, 28.0]");
        argsList.add(Linq.of(new float[]{6.8f, 9.4f, 10f, 0, -5.6f}), "[6.8, 9.4, 10.0, 0.0, -5.6]");
        argsList.add(Linq.of(new float[]{-5.5f, Float.NEGATIVE_INFINITY, 9.9f, Float.NEGATIVE_INFINITY}), "[-5.5, -Infinity, 9.9, -Infinity]");

        argsList.add(Linq.of(new float[]{Float.NaN, 6.8f, 9.4f, 10f, 0, -5.6f}), "[NaN, 6.8, 9.4, 10.0, 0.0, -5.6]");
        argsList.add(Linq.of(new float[]{6.8f, 9.4f, 10f, 0, -5.6f, Float.NaN}), "[6.8, 9.4, 10.0, 0.0, -5.6, NaN]");
        argsList.add(Linq.of(new float[]{Float.NaN, Float.NEGATIVE_INFINITY}), "[NaN, -Infinity]");
        argsList.add(Linq.of(new float[]{Float.NEGATIVE_INFINITY, Float.NaN}), "[-Infinity, NaN]");

        argsList.add(Linq.repeat(Float.NaN, 3), "[NaN, NaN, NaN]");

        argsList.add(Linq.range(1, 10).select(i -> (float) i).concat(Linq.repeat(Float.NaN, 1)).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, NaN]");
        argsList.add(Linq.of(new float[]{-1F, -10, Float.NaN, 10, 200, 1000}), "[-1.0, -10.0, NaN, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new float[]{Float.MIN_VALUE, 3000F, 100, 200, Float.NaN, 1000}), "[1.4E-45, 3000.0, 100.0, 200.0, NaN, 1000.0]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_Double_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.repeat(42.0, 1), "[42.0]");
        argsList.add(Linq.range(1, 10).select(i -> (double) i).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]");
        argsList.add(Linq.of(new double[]{-1, -10, 10, 200, 1000}), "[-1.0, -10.0, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new double[]{3000, 100, 200, 1000}), "[3000.0, 100.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new double[]{3000, 100, 200, 1000}).concat(Linq.repeat(Double.MIN_VALUE, 1)), "[3000.0, 100.0, 200.0, 1000.0, 4.9E-324]");

        argsList.add(Linq.repeat(5.5, 1), "[5.5]");
        argsList.add(Linq.of(new double[]{-2.5, 4.9, 130, 4.7, 28}), "[-2.5, 4.9, 130.0, 4.7, 28.0]");
        argsList.add(Linq.of(new double[]{6.8, 9.4, 10, 0, -5.6}), "[6.8, 9.4, 10.0, 0.0, -5.6]");
        argsList.add(Linq.of(new double[]{-5.5, Double.NEGATIVE_INFINITY, 9.9, Double.NEGATIVE_INFINITY}), "[-5.5, -Infinity, 9.9, -Infinity]");

        argsList.add(Linq.repeat(Double.NaN, 3), "[NaN, NaN, NaN]");

        argsList.add(Linq.of(new double[]{Double.NaN, 6.8, 9.4, 10, 0, -5.6}), "[NaN, 6.8, 9.4, 10.0, 0.0, -5.6]");
        argsList.add(Linq.of(new double[]{6.8, 9.4, 10, 0, -5.6, Double.NaN}), "[6.8, 9.4, 10.0, 0.0, -5.6, NaN]");
        argsList.add(Linq.of(new double[]{Double.NaN, Double.NEGATIVE_INFINITY}), "[NaN, -Infinity]");
        argsList.add(Linq.of(new double[]{Double.NEGATIVE_INFINITY, Double.NaN}), "[-Infinity, NaN]");

        argsList.add(Linq.range(1, 10).select(i -> (double) i).concat(Linq.repeat(Double.NaN, 1)).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, NaN]");
        argsList.add(Linq.of(new double[]{-1, -10, Double.NaN, 10, 200, 1000}), "[-1.0, -10.0, NaN, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(new double[]{Double.MIN_VALUE, 3000F, 100, 200, Double.NaN, 1000}), "[4.9E-324, 3000.0, 100.0, 200.0, NaN, 1000.0]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_Decimal_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.repeat(m("42"), 1), "[42.000000]");
        argsList.add(Linq.range(1, 10).select(i -> m(i)).toArray(), "[1.000000, 2.000000, 3.000000, 4.000000, 5.000000, 6.000000, 7.000000, 8.000000, 9.000000, 10.000000]");
        argsList.add(Linq.of(m(-1), m(-10), m(10), m(200), m(1000)), "[-1.000000, -10.000000, 10.000000, 200.000000, 1000.000000]");
        argsList.add(Linq.of(m(3000), m(100), m(200), m(1000)), "[3000.000000, 100.000000, 200.000000, 1000.000000]");
        argsList.add(Linq.of(m(3000), m(100), m(200), m(1000)).concat(Linq.repeat(MIN_DECIMAL, 1)), "[3000.000000, 100.000000, 200.000000, 1000.000000, -999999999999999999999999999999.000000]");

        argsList.add(Linq.repeat(m("5.5"), 1), "[5.500000]");
        argsList.add(Linq.repeat(m("-3.4"), 5), "[-3.400000, -3.400000, -3.400000, -3.400000, -3.400000]");
        argsList.add(Linq.of(m("-2.5"), m("4.9"), m("130"), m("4.7"), m("28")), "[-2.500000, 4.900000, 130.000000, 4.700000, 28.000000]");
        argsList.add(Linq.of(m("6.8"), m("9.4"), m("10"), m("0"), m("0"), MIN_DECIMAL), "[6.800000, 9.400000, 10.000000, 0.000000, 0.000000, -999999999999999999999999999999.000000]");
        argsList.add(Linq.of(m("-5.5"), m("0"), m("9.9"), m("-5.5"), m("5")), "[-5.500000, 0.000000, 9.900000, -5.500000, 5.000000]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_NullableInt_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> i).toArray(), "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        argsList.add(Linq.of(null, -1, -10, 10, 200, 1000), "[null, -1, -10, 10, 200, 1000]");
        argsList.add(Linq.of(null, 3000, 100, 200, 1000), "[null, 3000, 100, 200, 1000]");
        argsList.add(Linq.of(null, 3000, 100, 200, 1000).concat(Linq.repeat(Integer.MIN_VALUE, 1)), "[null, 3000, 100, 200, 1000, -2147483648]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.repeat(42, 1), "[42]");

        argsList.add(Linq.<Integer>empty(), "[]");
        argsList.add(Linq.repeat(20, 1), "[20]");
        argsList.add(Linq.repeat(null, 5), "[null, null, null, null, null]");
        argsList.add(Linq.of(6, null, 9, 10, null, 7, 8), "[6, null, 9, 10, null, 7, 8]");
        argsList.add(Linq.of(null, null, null, null, null, -5), "[null, null, null, null, null, -5]");
        argsList.add(Linq.of(6, null, null, 0, 9, 0, 10, 0), "[6, null, null, 0, 9, 0, 10, 0]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_NullableLong_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> (long) i).toArray(), "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        argsList.add(Linq.of(null, -1L, -10L, 10L, 200L, 1000L), "[null, -1, -10, 10, 200, 1000]");
        argsList.add(Linq.of(null, 3000L, 100L, 200L, 1000L), "[null, 3000, 100, 200, 1000]");
        argsList.add(Linq.of(null, 3000L, 100L, 200L, 1000L).concat(Linq.repeat(Long.MIN_VALUE, 1)), "[null, 3000, 100, 200, 1000, -9223372036854775808]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.repeat(42L, 1), "[42]");

        argsList.add(Linq.<Long>empty(), "[]");
        argsList.add(Linq.repeat(Long.MAX_VALUE, 1), "[9223372036854775807]");
        argsList.add(Linq.repeat(null, 5), "[null, null, null, null, null]");
        argsList.add(Linq.of(Long.MIN_VALUE, null, 9L, 10L, null, 7L, 8L), "[-9223372036854775808, null, 9, 10, null, 7, 8]");
        argsList.add(Linq.of(null, null, null, null, null, -Long.MAX_VALUE), "[null, null, null, null, null, -9223372036854775807]");
        argsList.add(Linq.of(6L, null, null, 0L, 9L, 0L, 10L, 0L), "[6, null, null, 0, 9, 0, 10, 0]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_NullableFloat_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> (float) i).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]");
        argsList.add(Linq.of(null, -1f, -10f, 10f, 200f, 1000f), "[null, -1.0, -10.0, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(null, 3000f, 100f, 200f, 1000f), "[null, 3000.0, 100.0, 200.0, 1000.0]");
        argsList.add(Linq.of(null, 3000f, 100f, 200f, 1000f).concat(Linq.repeat(Float.MIN_VALUE, 1)), "[null, 3000.0, 100.0, 200.0, 1000.0, 1.4E-45]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.repeat(42f, 1), "[42.0]");

        argsList.add(Linq.<Float>empty(), "[]");
        argsList.add(Linq.repeat(Float.MIN_VALUE, 1), "[1.4E-45]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.of(-4.50f, null, 10.98f, null, 7.5f, 8.6f), "[-4.5, null, 10.98, null, 7.5, 8.6]");
        argsList.add(Linq.of(null, null, null, null, null, 0f), "[null, null, null, null, null, 0.0]");
        argsList.add(Linq.of(6.4f, null, null, -0.5f, 9.4f, -0.5f, 10.9f, -0.5f), "[6.4, null, null, -0.5, 9.4, -0.5, 10.9, -0.5]");

        argsList.add(Linq.of(Float.NaN, 6.8f, 9.4f, 10f, 0f, null, -5.6f), "[NaN, 6.8, 9.4, 10.0, 0.0, null, -5.6]");
        argsList.add(Linq.of(6.8f, 9.4f, 10f, 0f, null, -5.6f, Float.NaN), "[6.8, 9.4, 10.0, 0.0, null, -5.6, NaN]");
        argsList.add(Linq.of(Float.NaN, Float.NEGATIVE_INFINITY), "[NaN, -Infinity]");
        argsList.add(Linq.of(Float.NEGATIVE_INFINITY, Float.NaN), "[-Infinity, NaN]");
        argsList.add(Linq.of(Float.NaN, null, null, null), "[NaN, null, null, null]");
        argsList.add(Linq.of(null, null, null, Float.NaN), "[null, null, null, NaN]");
        argsList.add(Linq.of(null, Float.NaN, null), "[null, NaN, null]");

        argsList.add(Linq.repeat(Float.NaN, 3), "[NaN, NaN, NaN]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_NullableDouble_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> (double) i).toArray(), "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]");
        argsList.add(Linq.of(null, -1d, -10d, 10d, 200d, 1000d), "[null, -1.0, -10.0, 10.0, 200.0, 1000.0]");
        argsList.add(Linq.of(null, 3000d, 100d, 200d, 1000d), "[null, 3000.0, 100.0, 200.0, 1000.0]");
        argsList.add(Linq.of(null, 3000d, 100d, 200d, 1000d).concat(Linq.repeat(Double.MIN_VALUE, 1)), "[null, 3000.0, 100.0, 200.0, 1000.0, 4.9E-324]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.repeat(42d, 1), "[42.0]");

        argsList.add(Linq.<Double>empty(), "[]");
        argsList.add(Linq.repeat(Double.MIN_VALUE, 1), "[4.9E-324]");
        argsList.add(Linq.repeat(null, 5), "[null, null, null, null, null]");
        argsList.add(Linq.of(-4.50, null, 10.98, null, 7.5, 8.6), "[-4.5, null, 10.98, null, 7.5, 8.6]");
        argsList.add(Linq.of(null, null, null, null, null, 0d), "[null, null, null, null, null, 0.0]");
        argsList.add(Linq.of(6.4, null, null, -0.5, 9.4, -0.5, 10.9, -0.5), "[6.4, null, null, -0.5, 9.4, -0.5, 10.9, -0.5]");

        argsList.add(Linq.of(Double.NaN, 6.8, 9.4, 10.0, 0.0, null, -5.6), "[NaN, 6.8, 9.4, 10.0, 0.0, null, -5.6]");
        argsList.add(Linq.of(6.8, 9.4, 10d, 0.0, null, -5.6d, Double.NaN), "[6.8, 9.4, 10.0, 0.0, null, -5.6, NaN]");
        argsList.add(Linq.of(Double.NaN, Double.NEGATIVE_INFINITY), "[NaN, -Infinity]");
        argsList.add(Linq.of(Double.NEGATIVE_INFINITY, Double.NaN), "[-Infinity, NaN]");
        argsList.add(Linq.of(Double.NaN, null, null, null), "[NaN, null, null, null]");
        argsList.add(Linq.of(null, null, null, Double.NaN), "[null, null, null, NaN]");
        argsList.add(Linq.of(null, Double.NaN, null), "[null, NaN, null]");

        argsList.add(Linq.repeat(Double.NaN, 3), "[NaN, NaN, NaN]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_NullableDecimal_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> m(i)).toArray(), "[1.000000, 2.000000, 3.000000, 4.000000, 5.000000, 6.000000, 7.000000, 8.000000, 9.000000, 10.000000]");
        argsList.add(Linq.of(null, m(-1), m(-10), m(10), m(200), m(1000)), "[null, -1.000000, -10.000000, 10.000000, 200.000000, 1000.000000]");
        argsList.add(Linq.of(null, m(3000), m(100), m(200), m(1000)), "[null, 3000.000000, 100.000000, 200.000000, 1000.000000]");
        argsList.add(Linq.of(null, m(3000), m(100), m(200), m(1000)).concat(Linq.repeat(MIN_DECIMAL, 1)), "[null, 3000.000000, 100.000000, 200.000000, 1000.000000, -999999999999999999999999999999.000000]");
        argsList.add(Linq.repeat(null, 100), "[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]");
        argsList.add(Linq.repeat(m("42"), 1), "[42.000000]");

        argsList.add(Linq.<BigDecimal>empty(), "[]");
        argsList.add(Linq.repeat(MAX_DECIMAL, 1), "[999999999999999999999999999999.000000]");
        argsList.add(Linq.repeat(null, 5), "[null, null, null, null, null]");
        argsList.add(Linq.of(m("-4.50"), null, null, m("10.98"), null, m("7.5"), m("8.6")), "[-4.500000, null, null, 10.980000, null, 7.500000, 8.600000]");
        argsList.add(Linq.of(null, null, null, null, null, m("0")), "[null, null, null, null, null, 0.000000]");
        argsList.add(Linq.of(m("6.4"), null, null, MIN_DECIMAL, m("9.4"), MIN_DECIMAL, m("10.9"), MIN_DECIMAL), "[6.400000, null, null, -999999999999999999999999999999.000000, 9.400000, -999999999999999999999999999999.000000, 10.900000, -999999999999999999999999999999.000000]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_DateTime_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> newDate(2000, 1, i)).toArray(), "[Sat Jan 01 00:00:00 CST 2000, Sun Jan 02 00:00:00 CST 2000, Mon Jan 03 00:00:00 CST 2000, Tue Jan 04 00:00:00 CST 2000, Wed Jan 05 00:00:00 CST 2000, Thu Jan 06 00:00:00 CST 2000, Fri Jan 07 00:00:00 CST 2000, Sat Jan 08 00:00:00 CST 2000, Sun Jan 09 00:00:00 CST 2000, Mon Jan 10 00:00:00 CST 2000]");
        argsList.add(Linq.of(newDate(2000, 12, 1), newDate(2000, 1, 1), newDate(2000, 1, 12)), "[Fri Dec 01 00:00:00 CST 2000, Sat Jan 01 00:00:00 CST 2000, Wed Jan 12 00:00:00 CST 2000]");

        Date[] hundred = new Date[]{
                newDate(3000, 1, 1),
                newDate(100, 1, 1),
                newDate(200, 1, 1),
                newDate(1000, 1, 1)
        };
        argsList.add(Linq.of(hundred), "[Wed Jan 01 00:00:00 CST 3000, Wed Jan 01 00:00:00 CST 100, Tue Jan 01 00:00:00 CST 200, Mon Jan 01 00:00:00 CST 1000]");
        argsList.add(Linq.of(hundred).concat(Linq.repeat(MIN_DATE, 1)), "[Wed Jan 01 00:00:00 CST 3000, Wed Jan 01 00:00:00 CST 100, Tue Jan 01 00:00:00 CST 200, Mon Jan 01 00:00:00 CST 1000, Sat Jan 01 00:00:00 CST 1]");
        return argsList;
    }

    private static IEnumerable<Object[]> Format_String_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(Linq.range(1, 10).select(i -> i.toString()).toArray(), "['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']");
        argsList.add(Linq.of("Alice", "Bob", "Charlie", "Eve", "Mallory", "Trent", "Victor"), "['Alice', 'Bob', 'Charlie', 'Eve', 'Mallory', 'Trent', 'Victor']");
        argsList.add(Linq.of(null, "Charlie", null, "Victor", "Trent", null, "Eve", "Alice", "Mallory", "Bob"), "[null, 'Charlie', null, 'Victor', 'Trent', null, 'Eve', 'Alice', 'Mallory', 'Bob']");

        argsList.add(Linq.<String>empty(), "[]");
        argsList.add(Linq.repeat("Hello", 1), "['Hello']");
        argsList.add(Linq.repeat("hi", 5), "['hi', 'hi', 'hi', 'hi', 'hi']");
        argsList.add(Linq.of("aaa", "abcd", "bark", "temp", "cat"), "['aaa', 'abcd', 'bark', 'temp', 'cat']");
        argsList.add(Linq.of(null, null, null, null, "aAa"), "[null, null, null, null, 'aAa']");
        argsList.add(Linq.of("ooo", "www", "www", "ooo", "ooo", "ppp"), "['ooo', 'www', 'www', 'ooo', 'ooo', 'ppp']");
        argsList.add(Linq.repeat(null, 5), "[null, null, null, null, null]");
        return argsList;
    }

    @Test
    void SameResultsRepeatCallsIntQuery() {
        IEnumerable<Integer> q = Linq.of(new int[]{9999, 0, 888, -1, 66, -777, 1, 2, -12345})
                .where(x -> x > Integer.MIN_VALUE);

        assertEquals(q.format(), q.format());
    }

    @Test
    void SameResultsRepeatCallsStringQuery() {
        IEnumerable<String> q = Linq.of("!@#$%^", "C", "AAA", "", "Calling Twice", "SoS", Empty)
                .where(x -> !IsNullOrEmpty(x));

        assertEquals(q.format(), q.format());
    }

    @ParameterizedTest
    @MethodSource("Format_Int_TestData")
    void Format_Int(IEnumerable<Integer> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_Int_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Integer>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Integer>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_Int_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<Integer>empty().format());
        assertEquals("[]", Linq.<Integer>empty().format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_Long_TestData")
    void Format_Long(IEnumerable<Long> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_Long_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Long>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Long>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_Long_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<Long>empty().format());
        assertEquals("[]", Linq.<Long>empty().format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_Float_TestData")
    void Format_Float(IEnumerable<Float> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_Float_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Float>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Float>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_Float_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<Float>empty().format());
        assertEquals("[]", Linq.<Float>empty().format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_Double_TestData")
    void Format_Double(IEnumerable<Double> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_Double_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Double>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Double>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_Double_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<Double>empty().format());
        assertEquals("[]", Linq.<Double>empty().format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_Decimal_TestData")
    void Format_Decimal(IEnumerable<BigDecimal> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_Decimal_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<BigDecimal>empty().format());
        assertEquals("[]", Linq.<BigDecimal>empty().format(Formatter.DEFAULT));
    }

    @Test
    void Format_Decimal_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<BigDecimal>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<BigDecimal>) null).format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_NullableInt_TestData")
    void Format_NullableInt(IEnumerable<Integer> source, String expected) {
        assertEquals(expected, source.format());
    }

    @ParameterizedTest
    @MethodSource("Format_NullableInt_TestData")
    void Format_NullableIntRunOnce(IEnumerable<Integer> source, String expected) {
        assertEquals(expected, source.runOnce().format());
    }

    @Test
    void Format_NullableInt_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Integer>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Integer>) null).format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_NullableLong_TestData")
    void Format_NullableLong(IEnumerable<Long> source, String expected) {
        assertEquals(expected, source.format());
    }

    @Test
    void Format_NullableLong_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Long>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Long>) null).format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_NullableFloat_TestData")
    void Format_NullableFloat(IEnumerable<Float> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_NullableFloat_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Float>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Float>) null).format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_NullableDouble_TestData")
    void Format_NullableDouble(IEnumerable<Double> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_NullableDouble_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Double>) null).format());
    }

    @ParameterizedTest
    @MethodSource("Format_NullableDecimal_TestData")
    void Format_NullableDecimal(IEnumerable<BigDecimal> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_NullableDecimal_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<BigDecimal>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<BigDecimal>) null).format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_DateTime_TestData")
    void Format_DateTime(IEnumerable<Date> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @Test
    void Format_DateTime_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Date>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<Date>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_DateTime_EmptySource_ThrowsInvalidOperationException() {
        assertEquals("[]", Linq.<Date>empty().format());
        assertEquals("[]", Linq.<Date>empty().format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_String_TestData")
    void Format_String(IEnumerable<String> source, String expected) {
        assertEquals(expected, source.format());
        assertEquals(expected, source.format(Formatter.DEFAULT));
    }

    @ParameterizedTest
    @MethodSource("Format_String_TestData")
    void Format_StringRunOnce(IEnumerable<String> source, String expected) {
        assertEquals(expected, source.runOnce().format());
        assertEquals(expected, source.runOnce().format(Formatter.DEFAULT));
    }

    @Test
    void Format_String_NullSource_ThrowsArgumentNullException() {
        assertThrows(NullPointerException.class, () -> ((IEnumerable<String>) null).format());
        assertThrows(NullPointerException.class, () -> ((IEnumerable<String>) null).format(Formatter.DEFAULT));
    }

    @Test
    void Format_Int_WithFormatterAccessingProperty() {
        NameNum<Integer>[] source = new NameNum[]{
                new NameNum("Tim", 10),
                new NameNum("John", -105),
                new NameNum("Bob", -30)
        };
        assertEquals("SelectArrayIterator[ 10,-105,-30 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_Int_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Integer>empty().format(formatter));
    }

    @Test
    void Format_Long_WithFormatterAccessingProperty() {
        NameNum<Long>[] source = new NameNum[]{
                new NameNum("Tim", 10L),
                new NameNum("John", Long.MIN_VALUE),
                new NameNum("Bob", -10L)
        };

        assertEquals("SelectArrayIterator[ 10,-9223372036854775808,-10 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_Long_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Long>empty().format(formatter));
    }

    @Test
    void Format_Float_WithFormatterAccessingProperty() {
        NameNum<Float>[] source = new NameNum[]{
                new NameNum("Tim", -45.5f),
                new NameNum("John", -132.5f),
                new NameNum("Bob", 20.45f)
        };
        assertEquals("SelectArrayIterator[ -45.5,-132.5,20.45 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_Float_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Float>empty().format(formatter));
    }

    @Test
    void Format_Double_WithFormatterAccessingProperty() {
        NameNum<Double>[] source = new NameNum[]{
                new NameNum("Tim", -45.5),
                new NameNum("John", -132.5),
                new NameNum("Bob", 20.45)
        };
        assertEquals("SelectArrayIterator[ -45.5,-132.5,20.45 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_Double_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Double>empty().format(formatter));
    }

    @Test
    void Format_Decimal_WithFormatterAccessingProperty() {
        NameNum<BigDecimal>[] source = new NameNum[]{
                new NameNum("Tim", m("100.45")),
                new NameNum("John", m("10.5")),
                new NameNum("Bob", m("0.05"))
        };
        assertEquals("SelectArrayIterator[ 100.45,10.5,0.05 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_Decimal_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<BigDecimal>empty().format(formatter));
    }

    @Test
    void Format_NullableInt_WithFormatterAccessingProperty() {
        NameNum<Integer>[] source = new NameNum[]{
                new NameNum("Tim", 10),
                new NameNum("John", null),
                new NameNum("Bob", -30)
        };
        assertEquals("SelectArrayIterator[ 10,NULL,-30 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_NullableInt_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Integer>empty().format(formatter));
    }

    @Test
    void Format_NullableLong_WithFormatterAccessingProperty() {
        NameNum<Long>[] source = new NameNum[]{
                new NameNum("Tim", null),
                new NameNum("John", Long.MIN_VALUE),
                new NameNum("Bob", -10L)
        };
        assertEquals("SelectArrayIterator[ NULL,-9223372036854775808,-10 ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_NullableLong_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Long>empty().format(formatter));
    }

    @Test
    void Format_NullableFloat_WithFormatterAccessingProperty() {
        NameNum<Float>[] source = new NameNum[]{
                new NameNum("Tim", -45.5f),
                new NameNum("John", -132.5f),
                new NameNum("Bob", null)
        };

        assertEquals("SelectArrayIterator[ -45.5,-132.5,NULL ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_NullableFloat_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Float>empty().format(formatter));
    }

    @Test
    void Format_NullableDouble_WithFormatterAccessingProperty() {
        NameNum<Double>[] source = new NameNum[]{
                new NameNum("Tim", -45.5d),
                new NameNum("John", -132.5d),
                new NameNum("Bob", null)
        };
        assertEquals("SelectArrayIterator[ -45.5,-132.5,NULL ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_NullableDouble_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Double>empty().format(formatter));
    }

    @Test
    void Format_NullableDecimal_WithFormatterAccessingProperty() {
        NameNum<BigDecimal>[] source = new NameNum[]{
                new NameNum("Tim", m("100.45")),
                new NameNum("John", m("10.5")),
                new NameNum("Bob", null)
        };
        assertEquals("SelectArrayIterator[ 100.45,10.5,NULL ]", Linq.of(source).select(e -> e.num).format(this.formatter));
    }

    @Test
    void Format_NullableDecimal_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<BigDecimal>empty().format(formatter));
    }

    @Test
    void Format_DateTime_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Date>empty().format(formatter));
    }

    @Test
    void Format_String_WithFormatterAccessingProperty() {
        NameNum<String>[] source = new NameNum[]{
                new NameNum("Tim", m("100.45")),
                new NameNum("John", m("10.5")),
                new NameNum("Bob", m("0.05"))
        };
        assertEquals("SelectArrayIterator[ \"Tim\",\"John\",\"Bob\" ]", Linq.of(source).select(e -> e.name).format(this.formatter));
    }

    @Test
    void Format_String_NullFormatter_ThrowsArgumentNullException() {
        Formatter formatter = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<String>empty().format(formatter));
    }

    @Test
    void Format_Bool_EmptySource_ThrowsInvalodOperationException() {
        assertEquals("[]", Linq.<Boolean>empty().format());
    }

    @Test
    void Overflow() {
        assertThrows(OutOfMemoryError.class, () -> Linq.repeat(Float.NaN, Integer.MAX_VALUE).format());
    }


    private static class NameNum<T> extends ValueType {
        private final String name;
        private final T num;

        private NameNum(String name, T num) {
            this.name = name;
            this.num = num;
        }
    }
}
