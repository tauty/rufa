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
import org.junit.runner.RunWith;

import java.util.Map;

import static com.github.tauty.rufa.common.util.CollectionUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * RTheories examples of wrong use case.
 */
@RunWith(Enclosed.class)
public class RTheoriesFailTest {

    public static class DataSourceArrayTest_wrongExpected {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint(bindTo = {"foo", "bar", "baz"})
        Object[][] objsList = {
                {"100", "200", 301},
                {"5", "8", 14},
                {"02001", "1999", 4001}
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

    public static class DataSourceArrayTest_NoBindToAttr {

        @Rule
        public RTheories rt = new RTheories(this, true);

        // @formatter:off
        @RDataPoint
        Object[][] objsList = {
                {"100", "200", 301},
                {"5", "8", 14},
                {"02001", "1999", 4001}
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

    public static class DataSourceTest_lackTargetAndWrongExpected {

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
        int baz;

        @Test
        @RTheory
        public void test() {
            int result = Integer.parseInt(foo);
            assertThat(result, is(baz));
        }
    }

    public static class DataSourceTest_noTarget {

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

        @Test
        @RTheory
        public void test() {
        }
    }

    public static class DatapointTo1_noTarget {

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

        @Test
        @RTheory
        public void test() {
        }
    }

    public static class Method_DataSourceMap_WrongParameter {

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint
        Map[] genSes(String a) {
            // @formatter:off
            Map[] ses = {
                    $("foo", "100").$("bar", "200").$("baz", 300),
                    $("foo", "1").$("bar", "2").$("baz", 3),
                    $("foo", "999").$("bar", "1").$("baz", 1000)
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

    public static class FromToTest_MissingTo {

        @Rule
        public RTheories rt = new RTheories(this, true);

        @RDataPoint(from = 1, step = 2)
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
}
