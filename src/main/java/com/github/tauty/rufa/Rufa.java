package com.github.tauty.rufa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tauty.rufa.common.exception.IORuntimeException;
import com.github.tauty.rufa.common.util.CollectionUtil.ChainMap;
import com.github.tauty.rufa.common.util.Const;
import com.github.tauty.rufa.common.util.PureIterator;
import junit.framework.AssertionFailedError;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tauty.rufa.common.util.CommonUtil.equalsSafely;

/**
 * Entry point of Rufa test environment.
 *
 * @author tauty
 */
public class Rufa implements TestRule {

    private final Object testInstance;
    private Statement stat;
    private Description desc;
    private File file;

    private enum Mode {STUDY_1st, STUDY_2nd, COMPARE}

    private Mode mode;
    private int assertionCount;
    private Map<String, Integer> assertionCountMap = new HashMap<String, Integer>();

    public Rufa(Object testInstance) {
        this.testInstance = testInstance;
    }

    private void init(Mode mode) {
        this.mode = mode;
        this.assertionCount = 0;
        this.assertionCountMap.clear();
    }

    public Statement apply(Statement statement, Description description) {
        this.stat = statement;
        this.desc = description;
        // test definition file
        this.file = new File("./src/test/resources/"
                + desc.getClassName().replace('.', '/') + "/" + desc.getMethodName() + ".yml");

        System.out.println("About to return Statement");
        if (!this.file.exists()) {
            // study mode
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    System.out.println("study mode start");
                    init(Mode.STUDY_1st);
                    stat.evaluate();
                    init(Mode.STUDY_2nd);
                    stat.evaluate();
                    System.out.println("study mode end");
                }
            };
        } else {
            // compare mode
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    System.out.println("compare mode start");
                    init(Mode.COMPARE);
                    stat.evaluate();
                    System.out.println("compare mode end");
                }
            };
        }
    }

    private static final String INDENT = "  ";

    public void tmpName_assertJSONAutomatically(String id, String definition, String json) {

        Object actual;
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        try {
            actual = mapper.readValue(json, Object.class);
            System.out.println("actual2:" + actual);
            System.out.println("actual2:" + mapper.writeValueAsString(actual));

            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual));
            System.out.println(prettyJSON(mapper, actual, INDENT + INDENT));


        } catch (IOException e) {
            throw new IORuntimeException(e);
        }


        try {
            if (this.file.exists()) {
                // compare mode
                BufferedReader reader = new BufferedReader(new FileReader(this.file));
//                for (String line; null != (line = reader.readLine()); ) {
//                    System.out.println(line);
//                }
                String line = reader.readLine();
                System.out.println(line);
                line = reader.readLine();
                System.out.println(line);
                line = reader.readLine();
                System.out.println(line);

                Object expected = mapper.readValue(line, Object.class);
                ChainMap<String, String> defMap = compare(expected, actual);
                System.out.println(defMap);
                if (!defMap.isEmpty()) throw new AssertionFailedError(defMap.toString());

            } else {
                File parent = this.file.getParentFile();
                parent.mkdirs();
                FileWriter writer = new FileWriter(this.file);
                writer.append(id).append(": ").append(Const.CRLF);
                writer.append(INDENT).append("data: |").append(Const.CRLF);
                writer.append(INDENT).append(INDENT).append(mapper.writeValueAsString(actual)).append(Const.CRLF);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String prettyJSON(ObjectMapper mapper, Object fromJsonObj, String INDENT) throws JsonProcessingException {
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fromJsonObj);
        StringBuilder sb = new StringBuilder();
        for (String line : s.split("\n")) {
            sb.append(INDENT).append(line).append('\n');
        }
        return sb.toString();
    }

    private ChainMap<String, String> compare(Object expected, Object actual) {
        return compare(expected, actual, "", new ChainMap<String, String>());
    }

    private ChainMap<String, String> compare(Object expected, Object actual, String path, ChainMap<String, String> defMap) {
        if (expected == null && actual == null) return defMap;
        if (expected == null || actual == null) return defMap.$(path, "@Nullable");
        if (expected.getClass() != actual.getClass()) return defMap.$(path, "@TypeCanBeDifferent");

        if (expected instanceof Map) return compare((Map) expected, (Map) actual, path, defMap);
        if (expected instanceof List) return compare((List) expected, (List) actual, path, defMap);
        return compareAtom(expected, actual, path, defMap);
    }


    private ChainMap<String, String> compare(Map expMap, Map actMap, String path, ChainMap<String, String> defMap) {
        if (isSameOrder(expMap.keySet(), actMap.keySet())) {
            return compareLinkedMap(expMap, actMap, path, defMap);
        } else {
            // @IgnoreOrder
            return compareMap(expMap, actMap, path, defMap.$(path, "@IgnoreOrder"));
        }
    }

    private interface Func<T> {
        T invoke();
    }

    private ChainMap<String, String> compareLinkedMap(final Map expMap, final Map actMap, final String path,
                                                      final ChainMap<String, String> defMap) {

        return new Func<ChainMap<String, String>>() {

            public ChainMap<String, String> invoke() {
                return comp(new PureIterator<Object>(expMap.keySet()), new PureIterator<Object>(actMap.keySet()), defMap);
            }

            ChainMap<String, String> comp(PureIterator<Object> expItr, PureIterator<Object> actItr
                    , ChainMap<String, String> defMap) {
                if (!expItr.hasNext() && !actItr.hasNext()) return defMap;
                if (!expItr.hasNext())
                    return comp(expItr, actItr.tail(), defMap.$(genPath(path, actItr.head()), "@KeyNullable"));
                if (!actItr.hasNext())
                    return comp(expItr.tail(), actItr, defMap.$(genPath(path, expItr.head()), "@KeyNullable"));
                if (equalsSafely(expItr.head(), actItr.head())) {
                    compare(expMap.get(expItr.head()), actMap.get(actItr.head()), path, defMap);
                    return comp(expItr.tail(), actItr.tail(), defMap);
                } else if (expItr.contains(actItr.head())) {
                    return comp(expItr.tail(), actItr, defMap.$(genPath(path, expItr.head()), "@KeyNullable"));
                } else if (actItr.contains(expItr.head())) {
                    return comp(expItr, actItr.tail(), defMap.$(genPath(path, actItr.head()), "@KeyNullable"));
                } else {
                    return comp(expItr.tail(), actItr.tail(), defMap
                            .$(genPath(path, expItr.head()), "@KeyNullable")
                            .$(genPath(path, actItr.head()), "@KeyNullable"));
                }
            }
        }.invoke();
    }

    private ChainMap<String, String> compareMap(Map<?, ?> expMap, Map<?, ?> actMap, String path, ChainMap<String, String> defMap) {
        for (Map.Entry<?, ?> exp : expMap.entrySet()) {
            if (!actMap.containsKey(exp.getKey())) {
                defMap.$(genPath(path, exp.getKey()), "@KeyNullable");
                continue;
            }
            defMap = compare(exp.getValue(), actMap.get(exp.getKey()), genPath(path, exp.getKey()), defMap);
        }
        for (Map.Entry<?, ?> act : actMap.entrySet()) {
            if (!expMap.containsKey(act.getKey())) {
                defMap.$(genPath(path, act.getKey()), "@KeyNullable");
            }
        }
        return defMap;
    }

    private ChainMap<String, String> compare(List<?> expList, List<?> actList, String path, ChainMap<String, String> defMap) {
        // TODO implement

        // same order mode
        // same values mode (@IgnoreOrder) -> only if the values are atoms
        //   -> generally, list in list is rare. so I can concentrate to consider map in list.
        //      and many of the case, unique key can be found top of the map. this can be useful.
        // same order but some values are missing (@IgnoreMissing)
        // different order and some values are missing (@IgnoreOrderAndMissing) -> only if the values are

        // It is difficult to solve the problem the order of the list can be changed each time.
        // So, let's skip now.

        for (int i = 0; i < Math.min(expList.size(), actList.size()); i++) {
            defMap = compare(expList.get(i), actList.get(i), genPath(path, i), defMap);
        }
        return defMap;
    }

    private ChainMap<String, String> compareAtom(Object exp, Object act, String path, ChainMap<String, String> defMap) {
        System.out.println(path + ": exp = " + exp + ", act = " + act);
        if (equalsSafely(exp, act)) return defMap;
        if (exp.getClass() == act.getClass()) {
            // today, timestamp, random, scale of BigDecimal
            if (isToday(exp, act)) return defMap.$(path, "@Today");
            if (isNow(exp, act)) return defMap.$(path, "@Now");
            if (isRandom(exp, act)) return defMap.$(path, "@Random");
        } else {
            // TODO 1 & 1.0, "1" & 1, "1.0" & 1.0, "true" & true. How about "null" and null?
        }
        return defMap.$(path, "@Difference");
    }

    private static boolean isToday(Object exp, Object act) {
        return isDate(exp) && isToday(act);
    }

    // ex 2008-08-07, 2013/08/22, 20170715
    private static final Pattern DAY_PTN = Pattern.compile("([0-9]{4})[-/]?([0-9]{2})[-/]?([0-9]{2})");

    private static boolean isToday(Object o) {
        Matcher m = DAY_PTN.matcher(String.valueOf(o));
        return m.matches() && isToday(m.group(1), m.group(2), m.group(3));
    }

    private static boolean isToday(String year, String month, String day) {
        // TODO 5min before matching
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == Integer.parseInt(year)
                && (calendar.get(Calendar.MONTH) + 1) == Integer.parseInt(month)
                && calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day);
    }

    private static boolean isDate(Object o) {
        Matcher m = DAY_PTN.matcher(String.valueOf(o));
        return m.matches() && isDate(m.group(1), m.group(2), m.group(3));
    }

    private static boolean isDate(String year, String month, String day) {
        return isBetween(year, 1970, 2100)
                && isBetween(month, 1, 12)
                && isBetween(day, 1, 31);
    }

    private static boolean isNow(Object exp, Object act) {
        return isDaytime(exp) && isNow(act);
    }

    private static final Pattern DAYTIME_PTN = Pattern.compile(
            "([0-9]{4})[-/]?([0-9]{2})[-/]?([0-9]{2})[- T]([0-9]{2}):?([0-9]{2}):?([0-9]{2})(?:\\.[0-9]{3}Z?)?");

    private static boolean isNow(Object o) {
        Matcher m = DAYTIME_PTN.matcher(String.valueOf(o));
        return m.matches() && isToday(m.group(1), m.group(2), m.group(3))
                && isNowTime(m.group(4), m.group(5), m.group(6));
    }

    private static boolean isDaytime(Object o) {
        Matcher m = DAYTIME_PTN.matcher(String.valueOf(o));
        return m.matches() && isDate(m.group(1), m.group(2), m.group(3))
                && isTime(m.group(4), m.group(5), m.group(6));
    }

    private static boolean isNowTime(String hour, String minute, String second) {
        // TODO 5min before matching
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(hour)
                && (calendar.get(Calendar.MINUTE)) == Integer.parseInt(minute)
                && isBetween(second, 0, 60);
        //  Note: second should be tested like above, because it should be valid value at least.
    }

    private static boolean isTime(String hour, String minute, String second) {
        // Note: There may be leap seconds.
        return isBetween(hour, 0, 23)
                && isBetween(minute, 0, 59)
                && isBetween(second, 0, 60);
    }

    private static boolean isRandom(Object exp, Object act) {
        return isRandom(String.valueOf(exp), String.valueOf(act));
    }

    private static boolean isRandom(String exp, String act) {
        if (exp.length() != act.length()) return false;
        return isRandom(exp) && isRandom(act);
    }

    private static final Pattern RANDOM_PTN = Pattern.compile("[0-9a-zA-Z+/=-]+");

    private static boolean isRandom(String s) {
        return isBetween(s.length(), 8, 128) && RANDOM_PTN.matcher(s).matches();
    }

    private static boolean isBetween(String numStr, int first, int last) {
        return isBetween(Integer.parseInt(numStr), first, last);
    }

    private static boolean isBetween(int num, int first, int last) {
        return first <= num && num <= last;
    }

    boolean isSameOrder(Set expKeys, Set actKeys) {
        return isSameOrder(new PureIterator(expKeys), new PureIterator(actKeys));
    }

    boolean isSameOrder(PureIterator expItr, PureIterator actItr) {
        if (!expItr.hasNext() || !actItr.hasNext()) return true;
        if (equalsSafely(expItr.head(), actItr.head())) return isSameOrder(expItr.tail(), actItr.tail());
        if (expItr.contains(actItr.head()) && actItr.contains(expItr.head())) return false;
        if (expItr.contains(actItr.head())) return isSameOrder(expItr.tail(), actItr);
        if (actItr.contains(expItr.head())) return isSameOrder(expItr, actItr.tail());
        return isSameOrder(expItr.tail(), actItr.tail());
    }

    private String genPath(String path, Object key) {
        return path + '~' + key;
    }

}
