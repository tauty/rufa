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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Parameterized example.
 */
@RunWith(Parameterized.class)
public class ParameterizedTest {

    @Parameters(name = "{0} + {1} = {2}.")
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {"10", "20", 30},
                {"88", "12", 100},
                {"3291", "6708", 9999},
                {"55", "5500", 55555},
                {"21", "22", 42}
        });
    }

    private final String foo;
    private final String bar;
    private final int baz;

    public ParameterizedTest(String foo, String bar, int baz) {
        this.foo = foo;
        this.bar = bar;
        this.baz = baz;
    }

    @Test
    public void sample() {
        int actual = Integer.parseInt(foo) + Integer.parseInt(bar);
        int expected = baz;
        assertThat(actual, is(expected));
    }

}
