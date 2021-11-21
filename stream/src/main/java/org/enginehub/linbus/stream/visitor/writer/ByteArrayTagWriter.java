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

package org.enginehub.linbus.stream.visitor.writer;

import org.enginehub.linbus.stream.visitor.LinByteArrayTagVisitor;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

public class ByteArrayTagWriter implements LinByteArrayTagVisitor {
    private final byte[] copyBuffer = new byte[4096];
    private final DataOutput output;

    public ByteArrayTagWriter(DataOutput output) {
        this.output = output;
    }

    @Override
    public void visitSize(int size) {
        try {
            output.writeInt(size);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitChunk(ByteBuffer buffer) {
        int size = Math.min(copyBuffer.length, buffer.remaining());
        buffer.get(copyBuffer, 0, size);
        try {
            output.write(copyBuffer, 0, size);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitEnd() {
    }
}
