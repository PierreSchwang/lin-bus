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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a list of {@link LinTag LinTags}.
 *
 * @param <T> the type of the elements in the list
 */
public final class LinListTag<T extends @NotNull LinTag<?, T>> extends LinTag<@NotNull List<T>, LinListTag<T>> {
    /**
     * Read a list tag from the given stream.
     *
     * @param tokens the stream to read from
     * @return the list tag
     */
    public static <T extends @NotNull LinTag<?, T>> LinListTag<T> readFrom(@NotNull Iterator<? extends @NotNull LinToken> tokens) {
        return LinTagReader.readList(tokens);
    }

    /**
     * Get an empty list of the given element type.
     *
     * @param elementType the element type of the list
     * @param <T> the type of the elements in the list
     * @return an empty list
     */
    public static <T extends @NotNull LinTag<?, T>> LinListTag<T> empty(LinTagType<T> elementType) {
        return builder(elementType).build();
    }

    /**
     * Creates a new builder for a list of the given element type.
     *
     * @param elementType the element type of the list
     * @param <T> the type of the elements in the list
     * @return a new builder
     */
    public static <T extends @NotNull LinTag<?, T>> Builder<T> builder(LinTagType<T> elementType) {
        return new Builder<>(elementType);
    }

    /**
     * A builder for {@link LinListTag LinListTags}.
     *
     * @param <T> the type of the elements in the list
     */
    public static final class Builder<T extends @NotNull LinTag<?, T>> {
        private final LinTagType<T> elementType;
        private final List<T> collector;

        private Builder(LinTagType<T> elementType) {
            this.elementType = elementType;
            this.collector = new ArrayList<>();
        }

        private Builder(LinListTag<T> base) {
            this.elementType = base.elementType;
            this.collector = new ArrayList<>(base.value);
        }

        /**
         * Add an element to the list.
         *
         * @param tag the element
         * @return this builder
         */
        public Builder<T> add(T tag) {
            if (tag.type() != elementType) {
                throw new IllegalArgumentException("Element is not of type " + elementType.name() + " but "
                    + tag.type().name());
            }
            this.collector.add(tag);
            return this;
        }

        /**
         * Add a collection of elements to the list.
         *
         * @param tags the elements
         * @return this builder
         */
        public Builder<T> addAll(Collection<? extends T> tags) {
            tags.forEach(this::add);
            return this;
        }

        /**
         * Finish building the list tag.
         *
         * @return the built tag
         */
        public LinListTag<T> build() {
            return new LinListTag<>(this.elementType, List.copyOf(this.collector), false);
        }
    }

    private final LinTagType<T> elementType;
    private final List<T> value;

    /**
     * Creates a new list tag.
     *
     * <p>
     * The list will be copied as per the {@link List#copyOf(Collection)} method.
     * </p>
     *
     * @param elementType the element type of the list
     * @param value the elements in the list
     */
    public LinListTag(LinTagType<T> elementType, List<T> value) {
        this(elementType, List.copyOf(value), true);
    }

    private LinListTag(LinTagType<T> elementType, List<T> value, boolean check) {
        Objects.requireNonNull(value, "value is null");
        if (check) {
            for (T t : value) {
                if (t.type() != elementType) {
                    throw new IllegalArgumentException("Element is not of type " + elementType.name() + " but "
                        + t.type().name());
                }
            }
        }
        if (!value.isEmpty() && elementType == LinTagType.endTag()) {
            throw new IllegalArgumentException("A non-empty list cannot be of type END");
        }
        this.elementType = elementType;
        this.value = value;
    }

    @Override
    public @NotNull LinTagType<LinListTag<T>> type() {
        return LinTagType.listTag();
    }

    /**
     * {@return the element type of this list}
     */
    public LinTagType<T> elementType() {
        return elementType;
    }

    @Override
    public @NotNull List<T> value() {
        return value;
    }

    @Override
    public @NotNull Iterator<@NotNull LinToken> iterator() {
        return Iterators.combine(
            Iterators.of(new LinToken.ListStart(value.size(), elementType.id())),
            Iterators.combine(new EntryTokenIterator()),
            Iterators.of(new LinToken.ListEnd())
        );
    }

    private class EntryTokenIterator extends AbstractIterator<Iterator<? extends @NotNull LinToken>> {
        private final Iterator<T> entryIterator = value.iterator();

        @Override
        protected Iterator<? extends @NotNull LinToken> computeNext() {
            if (!entryIterator.hasNext()) {
                return end();
            }
            return entryIterator.next().iterator();
        }
    }

    /**
     * Direct shorthand for {@link #value() value()}{@code .}{@link List#get(int) get(index)}.
     *
     * @param index the index of the element to get
     * @return the element at the given index
     */
    public T get(int index) {
        return value.get(index);
    }

    /**
     * Converts this tag into a {@link Builder}.
     *
     * @return a new builder
     */
    public Builder<T> toBuilder() {
        return new Builder<>(this);
    }

    @Override
    public @NotNull String toString() {
        return getClass().getSimpleName() + value;
    }
}