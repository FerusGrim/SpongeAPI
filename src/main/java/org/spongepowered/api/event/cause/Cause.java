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
package org.spongepowered.api.event.cause;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.util.damage.source.DamageSource;
import org.spongepowered.api.event.entity.util.spawn.SpawnCause;

import java.util.Arrays;

import javax.annotation.Nullable;

/**
 * A cause represents the reason or initiator of an event.
 *
 * <p>For example, if a block of sand is placed where it drops, the block
 * of sand would create a falling sand entity, which then would place another
 * block of sand. The block place event for the final block of sand would have
 * the cause chain of the block of sand -&gt; falling sand entity.</p>
 *
 * <p>It is not possible to accurately the describe the chain of causes in
 * all scenarios so a best effort approach is generally acceptable. For
 * example, a player might press a lever, activating a complex Redstone
 * circuit, which would then launch TNT and cause the destruction of
 * some blocks, but tracing this event would be too complicated and thus
 * may not be attempted.</p>
 */
@SuppressWarnings("unchecked")
public abstract class Cause {

    public static Cause empty() {
        return new EmptyCause();
    }

    public static Cause of(Object... objects) {
        return new PresentCause(checkNotNull(objects));
    }

    public static Cause fromNullable(@Nullable Object... objects) {
        if (objects == null) {
            return new EmptyCause();
        } else {
            return new PresentCause(objects);
        }
    }


    Cause() {}

    /**
     * Gets whether this {@link Cause} is empty of any causes or not. An empty
     * cause may mean the {@link Cause} is not originating from any vanilla
     * interactions, or it may mean the cause is simply not known.
     *
     * @return True if this cause is empty, false otherwise
     */
    public abstract boolean isEmpty();

    /**
     * Gets the root {@link Object} of this cause. The root can be anything,
     * including but not limited to: {@link DamageSource}, {@link Entity},
     * {@link SpawnCause}, etc.
     *
     * @return The root object cause for this cause
     */
    public abstract Optional<?> getRoot();

    /**
     * Gets the first {@link T} object of this {@link Cause}, if available.
     *
     * @param target The class of the target type
     * @param <T> The type of object being queried for
     * @return The first element of the type, if available
     */
    public abstract <T> Optional<T> getFirst(Class<T> target);

    /**
     * Gets the last object instance of the {@link Class} of type {@link T}.
     *
     * @param target The class of the target type
     * @param <T> The type of object being queried for
     * @return The last element of the type, if available
     */
    public abstract <T> Optional<T> getLast(Class<T> target);

    /**
     * Gets an {@link ImmutableList} of all objects that are instances of the
     * given {@link Class} type {@link T}.
     *
     * @param target The class of the target type
     * @param <T> The type of objects to query for
     * @return An immutable list of the objects queried
     */
    public abstract <T> ImmutableList<T> getAllOf(Class<T> target);

    /**
     * Gets an {@link ImmutableList} of all causes within this {@link Cause}.
     *
     * @return An immutable list of all the causes
     */
    public abstract ImmutableList<Object> getAllCauses();

    /**
     * Returns {@code true} if {@code object} is a {@code Cause} instance, and either
     * the contained references are {@linkplain Object#equals equal} to each other or both
     * are absent.
     */
    @Override
    public abstract boolean equals(@Nullable Object object);

    /**
     * Returns a hash code for this instance.
     */
    @Override
    public abstract int hashCode();

    private static final class PresentCause extends Cause {
        private final Object[] cause;

        PresentCause(Object... cause) {
            checkNotNull(cause, "cause");
            for (Object aCause : cause) {
                checkNotNull(aCause, "Null cause element!");
            }
            this.cause = Arrays.copyOf(cause, cause.length);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Optional<?> getRoot() {
            return Optional.of(this.cause[0]);
        }

        @Override
        public <T> Optional<T> getFirst(Class<T> target) {
            for (Object aCause : this.cause) {
                if (target.isInstance(aCause)) {
                    return Optional.of((T) aCause);
                }
            }
            return Optional.absent();
        }

        @Override
        public <T> ImmutableList<T> getAllOf(Class<T> target) {
            ImmutableList.Builder<T> builder = ImmutableList.builder();
            for (Object aCause : this.cause) {
                if (target.isInstance(aCause)) {
                    builder.add((T) aCause);
                }
            }
            return builder.build();
        }

        @Override
        public <T> Optional<T> getLast(Class<T> target) {
            for (int i = this.cause.length - 1; i >= 0; i--) {
                if (target.isInstance(this.cause[i])) {
                    return Optional.of((T) this.cause[i]);
                }
            }
            return Optional.absent();
        }

        @Override
        public ImmutableList<Object> getAllCauses() {
            return ImmutableList.copyOf(this.cause);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof PresentCause) {
                PresentCause cause = ((PresentCause) object);
                return Arrays.equals(this.cause, cause.cause);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.cause);
        }
    }

    private static final class EmptyCause extends Cause {

        EmptyCause() {
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Optional<?> getRoot() {
            return Optional.absent();
        }

        @Override
        public <T> Optional<T> getFirst(Class<T> target) {
            return Optional.absent();
        }

        @Override
        public <T> Optional<T> getLast(Class<T> target) {
            return Optional.absent();
        }

        @Override
        public <T> ImmutableList<T> getAllOf(Class<T> target) {
            return ImmutableList.of();
        }

        @Override
        public ImmutableList<Object> getAllCauses() {
            return ImmutableList.of();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return object == this;
        }

        @Override
        public int hashCode() {
            return 0x39e8a5b;
        }
    }

}
