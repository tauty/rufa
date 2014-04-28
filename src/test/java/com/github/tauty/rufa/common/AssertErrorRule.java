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
package com.github.tauty.rufa.common;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by tetsuo.uchiumi on 4/22/14.
 */
public class AssertErrorRule implements TestRule {
    @Override
    public Statement apply(final Statement stmt, final Description desc) {
        final ExpectAssertError annotation = desc.getAnnotation(ExpectAssertError.class);
        if(annotation == null) return stmt;
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable throwable;
                try {
                    stmt.evaluate();
                    throwable = null;
                } catch (Throwable t) {
                    throwable = t;
                }
            }
        };
    }
}
