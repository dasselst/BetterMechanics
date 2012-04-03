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

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class TestEntry<I, O> {

    private I input;
    private O output;
    private Object[] extraArgs;
    private Object invokeObject;

    public TestEntry(I in, O out, Object invoke, Object... args) {
        input = in;
        output = out;
        invokeObject = invoke;
        extraArgs = args;
    }

    public I getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }

    public Object getInvokeObject() {
        return invokeObject;
    }

    public boolean hasMoreArgs() {
        return extraArgs == null || extraArgs.length == 0;
    }

    public Object[] getExtraArgs() {
        return extraArgs;
    }
}
