package arrayDestructuring.tests;

import arrayDestructuring.ConvertException;
import arrayDestructuring.Converter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing
 */
public class ConverterTest {

    private String getPath(int testNumber) {
        //todo remove harcode path
        String path = "/home/tihonovcore/IdeaProjects/arrayDestructuring/src/arrayDestructuring/tests/input";
        return path + "/test" + testNumber + ".js";
    }

    private StringBuilder actual = new StringBuilder();

    private void setActual(int testNumber) throws ConvertException {
        actual = Converter.convert(getPath(testNumber));
    }

    private boolean contains(String value) {
        for (int i = 0; i < actual.length() - value.length(); i++) {
            if (actual.substring(i).startsWith(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsInOrder(String... values) {
        int current = 0;
        for (int i = 0; i < actual.length(); i++) {
            if (current == values.length) {
                return true;
            }

            if (actual.substring(i).startsWith(values[current])) {
                current++;
            }
        }
        return false;
    }

    @Test
    public void test1() {
        int testNumber = 1;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(contains("a + b + 2"));
            Assert.assertTrue(contains("var a"));
            Assert.assertTrue(contains("var b"));
            Assert.assertTrue(contains("var c"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }

    @Test
    public void test2() {
        int testNumber = 2;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(containsInOrder("var x = [1, 2, 3, 4, 5]", "var [y, xx, z] = x"));
            Assert.assertTrue(containsInOrder("var x = [1, 2, 3, 4, 5]", "y = x[1]"));
            Assert.assertTrue(contains("bbb()"));
            Assert.assertTrue(contains("ddd()"));
            Assert.assertTrue(contains("var x = [1, 2, 3, 4, 5]"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }

    @Test
    public void test3() {
        int testNumber = 3;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(contains("var [a, b] = arr"));
            Assert.assertTrue(contains("var [a, , b] = arr"));
            Assert.assertTrue(contains("var [b, , a] = arr"));
            Assert.assertTrue(contains("var [, a, b] = arr"));
            Assert.assertTrue(contains("var [, , a"));
            Assert.assertTrue(contains("var [, , b"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }

    @Test
    public void test4() {
        int testNumber = 4;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(containsInOrder("var arr1 = ", "var [a, b] = arr1"));
            Assert.assertTrue(containsInOrder("var arr2 = ", "var [, , c, d] = arr2"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }


    @Test
    public void test5() {
        int testNumber = 5;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(contains("var [a, , , d] = arr"));
            Assert.assertTrue(contains("var [b, c] = arr[1]"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }

    @Test
    public void test6() {
        int testNumber = 6;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);

            Assert.assertTrue(containsInOrder("var arr = [1, 2, 3];", "var [, aa, ba] = arr;"));
            Assert.assertTrue(containsInOrder("var arr = [1, 2, 3];", "var [, a, b] = arr;"));
            Assert.assertTrue(containsInOrder("var darr = [3, 2, 1];", "var [, _a, _b] = darr;"));
        } catch (ConvertException e) {
            Assert.fail("Error while testing: " + e.getMessage());
        }
        System.out.println("OK");
    }

    @Test(expected = ConvertException.class)
    public void test7() throws ConvertException {
        int testNumber = 7;

        System.out.print("Test " + testNumber + ": ");
        try {
            setActual(testNumber);
        } catch (ConvertException e) {
            System.out.println("OK");
            throw e;
        }
    }
}
