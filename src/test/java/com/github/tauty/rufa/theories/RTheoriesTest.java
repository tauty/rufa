/*
 * Copyright 2014 tetsuo.ohta[at]gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tauty.rufa.theories;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.github.tauty.rufa.common.util.CollectionUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * RTheories examples.
 * You can learn what RTheories is.
 */
@RunWith(Enclosed.class)
public class RTheoriesTest {

    public static class DataSourceArrayTest {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint(bindTo = {"foo", "bar", "baz"})
        Object[][] objsList = {
                {"100", "200", 300},
                {"5", "8", 13},
                {"02001", "1999", 4000}
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DataSourceArrayTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint(bindTo = {"foo", "bar", "baz"})
        Object[][] objsList = {
                {"100", "200", 300},
                {"5", "8", 13},
                {"02001", "1999", 4000}
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "objsList")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DataSourceTest {

        private static class S {
            String foo;
            String bar;
            int baz;

            S(String foo, String bar, int baz) {
                this.foo = foo;
                this.bar = bar;
                this.baz = baz;
            }
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint
        S[] ses = {
                new S("100", "1", 101),
                new S("49", "51", 100),
                new S("33", "66", 99)
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DataSourceTest_Target {

        private static class S {
            String foo;
            String bar;
            int baz;

            S(String foo, String bar, int baz) {
                this.foo = foo;
                this.bar = bar;
                this.baz = baz;
            }
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        S[] ses = {
                new S("100", "1", 101),
                new S("49", "51", 100),
                new S("33", "66", 99)
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "ses")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DatapointTo1 {

        private static class S {
            String foo;
            String bar;
            int baz;

            S(String foo, String bar, int baz) {
                this.foo = foo;
                this.bar = bar;
                this.baz = baz;
            }
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint(bindTo = "s")
        S[] ses = {
                new S("4", "2", 6),
                new S("8", "10", 18),
                new S("0", "0", 0)
        };
        // @formatter:on

        S s;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(s.foo) + Integer.parseInt(s.bar);
            assertThat(result, is(s.baz));
        }
    }

    public static class DataSourceMapTest {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint
        Map[] ses = {
                $("foo", "100").$("bar", "200").$("baz", 300),
                $("foo", "1").$("bar", "2").$("baz", 3),
                $("foo", "999").$("bar", "1").$("baz", 1000)
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DataSourceMapTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        Map[] ses = {
                $("foo", "100").$("bar", "200").$("baz", 300),
                $("foo", "1").$("bar", "2").$("baz", 3),
                $("foo", "999").$("bar", "1").$("baz", 1000)
        };
        // @formatter:on

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "ses")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class Method_DataSourceArrayTest {
        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint(bindTo = {"foo", "bar", "baz"})
        Object[][] genAry() {
            // @formatter:off
            Object[][] objsList = {
                    {"1928", "3072", 5000},
                    {"42", "523", 565},
                    {"1975", "1976", 3951},
                    {"87", "822", 909}
            };
            // @formatter:on
            return objsList;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class Method_DataSourceMapTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        Map[] genSes() {
            // @formatter:off
            Map[] ses = {
                    $("foo", "123").$("bar", "321").$("baz", 444),
                    $("foo", "3").$("bar", "9").$("baz", 12),
                    $("foo", "1856").$("bar", "1919").$("baz", 3775),
                    $("foo", "9999").$("bar", "1").$("baz", 10000)
            };
            // @formatter:on
            return ses;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "genSes")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class StaticMethod_DataSourceMapTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        static Map[] genSes() {
            // @formatter:off
            Map[] ses = {
                    $("foo", "123").$("bar", "321").$("baz", 444),
                    $("foo", "3").$("bar", "9").$("baz", 12),
                    $("foo", "1856").$("bar", "1919").$("baz", 3775),
                    $("foo", "9999").$("bar", "1").$("baz", 10000)
            };
            // @formatter:on
            return ses;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "genSes")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DescMethod_DataSourceMapTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        Map[] genSes(Description desc) {
            System.out.println(desc.getDisplayName());
            // @formatter:off
            Map[] ses = {
                    $("foo", "123").$("bar", "321").$("baz", 444),
                    $("foo", "3").$("bar", "9").$("baz", 12),
                    $("foo", "1856").$("bar", "1919").$("baz", 3775),
                    $("foo", "9999").$("bar", "1").$("baz", 10000)
            };
            // @formatter:on
            return ses;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = "genSes")
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DescVoidMethod_DataSourceMapTest {

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint
        void doNothing(Description desc) {
            System.out.println(desc.getDisplayName());
        }

        @RDataPoint
        Map[] genSes() {
            // @formatter:off
            Map[] ses = {
                    $("foo", "123").$("bar", "321").$("baz", 444),
                    $("foo", "3").$("bar", "9").$("baz", 12),
                    $("foo", "1856").$("bar", "1919").$("baz", 3775),
                    $("foo", "9999").$("bar", "1").$("baz", 10000)
            };
            // @formatter:on
            return ses;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class DescVoidMethod_DataSourceMapTest_Target {

        @Rule
        public RTheories rt = new RTheories(this, true);

        void doNothing(Description desc) {
            System.out.println(desc.getDisplayName());
        }

        Map[] genSes() {
            // @formatter:off
            Map[] ses = {
                    $("foo", "123").$("bar", "321").$("baz", 444),
                    $("foo", "3").$("bar", "9").$("baz", 12),
                    $("foo", "1856").$("bar", "1919").$("baz", 3775),
                    $("foo", "9999").$("bar", "1").$("baz", 10000)
            };
            // @formatter:on
            return ses;
        }

        String foo;
        String bar;
        int baz;

        @Test
        @RTheory(targets = {"genSes", "doNothing"})
        public void test() {
            int result = Integer.parseInt(foo) + Integer.parseInt(bar);
            assertThat(result, is(baz));
        }
    }

    public static class FromToTest {

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint(from = 1, to = 100, step = 2)
        int foo;

        @Test
        @RTheory
        public void test() {
            int result = foo % 2;
            assertThat(result, is(1));
        }
    }

    public static class FromToMinusTest {

        @Rule
        public RTheories rt = new RTheories(this);

        @RDataPoint(to = -3)
        int foo;

        @RDataPoint(from = -10, to = -20, step = 3)
        int bar;

        @Test
        @RTheory
        public void test() {
            int result = foo + bar;
            assertTrue(result < 0);
        }
    }

    public static class BoolEnumTest {

        private static enum E {
            A, B, C, D, E
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint
        boolean foo;

        @RDataPoint
        E bar;

        @Test
        @RTheory
        public void test() {
        }
    }

    public static class BoolEnumTest_Target {

        private static enum E {
            A, B, C, D, E
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        boolean foo;
        E bar;

        @Test
        @RTheory(targets = {"foo", "bar"})
        public void useTwo() {
        }

        @Test
        @RTheory(targets = {"bar"})
        public void useOne() {
        }
    }

    public static class ExceptionTest {

        private static enum E {
            A, B, C, D, E
        }

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint
        boolean foo;

        @RDataPoint
        E bar;

        @Test(expected = UnsupportedOperationException.class)
        @RTheory
        public void exceptionTest() {
            throw new UnsupportedOperationException("Woops!");
        }
    }
}
