/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
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
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.agent.core.logging;

import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.skywalking.apm.agent.core.conf.Constants;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

/**
 * Created by wusheng on 2017/2/28.
 */
public class EasyLoggerTest {
    private static PrintStream OUT_REF;
    private static PrintStream ERR_REF;

    @BeforeClass
    public static void initAndHoldOut() {
        OUT_REF = System.out;
        ERR_REF = System.err;
    }

    @Test
    public void testLog() {
        PrintStream output = Mockito.mock(PrintStream.class);
        System.setOut(output);
        PrintStream err = Mockito.mock(PrintStream.class);
        System.setErr(err);
        EasyLogger logger = new EasyLogger(EasyLoggerTest.class);

        Assert.assertTrue(logger.isDebugEnable());
        Assert.assertTrue(logger.isInfoEnable());
        Assert.assertTrue(logger.isWarnEnable());
        Assert.assertTrue(logger.isErrorEnable());

        logger.debug("hello world");
        logger.debug("hello {}", "world");
        logger.info("hello world");
        logger.info("hello {}", "world");

        logger.warn("hello {}", "world");
        logger.warn("hello world");
        logger.error("hello world");
        logger.error("hello world", new NullPointerException());
        logger.error(new NullPointerException(), "hello {}", "world");

        Mockito.verify(output, times(9))
            .println(anyString());
    }

    @Test
    public void testLogWithSpecialChar() {
        PrintStream output = Mockito.mock(PrintStream.class);
        System.setOut(output);
        PrintStream err = Mockito.mock(PrintStream.class);
        System.setErr(err);
        EasyLogger logger = new EasyLogger(EasyLoggerTest.class);

        Assert.assertTrue(logger.isDebugEnable());
        Assert.assertTrue(logger.isInfoEnable());
        Assert.assertTrue(logger.isWarnEnable());
        Assert.assertTrue(logger.isErrorEnable());

        logger.debug("$^!@#*()");
        logger.debug("hello {}", "!@#$%^&*(),./[]:;");
        logger.info("{}{}");
        logger.info("hello {}", "{}{}");

        logger.warn("hello {}", "\\");
        logger.warn("hello \\");
        logger.error("hello <>..");
        logger.error("hello ///\\\\", new NullPointerException());
        logger.error(new NullPointerException(), "hello {}", "&&&**%%");

        Mockito.verify(output, times(9))
            .println(anyString());
    }

    @Test
    public void testFormat() {
        NullPointerException exception = new NullPointerException();
        EasyLogger logger = new EasyLogger(EasyLoggerTest.class);
        String formatLines = logger.format(exception);
        String[] lines = formatLines.split(Constants.LINE_SEPARATOR);
        Assert.assertEquals("java.lang.NullPointerException", lines[1]);
        Assert.assertEquals("\tat org.skywalking.apm.agent.core.logging.EasyLoggerTest.testFormat(EasyLoggerTest.java:103)", lines[2]);
    }

    @AfterClass
    public static void reset() {
        System.setOut(OUT_REF);
        System.setErr(ERR_REF);
    }
}
