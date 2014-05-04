package com.github.tauty.rufa.rules;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.hamcrest.CoreMatchers.*;

public class RErrorCollectorTest {

    @Rule
    public RErrorCollector rec = new RErrorCollector();

    @Test
    public void checkThat() throws Exception {
        rec.checkThat("aaa", is("aaa"));
        rec.checkThat("aaa", is("abc"));
        rec.checkThat("abc", is("abc"));
        rec.checkThat("abc", is("xyz"));
        rec.checkThat("xyz", is("xyz"));
        throw new UnsupportedOperationException("end.");
    }

    @Test
    public void checkThatWithMsg() throws Exception {
        rec.checkThat("1", "aaa", is("aaa"));
        rec.checkThat("2", "aaa", is("abc"));
        rec.checkThat("3", "abc", is("abc"));
        rec.checkThat("4", "abc", is("xyz"));
        rec.checkThat("5", "xyz", is("xyz"));
        throw new UnsupportedOperationException("FIN.");
    }

    @Test
    public void testAddError() throws Exception {
        try {
            "".charAt(10);
        }catch (Exception e) {
            rec.addError(e);
        }
    }

    @Test
    public void checkSucceeds() throws Exception {
        rec.addError(new Exception("dummy"));
        rec.checkSucceeds(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return "".charAt(10);
            }
        });
    }
}