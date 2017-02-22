package com.github.tauty.rufa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tauty.rufa.common.exception.IORuntimeException;
import com.github.tauty.rufa.common.util.CollectionUtil.ChainMap;
import com.github.tauty.rufa.common.util.Const;
import junit.framework.AssertionFailedError;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.util.*;

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

    public Rufa(Object testInstance) {
        this.testInstance = testInstance;
    }

    public Statement apply(Statement statement, Description description) {
        this.stat = statement;
        this.desc = description;
        // test definition file
        this.file = new File("./src/test/resources/"
                + desc.getClassName().replace('.', '/') + "/" + desc.getMethodName() + ".yml");

        System.out.println("About to return Statement");
        if (this.file.exists()) {
            // compare mode
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    System.out.println("compare mode start");
                    stat.evaluate();
                    System.out.println("compare mode end");
                }
            };
        } else {
            // study mode
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    System.out.println("study mode start");
                    stat.evaluate();
                    System.out.println("study mode end");
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
                if(!defMap.isEmpty()) throw new AssertionFailedError(defMap.toString());

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
            defMap = compareLinkedMap(expMap, actMap, path, defMap);
        } else {
            // @IgnoreOrder
            defMap.$(path, "@IgnoreOrder");
            defMap = compareMap(expMap, actMap, path, defMap);
        }
        return defMap;
    }

    private ChainMap<String, String> compareLinkedMap(Map expMap, Map actMap, String path, ChainMap<String, String> defMap) {
        Iterator<Map.Entry> expItr = expMap.entrySet().iterator();
        Iterator<Map.Entry> actItr = actMap.entrySet().iterator();

        while (expItr.hasNext() && actItr.hasNext()) {
            Map.Entry exp = expItr.next();
            Map.Entry act = actItr.next();
            if (!equalsSafely(exp.getKey(), act.getKey())){
                if (expMap.containsKey(act)) {
                    while (!equalsSafely(act, exp = expItr.next())) {
                        defMap.$(genPath(path, exp.getKey()), "@KeyNullable");
                    }
                } else if(actMap.containsKey(exp)) {
                    while (!equalsSafely(exp, act = actItr.next())) {
                        defMap.$(genPath(path, act.getKey()), "@KeyNullable");
                    }
                } else {
                    defMap.$(genPath(path, exp.getKey()), "@KeyNullable");
                    defMap.$(genPath(path, act.getKey()), "@KeyNullable");
                    continue;
                }
            }
            defMap = compare(exp.getValue(), act.getValue(), genPath(path, exp.getKey()), defMap);
        }
        while (expItr.hasNext()) {
            defMap.$(genPath(path, expItr.next().getKey()), "@KeyNullable");
        }
        while (actItr.hasNext()) {
            defMap.$(genPath(path, actItr.next().getKey()), "@KeyNullable");
        }
        return defMap;
    }

    private ChainMap<String, String> compareMap(Map<?, ?> expMap, Map<?, ?> actMap, String path, ChainMap<String, String> defMap) {
        for (Map.Entry<?, ?> exp : expMap.entrySet()) {
            if (!actMap.containsKey(exp.getKey())){
                defMap.$(genPath(path, exp.getKey()), "@KeyNullable");
                continue;
            }
            defMap = compare(exp.getValue(), actMap.get(exp.getKey()), genPath(path, exp.getKey()), defMap);
        }
        for (Map.Entry<?, ?> act : actMap.entrySet()) {
            if(!expMap.containsKey(act.getKey())) {
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

        for (int i = 0; i < Math.min(expList.size(), actList.size()) ; i++) {
            defMap = compare(expList.get(i), actList.get(i), genPath(path, i), defMap);
        }
        return defMap;
    }

    private ChainMap<String, String> compareAtom(Object exp, Object act, String path, ChainMap<String, String> defMap) {
        if(equalsSafely(exp, act)) return defMap;
        return defMap.$(path, "@Difference");
    }

    boolean isSameOrder(Set expKeys, Set actKeys) {
        Iterator expItr = expKeys.iterator();
        Iterator actItr = actKeys.iterator();

        while (expItr.hasNext() && actItr.hasNext()) {
            Object exp = expItr.next();
            Object act = actItr.next();
            if (equalsSafely(exp, act)) continue;
            if (expKeys.contains(act) && actKeys.contains(exp)) return false;
            if (expKeys.contains(act)) {
                while (!equalsSafely(act, exp = expItr.next())) {
                    if (actKeys.contains(exp)) return false;
                }
            } else if(actKeys.contains(exp)) {
                while (!equalsSafely(exp, act = actItr.next())) {
                    if (expKeys.contains(act)) return false;
                }
            }
        }
        return true;
    }

    private String genPath(String path, Object key) {
        return path + '~' + key;
    }

}
