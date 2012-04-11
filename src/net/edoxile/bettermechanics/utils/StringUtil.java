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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package net.edoxile.bettermechanics.utils;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class StringUtil {

    public static String stripBrackets(String string) {
        if (string.length() > 2 && string.charAt(0) == '[' && string.charAt(string.length() - 1) == ']') {
            return string.substring(1, string.length() - 2);
        } else {
            return string;
        }
    }

    public static String merge(String[] data, int offset) {
        String str = "";
        for (int i = offset; i < data.length; i++) {
            str += data[i];
            if ((i + 1) != data.length)
                str += " ";
        }
        return str;
    }
}
