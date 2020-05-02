package com.bestvike.linq.enumerable;

import com.bestvike.TestCase;
import com.bestvike.ValueType;
import com.bestvike.collections.generic.Comparer;
import com.bestvike.collections.generic.StringComparer;
import com.bestvike.function.Func1;
import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.IOrderedEnumerable;
import com.bestvike.linq.Linq;
import com.bestvike.linq.exception.ArgumentNullException;
import com.bestvike.linq.util.ArgsList;
import com.bestvike.tuple.Tuple;
import com.bestvike.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

/**
 * Created by 许崇雷 on 2019-05-24.
 */
class OrderByDescendingTest extends TestCase {
    private static IEnumerable<Object[]> SortsRandomizedEnumerableCorrectly_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(0);
        argsList.add(1);
        argsList.add(2);
        argsList.add(3);
        argsList.add(8);
        argsList.add(16);
        argsList.add(1024);
        argsList.add(4096);
        argsList.add(1_000_000);
        return argsList;
    }

    @Test
    void SameResultsRepeatCallsIntQuery() {
        IEnumerable<Tuple2<Integer, Integer>> q = Linq.of(new int[]{1, 6, 0, -1, 3})
                .selectMany(x1 -> Linq.of(new int[]{55, 49, 9, -100, 24, 25}), (x1, x2) -> Tuple.create(x1, x2));

        assertEquals(q.orderByDescending(e -> e.getItem1()), q.orderByDescending(e -> e.getItem1()));
    }

    @Test
    void SameResultsRepeatCallsStringQuery() {
        IEnumerable<Tuple2<Integer, String>> q = Linq.of(new int[]{55, 49, 9, -100, 24, 25, -1, 0})
                .selectMany(x1 -> Linq.of("!@#$%^", "C", "AAA", "", null, "Calling Twice", "SoS", Empty), (x1, x2) -> Tuple.create(x1, x2))
                .where(t -> !IsNullOrEmpty(t.getItem2()));

        assertEquals(q.orderByDescending(e -> e.getItem1()).thenBy(f -> f.getItem2()), q.orderByDescending(e -> e.getItem1()).thenBy(f -> f.getItem2()));
    }

    @Test
    void SourceEmpty() {
        int[] source = {};
        assertEmpty(Linq.of(source).orderByDescending(e -> e));
    }

    @Test
    void KeySelectorReturnsNull() {
        Integer[] source = {null, null, null};
        Integer[] expected = {null, null, null};

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e));
    }

    @Test
    void ElementsAllSameKey() {
        Integer[] source = {9, 9, 9, 9, 9, 9};
        Integer[] expected = {9, 9, 9, 9, 9, 9};

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e));
    }

    @Test
    void KeySelectorCalled() {
        NameScore[] source = new NameScore[]{
                new NameScore("Alpha", 90),
                new NameScore("Robert", 45),
                new NameScore("Prakash", 99),
                new NameScore("Bob", 0)
        };
        NameScore[] expected = new NameScore[]{
                new NameScore("Robert", 45),
                new NameScore("Prakash", 99),
                new NameScore("Bob", 0),
                new NameScore("Alpha", 90)
        };

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e.Name, null));
    }

    @Test
    void FirstAndLastAreDuplicatesCustomComparer() {
        String[] source = {"Prakash", "Alpha", "DAN", "dan", "Prakash"};
        String[] expected = {"Prakash", "Prakash", "DAN", "dan", "Alpha"};

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e, StringComparer.OrdinalIgnoreCase));
    }

    @Test
    void RunOnce() {
        String[] source = {"Prakash", "Alpha", "DAN", "dan", "Prakash"};
        String[] expected = {"Prakash", "Prakash", "DAN", "dan", "Alpha"};

        assertEquals(Linq.of(expected), Linq.of(source).runOnce().orderByDescending(e -> e, StringComparer.OrdinalIgnoreCase));
    }

    @Test
    void FirstAndLastAreDuplicatesNullPassedAsComparer() {
        int[] source = {5, 1, 3, 2, 5};
        int[] expected = {5, 5, 3, 2, 1};

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e, null));
    }

    @Test
    void SourceReverseOfResultNullPassedAsComparer() {
        int[] source = {-75, -50, 0, 5, 9, 30, 100};
        int[] expected = {100, 30, 9, 5, 0, -50, -75};

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e, null));
    }

    @Test
    void SameKeysVerifySortStable() {
        NameScore[] source = new NameScore[]{
                new NameScore("Alpha", 90),
                new NameScore("Robert", 45),
                new NameScore("Prakash", 99),
                new NameScore("Bob", 90),
                new NameScore("Thomas", 45),
                new NameScore("Tim", 45),
                new NameScore("Mark", 45),
        };
        NameScore[] expected = new NameScore[]{
                new NameScore("Prakash", 99),
                new NameScore("Alpha", 90),
                new NameScore("Bob", 90),
                new NameScore("Robert", 45),
                new NameScore("Thomas", 45),
                new NameScore("Tim", 45),
                new NameScore("Mark", 45),
        };

        assertEquals(Linq.of(expected), Linq.of(source).orderByDescending(e -> e.Score));
    }

    @Test
    void OrderByExtremeComparer() {
        int[] outOfOrder = new int[]{7, 1, 0, 9, 3, 5, 4, 2, 8, 6};

        // The .NET Framework has a bug where the input is incorrectly ordered if the comparer
        // returns int.MaxValue or int.MinValue. See https://github.com/dotnet/corefx/pull/2240.
        IEnumerable<Integer> ordered = Linq.of(outOfOrder).orderByDescending(i -> i, new ExtremeComparer()).toArray();
        assertEquals(Linq.range(0, 10).reverse(), ordered);
    }

    @Test
    void NullSource() {
        IEnumerable<Integer> source = null;
        assertThrows(NullPointerException.class, () -> source.orderByDescending(i -> i));
    }

    @Test
    void NullKeySelector() {
        Func1<Date, Integer> keySelector = null;
        assertThrows(ArgumentNullException.class, () -> Linq.<Date>empty().orderByDescending(keySelector));
    }

    @Test
    void SortsLargeAscendingEnumerableCorrectly() {
        final int Items = 1_000_000;
        IEnumerable<Integer> expected = NumberRangeGuaranteedNotCollectionType(0, Items);

        IEnumerable<Integer> unordered = expected.select(i -> i);
        IOrderedEnumerable<Integer> ordered = unordered.orderByDescending(i -> -i);

        assertEquals(expected, ordered);
    }

    @Test
    void SortsLargeDescendingEnumerableCorrectly() {
        final int Items = 1_000_000;
        IEnumerable<Integer> expected = NumberRangeGuaranteedNotCollectionType(0, Items);

        IEnumerable<Integer> unordered = expected.select(i -> Items - i - 1);
        IOrderedEnumerable<Integer> ordered = unordered.orderByDescending(i -> -i);

        assertEquals(expected, ordered);
    }

    @ParameterizedTest
    @MethodSource("SortsRandomizedEnumerableCorrectly_TestData")
    void SortsRandomizedEnumerableCorrectly(int items) {
        Random r = new Random(42);
        Integer[] randomized = Linq.range(0, items).select(i -> r.nextInt()).toArray(Integer.class);
        Integer[] ordered = ForceNotCollection(Linq.of(randomized)).orderByDescending(i -> -i).toArray(Integer.class);

        Arrays.sort(randomized, Comparator.comparingInt(a -> a));
        assertEquals(Linq.of(randomized), Linq.of(ordered));
    }

    @Test
    void testOrderByDesc() {
        //null 在后,值相等的按原始顺序,同 testOrderByWithComparer()
        String s = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Cedric, Bill, Fred, Eric, Janet, Gates]", s);

        String ss = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno)
                .thenBy(emp -> emp.name)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Cedric, Bill, Eric, Fred, Janet, Gates]", ss);

        String sss = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno)
                .thenByDescending(emp -> emp.name)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Cedric, Bill, Janet, Fred, Eric, Gates]", sss);
    }

    @Test
    void testOrderByDescWithComparer() {
        //null 在后,值相等的按原始顺序
        Comparator<Object> reverse = Comparer.Default().reversed();

        String s = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno, reverse)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Gates, Fred, Eric, Janet, Bill, Cedric]", s);

        String ss = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno, reverse)
                .thenBy(emp -> emp.name, reverse)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Gates, Janet, Fred, Eric, Bill, Cedric]", ss);

        String sss = Linq.of(emps).concat(Linq.of(badEmps))
                .orderByDescending(emp -> emp.deptno, reverse)
                .thenByDescending(emp -> emp.name, reverse)
                .select(emp -> emp.name)
                .toList()
                .toString();
        assertEquals("[Gates, Eric, Fred, Janet, Bill, Cedric]", sss);
    }


    private static class NameScore extends ValueType {
        private final String Name;
        private final int Score;

        private NameScore(String name, int score) {
            this.Name = name;
            this.Score = score;
        }
    }

    private static class ExtremeComparer implements Comparator<Integer> {
        @Override
        public int compare(Integer x, Integer y) {
            if (x == y)
                return 0;
            if (x < y)
                return Integer.MIN_VALUE;
            return Integer.MAX_VALUE;
        }
    }
}
