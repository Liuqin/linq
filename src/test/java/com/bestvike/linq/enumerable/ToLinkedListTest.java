package com.bestvike.linq.enumerable;

import com.bestvike.TestCase;
import com.bestvike.collections.generic.ICollection;
import com.bestvike.function.Action1;
import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.Linq;
import com.bestvike.linq.util.ArgsList;
import com.bestvike.tuple.Tuple;
import com.bestvike.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 许崇雷 on 2019-07-03.
 */
class ToLinkedListTest extends TestCase {
    private static <T> void RunToListOnAllCollectionTypes(T[] items, Action1<List<T>> validation) {
        validation.apply(Linq.of(items).toLinkedList());
        validation.apply(Linq.of(Arrays.asList(items)).toLinkedList());
        validation.apply(new TestEnumerable<>(items).toLinkedList());
        validation.apply(new TestReadOnlyCollection<>(items).toLinkedList());
        validation.apply(new TestCollection<>(items).toLinkedList());
    }

    private static IEnumerable<Object[]> ToList_ArrayWhereSelect_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(new int[]{}, new String[]{});
        argsList.add(new int[]{1}, new String[]{"1"});
        argsList.add(new int[]{1, 2, 3}, new String[]{"1", "2", "3"});
        return argsList;
    }

    private static IEnumerable<Object[]> ToList_ListWhereSelect_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(new int[]{}, new String[]{});
        argsList.add(new int[]{1}, new String[]{"1"});
        argsList.add(new int[]{1, 2, 3}, new String[]{"1", "2", "3"});
        return argsList;
    }

    private static IEnumerable<Object[]> ToList_IListWhereSelect_TestData() {
        ArgsList argsList = new ArgsList();
        argsList.add(new int[]{}, new String[]{});
        argsList.add(new int[]{1}, new String[]{"1"});
        argsList.add(new int[]{1, 2, 3}, new String[]{"1", "2", "3"});
        return argsList;
    }

    @Test
    void ToList_AlwaysCreateACopy() {
        List<Integer> sourceList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> resultList = Linq.of(sourceList).toLinkedList();

        assertNotSame(sourceList, resultList);
        assertEquals(sourceList, resultList);
    }

    @Test
    void ToList_WorkWithEmptyCollection() {
        RunToListOnAllCollectionTypes(new Integer[0], resultList -> {
            assertNotNull(resultList);
            assertEquals(0, resultList.size());
        });
    }

    @Test
    void ToList_ProduceCorrectList() {
        Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        RunToListOnAllCollectionTypes(sourceArray, resultList -> {
            assertEquals(sourceArray.length, resultList.size());
            assertEquals(Linq.of(sourceArray), Linq.of(resultList));
        });

        String[] sourceStringArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        RunToListOnAllCollectionTypes(sourceStringArray, resultStringList -> {
            assertEquals(sourceStringArray.length, resultStringList.size());
            for (int i = 0; i < sourceStringArray.length; i++)
                assertSame(sourceStringArray[i], resultStringList.get(i));
        });
    }

    @Test
    void RunOnce() {
        assertEquals(Linq.range(3, 9), Linq.of(Linq.range(3, 9).runOnce().toLinkedList()));
    }

    @Test
    void ToList_TouchCountWithICollection() {
        TestCollection<Integer> source = new TestCollection<>(new Integer[]{1, 2, 3, 4});
        List<Integer> resultList = source.toLinkedList();

        assertEquals(source, Linq.of(resultList));
        assertEquals(1, source.CountTouched);
    }

    @Test
    void ToList_ThrowArgumentNullExceptionWhenSourceIsNull() {
        IEnumerable<Integer> source = null;
        assertThrows(NullPointerException.class, () -> source.toLinkedList());
    }

    // Generally the optimal approach. Anything that breaks this should be confirmed as not harming performance.
    @Test
    void ToList_UseCopyToWithICollection() {
        TestCollection<Integer> source = new TestCollection<>(new Integer[]{1, 2, 3, 4});
        List<Integer> resultList = source.toLinkedList();

        assertEquals(source, Linq.of(resultList));
        // This is different from C#. C# call ICollection.copy in List.ctor(), but java call Collection.toArray() then copy the array in ArrayList.ctor().
        assertEquals(0, source.CopyToTouched);
        assertEquals(0, source.ToListTouched);
    }

    @ParameterizedTest
    @MethodSource("ToList_ArrayWhereSelect_TestData")
    void ToList_ArrayWhereSelect(int[] sourceIntegers, String[] convertedStrings) {
        List<Integer> sourceList = new ArrayList<>(Linq.of(sourceIntegers).toList());
        List<String> convertedList = new ArrayList<>(Linq.of(convertedStrings).toList());

        List<Integer> emptyIntegersList = new ArrayList<>();
        List<String> emptyStringsList = new ArrayList<>();

        assertEquals(convertedList, Linq.of(sourceIntegers).select(i -> i.toString()).toLinkedList());

        assertEquals(sourceList, Linq.of(sourceIntegers).where(i -> true).toLinkedList());
        assertEquals(emptyIntegersList, Linq.of(sourceIntegers).where(i -> false).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceIntegers).where(i -> true).select(i -> i.toString()).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceIntegers).where(i -> false).select(i -> i.toString()).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceIntegers).select(i -> i.toString()).where(s -> s != null).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceIntegers).select(i -> i.toString()).where(s -> s == null).toLinkedList());
    }

    @ParameterizedTest
    @MethodSource("ToList_ListWhereSelect_TestData")
    void ToList_ListWhereSelect(int[] sourceIntegers, String[] convertedStrings) {
        List<Integer> sourceList = new ArrayList<>(Linq.of(sourceIntegers).toList());
        List<String> convertedList = new ArrayList<>(Linq.of(convertedStrings).toList());

        List<Integer> emptyIntegersList = new ArrayList<>();
        List<String> emptyStringsList = new ArrayList<>();

        assertEquals(convertedList, Linq.of(sourceList).select(i -> i.toString()).toLinkedList());

        assertEquals(sourceList, Linq.of(sourceList).where(i -> true).toLinkedList());
        assertEquals(emptyIntegersList, Linq.of(sourceList).where(i -> false).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceList).where(i -> true).select(i -> i.toString()).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceList).where(i -> false).select(i -> i.toString()).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceList).select(i -> i.toString()).where(s -> s != null).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceList).select(i -> i.toString()).where(s -> s == null).toLinkedList());
    }

    @ParameterizedTest
    @MethodSource("ToList_IListWhereSelect_TestData")
    void ToList_IListWhereSelect(int[] sourceIntegers, String[] convertedStrings) {
        List<Integer> sourceList = Collections.unmodifiableList(Linq.of(sourceIntegers).toList());
        List<String> convertedList = Collections.unmodifiableList(Linq.of(convertedStrings).toList());

        List<Integer> emptyIntegersList = Collections.unmodifiableList(Linq.<Integer>empty().toLinkedList());
        List<String> emptyStringsList = Collections.unmodifiableList(Linq.<String>empty().toLinkedList());

        assertEquals(convertedList, Linq.of(sourceList).select(i -> i.toString()).toLinkedList());

        assertEquals(sourceList, Linq.of(sourceList).where(i -> true).toLinkedList());
        assertEquals(emptyIntegersList, Linq.of(sourceList).where(i -> false).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceList).where(i -> true).select(i -> i.toString()).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceList).where(i -> false).select(i -> i.toString()).toLinkedList());

        assertEquals(convertedList, Linq.of(sourceList).select(i -> i.toString()).where(s -> s != null).toLinkedList());
        assertEquals(emptyStringsList, Linq.of(sourceList).select(i -> i.toString()).where(s -> s == null).toLinkedList());
    }

    @Test
    void SameResultsRepeatCallsFromWhereOnIntQuery() {
        IEnumerable<Integer> q = Linq.of(new int[]{9999, 0, 888, -1, 66, -777, 1, 2, -12345}).where(x -> x > Integer.MIN_VALUE);

        assertEquals(q.toLinkedList(), q.toLinkedList());
    }

    @Test
    void SameResultsRepeatCallsFromWhereOnStringQuery() {
        IEnumerable<String> q = Linq.of("!@#$%^", "C", "AAA", "", "Calling Twice", "SoS", Empty).where(x -> !IsNullOrEmpty(x));

        assertEquals(q.toLinkedList(), q.toLinkedList());
    }

    @Test
    void SourceIsEmptyICollectionT() {
        int[] source = {};

        ICollection<Integer> collection = Linq.of(source).toArray();

        assertEmpty(Linq.of(Linq.of(source).toLinkedList()));
        assertEmpty(Linq.of(collection.toLinkedList()));
    }

    @Test
    void SourceIsICollectionTWithFewElements() {
        Integer[] source = {-5, null, 0, 10, 3, -1, null, 4, 9};
        Integer[] expected = {-5, null, 0, 10, 3, -1, null, 4, 9};

        ICollection<Integer> collection = Linq.of(source).toArray();

        assertEquals(Linq.of(expected), Linq.of(Linq.of(source).toLinkedList()));
        assertEquals(Linq.of(expected), Linq.of(collection.toLinkedList()));
    }

    @Test
    void SourceNotICollectionAndIsEmpty() {
        IEnumerable<Integer> source = NumberRangeGuaranteedNotCollectionType(-4, 0);
        assertEmpty(Linq.of(source.toLinkedList()));
    }

    @Test
    void SourceNotICollectionAndHasElements() {
        IEnumerable<Integer> source = NumberRangeGuaranteedNotCollectionType(-4, 10);
        int[] expected = {-4, -3, -2, -1, 0, 1, 2, 3, 4, 5};

        assertNull(as(source, ICollection.class));

        assertEquals(Linq.of(expected), Linq.of(source.toLinkedList()));
    }

    @Test
    void SourceNotICollectionAndAllNull() {
        IEnumerable<Integer> source = RepeatedNullableNumberGuaranteedNotCollectionType(null, 5);
        Integer[] expected = {null, null, null, null, null};

        assertNull(as(source, ICollection.class));

        assertEquals(Linq.of(expected), Linq.of(source.toLinkedList()));
    }

    @Test
    void ConstantTimeCountPartitionSelectSameTypeToList() {
        IEnumerable<Integer> source = Linq.range(0, 100).select(i -> i * 2).skip(1).take(5);
        assertEquals(Linq.of(new int[]{2, 4, 6, 8, 10}), Linq.of(source.toLinkedList()));
    }

    @Test
    void ConstantTimeCountPartitionSelectDiffTypeToList() {
        IEnumerable<String> source = Linq.range(0, 100).select(i -> i.toString()).skip(1).take(5);
        assertEquals(Linq.of("1", "2", "3", "4", "5"), Linq.of(source.toLinkedList()));
    }

    @Test
    void ConstantTimeCountEmptyPartitionSelectSameTypeToList() {
        IEnumerable<Integer> source = Linq.range(0, 100).select(i -> i * 2).skip(1000);
        assertEmpty(Linq.of(source.toLinkedList()));
    }

    @Test
    void ConstantTimeCountEmptyPartitionSelectDiffTypeToList() {
        IEnumerable<String> source = Linq.range(0, 100).select(i -> i.toString()).skip(1000);
        assertEmpty(Linq.of(source.toLinkedList()));
    }

    @Test
    void NonConstantTimeCountPartitionSelectSameTypeToList() {
        IEnumerable<Integer> source = NumberRangeGuaranteedNotCollectionType(0, 100).orderBy(i -> i).select(i -> i * 2).skip(1).take(5);
        assertEquals(Linq.of(new int[]{2, 4, 6, 8, 10}), Linq.of(source.toLinkedList()));
    }

    @Test
    void NonConstantTimeCountPartitionSelectDiffTypeToList() {
        IEnumerable<String> source = NumberRangeGuaranteedNotCollectionType(0, 100).orderBy(i -> i).select(i -> i.toString()).skip(1).take(5);
        assertEquals(Linq.of("1", "2", "3", "4", "5"), Linq.of(source.toLinkedList()));
    }

    @Test
    void NonConstantTimeCountEmptyPartitionSelectSameTypeToList() {
        IEnumerable<Integer> source = NumberRangeGuaranteedNotCollectionType(0, 100).orderBy(i -> i).select(i -> i * 2).skip(1000);
        assertEmpty(Linq.of(source.toLinkedList()));
    }

    @Test
    void NonConstantTimeCountEmptyPartitionSelectDiffTypeToList() {
        IEnumerable<String> source = NumberRangeGuaranteedNotCollectionType(0, 100).orderBy(i -> i).select(i -> i.toString()).skip(1000);
        assertEmpty(Linq.of(source.toLinkedList()));
    }

    @Test
    void testToListGrouping() {
        int[] element = {60, -10, 40, 100};
        Tuple2[] source = new Tuple2[]{
                Tuple.create("Tim", element[0]),
                Tuple.create("Tim", element[1]),
                Tuple.create("miT", element[2]),
                Tuple.create("miT", element[3])
        };

        assertEquals(Arrays.asList(99, 60, -10), Linq.of(source).toLookup(Tuple2::getItem1, Tuple2::getItem2).get("Tim").prepend(99).toLinkedList());
    }

    @Test
    void testToList() {
        Object[] source = {1, 2, 3};
        List<Integer> target = Linq.of(source).cast(Integer.class).toLinkedList();
        assertEquals(3, target.size());
        assertTrue(Linq.of(source).cast(Integer.class).sequenceEqual(Linq.of(target)));

        Set<Integer> source2 = new HashSet<>();
        source2.add(1);
        source2.add(2);
        source2.add(3);
        List<Integer> target2 = Linq.of(source2).toLinkedList();
        assertEquals(3, target2.size());
        assertTrue(Linq.of(target2).sequenceEqual(Linq.of(source2)));

        List<Character> lst = Linq.of(Arrays.asList('h', 'e', 'l', 'l', 'o')).toLinkedList();
        assertEquals(5, lst.size());
        assertEquals("o", lst.get(4).toString());

        Character[] arrChar = {'h', 'e', 'l', 'l', 'o'};
        List<Character> arr = Linq.of(arrChar).toLinkedList();
        assertEquals(5, arr.size());
        assertEquals("o", arr.get(4).toString());

        List<Character> hello = Linq.chars("hello").toLinkedList();
        assertEquals(5, hello.size());
        assertEquals("o", hello.get(4).toString());

        List<Character> h = Linq.singleton('h').toLinkedList();
        assertEquals(1, h.size());
        assertEquals("h", h.get(0).toString());

        List<Character> empty = Linq.<Character>empty().toLinkedList();
        assertEquals(0, empty.size());

        assertEquals(Arrays.asList(true, false, false), Linq.of(new boolean[]{true, false, false}).toLinkedList());
        assertEquals(Arrays.asList((byte) 1, (byte) 2, (byte) 3), Linq.of(new byte[]{1, 2, 3}).toLinkedList());
        assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3), Linq.of(new short[]{1, 2, 3}).toLinkedList());
        assertEquals(Arrays.asList(1, 2, 3), Linq.of(new int[]{1, 2, 3}).toLinkedList());
        assertEquals(Arrays.asList(1L, 2L, 3L), Linq.of(new long[]{1, 2, 3}).toLinkedList());
        assertEquals(Arrays.asList('a', 'b', 'c'), Linq.of(new char[]{'a', 'b', 'c'}).toLinkedList());
        assertEquals(Arrays.asList(1f, 2f, 3f), Linq.of(new float[]{1f, 2f, 3f}).toLinkedList());
        assertEquals(Arrays.asList(1d, 2d, 3d), Linq.of(new double[]{1d, 2d, 3d}).toLinkedList());
    }
}
