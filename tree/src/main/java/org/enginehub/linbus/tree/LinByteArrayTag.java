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

package org.enginehub.linbus.tree;

import org.enginehub.linbus.common.internal.AbstractIterator;
import org.enginehub.linbus.common.internal.Iterators;
import org.enginehub.linbus.stream.token.LinToken;
import org.enginehub.linbus.tree.impl.LinTagReader;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a byte array tag.
 */
public final class LinByteArrayTag extends LinTag<byte @NotNull [], LinByteArrayTag> {
    /**
     * Read a byte array tag from the given stream.
     *
     * @param tokens the stream to read from
     * @return the byte array tag
     */
    public static LinByteArrayTag readFrom(@NotNull Iterator<? extends @NotNull LinToken> tokens) {
        return LinTagReader.readValue(tokens, LinTagType.byteArrayTag());
    }

    private final byte[] value;

    /**
     * Creates a new byte array tag from the given byte array. The array will be {@linkplain  Object#clone() cloned}.
     *
     * @param value the value
     */
    public LinByteArrayTag(byte @NotNull ... value) {
        this(value.clone(), true);
    }

    LinByteArrayTag(byte @NotNull [] value, boolean iSwearToNotModifyValue) {
        if (!iSwearToNotModifyValue) {
            throw new IllegalArgumentException("You think you're clever, huh?");
        }
        this.value = value;
    }

    @Override
    public @NotNull LinTagType<LinByteArrayTag> type() {
        return LinTagType.byteArrayTag();
    }

    @Override
    public byte @NotNull [] value() {
        return value.clone();
    }

    /**
     * Alternative no-copy byte access, returns a new {@link ByteBuffer} that is read-only, and directly wraps the
     * underlying array.
     *
     * @return a read-only {@link ByteBuffer} providing view access to the contents of this tag
     */
    public @NotNull ByteBuffer view() {
        return ByteBuffer.wrap(value).asReadOnlyBuffer();
    }

    @Override
    public @NotNull Iterator<@NotNull LinToken> iterator() {
        return Iterators.combine(
            Iterators.of(new LinToken.ByteArrayStart(value.length)),
            new AbstractIterator<>() {
                private static final int BUFFER_SIZE = 4096;
                private int i = 0;

                @Override
                protected LinToken computeNext() {
                    if (i >= value.length) {
                        return end();
                    }
                    var length = Math.min(BUFFER_SIZE, value.length - i);
                    var buffer = ByteBuffer.wrap(value, i, length).asReadOnlyBuffer();
                    i += length;
                    return new LinToken.ByteArrayContent(buffer);
                }
            },
            Iterators.of(new LinToken.ByteArrayEnd())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinByteArrayTag that = (LinByteArrayTag) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + Arrays.toString(value());
    }
}