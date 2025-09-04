package plus.dragons.createintegratedfarming.integration.crabbersdelight.fishing;

import alabaster.crabbersdelight.common.block.entity.CrabTrapBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class CrabTrapArmInteractionPoint extends ArmInteractionPoint {
    public CrabTrapArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        if(level.getBlockEntity(pos) instanceof CrabTrapBlockEntity interaction){
            var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, level.getBlockState(pos), interaction, Direction.UP);
            if(cap!=null) return cap.insertItem(0, stack, simulate);
        }
        return stack;
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        if(level.getBlockEntity(pos) instanceof CrabTrapBlockEntity interaction){
            var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, level.getBlockState(pos), interaction, Direction.DOWN);
            if(cap!=null) return cap.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 28;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockEntity(pos) instanceof CrabTrapBlockEntity;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CrabTrapArmInteractionPoint(this, level, pos, state);
        }
    }
}
