/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.data.ValueChangeEvent;

import java.util.List;
import javax.annotation.Nullable;

/**
 * A type of builder for building {@link DataTransactionResult}s. The common
 * use is for both implementations of {@link DataHolder}s, and various
 * {@link ValueChangeEvent}s.
 */
public final class DataTransactionBuilder {

    @Nullable private List<ImmutableValue<?>> rejected;
    @Nullable private List<ImmutableValue<?>> replaced;
    @Nullable private List<ImmutableValue<?>> successful;
    private DataTransactionResult.Type resultType;

    private DataTransactionBuilder() {
    }

    /**
     * Gets a new {@link DataTransactionBuilder} to build a new
     * {@link DataTransactionResult}.
     *
     * @return The new builder, for chaining
     */
    public static DataTransactionBuilder builder() {
        return new DataTransactionBuilder();
    }

    /**
     * Creates a {@link DataTransactionResult} with no data successfully added,
     * removed, or rejected, and with the
     * {@link org.spongepowered.api.data.DataTransactionResult.Type} of
     * {@link org.spongepowered.api.data.DataTransactionResult.Type#SUCCESS}
     * result type.
     *
     * @return A clean and empty data transaction
     */
    public static DataTransactionResult successNoData() {
        return builder().result(DataTransactionResult.Type.SUCCESS).build();
    }

    /**
     * Creates a new {@link DataTransactionResult} with the provided
     * {@link ImmutableValue} being the successful addition. The result type is
     * still {@link DataTransactionResult.Type#SUCCESS}. If a {@link Value} is
     * necessary, use {@link Value#asImmutable()} to use this method. A
     * {@link DataTransactionResult} is always immutable once created, and any
     * {@link BaseValue}s should be provided as {@link ImmutableValue}s or
     * transformed into {@link ImmutableValue}s.
     *
     * @param value The successfully added immutable value
     * @return The new data transaction result
     */
    public static DataTransactionResult successResult(final ImmutableValue<?> value) {
        return builder().success(value).result(DataTransactionResult.Type.SUCCESS).build();
    }

    /**
     * Creates a new {@link DataTransactionResult} with the provided
     * {@link ImmutableValue} being the successful addition. The result type is
     * still {@link DataTransactionResult.Type#SUCCESS}. If a {@link Value} is
     * necessary, use {@link Value#asImmutable()} to use this method. A
     * {@link DataTransactionResult} is always immutable once created, and any
     * {@link BaseValue}s should be provided as {@link ImmutableValue}s or
     * transformed into {@link ImmutableValue}s.
     *
     * @param successful The successfully added immutable value
     * @param replaced The replaced value
     * @return The new data transaction result
     */
    public static DataTransactionResult successReplaceResult(final ImmutableValue<?> successful, final ImmutableValue<?> replaced) {
        return builder().result(DataTransactionResult.Type.SUCCESS).success(successful).replace(replaced).build();
    }

    /**
     * Creates a new {@link DataTransactionResult} with the provided
     * {@link ImmutableValue}s being the successful additions and
     * the provided {@link ImmutableValue}s that were replaced. The result type
     * is still {@link DataTransactionResult.Type#SUCCESS}. If a {@link Value}
     * is necessary, use {@link Value#asImmutable()} to use this method. A
     * {@link DataTransactionResult} is always immutable once created, and any
     * {@link BaseValue}s should be provided as {@link ImmutableValue}s or
     * transformed into {@link ImmutableValue}s.
     *
     * @param successful The successfully added immutable values
     * @param replaced The successfully replaced immutable values
     * @return The new data transaction result
     */
    public static DataTransactionResult successReplaceResult(Iterable<ImmutableValue<?>> successful, Iterable<ImmutableValue<?>> replaced) {
        return builder().success(successful).replace(replaced).result(DataTransactionResult.Type.SUCCESS).build();
    }

    /**
     * Creates a new {@link DataTransactionResult} that ends in failure. The
     * provided {@link ImmutableValue} is considered "rejected" and was not
     * successfully added.
     *
     * @param value The value that was rejected
     * @return The new data transaction result
     */
    public static DataTransactionResult failResult(final ImmutableValue<?> value) {
        return builder().reject(value).result(DataTransactionResult.Type.FAILURE).build();
    }

