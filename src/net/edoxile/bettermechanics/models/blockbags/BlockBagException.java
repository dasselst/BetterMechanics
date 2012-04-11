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

package net.edoxile.bettermechanics.models.blockbags;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BlockBagException extends Exception {
    public enum Type {
        NO_BAG_FOUND("A BlockBag could not be found!"),
        NO_SPACE_LEFT("This sign is not placed in an ordinal direction (east, west, north or south)!"),
        NOT_ENOUGH_MATERIALS("This sign-type is wrong (wall sign <> sign post)!");

        private final String errorMessage;

        private Type(String errorMsg) {
            errorMessage = errorMsg;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public BlockBagException(Type type) {
        super(type.getErrorMessage());
    }
}
