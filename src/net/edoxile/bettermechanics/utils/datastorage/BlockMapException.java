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

package net.edoxile.bettermechanics.utils.datastorage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BlockMapException extends Exception {
    public enum Type {
        END_NOT_FOUND("The other side could not be found."),
        MECHANIC_NOT_FOUND("The %s could not be found. Distance between Sign and %s might be to big."),
        NON_ORDINAL_SIGN("This sign is not placed in an ordinal direction (east, west, north or south)."),
        MISPLACED_SIGN("This sign-type is wrong (wall sign <> sign post)."),
        NON_ALLOWED_MATERIAL("%s is not made of an allowed material."),
        SIZE_LIMIT_EXCEEDED("%s exceeds the size limit in some way."),
        SIDES_DO_NOT_MATCH("Both sides of the bridge need to be made of the same material."),
        NO_BLOCKMAP("%s doesn't have a BlockMap, but it was called anyway."),
        INVALID_KEY_ON_SIGN("%s was called, but the keyword on this sign doesn't belong to %s.");

        private final String errorMessage;

        private Type(String errorMsg) {
            errorMessage = errorMsg;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private final Type type;

    public BlockMapException(Type type) {
        super(type.getErrorMessage());
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
