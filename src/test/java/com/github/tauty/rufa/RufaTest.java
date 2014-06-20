package com.github.tauty.rufa;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * Created by tetsuo.uchiumi on 5/21/14.
 */
public class RufaTest {

    @Rule
    Rufa rufa = new Rufa(this);

    @Test
    public void test() {

        for (int i=0; i<100; i++) {
            
        }

//        Assert.assertThat(actual, is());
//        assertThat(dump(actual), is(contentsOf("test.txt")));
        String result = new Callable<String>() {
            @Override
            public String call() {
                return null;
            }
        }.call();
    }
}
