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

import com.github.tauty.rufa.common.exception.Errors;
import com.github.tauty.rufa.common.exception.FieldNotFoundException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.github.tauty.rufa.common.util.CollectionUtil.*;
import static com.github.tauty.rufa.common.util.ReflectionUtil.*;

/**
 * Created by tetsuo.uchiumi on 3/27/14.
 */
public class RTheories implements TestRule {

    private final Object testInstance;
    private final boolean isVerbose;
    private Errors errors;
    private Statement stmt;
    private Description desc;

    public RTheories(Object testInstance) {
        this(testInstance, false);
    }

    public RTheories(Object testInstance, boolean isVerbose) {
        this.testInstance = testInstance;
        this.isVerbose = isVerbose;
    }

    @Override
    public Statement apply(final Statement stmt, final Description desc) {
        this.stmt = stmt;
        this.desc = desc;
        this.errors = new Errors("There are failure patterns:" + CRLF
                + "[" + desc.getDisplayName() + "]");
        final RTheory theory = desc.getAnnotation(RTheory.class);
        if (theory == null) return stmt;
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                tourToEvaluate(createDataPointsList(theory));
                errors.checkFailure();
            }
        };
    }

    private List<DataPoints> createDataPointsList(RTheory theory) {
        List<DataPoints> dataPointsList = new ArrayList<DataPoints>();
        if (theory.targets().length == 0) {
            for (AccessibleObject member : getAllMembers(testInstance)) {
                RDataPoint dp = member.getAnnotation(RDataPoint.class);
                if (dp != null) {
                    dataPointsList.add(createDataPoints(member, dp));
                }
            }
        } else {
            for (String s : theory.targets()) {
                for (AccessibleObject member : getMembers(testInstance, s)) {
                    dataPointsList.add(createDataPoints(member));
                }
            }
        }
        return dataPointsList;
    }

    private DataPoints createDataPoints(AccessibleObject member) {
        return createDataPoints(member, member.getAnnotation(RDataPoint.class));
    }

    private DataPoints createDataPoints(AccessibleObject member, RDataPoint dp) {
        if (member instanceof Field) {
            return new FieldDataPoints((Field) member, dp);
        } else {
            return new MethodDataPoints((Method) member, dp);
        }
    }

    private void tourToEvaluate(List<DataPoints> dataPointsList) {
        tourToEvaluate(dataPointsList, 0, "");
    }

    private void tourToEvaluate(List<DataPoints> dataPointsList, final int index, String testCaseName) {
        if (index >= dataPointsList.size()) {
            evaluate(testCaseName);
            return;
        }
        DataPoints dataPoints = dataPointsList.get(index);
        for (Map<?, ?> map : dataPoints) {
            StringBuilder sb = new StringBuilder();
            if (map != null) {
                sb.append("{");
                boolean isUpdated = false;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Field f = getField(testInstance, String.valueOf(entry.getKey()));
                    if (f == null) continue;
                    set(testInstance, f, entry.getValue());
                    if (isUpdated) {
                        sb.append(",");
                    }
                    isUpdated = true;
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
                if (!isUpdated) throw new FieldNotFoundException(msg(testInstance, map.keySet()));
                sb.append("}");
            }
            tourToEvaluate(dataPointsList, index + 1, testCaseName + sb);
        }
    }

    private void evaluate(String testCaseName) {
        Title title = new Title(testCaseName);
        try {
            stmt.evaluate();
            println(title);
        } catch (Throwable t) {
            this.errors.addErrors(title.toString(), t);
        }
    }

    private String msg(Object testInstance, Set<?> nameSet) {
        StringBuilder sb = new StringBuilder(testInstance.getClass().getName())
                .append(" has none of the fields, ");
        for (Object o : nameSet) {
            sb.append(o).append(" ");
        }
        return sb.append(".").toString();
    }

    private class Title {
        private final String testCaseName;
        private final long start;

        Title(String testCaseName) {
            this.testCaseName = testCaseName;
            this.start = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(desc.getDisplayName()).append(" - ");
            sb.append(testCaseName).append(": ");
            sb.append(System.currentTimeMillis() - start).append("ms");
            return sb.toString();
        }
    }

    private void println(Object... msgs) {
        if (!isVerbose) return;
        StringBuilder sb = new StringBuilder();
        for (Object msg : msgs) {
            sb.append(msg);
        }
        System.out.print(sb.append(CRLF));
    }

    private abstract class DataPoints implements Iterable<Map<?, ?>> {

        final String name;
        final RDataPoint dp;
        final Object data;

        DataPoints(String name, RDataPoint dp, Object data) {
            this.name = name;
            this.dp = dp;
            this.data = data;
        }

        @Override
        public Iterator<Map<?, ?>> iterator() {
            final Iterator<?> dataIterator = toIterator(data);
            if (dataIterator != null) {
                return new ImmutableIterator<Map<?, ?>>() {
                    @Override
                    public boolean hasNext() {
                        return dataIterator.hasNext();
                    }

                    @Override
                    public Map<?, ?> next() {
                        return genCurMap(dataIterator.next());
                    }
                };
            }
            Iterator<Map<?, ?>> ite = this.getAdditionalIterator();
            if (ite != null) return ite;
            throw new IllegalArgumentException("The member, " + name
                    + ", is not followed the specification of 'RTheory'."
                    + CRLF + " - " + desc.getDisplayName());
        }

        protected abstract Iterator<Map<?, ?>> getAdditionalIterator();

        private Map<?, ?> genCurMap(Object data) {
            // single value
            if (dp != null && dp.bindTo().length == 1 && getFieldExactly(testInstance, dp.bindTo()[0])
                    .getType().isAssignableFrom(data.getClass())) {
                return $(dp.bindTo()[0], data);
            }

            // map
            if (data instanceof Map) {
                return (Map<?, ?>) data;
            }

            // Iterable or Array
            Iterator<?> ite = toIterator(data);
            if (ite != null) {
                if (dp == null || dp.bindTo().length == 0) throw new IllegalArgumentException(missingBindToMsg());
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (String fName : dp.bindTo()) {
                    if (!ite.hasNext()) break;
                    map.put(fName, ite.next());
                }
                return map;
            }

            // Object
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            for (Field f : getFields(data)) {
                map.put(f.getName(), get(data, f));
            }
            return map;
        }

        private String missingBindToMsg() {
            return new StringBuilder(RDataPoint.class.getSimpleName())
                    .append(".bindTo attribute of '")
                    .append(name)
                    .append("' is required.").append(CRLF)
                    .append(" - ").append(desc.getDisplayName())
                    .toString();
        }
    }

    private class MethodDataPoints extends DataPoints {
        private final boolean isVoid;

        MethodDataPoints(Method dpMethod, RDataPoint dp) {
            super(dpMethod.getName(), dp, invokeWithDesc(dpMethod));
            this.isVoid = dpMethod.getReturnType() == void.class || dpMethod.getReturnType() == Void.class;
        }

        @Override
        public Iterator<Map<?, ?>> iterator() {
            if (isVoid) {
                return new ImmutableIterator<Map<?, ?>>() {
                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        return 0 == i++;
                    }

                    @Override
                    public Map<?, ?> next() {
                        return null;
                    }
                };
            }
            return super.iterator();
        }

        protected Iterator<Map<?, ?>> getAdditionalIterator() {
            return null;
        }
    }

    private Object invokeWithDesc(Method m) {
        Class<?>[] types = m.getParameterTypes();
        if (types.length == 0) {
            return invoke(testInstance, m);
        } else if (types.length == 1 && Description.class.isAssignableFrom(types[0])) {
            return invoke(testInstance, m, this.desc);
        }
        throw new IllegalArgumentException(m.getName()
                + " must have no parameter or a parameter of 'Description' only."
                + CRLF + " - " + desc.getDisplayName());
    }

    private class FieldDataPoints extends DataPoints {

        private final Field dpField;

        FieldDataPoints(Field dpField, RDataPoint dp) {
            super(dpField.getName(), dp, get(testInstance, dpField));
            this.dpField = dpField;
        }

        @Override
        protected Iterator<Map<?, ?>> getAdditionalIterator() {
            if (dpField.getType() == boolean.class || dpField.getType() == Boolean.class) {
                return genMapIterator(dpField.getName(), Boolean.TRUE, Boolean.FALSE);
            } else if (dpField.getType().isEnum()) {
                return genMapIterator(dpField.getName(), (Object[]) invokeStatic(dpField.getType(), "values"));
            } else if (dp != null && dp.to() != Integer.MIN_VALUE) {
                return new ImmutableIterator<Map<?, ?>>() {
                    int current = dp.from();
                    int STEP = 0 < (dp.to() - dp.from()) ? Math.abs(dp.step()) : -Math.abs(dp.step());

                    @Override
                    public boolean hasNext() {
                        return STEP > 0 ? current <= dp.to() : current >= dp.to();
                    }

                    @Override
                    public Map<?, ?> next() {
                        Map<?, ?> map = $(dpField.getName(), current);
                        current += STEP;
                        return map;
                    }
                };
            }
            return null;
        }

        private Iterator<Map<?, ?>> genMapIterator(String key, Object... values) {
            ArrayList<Map<?, ?>> maps = new ArrayList<Map<?, ?>>();
            for (Object value : values) {
                maps.add($(key, value));
            }
            return maps.iterator();
        }
    }

}
