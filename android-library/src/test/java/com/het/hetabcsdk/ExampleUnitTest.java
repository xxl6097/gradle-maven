package com.het.hetabcsdk;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String regex = "[,，\u003B\uFF1B、]";
        //这里或者String regex = ",|，|;|；|、";
        String stds1 = "YD/T 2564.2-2013;YD/T 2714-2014,YD/T 1819-2008，YD/T 2198-2010；YD/T 1464-2006、YD/T 1480-2006";
        List<String> result1 = Arrays.asList(stds1.split(regex));
        System.out.println(result1);
        for (String s : result1) {
            System.out.println(s);
        }
        String stds2 = "YD/T 1350.4-2007";
        List<String> result2 = Arrays.asList(stds2.split(regex));
        System.out.println(result2);
    }
}