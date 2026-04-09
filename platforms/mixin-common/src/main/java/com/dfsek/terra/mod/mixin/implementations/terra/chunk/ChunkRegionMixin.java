/*
 * This file is part of Terra.
 *
 * Terra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Terra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Terra.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dfsek.terra.mod.mixin.implementations.terra.chunk;

import com.dfsek.seismic.math.coord.CoordFunctions;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.block.state.BlockStateExtended;
import com.dfsek.terra.api.world.chunk.Chunk;
import com.dfsek.terra.mod.util.MinecraftUtil;


@Mixin(ChunkRegion.class)
@Implements(@Interface(iface = Chunk.class, prefix = "terraChunk$"))
public abstract class ChunkRegionMixin implements StructureWorldAccess {

    @Shadow
    public abstract ChunkPos getCenterPos();

    @Shadow
    public abstract ServerWorld toServerWorld();

    @Shadow
    public abstract QueryableTickScheduler<Block> getBlockTickScheduler();

    @Shadow
    public abstract QueryableTickScheduler<Fluid> getFluidTickScheduler();

    @Shadow
    public abstract net.minecraft.world.chunk.Chunk getChunk(int chunkX, int chunkZ);

    @Shadow
    public abstract net.minecraft.block.BlockState getBlockState(BlockPos pos);

    @Shadow
    public abstract boolean setBlockState(BlockPos pos, net.minecraft.block.BlockState state, int flags, int maxUpdateDepth);


    public void terraChunk$setBlock(int x, int y, int z, @NotNull BlockState data, boolean physics) {
        ChunkPos pos = getCenterPos();
        BlockPos blockPos = new BlockPos(CoordFunctions.chunkAndRelativeToAbsolute(pos.x, x), y,
            CoordFunctions.chunkAndRelativeToAbsolute(pos.z, z));
        net.minecraft.block.BlockState state;

        boolean isExtended = MinecraftUtil.isCompatibleBlockStateExtended(data);

        if(isExtended) {
            BlockStateArgument arg = ((BlockStateArgument) data);
            state = arg.getBlockState();
            setBlockState(blockPos, state, 0, 512);
            net.minecraft.world.chunk.Chunk chunk = getChunk(pos.x, pos.z);
            NbtCompound nbt = ((NbtCompound) (Object) ((BlockStateExtended) data).getData());
            MinecraftUtil.loadBlockEntity(chunk, toServerWorld(), blockPos, state, nbt);
        } else {
            state = (net.minecraft.block.BlockState) data;
            setBlockState(blockPos, state, 0, 512);
        }

        if(physics) {
            MinecraftUtil.schedulePhysics(state, blockPos, getFluidTickScheduler(), getBlockTickScheduler());
        }
    }

    public @NotNull BlockState terraChunk$getBlock(int x, int y, int z) {
        ChunkPos centerPos = getCenterPos();
        return (BlockState) ((ChunkRegion) (Object) this).getBlockState(
            new BlockPos(x + (centerPos.x << 4), y, z + (centerPos.z << 4)));
    }

    public int terraChunk$getX() {
        return getCenterPos().x;
    }

    public int terraChunk$getZ() {
        return getCenterPos().z;
    }
}
