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
package com.github.tauty.rufa.common.util;

import com.github.tauty.rufa.common.exception.FieldNotFoundException;
import com.github.tauty.rufa.common.exception.WrapException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.tauty.rufa.common.util.CollectionUtil.*;
import static com.github.tauty.rufa.common.util.ExceptionUtil.*;

/**
 * A utility class which enables you to use reflection and meta-programming easily.
 */
public class ReflectionUtil {

    public static String CRLF = System.getProperty("line.separator");

    public static Object invokeStatic(Class clazz, String methodName, Object... params) {
        Class[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = params[i].getClass();
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, classes);
            return ensureAccessible(method).invoke(null, params);
        } catch (Exception e) {
            throw wrapIfChecked(e);
        }
    }

    public static Object invoke(Object receiver, String methodName, Object... params) {
        Class[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = params[i].getClass();
        }
        try {
            Method method = receiver.getClass().getDeclaredMethod(methodName, classes);
            return ensureAccessible(method).invoke(receiver, params);
        } catch (Exception e) {
            throw wrapIfChecked(e);
        }
    }

    public static Object invoke(Object receiver, Method method, Object... params) {
        try {
            return ensureAccessible(method).invoke(receiver, params);
        } catch (Exception e) {
            throw wrapIfChecked(e);
        }
    }

    public static Field getField(Object receiver, String fName) {
        if (receiver == null) return null;
        return getField(receiver.getClass(), fName);
    }

    public static Field getField(Class clazz, String fName) {
        return $class(clazz).getField(fName);
    }

    public static Field getFieldExactly(Object receiver, String fName) {
        if (receiver == null) throw new NullPointerException("'receiver' is null.");
        return getFieldExactly(receiver.getClass(), fName);
    }

    public static Field getFieldExactly(Class clazz, String fName) {
        Field f = getField(clazz, fName);
        if (f == null) {
            throw new FieldNotFoundException(clazz.getName() + " does not have the field, '" + fName + "'.");
        }
        return f;
    }

    public static List<Field> getFields(Object receiver) {
        if (receiver == null) return null;
        return getFields(receiver.getClass());
    }

    public static List<Field> getFields(Class clazz) {
        return $class(clazz).getAllFields();
    }

    public static List<AccessibleObject> getMembers(Object receiver, String name) {
        if (receiver == null) return null;
        return getMembers(receiver.getClass(), name);
    }

    public static List<AccessibleObject> getMembers(Class clazz, String name) {
        return $class(clazz).getMembers(name);
    }

    public static List<AccessibleObject> getAllMembers(Object receiver) {
        if (receiver == null) return null;
        return getAllMembers(receiver.getClass());
    }

    public static List<AccessibleObject> getAllMembers(Class clazz) {
        return $class(clazz).getAllMembers();
    }

    public static void set(Object receiver, Field field, Object value) {
        if (field == null) return;
        try {
            field.set(receiver, value);
        } catch (IllegalAccessException e) {
            throw new WrapException(e);
        }
    }

    public static void setQuietly(Object receiver, Field field, Object value) {
        try {
            set(receiver, field, value);
        } catch (WrapException ignore) {
        }
    }

    public static Object get(Object receiver, Field field) {
        if (field == null) return null;
        try {
            return field.get(receiver);
        } catch (IllegalAccessException e) {
            throw new WrapException(e);
        }
    }

    public static Object getQuietly(Object receiver, Field field) {
        try {
            return get(receiver, field);
        } catch (WrapException ignore) {
            return null;
        }
    }

    public static MetaClass $class(Class clazz) {
        return MetaClass.get(clazz);
    }

    public static class MetaClass {
        private static final ConcurrentMap<String, MetaClass> META_MAP = new ConcurrentHashMap<String, MetaClass>();
        private static final MetaClass EMPTY_META_CLASS = new MetaClass();

        static MetaClass get(Class clazz) {
            if (isTerminal(clazz)) return EMPTY_META_CLASS;
            MetaClass metaClass = META_MAP.get(clazz.getName());
            if (metaClass == null) {
                MetaClass already = META_MAP.putIfAbsent(clazz.getName(), metaClass = new MetaClass(clazz));
                if (already != null) {
                    metaClass = already;
                }
            }
            return metaClass;
        }

        private final Class clazz;
        private final Class superClass;
        private final Map<String, Field> fMap;
        private final Map<String, List<Method>> mMap;

        /**
         * For empty meta
         */
        private MetaClass() {
            this.clazz = MetaClass.class;
            this.superClass = Object.class;
            this.fMap = Collections.emptyMap();
            this.mMap = Collections.emptyMap();
        }

        private MetaClass(Class clazz) {
            this.clazz = clazz;
            this.superClass = clazz.getSuperclass();

            // field map
            Map<String, Field> fMap = new HashMap<String, Field>();
            for (Field f : clazz.getDeclaredFields()) {
                fMap.put(f.getName(), ensureAccessible(f));
            }
            this.fMap = Collections.unmodifiableMap(fMap);

            // method map
            Map<String, List<Method>> mMap = newMap();
            for (Method m : clazz.getDeclaredMethods()) {
                getOrNew(mMap, m.getName()).add(ensureAccessible(m));
            }
            this.mMap = Collections.unmodifiableMap(mMap);
        }

        public Field getField(String fName) {
            if (this == EMPTY_META_CLASS) return null;
            Field f = fMap.get(fName);
            return f != null ? f : get(superClass).getField(fName);
        }

        public List<Field> getAllFields() {
            if (this == EMPTY_META_CLASS) return Collections.emptyList();
            ArrayList<Field> fields = new ArrayList<Field>();
            fields.addAll(fMap.values());
            fields.addAll(get(superClass).getAllFields());
            return fields;
        }

        public List<Method> getMethods(String mName) {
            if (this == EMPTY_META_CLASS) return null;
            List<Method> methods = mMap.get(mName);
            return methods != null ? methods : get(superClass).getMethods(mName);
        }

        public List<Method> getAllMethods() {
            if (this == EMPTY_META_CLASS) return Collections.emptyList();
            List<Method> allMethods = newList();
            for (List<Method> methods : mMap.values()) {
                allMethods.addAll(methods);
            }
            allMethods.addAll(get(superClass).getAllMethods());
            return allMethods;
        }

        public List<AccessibleObject> getMembers(String name) {
            List<AccessibleObject> members = newList();
            Field f = getField(name);
            if (f != null) {
                members.add(f);
            }
            List<Method> methods = getMethods(name);
            if (methods != null) {
                members.addAll(methods);
            }
            return members;
        }

        public List<AccessibleObject> getAllMembers() {
            if (this == EMPTY_META_CLASS) return Collections.emptyList();
            List<AccessibleObject> allMembers = newList();
            allMembers.addAll(getAllFields());
            allMembers.addAll(getAllMethods());
            allMembers.addAll(get(superClass).getAllMembers());
            return allMembers;
        }

        private List<Method> getOrNew(Map<String, List<Method>> mMap, String mName) {
            List<Method> methods = mMap.get(mName);
            if (methods == null) {
                mMap.put(mName, methods = newList());
            }
            return methods;
        }

    }

    private static boolean isTerminal(Class clazz) {
        return clazz == Object.class || Iterable.class.isAssignableFrom(clazz)
                || clazz.isArray() || Map.class.isAssignableFrom(clazz);
    }

    private static Field ensureAccessible(Field f) {
        if (!f.isAccessible()) {
            f.setAccessible(true);
        }
        return f;
    }

    private static Method ensureAccessible(Method m) {
        if (!m.isAccessible()) {
            m.setAccessible(true);
        }
        return m;
    }
}