    /**
     * Creates a new {@link DataTransactionResult} that ends in failure. The
     * provided {@link ImmutableValue}s are considered "rejected" and were not
     * successfully added.
     *
     * @param values The values that were rejected
     * @return The new data transaction result
     */
    public static DataTransactionResult failResult(final ImmutableValue<?>... values) {
        return builder().reject(values).result(DataTransactionResult.Type.FAILURE).build();
    }


    /**
     * Creates a new {@link DataTransactionResult} that ends in failure. The
     * provided {@link ImmutableValue} is considered "incompatible" and was not
     * successfully added.
     *
     * @param value The value that was incompatible or errored
     * @return The new data transaction result
     */
    public static DataTransactionResult errorResult(final ImmutableValue<?> value) {
        return builder().result(DataTransactionResult.Type.ERROR).reject(value).build();
    }

    /**
     * Sets the expectant {@link DataTransactionResult.Type} to the provided
     * type. A {@link DataTransactionResult} must always have a type to mark
     * the transaction a "success" or "failure".
     *
     * @param type The type of the transaction result
     * @return This builder, for chaining
     */
    public DataTransactionBuilder result(final DataTransactionResult.Type type) {
        this.resultType = checkNotNull(type);
        return this;
    }

    /**
     * Adds the provided {@link ImmutableValue} to the {@link List} of
     * "replaced" {@link ImmutableValue}s. The replaced values are always
     * copied for every {@link DataTransactionResult} for referencing. It is
     * also possible ot retrieve these replaced {@link ImmutableValue}s to
     * {@link DataHolder#undo(DataTransactionResult)} at a later point in
     * the lifespan of the {@link DataHolder}.
     *
     * @param value
     * @return
     */
    public DataTransactionBuilder replace(final ImmutableValue<?> value) {
        if (this.replaced == null) {
            this.replaced = Lists.newArrayList();
        }
        this.replaced.add(checkNotNull(value));
        return this;
    }

    public DataTransactionBuilder replace(final ImmutableValue<?>... values) {
        for (ImmutableValue<?> value : values) {
            replace(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionBuilder replace(final Iterable<ImmutableValue<?>> values) {
        for (ImmutableValue<?> value : values) {
            replace(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionBuilder reject(final ImmutableValue<?> value) {
        if (this.rejected == null) {
            this.rejected = Lists.newArrayList();
        }
        this.rejected.add(checkNotNull(value));
        return this;
    }

    public DataTransactionBuilder reject(final ImmutableValue<?>... values) {
        for (ImmutableValue<?> value : values) {
            reject(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionBuilder reject(final Iterable<ImmutableValue<?>> values) {
        for (ImmutableValue<?> value : values) {
            reject(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionBuilder success(final ImmutableValue<?> value) {
        if (this.successful == null) {
            this.successful = Lists.newArrayList();
        }
        this.successful.add(checkNotNull(value));
        return this;
    }

    public DataTransactionBuilder success(final ImmutableValue<?>... values) {
        for (ImmutableValue<?> value : values) {
            success(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionBuilder success(final Iterable<ImmutableValue<?>> values) {
        for (ImmutableValue<?> value : values) {
            success(checkNotNull(value));
        }
        return this;
    }

    public DataTransactionResult build() {
        checkState(this.resultType != null);
        return new BuilderResult(this);
    }

    private static final class BuilderResult implements DataTransactionResult {

        private final Type type;
        @Nullable private final ImmutableList<ImmutableValue<?>> rejected;
        @Nullable private final ImmutableList<ImmutableValue<?>> replaced;
        @Nullable private final ImmutableList<ImmutableValue<?>> success;

        BuilderResult(final DataTransactionBuilder builder) {
            this.type = builder.resultType;
            if (builder.rejected != null) {
                this.rejected = ImmutableList.copyOf(builder.rejected);
            } else {
                this.rejected = null;
            }
            if (builder.replaced != null) {
                this.replaced = ImmutableList.copyOf(builder.replaced);
            } else {
                this.replaced = null;
            }
            if (builder.successful != null) {
                this.success = ImmutableList.copyOf(builder.successful);
            } else {
                this.success = null;
            }
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public Optional<ImmutableList<ImmutableValue<?>>> getSuccessfulData() {
            return Optional.fromNullable(this.success);
        }

        @Override
        public Optional<ImmutableList<ImmutableValue<?>>> getRejectedData() {
            return Optional.fromNullable(this.rejected);
        }

        @Override
        public Optional<ImmutableList<ImmutableValue<?>>> getReplacedData() {
            return Optional.fromNullable(this.replaced);
        }
    }

}
