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

import org.enginehub.linbus.common.LinTagId;
import org.enginehub.linbus.stream.visitor.LinListTagVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class TreeListVisitor extends TreeContainerVisitor<Integer, LinListTag<@NotNull LinTag<?, ?>>> implements LinListTagVisitor {
    private LinListTag.Builder<@NotNull LinTag<?, ?>> builder;

    protected TreeListVisitor(Consumer<LinListTag<@NotNull LinTag<?, ?>>> tagConsumer) {
        super(tagConsumer);
    }

    @Override
    protected void acceptChild(Integer key, LinTag<?, ?> tag) {
        builder.add(tag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitSizeAndType(int size, LinTagId type) {
        builder = LinListTag.builder((LinTagType<LinTag<?, ?>>) LinTagType.fromId(type));
    }

    @Override
    public void visitEnd() {
        tagFinished(builder.build());
    }
}
