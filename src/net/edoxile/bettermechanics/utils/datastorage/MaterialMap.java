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

package net.edoxile.bettermechanics.utils.datastorage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class MaterialMap {
    private int size = 0;

    private transient int[] _keys;
    private transient int[] _values;

    public MaterialMap() {
        _keys = new int[size];
        _values = new int[size];
    }

    private MaterialMap(int[] k, int[] v, int s) {
        _keys = k;
        _values = v;
        size = s;
    }

    private int getKey(int id, byte data) {
        return ((id << 8) | data);
    }

    public void put(int id, byte data, int value) {
        int key = getKey(id, data);
        try {
            update(id, data, value);
        } catch (KeyNotFoundException e) {
            int[] keys = new int[size + 1];
            int[] values = new int[size + 1];

            for (int i = 0; i < size; i++) {
                keys[i] = _keys[i];
                values[i] = _values[i];
            }

            keys[size] = key;
            values[size] = value;

            _values = values;
            _keys = keys;

            size = _values.length;
        }
    }

    public void add(int id, byte data, int amount) {
        try {
            int index = getIndex(id, data);
            _values[index] += amount;
        } catch (KeyNotFoundException e) {
            put(id, data, amount);
        }
    }

    public void remove(int id, byte data, int amount) {
        try {
            _values[getIndex(id, data)] -= amount;
        } catch (KeyNotFoundException e) {
            //do nothing, value not found so doesn't have to be deleted.
        }
    }

    public void remove(int id, byte data) {
        try {
            int keyIndex = getIndex(id, data);

            size--;

            int[] keys = new int[size];
            int[] values = new int[size];

            int index = 0;
            for (int k = 0; k < size + 1; k++) {
                if (index == keyIndex) {
                    continue;
                }
                values[index] = _values[k];
                keys[index] = _keys[k];
                index++;
            }
            _keys = keys;
            _values = values;
            size = _values.length;
        } catch (KeyNotFoundException e) {
        }
    }

    public int get(int id, byte data) {
        try {
            return _values[getIndex(id, data)];
        } catch (KeyNotFoundException e) {
            return 0;
        }
    }

    public MaterialMapIterator iterator() {
        return MaterialMapIterator.create(_keys, _values);
    }

    private int getIndex(int id, byte data) throws KeyNotFoundException {
        int key = getKey(id, data);
        for (int index = 0; index < size; index++) {
            if (_keys[index] == key) {
                return index;
            }
        }
        throw new KeyNotFoundException();
    }

    private void update(int id, byte data, int value) throws KeyNotFoundException {
        _values[getIndex(id, data)] = value;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        }

        String msg = "{ ";
        for (int index = 0; index < size; index++) {
            msg += "[" + _keys[index] + ", " + _values[index] + "], ";
        }
        return msg.substring(0, msg.length() - 2) + " }";
    }

    @Override
    public MaterialMap clone() {
        return new MaterialMap(_keys.clone(), _values.clone(), size);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public class KeyNotFoundException extends Exception {
    }
}

