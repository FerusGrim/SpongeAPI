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
package org.spongepowered.api.world.gen.populator;

import org.spongepowered.api.world.gen.Populator;

/**
 * Represents a populator which spawns desert wells. The wells will be created
 * with a random chance of 1 in {@link #getSpawnChance()} and if the spawn
 * conditions are met (that the block its spawning on is sand).
 */
public interface DesertWell extends Populator {

    /**
     * Gets the chance of a desert well spawning. The final chance is calculated
     * as {@code if ( [0,chance) == 0 )}.
     * 
     * @return The spawn chance of a well
     */
    int getSpawnChance();

    /**
     * Sets the chance of a desert well spawning. The final chance is calculated
     * as {@code if ( [0,chance) == 0 )}. This must be greater than zero, The
     * default is 1000.
     * 
     * @param chance The new spawn chance
     */
    void setSpawnChance(int chance);

    /**
     * A builder for constructing {@link DesertWell} populators.
     */
    interface Builder {

        /**
         * Sets the chance of a desert well spawning. The final chance is
         * calculated as {@code if ( [0,chance) == 0 )}. This must be greater
         * than zero, The default is 1000.
         * 
         * @param chance The new spawn chance
         * @return This builder, for chaining
         */
        Builder chance(int chance);

        /**
         * Resets this builder to the default values.
         * 
         * @return This builder, for chaining
         */
        Builder reset();

        /**
         * Builds a new instance of a {@link DesertWell} populator with the
         * settings set within the builder.
         * 
         * @return A new instance of the populator
         * @throws IllegalStateException If there are any settings left unset
         *             which do not have default values
         */
        DesertWell build() throws IllegalStateException;

    }

}
