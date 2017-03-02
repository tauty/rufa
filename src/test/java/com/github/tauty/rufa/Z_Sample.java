package com.github.tauty.rufa;

import java.util.Calendar;

/**
 * Created by tetz on 2017/02/26.
 */
public class Z_Sample {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH) + 1);
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
    }
}
