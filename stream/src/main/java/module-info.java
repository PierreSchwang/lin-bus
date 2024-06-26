/*
 * Copyright (c) EngineHub <https://enginehub.org>
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jspecify.annotations.NullMarked;

/**
 * The streaming module of lin-bus. Features a Jackson-like API for decoding and encoding NBT.
 */
@NullMarked
module org.enginehub.linbus.stream {
    exports org.enginehub.linbus.stream;
    exports org.enginehub.linbus.stream.exception;
    exports org.enginehub.linbus.stream.token;
    exports org.enginehub.linbus.stream.internal to org.enginehub.linbus.tree;
    requires static transitive org.jspecify;
    requires transitive org.enginehub.linbus.common;
}
