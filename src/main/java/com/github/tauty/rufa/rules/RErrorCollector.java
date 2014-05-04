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
package com.github.tauty.rufa.rules;

import com.github.tauty.rufa.common.exception.Errors;
import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * A TestRule class which provides you 'checkThat' method which can be used with assertion method and exception.
 *
 * @see org.junit.rules.ErrorCollector
 * @author tauty
 */
public class RErrorCollector implements TestRule {
    private final Errors errs = new Errors("Multiple failures are detected:");

    @Override
    public Statement apply(final Statement stmt, Description desc) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    stmt.evaluate();
                } catch (Throwable t) {
                    errs.addError(t);
                }
                if (errs.isFailure()) throw errs.unwrapIfSingle();
            }
        };
    }

    public <T> void checkThat(T actual, Matcher m) {
        checkThat("", actual, m);
    }

    public <T> void checkThat(String msg, T actual, Matcher m) {
        try {
            assertThat(msg, actual, m);
        } catch (Throwable t) {
            addError(t);
        }
    }

    public void addError(Throwable t) {
        errs.addError(t);
    }

    public <T> T checkSucceeds(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable t) {
            addError(t);
            return null;
        }
    }
}
