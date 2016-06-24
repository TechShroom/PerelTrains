/*
 * This file is part of PerelTrains, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
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
package com.techshroom.mods.pereltrains.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.techshroom.mods.pereltrains.Constants;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class GeneralUtility {

    @SideOnly(Side.CLIENT)
    public static final class Client {

        public static boolean buttonIsPressed(int id, GuiButton check) {
            return check.enabled && check.id == id;
        }

        private Client() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class SetBlockFlag {

        public static final int UPDATE = 1, SEND = 2, DONT_RE_RENDER = 4;
        public static final int UPDATE_AND_SEND = UPDATE | SEND;
        public static final int SEND_AND_DONT_RE_RENDER = SEND | DONT_RE_RENDER;
        public static final int UPDATE_AND_DONT_RE_RENDER =
                UPDATE | DONT_RE_RENDER;
        public static final int UPDATE_SEND_AND_DONT_RE_RENDER =
                UPDATE | SEND | DONT_RE_RENDER;

        private SetBlockFlag() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class SideConstants {

        public static final int BOTTOM = EnumFacing.DOWN.getIndex();
        public static final int TOP = EnumFacing.UP.getIndex();
        public static final int NORTH = EnumFacing.NORTH.getIndex();
        public static final int SOUTH = EnumFacing.SOUTH.getIndex();
        public static final int WEST = EnumFacing.WEST.getIndex();
        public static final int EAST = EnumFacing.EAST.getIndex();

        private SideConstants() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class Time {

        /**
         * @deprecated {@link TimeUnit}
         */
        @Deprecated
        public static int minutesAsSeconds(int minutes) {
            return minutes * 60;
        }

        public static int minutesAsTicks(int minutes) {
            return secondsAsTicks(minutesAsSeconds(minutes));
        }

        public static int secondsAsTicks(int seconds) {
            return seconds * 20;
        }

        private Time() {
            throw new AssertionError("Nope.");
        }
    }

    /**
     * Generates an array of length {@code len}, with each element equal to its
     * index.
     */
    public static int[] indexEqualsIndexArray(int len) {
        int[] out = new int[len];
        for (int i = 0; i < out.length; i++) {
            out[i] = i;
        }
        return out;
    }

    public static String address(String id, String object) {
        return id + ":" + object;
    }

    public static String addressMod(String object) {
        return address(Constants.MOD_ID, object);
    }

    private static final int[] _c;
    private static final int[] cc;

    static {
        cc = new int[6];
        _c = new int[6];
        _c[SideConstants.TOP] = cc[SideConstants.TOP] = SideConstants.TOP;
        _c[SideConstants.BOTTOM] =
                cc[SideConstants.BOTTOM] = SideConstants.BOTTOM;
        _c[SideConstants.NORTH] = SideConstants.EAST;
        _c[SideConstants.EAST] = SideConstants.SOUTH;
        _c[SideConstants.SOUTH] = SideConstants.WEST;
        _c[SideConstants.WEST] = SideConstants.NORTH;

        cc[SideConstants.NORTH] = SideConstants.WEST;
        cc[SideConstants.WEST] = SideConstants.SOUTH;
        cc[SideConstants.SOUTH] = SideConstants.EAST;
        cc[SideConstants.EAST] = SideConstants.NORTH;
    }

    public static int clockwise(int side) {
        return _c[side];
    }

    public static int counterClockwise(int side) {
        return cc[side];
    }

    public static EnumFacing getSignalFacing(EnumFacing signalToRailFace) {
        // Following factorio rules:
        // Signals north of the rails face west
        // Signals south of the rails face east
        // Signals west of the rails face south
        // Signals east of the rails face north
        switch (signalToRailFace.getOpposite()) {
            case NORTH:
                return EnumFacing.WEST;
            case SOUTH:
                return EnumFacing.EAST;
            case WEST:
                return EnumFacing.SOUTH;
            case EAST:
                return EnumFacing.NORTH;
            default:
                throw new IllegalStateException(
                        "non-standard rail-face " + signalToRailFace);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static boolean isClient(World w) {
        return w == null
                ? FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT
                : w.isRemote;
    }

    public static String getVariantString(IBlockState state) {
        return getPropertyString(state.getProperties());
    }

    private static String
            getPropertyString(Map<IProperty<?>, Comparable<?>> values) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Entry<IProperty<?>, Comparable<?>> entry : values.entrySet()) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty<?> property = entry.getKey();
            stringbuilder.append(property.getName());
            stringbuilder.append("=");
            stringbuilder.append(getPropertyName(property, entry.getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String
            getPropertyName(IProperty<T> property, Comparable<?> comparable) {
        return property.getName((T) comparable);
    }

    private GeneralUtility() {
    }

    public static NBTBase blockPosData(BlockPos conn) {
        return new NBTTagIntArray(
                new int[] { conn.getX(), conn.getY(), conn.getZ() });
    }

    public static BlockPos blockPosData(NBTBase nbt) {
        int[] data = ((NBTTagIntArray) nbt).getIntArray();
        return new BlockPos(data[0], data[1], data[2]);
    }

}
