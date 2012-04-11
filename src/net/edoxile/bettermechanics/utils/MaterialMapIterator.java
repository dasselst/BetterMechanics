/*
 * Copyright (c) 2012 Edoxile
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

package net.edoxile.bettermechanics.utils;


import net.edoxile.bettermechanics.exceptions.InvalidConstructionException;

public class MaterialMapIterator {
    private transient int[] _keys, _values;
    private int pointer = -1;
    private int size = 0;

    public MaterialMapIterator(int[] keys, int[] values) throws InvalidConstructionException {
        if (keys.length != values.length)
            throw new InvalidConstructionException();
        _keys = keys;
        _values = values;
        size = _keys.length;
    }

    public boolean hasNext() {
        return pointer < (size - 1);
    }

    public boolean hasPrevious() {
        return pointer > 1;
    }

    public void next() {
        pointer++;
    }

    public void remove() {
        int[] keys = new int[size - 1];
        int[] values = new int[size - 1];

        int index = 0;
        for (int k = 0; k < size; k++) {
            if (index == pointer)
                continue;
            values[index] = _values[k];
            keys[index] = _keys[k];
            index++;
        }

        size = _keys.length;
    }

    public void previous() {
        pointer--;
    }

    public int key() {
        return _keys[pointer];
    }

    public int value() {
        return _values[pointer];
    }

    public void rewind() {
        pointer = -1;
    }

    public void end() {
        pointer = size - 1;
    }

    @Override
    public String toString() {
        if (size == 0)
            return "[0 items] {} @ 0";
        String msg = "[" + size + " items] :: { ";
        for (int index = 0; index < size; index++) {
            msg += "[" + _keys[index] + ", " + _values[index] + "], ";
        }
        return msg.substring(0, msg.length() - 2) + " } @ " + pointer;
    }
}
