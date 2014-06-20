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
package com.github.tauty.rufa.common.exception;

import com.github.tauty.rufa.common.tuple.Pair;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import static com.github.tauty.rufa.common.tuple.Tuples.*;
import static com.github.tauty.rufa.common.util.CommonUtil.*;
import static com.github.tauty.rufa.common.util.CollectionUtil.*;

/**
 * AssertionError class which can contains multiple exceptions and errors.
 * The stack traces of them would be output when the 'stackTrace' method of
 * this class is called.
 *
 * @author tauty
 * @see com.github.tauty.rufa.rules.RErrorCollector
 * @see com.github.tauty.rufa.theories.RTheories
 */
public class Errors extends AssertionError {
    /***/
    private static final long serialVersionUID = 1L;
    private final List<Pair<String, Throwable>> errList = newList();

    public Errors(String msg, Throwable... causes) {
        super(msg);
        for (Throwable cause : causes) {
            addError(cause);
        }
    }

    public void addError(String title, Throwable t) {
        errList.add(pair(title, t));
    }

    public void addError(Throwable t) {
        String title = "";
        for (StackTraceElement e : t.getStackTrace()) {
            if (!containsOnStartsWith(e.getClassName(),
                    "org.junit.",
                    "org.hamcrest.",
                    "com.github.tauty.rufa.")) {
                title = e.getMethodName() + "(" + e.getLineNumber() + ")";
                break;
            }
        }
        addError(title, t);
    }

    public void checkFailure() throws Throwable {
        if (isFailure()) throw this;
    }

    public boolean isFailure() {
        return !errList.isEmpty();
    }

    public Throwable unwrapIfSingle() {
        return errList.size() != 1 ? this : errList.get(0)._2;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        for (Pair<String, Throwable> pair : errList) {
            s.println();
            s.print("*** ");
            s.print(pair._1);
            s.println(" ***");
            pair._2.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter w) {
        for (Pair<String, Throwable> pair : errList) {
            w.println();
            w.print("*** ");
            w.print(pair._1);
            w.println(" ***");
            pair._2.printStackTrace(w);
        }
    }
}
