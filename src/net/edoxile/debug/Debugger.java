/*
 * Copyright (c) 2012.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import static net.edoxile.debug.DebugManager.log;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class Debugger {

    public Debugger() {
        DebugManager.getInstance().registerDebugger(this);
    }

    public abstract List<TestEntry<?, ?>> getTests();

    public abstract Method getMethod();

    public boolean testCode() {
        boolean succeed = true;
        try {
            for (TestEntry<?, ?> entry : getTests()) {
                Object returnData;
                if (entry.hasMoreArgs()) {
                    returnData = getMethod().invoke(entry.getInvokeObject(), entry.getInput(), entry.getExtraArgs());
                } else {
                    returnData = getMethod().invoke(entry.getInvokeObject(), entry.getInput());
                }

                if (!test(entry, returnData)) {
                    log(
                            "[Debug] Test failed; output dump:\n- Input:\n\t"
                                    + entry.getInput().toString() + "\n- Expected output:\n\t"
                                    + entry.getOutput() + "\n- Real output:\n\t"
                                    + getTestOutput(entry, returnData),
                            Level.WARNING
                    );
                    succeed = false;
                }
            }
        } catch (IllegalAccessException e) {
            log("Method passed to DebugTest isn't accessible from within DebugTest: " + e.getMessage(), Level.SEVERE);
            e.getMessage();
        } catch (InvocationTargetException e) {
            log("Object passed to TestEntry could not invoke method passed to TestEntry: " + e.getMessage(), Level.SEVERE);
            succeed = false;
        }
        return succeed;
    }

    public abstract boolean test(TestEntry<?, ?> testEntry, Object returnData);

    public abstract String getTestOutput(TestEntry<?, ?> testEntry, Object returnData);

    public abstract String getName();
}
