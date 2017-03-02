package com.github.tauty.rufa;

import com.github.tauty.rufa.common.util.HereDoc;
import com.github.tauty.rufa.common.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;

import static com.github.tauty.rufa.common.util.CommonUtil.equalsSafely;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Exception class which is thrown while the resource is closing.
 *
 * @see com.github.tauty.rufa.common.util.Using
 * @author tauty
 */
public class RufaTest {

    private static Map<String, String> doc = HereDoc.get(IOUtil.loadFromRoot("JSONTestData.txt"));

    @Rule
    public Rufa rufa = new Rufa(this);

    @Before
    public void setUp() {
        System.out.println("@Before");
    }

    @After
    public void tearDown() {
        System.out.println("@After");
    }

    @Test
    public void test() {
        rufa.tmpName_assertJSONAutomatically("tako", "def", doc.get("sample"));
//        rufa.tmpName_assertJSONAutomatically("ika", "def", doc.get("sample2"));
    }

    @Test
    public void isSameOrder_all() {

        Rufa sut = new Rufa(this);

        assertThat(sut.isSameOrder(newSet(1, 2, 3), newSet(1, 2, 3)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 2, 3), newSet(1, 2, 3, 4)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 2, 4), newSet(1, 2, 3, 4)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 2, 3, 4), newSet(1, 2, 4)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 2, 3, 5), newSet(1, 2, 4, 5)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 2, 3), newSet(4, 5, 6)), is(true));
        assertThat(sut.isSameOrder(newSet(1, 10, 2, 20, 3), newSet(1, 100, 2, 200, 3)), is(true));

        assertThat(sut.isSameOrder(newSet(1, 2, 3, 4), newSet(1, 2, 4, 3)), is(false));
        assertThat(sut.isSameOrder(newSet(1, 2, 3, 4), newSet(4, 3, 2, 1)), is(false));
        assertThat(sut.isSameOrder(newSet(1, 2, 3, 4), newSet(1, 5, 3, 2)), is(false));
        assertThat(sut.isSameOrder(newSet(1, 6, 3, 2), newSet(1, 2, 3, 4)), is(false));
    }

    @Test
    public void isSameOrderCompletely_all() {

        assertThat(this.isSameOrderCompletely(newIte(1, 2, 3), newIte(1, 2, 3)), is(true));
        assertThat(this.isSameOrderCompletely(newIte(1, 2, 3), newIte(1, 2, 3, 4)), is(false));
        assertThat(this.isSameOrderCompletely(newIte(1, 2, 4), newIte(1, 2, 3, 4)), is(false));
        assertThat(this.isSameOrderCompletely(newIte(1, 2, 3, 4), newIte(1, 2, 4)), is(false));
        assertThat(this.isSameOrderCompletely(newIte(1, 2, 3, 5), newIte(1, 2, 4, 5)), is(false));
        assertThat(this.isSameOrderCompletely(newIte(1, 2, 3), newIte(4, 5, 6)), is(false));
        assertThat(this.isSameOrderCompletely(newIte(1, 10, 2, 20, 3), newIte(1, 100, 2, 200, 3)), is(false));
    }

    private static Iterator newIte(Object... objs) {
        return newSet(objs).iterator();
    }

    private static Set newSet(Object... objs) {
        Set set = new LinkedHashSet();
        Collections.addAll(set, objs);
        return set;
    }

    boolean isSameOrderCompletely(Iterator expItr, Iterator actItr) {
        while (expItr.hasNext() && actItr.hasNext()) {
            if (!equalsSafely(expItr.next(), actItr.next())) return false;
        }
        return !expItr.hasNext() && !actItr.hasNext();
    }

}
