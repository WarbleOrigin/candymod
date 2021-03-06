package com.ochotonida.candymod.block;

import com.ochotonida.candymod.CandyMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class BlockStackable extends Block {

    protected BlockStackable(Material material, MapColor mapcolor) {
        super(material, mapcolor);
        this.setCreativeTab(CandyMod.TAB_BLOCKS);
    }

    public void setName(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
    }


    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        boolean topSolid = worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
        return topSolid || worldIn.getBlockState(pos.down()).getBlock() instanceof BlockStackable;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    @Override // recursive block mining speed
    @SuppressWarnings("deprecation")
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.up()).getBlock().getClass().isInstance(this)) {
            return this.getBlockHardness(worldIn.getBlockState(pos.up()), worldIn, pos.up()) + this.blockHardness;
        }
        return this.blockHardness;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
            for (BlockPos blockpos : BlockPos.getAllInBox(pos.add(-4, -4, -4), pos.add(4, 4, 4))) {
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
                }
            }
        }
    }
}
